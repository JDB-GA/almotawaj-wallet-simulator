package com.almotawaj.wallet.voucher.controller;

import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.support.TestTokens;
import org.springframework.beans.factory.annotation.Value;
import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountStatus;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ParallelPurchaseHttpTest {
    private static final int BUYERS = 20;

    @LocalServerPort
    private int port;
    @Value("${jwt-secret}")
    private String jwtSecret;
    @Autowired
    private AccountHolderRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository transactionRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherPurchaseRepository purchaseRepository;

    @AfterEach
    void cleanUp() {
        purchaseRepository.deleteAll();
        voucherRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void twentySimultaneousRequestsForTheLastVoucher_giveOne201AndNineteen409() {
        AccountHolder merchant = user("http-shop@example.com", "+97333200000", AccountRole.MERCHANT);
        wallet(merchant, "BH02ALMT00000000000200");
        Voucher voucher = voucher(merchant);
        List<String> cookies = new ArrayList<>();
        for (int index = 1; index <= BUYERS; index++) {
            AccountHolder buyer = user("http-buyer" + index + "@example.com", "+973332%05d".formatted(index), AccountRole.CLIENT);
            wallet(buyer, "BH02ALMT%014d".formatted(200 + index));
            cookies.add(SecurityConstants.AUTH_COOKIE_NAME + "=" + TestTokens.forAccount(buyer, jwtSecret));
        }

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + port + "/api/vouchers/" + voucher.getId() + "/purchase");
        List<CompletableFuture<Integer>> calls = cookies.stream()
                .map(cookie -> HttpRequest.newBuilder(uri).header("Cookie", cookie).POST(HttpRequest.BodyPublishers.noBody()).build())
                .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenApply(HttpResponse::statusCode))
                .toList();
        Map<Integer, Long> statuses = calls.stream().map(CompletableFuture::join)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertThat(statuses).containsEntry(201, 1L).containsEntry(409, (long) BUYERS - 1).hasSize(2);
        assertThat(voucherRepository.findById(voucher.getId()).orElseThrow().getRemaining()).isZero();
        assertThat(purchaseRepository.count()).isEqualTo(1);
    }

    private Voucher voucher(AccountHolder merchant) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(merchant);
        voucher.setTitle("Last one");
        voucher.setPrice(new BigDecimal("10.000"));
        voucher.setFaceValue(new BigDecimal("12.000"));
        voucher.setTotalQuantity(1);
        voucher.setRemaining(1);
        voucher.setPerClientLimit(1);
        voucher.setSaleEndsAt(Instant.now().plus(Duration.ofDays(1)));
        return voucherRepository.saveAndFlush(voucher);
    }

    private AccountHolder user(String email, String mobile, AccountRole role) {
        AccountHolder user = new AccountHolder();
        user.setEmailAddress(email);
        user.setMobileNumber(mobile);
        user.setRole(role);
        user.setId(java.util.UUID.randomUUID());
        user.setStatus(AccountStatus.ACTIVE);
        user.setKycApproved(role == AccountRole.CLIENT);
        return userRepository.saveAndFlush(user);
    }

    private void wallet(AccountHolder owner, String iban) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban(iban);
        wallet.setBalance(new BigDecimal("100.000"));
        walletRepository.saveAndFlush(wallet);
    }
}
