package com.almotawaj.wallet.voucher.service;

import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import com.almotawaj.wallet.exception.ConflictException;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutBatchStatus;
import com.almotawaj.wallet.payout.model.request.CreatePayoutRequest;
import com.almotawaj.wallet.payout.model.request.PayoutItemRequest;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.payout.service.PayoutService;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.model.request.UpdateVoucherRequest;
import com.almotawaj.wallet.voucher.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.voucher.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrencyIntegrationTest {
    private static final int BUYERS = 20;
    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private IdempotentVoucherPurchaseService purchases;
    @Autowired
    private VoucherService vouchers;
    @Autowired
    private PayoutService payouts;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository transactionRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherPurchaseRepository purchaseRepository;
    @Autowired
    private IdempotencyRecordRepository idempotencyRepository;
    @Autowired
    private PayoutBatchRepository batchRepository;
    @Autowired
    private PayoutItemRepository itemRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;

    private User merchant;

    @BeforeEach
    void setUp() {
        merchant = user(UserRole.MERCHANT);
    }

    @AfterEach
    void cleanUp() {
        idempotencyRepository.deleteAll();
        purchaseRepository.deleteAll();
        itemRepository.deleteAll();
        batchRepository.deleteAll();
        voucherRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
    }

    @RepeatedTest(50)
    void lastVoucher_onlyOneOfManySimultaneousBuyersGetsIt() throws Exception {
        wallet(merchant, "0.000");
        Voucher voucher = voucher(1, 1);
        List<UUID> buyers = new ArrayList<>();
        for (int index = 0; index < BUYERS; index++) {
            User buyer = user(UserRole.CLIENT);
            wallet(buyer, "100.000");
            buyers.add(buyer.getId());
        }

        List<Outcome> outcomes = race(buyers.stream()
                .<Callable<VoucherPurchaseResponse>>map(buyerId -> () -> purchases.purchase(buyerId, voucher.getId(), null))
                .toList());

        assertThat(outcomes.stream().filter(Outcome::succeeded)).hasSize(1);
        assertThat(outcomes.stream().filter(outcome -> outcome.error() instanceof ConflictException)).hasSize(BUYERS - 1);
        assertThat(voucherRepository.findById(voucher.getId()).orElseThrow().getRemaining()).isZero();
        assertThat(purchaseRepository.count()).isEqualTo(1);
        assertThat(balanceOf(merchant)).isEqualByComparingTo("45.000");
        BigDecimal buyersTotal = buyers.stream().map(id -> walletRepository.findByUserId(id).orElseThrow().getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(buyersTotal).isEqualByComparingTo(new BigDecimal("100.000").multiply(BigDecimal.valueOf(BUYERS)).subtract(new BigDecimal("45.000")));
    }

    @Test
    void sameIdempotencyKey_chargesOnlyOnceEvenWhenSentInParallel() throws Exception {
        wallet(merchant, "0.000");
        Voucher voucher = voucher(5, 5);
        User buyer = user(UserRole.CLIENT);
        wallet(buyer, "100.000");
        String key = UUID.randomUUID().toString();
        List<Callable<VoucherPurchaseResponse>> tasks = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            tasks.add(() -> purchases.purchase(buyer.getId(), voucher.getId(), key));
        }

        List<Outcome> outcomes = race(tasks);

        assertThat(outcomes).allMatch(Outcome::succeeded);
        assertThat(outcomes.stream().map(outcome -> outcome.response().id()).distinct()).hasSize(1);
        assertThat(purchaseRepository.count()).isEqualTo(1);
        assertThat(balanceOf(buyer)).isEqualByComparingTo("55.000");
        assertThat(voucherRepository.findById(voucher.getId()).orElseThrow().getRemaining()).isEqualTo(4);
    }

    @Test
    void parallelPayouts_neverOverdrawTheMerchant() throws Exception {
        wallet(merchant, "100.000");
        List<PayoutItemRequest> items = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            User recipient = user(UserRole.CLIENT);
            wallet(recipient, "0.000");
            items.add(new PayoutItemRequest(recipient.getEmailAddress(), new BigDecimal("15.000")));
        }

        UUID batchId = payouts.create(merchant.getId(), new CreatePayoutRequest(items)).id();
        PayoutBatch batch = awaitFinished(batchId);

        assertThat(batch.getPaidCount()).isEqualTo(6);
        assertThat(batch.getFailedCount()).isEqualTo(4);
        assertThat(batch.getStatus()).isEqualTo(PayoutBatchStatus.PARTIALLY_FAILED);
        assertThat(balanceOf(merchant)).isEqualByComparingTo("10.000");
    }

    @Test
    void staleVoucherEdit_isRejectedInsteadOfOverwriting() {
        wallet(merchant, "0.000");
        Voucher voucher = voucher(5, 1);
        Instant expiry = Instant.now().plus(Duration.ofDays(2));
        vouchers.update(merchant.getId(), voucher.getId(),
                new UpdateVoucherRequest("New title", new BigDecimal("40.000"), voucher.getStatus(), expiry, null, voucher.getVersion()));

        assertThatThrownBy(() -> vouchers.update(merchant.getId(), voucher.getId(),
                new UpdateVoucherRequest("Stale title", new BigDecimal("30.000"), voucher.getStatus(), expiry, null, voucher.getVersion())))
                .isInstanceOf(ConflictException.class);
        assertThat(voucherRepository.findById(voucher.getId()).orElseThrow().getTitle()).isEqualTo("New title");
    }

    private List<Outcome> race(List<Callable<VoucherPurchaseResponse>> tasks) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(tasks.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<VoucherPurchaseResponse>> futures = tasks.stream().map(task -> pool.submit(() -> {
            start.await();
            return task.call();
        })).toList();
        start.countDown();
        List<Outcome> outcomes = new ArrayList<>();
        for (Future<VoucherPurchaseResponse> future : futures) {
            try {
                outcomes.add(new Outcome(future.get(), null));
            } catch (ExecutionException e) {
                outcomes.add(new Outcome(null, e.getCause()));
            }
        }
        pool.shutdown();
        return outcomes;
    }

    private PayoutBatch awaitFinished(UUID batchId) throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(20));
        PayoutBatch batch = batchRepository.findById(batchId).orElseThrow();
        while (batch.getStatus() == PayoutBatchStatus.PROCESSING && Instant.now().isBefore(deadline)) {
            Thread.sleep(50);
            batch = batchRepository.findById(batchId).orElseThrow();
        }
        return batch;
    }

    private Voucher voucher(int quantity, int perClientLimit) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(merchant);
        voucher.setTitle("Gift card");
        voucher.setPrice(new BigDecimal("45.000"));
        voucher.setFaceValue(new BigDecimal("50.000"));
        voucher.setTotalQuantity(quantity);
        voucher.setRemaining(quantity);
        voucher.setPerClientLimit(perClientLimit);
        voucher.setSaleEndsAt(Instant.now().plus(Duration.ofDays(1)));
        return voucherRepository.saveAndFlush(voucher);
    }

    private User user(UserRole role) {
        int number = SEQUENCE.incrementAndGet();
        User user = new User();
        user.setEmailAddress("user" + number + "@example.com");
        user.setMobileNumber("+9733%07d".formatted(number));
        user.setPassword("hashed");
        user.setRole(role);
        user.setKycStatus(role == UserRole.CLIENT ? KycStatus.APPROVED : KycStatus.NOT_SUBMITTED);
        return userRepository.saveAndFlush(user);
    }

    private void wallet(User owner, String balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban("BH02ALMT%014d".formatted(SEQUENCE.incrementAndGet()));
        wallet.setBalance(new BigDecimal(balance));
        walletRepository.saveAndFlush(wallet);
    }

    private BigDecimal balanceOf(User user) {
        return walletRepository.findByUserId(user.getId()).orElseThrow().getBalance();
    }

    private record Outcome(VoucherPurchaseResponse response, Throwable error) {
        boolean succeeded() {
            return error == null;
        }
    }
}
