package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.CheckoutStatus;
import com.almotawaj.wallet.model.KycStatus;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.UserRole;
import com.almotawaj.wallet.model.Voucher;
import com.almotawaj.wallet.model.VoucherPurchase;
import com.almotawaj.wallet.model.VoucherPurchaseStatus;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.model.response.CheckoutViewResponse;
import com.almotawaj.wallet.model.response.UsableVoucherResponse;
import com.almotawaj.wallet.repository.AuditLogRepository;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.repository.UserRepository;
import com.almotawaj.wallet.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import com.almotawaj.wallet.repository.WalletRepository;
import com.almotawaj.wallet.repository.WalletTransactionRepository;
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
class VoucherRedemptionIntegrationTest {
    private static final int TABS = 10;
    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private IdempotentVoucherPurchaseService purchases;
    @Autowired
    private CheckoutPaymentService payments;
    @Autowired
    private CheckoutRefundService refunds;
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
    private CheckoutSessionRepository sessionRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;

    private User merchant;
    private User buyer;
    private UUID voucherId;
    private UUID purchaseId;

    @BeforeEach
    void setUp() {
        merchant = user(UserRole.MERCHANT);
        wallet(merchant, "0.000");
        buyer = user(UserRole.CLIENT);
        wallet(buyer, "100.000");
        voucherId = voucher(merchant).getId();
        purchaseId = purchases.purchase(buyer.getId(), voucherId, null).id();
    }

    @AfterEach
    void cleanUp() {
        idempotencyRepository.deleteAll();
        purchaseRepository.deleteAll();
        sessionRepository.deleteAll();
        voucherRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void voucherWorthMoreThanTheOrder_paysItInFullAndMovesNoMoney() {
        CheckoutSession session = session(merchant, "20.000");

        CheckoutViewResponse view = payments.pay(buyer.getId(), session.getId(), purchaseId);

        assertThat(view.status()).isEqualTo(CheckoutStatus.PAID);
        assertThat(view.voucherDiscount()).isEqualByComparingTo("20.000");
        assertThat(balanceOf(buyer)).isEqualByComparingTo("55.000");
        assertThat(balanceOf(merchant)).isEqualByComparingTo("45.000");
        VoucherPurchase purchase = purchaseRepository.findById(purchaseId).orElseThrow();
        assertThat(purchase.getStatus()).isEqualTo(VoucherPurchaseStatus.REDEEMED);
        assertThat(purchase.getRedeemedAt()).isNotNull();
    }

    @Test
    void voucherWorthLessThanTheOrder_buyerPaysTheRest() {
        CheckoutSession session = session(merchant, "80.000");

        payments.pay(buyer.getId(), session.getId(), purchaseId);

        assertThat(balanceOf(buyer)).isEqualByComparingTo("25.000");
        assertThat(balanceOf(merchant)).isEqualByComparingTo("75.000");
    }

    @Test
    void usableVouchers_listsOnlyUnusedVouchersFromTheCheckoutMerchant() {
        CheckoutSession own = session(merchant, "20.000");
        User otherMerchant = user(UserRole.MERCHANT);
        CheckoutSession other = session(otherMerchant, "20.000");

        assertThat(payments.usableVouchers(buyer.getId(), own.getId())).extracting(UsableVoucherResponse::id).containsExactly(purchaseId);
        assertThat(payments.usableVouchers(buyer.getId(), other.getId())).isEmpty();

        payments.pay(buyer.getId(), own.getId(), purchaseId);

        assertThat(payments.usableVouchers(buyer.getId(), session(merchant, "5.000").getId())).isEmpty();
    }

    @Test
    void voucherFromAnotherMerchant_isRejectedAndNothingChanges() {
        User otherMerchant = user(UserRole.MERCHANT);
        wallet(otherMerchant, "0.000");
        CheckoutSession session = session(otherMerchant, "20.000");

        assertThatThrownBy(() -> payments.pay(buyer.getId(), session.getId(), purchaseId))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.VOUCHER_NOT_USABLE);
        assertThat(sessionRepository.findById(session.getId()).orElseThrow().getStatus()).isEqualTo(CheckoutStatus.PENDING);
        assertThat(purchaseRepository.findById(purchaseId).orElseThrow().getStatus()).isEqualTo(VoucherPurchaseStatus.UNUSED);
    }

