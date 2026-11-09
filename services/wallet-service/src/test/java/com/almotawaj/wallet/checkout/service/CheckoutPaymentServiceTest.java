package com.almotawaj.wallet.checkout.service;

import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.checkout.model.CheckoutSession;
import com.almotawaj.wallet.checkout.model.CheckoutStatus;
import com.almotawaj.wallet.checkout.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import com.almotawaj.wallet.wallet.service.Counterparties;
import com.almotawaj.wallet.wallet.service.DailyLimitPolicy;
import com.almotawaj.wallet.wallet.service.TransactionReferenceGenerator;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import com.almotawaj.wallet.wallet.service.WalletFixtures;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import com.almotawaj.wallet.wallet.service.WalletLedger;
import com.almotawaj.wallet.wallet.service.WalletLocker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckoutPaymentServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-06T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final UUID SESSION_ID = UUID.randomUUID();

    @Mock
    private CheckoutSessionRepository sessionRepository;
    @Mock
    private VoucherPurchaseRepository purchaseRepository;
    @Mock
    private WalletLocker walletLocker;
    @Mock
    private WalletHolderNames names;
    @Mock
    private WalletTransactionRepository transactionRepository;
    @Mock
    private TransactionReferenceGenerator referenceGenerator;
    @Mock
    private AuditService auditService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private CheckoutPaymentService service;
    private CheckoutSession session;
    private Wallet payerWallet;
    private Wallet merchantWallet;

    private WalletAccessPolicy accessPolicy;

    @BeforeEach
    void setUp() {
        accessPolicy = org.mockito.Mockito.spy(new WalletAccessPolicy(org.mockito.Mockito.mock(com.almotawaj.wallet.wallet.repository.WalletRepository.class)));
        service = new CheckoutPaymentService(sessionRepository, purchaseRepository, walletLocker, accessPolicy,
                new DailyLimitPolicy(transactionRepository, CLOCK), new WalletLedger(transactionRepository, referenceGenerator),
                new Counterparties(names), new CheckoutSessionMapper(names, CLOCK, "http://localhost:5173"), auditService,
                eventPublisher, CLOCK);
        payerWallet = WalletFixtures.wallet(AccountRole.CLIENT, "50.000");
        merchantWallet = WalletFixtures.wallet(AccountRole.MERCHANT, "0.000");
        session = new CheckoutSession();
        session.setId(SESSION_ID);
        session.setMerchant(merchantWallet.getUser());
        session.setOrderReference("ORDER-1");
        session.setAmount(new BigDecimal("20.000"));
        session.setExpiresAt(NOW.plusSeconds(60));
        when(sessionRepository.findByIdForUpdate(SESSION_ID)).thenReturn(Optional.of(session));
        org.mockito.Mockito.doReturn(payerWallet).when(accessPolicy).usableWallet(payerWallet.getUser().getId());
        org.mockito.Mockito.doReturn(merchantWallet).when(accessPolicy).usableWallet(merchantWallet.getUser().getId());
        WalletLocker.LockedPair locked = new WalletLocker.LockedPair(payerWallet, merchantWallet);
        when(walletLocker.lockPair(payerWallet.getId(), merchantWallet.getId())).thenReturn(locked);
        when(referenceGenerator.next()).thenReturn("TXN-20261006-AAAAAAAA", "TXN-20261006-BBBBBBBB");
        when(transactionRepository.sumAmountSince(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.saveAndFlush(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void pay_movesMoneyToTheMerchantAndMarksPaid() {
        var view = service.pay(payerWallet.getUser().getId(), SESSION_ID, null);

        assertThat(view.status()).isEqualTo(CheckoutStatus.PAID);
        assertThat(payerWallet.getBalance()).isEqualByComparingTo("30.000");
        assertThat(merchantWallet.getBalance()).isEqualByComparingTo("20.000");
        assertThat(session.getPayer()).isSameAs(payerWallet.getUser());
        assertThat(session.getPaidAt()).isEqualTo(NOW);
    }

    @Test
    void pay_rejectsExpiredAndAlreadyPaidSessions() {
        session.setExpiresAt(NOW);
        assertRejected(ErrorCodes.CHECKOUT_EXPIRED);

        session.setStatus(CheckoutStatus.PAID);
        assertRejected(ErrorCodes.CHECKOUT_NOT_PENDING);
    }

    @Test
    void pay_rejectsInsufficientBalance() {
        payerWallet.setBalance(new BigDecimal("19.999"));

        assertRejected(ErrorCodes.INSUFFICIENT_BALANCE);
    }

    private void assertRejected(String code) {
        assertThatThrownBy(() -> service.pay(payerWallet.getUser().getId(), SESSION_ID, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", code);
        assertThat(merchantWallet.getBalance()).isEqualByComparingTo("0.000");
        verify(transactionRepository, never()).saveAndFlush(any());
    }
}
