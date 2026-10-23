package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.CheckoutStatus;
import com.almotawaj.wallet.model.UserRole;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.model.WalletTransaction;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.repository.WalletTransactionRepository;
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
    private WalletProvisioner provisioner;
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

    @BeforeEach
    void setUp() {
        service = new CheckoutPaymentService(sessionRepository, purchaseRepository, provisioner, walletLocker, new WalletAccessPolicy(),
                new DailyLimitPolicy(transactionRepository, CLOCK), new WalletLedger(transactionRepository, referenceGenerator),
                new Counterparties(names), new CheckoutSessionMapper(names, CLOCK, "http://localhost:5173"), auditService,
                eventPublisher, CLOCK);
        payerWallet = WalletFixtures.wallet(UserRole.CLIENT, "50.000");
        merchantWallet = WalletFixtures.wallet(UserRole.MERCHANT, "0.000");
        session = new CheckoutSession();
        session.setId(SESSION_ID);
        session.setMerchant(merchantWallet.getUser());
        session.setOrderReference("ORDER-1");
        session.setAmount(new BigDecimal("20.000"));
        session.setExpiresAt(NOW.plusSeconds(60));
        when(sessionRepository.findByIdForUpdate(SESSION_ID)).thenReturn(Optional.of(session));
        when(provisioner.getOrCreate(payerWallet.getUser().getId())).thenReturn(payerWallet);
        when(provisioner.getOrCreate(merchantWallet.getUser().getId())).thenReturn(merchantWallet);
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
