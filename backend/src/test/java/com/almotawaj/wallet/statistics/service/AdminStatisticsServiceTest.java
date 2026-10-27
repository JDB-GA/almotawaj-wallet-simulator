package com.almotawaj.wallet.statistics.service;

import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminStatisticsServiceTest {
    private static final TransactionSearchRequest NO_FILTER = new TransactionSearchRequest(null, null, null, null, null, null, null);

    @Autowired
    private AdminStatisticsService service;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        Wallet wallet = wallet(user("sara@example.com", "+97333000001", UserRole.CLIENT), "BH02ALMT00000000000001");
        user("shop@example.com", "+97333000002", UserRole.MERCHANT);
        entry(wallet, TransactionType.TOP_UP, TransactionDirection.CREDIT, "50.000", "TXN-20261006-AAAAAAAA");
        entry(wallet, TransactionType.TRANSFER_OUT, TransactionDirection.DEBIT, "20.000", "TXN-20261006-BBBBBBBB");
    }

    @Test
    void userStatistics_countEveryRoleIncludingEmptyOnes() {
        var statistics = service.getUserStatistics();

        assertThat(statistics.totalUsers()).isEqualTo(2);
        assertThat(statistics.usersByRole()).containsEntry(UserRole.CLIENT, 1L).containsEntry(UserRole.MERCHANT, 1L)
                .containsEntry(UserRole.ADMIN, 0L).containsEntry(UserRole.SUPER_ADMIN, 0L);
    }

    @Test
    void transactionStatistics_splitCreditedAndDebitedTotals() {
        var statistics = service.getTransactionStatistics(NO_FILTER);

        assertThat(statistics.totalTransactions()).isEqualTo(2);
        assertThat(statistics.totalCredited()).isEqualByComparingTo("50.000");
        assertThat(statistics.totalDebited()).isEqualByComparingTo("20.000");
    }

    @Test
    void transactionStatistics_followTheFilters() {
        var statistics = service.getTransactionStatistics(
                new TransactionSearchRequest(null, null, TransactionDirection.DEBIT, null, null, null, null));

        assertThat(statistics.totalTransactions()).isEqualTo(1);
        assertThat(statistics.totalCredited()).isEqualByComparingTo("0");
    }

    @Test
    void listTransactions_searchesByOwnerEmail() {
        var owned = service.listTransactions(
                new TransactionSearchRequest("SARA@", null, null, null, null, null, null), PageRequest.of(0, 10));
        var none = service.listTransactions(
                new TransactionSearchRequest("nobody@", null, null, null, null, null, null), PageRequest.of(0, 10));

        assertThat(owned.content()).hasSize(2).allMatch(entry -> entry.walletOwnerEmail().equals("sara@example.com"));
        assertThat(none.content()).isEmpty();
    }

    private User user(String email, String mobile, UserRole role) {
        User user = new User();
        user.setEmailAddress(email);
        user.setMobileNumber(mobile);
        user.setPassword("hashed");
        user.setRole(role);
        return userRepository.save(user);
    }

    private Wallet wallet(User owner, String iban) {
        Wallet wallet = new Wallet();
        wallet.setUser(owner);
        wallet.setIban(iban);
        return walletRepository.save(wallet);
    }

    private void entry(Wallet wallet, TransactionType type, TransactionDirection direction, String amount, String reference) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(type);
        transaction.setDirection(direction);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setBalanceAfter(new BigDecimal(amount));
        transaction.setReference(reference);
        transaction.setCounterpartyName("Counterparty");
        transactionRepository.save(transaction);
    }
}