    @Test
    void expiredVoucher_isNotListedAndIsRejected() {
        expireVoucherAt(Instant.now().minus(Duration.ofMinutes(1)));
        CheckoutSession session = session(merchant, "20.000");

        assertThat(payments.usableVouchers(buyer.getId(), session.getId())).isEmpty();
        assertThatThrownBy(() -> payments.pay(buyer.getId(), session.getId(), purchaseId))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.VOUCHER_NOT_USABLE);
        assertThat(purchaseRepository.findById(purchaseId).orElseThrow().getStatus()).isEqualTo(VoucherPurchaseStatus.UNUSED);
    }

    @Test
    void voucherBeforeItsExpiry_canStillBeUsed() {
        expireVoucherAt(Instant.now().plus(Duration.ofDays(1)));
        CheckoutSession session = session(merchant, "20.000");

        assertThat(payments.usableVouchers(buyer.getId(), session.getId())).extracting(UsableVoucherResponse::id).containsExactly(purchaseId);
        assertThat(payments.pay(buyer.getId(), session.getId(), purchaseId).status()).isEqualTo(CheckoutStatus.PAID);
    }

    @RepeatedTest(20)
    void sameVoucherInManyTabsAtOnce_isUsedExactlyOnce() throws Exception {
        List<UUID> sessionIds = new ArrayList<>();
        for (int index = 0; index < TABS; index++) {
            sessionIds.add(session(merchant, "20.000").getId());
        }

        List<Throwable> errors = race(sessionIds);

        assertThat(errors.stream().filter(error -> error == null)).hasSize(1);
        assertThat(errors.stream().filter(error -> error instanceof BusinessRuleException rule
                && ErrorCodes.VOUCHER_NOT_USABLE.equals(rule.getCode()))).hasSize(TABS - 1);
        assertThat(sessionRepository.findAllById(sessionIds).stream().filter(found -> found.getStatus() == CheckoutStatus.PAID))
                .hasSize(1);
        assertThat(balanceOf(buyer)).isEqualByComparingTo("55.000");
    }

    @Test
    void refund_returnsOnlyTheCashPartAndFreesTheVoucher() {
        CheckoutSession session = session(merchant, "80.000");
        payments.pay(buyer.getId(), session.getId(), purchaseId);

        refunds.refund(merchant.getId(), session.getId());

        assertThat(balanceOf(buyer)).isEqualByComparingTo("55.000");
        assertThat(balanceOf(merchant)).isEqualByComparingTo("45.000");
        VoucherPurchase purchase = purchaseRepository.findById(purchaseId).orElseThrow();
        assertThat(purchase.getStatus()).isEqualTo(VoucherPurchaseStatus.UNUSED);
        assertThat(purchase.getRedeemedCheckout()).isNull();
    }

    private List<Throwable> race(List<UUID> sessionIds) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(sessionIds.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<CheckoutViewResponse>> futures = sessionIds.stream().map(sessionId -> pool.submit(() -> {
            start.await();
            return payments.pay(buyer.getId(), sessionId, purchaseId);
        })).toList();
        start.countDown();
        List<Throwable> errors = new ArrayList<>();
        for (Future<CheckoutViewResponse> future : futures) {
            try {
                future.get();
                errors.add(null);
            } catch (ExecutionException e) {
                errors.add(e.getCause());
            }
        }
        pool.shutdown();
        return errors;
    }

    private CheckoutSession session(User owner, String amount) {
        CheckoutSession session = new CheckoutSession();
        session.setMerchant(owner);
        session.setOrderReference("ORDER-" + SEQUENCE.incrementAndGet());
        session.setAmount(new BigDecimal(amount));
        session.setExpiresAt(Instant.now().plus(Duration.ofMinutes(30)));
        return sessionRepository.saveAndFlush(session);
    }

    private void expireVoucherAt(Instant expiresAt) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
        voucher.setExpiresAt(expiresAt);
        voucherRepository.saveAndFlush(voucher);
    }

    private Voucher voucher(User owner) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(owner);
        voucher.setTitle("Gift card");
        voucher.setPrice(new BigDecimal("45.000"));
        voucher.setFaceValue(new BigDecimal("50.000"));
        voucher.setTotalQuantity(5);
        voucher.setRemaining(5);
        voucher.setPerClientLimit(5);
        voucher.setSaleEndsAt(Instant.now().plus(Duration.ofDays(1)));
        return voucherRepository.saveAndFlush(voucher);
    }

    private User user(UserRole role) {
        int number = SEQUENCE.incrementAndGet();
        User user = new User();
        user.setEmailAddress("redeem" + number + "@example.com");
        user.setMobileNumber("+9734%07d".formatted(number));
        user.setPassword("hashed");
        user.setRole(role);
        user.setKycStatus(role == UserRole.CLIENT ? KycStatus.APPROVED : KycStatus.NOT_SUBMITTED);
        return userRepository.saveAndFlush(user);
    }

    private void wallet(User owner, String balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban("BH03ALMT%014d".formatted(SEQUENCE.incrementAndGet()));
        wallet.setBalance(new BigDecimal(balance));
        walletRepository.saveAndFlush(wallet);
    }

    private BigDecimal balanceOf(User user) {
        return walletRepository.findByUserId(user.getId()).orElseThrow().getBalance();
    }
}
