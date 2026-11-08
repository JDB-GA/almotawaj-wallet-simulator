package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.TopUpSource;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.model.request.TopUpRequest;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID WALLET_ID = UUID.randomUUID();
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-05T09:00:00Z"), ZoneOffset.UTC);

    @Mock
    private WalletProvisioner provisioner;
    @Mock
    private WalletLocker walletLocker;
    @Mock
    private WalletTransactionRepository transactionRepository;
    @Mock
    private TransactionReferenceGenerator referenceGenerator;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AuditService auditService;
    @Mock
    private WalletHolderNames holderNames;

    private WalletService walletService;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        WalletLedger ledger = new WalletLedger(transactionRepository, referenceGenerator);
        DailyLimitPolicy limiter = new DailyLimitPolicy(transactionRepository, CLOCK);
        walletService = new WalletService(provisioner, walletLocker, transactionRepository, ledger, limiter,
                eventPublisher, auditService, holderNames);
        wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setBalance(new BigDecimal("10.000"));
        when(provisioner.getOrCreate(USER_ID)).thenReturn(wallet);
        when(walletLocker.lock(WALLET_ID)).thenReturn(wallet);
    }

    private void receivedToday(String amount) {
        when(transactionRepository.sumAmountSince(eq(WALLET_ID), eq(TransactionType.TOP_UP), any()))
                .thenReturn(new BigDecimal(amount));
    }

    @Test
    void topUp_creditsBalanceWithSourceDetails() {
        receivedToday("0");
        when(referenceGenerator.next()).thenReturn("TXN-20261005-ABCDEFGH");
        when(transactionRepository.saveAndFlush(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = walletService.topUp(USER_ID, new TopUpRequest(TopUpSource.NBB_SALARY, new BigDecimal("150.5")));

        assertThat(wallet.getBalance()).isEqualByComparingTo("160.500");
        assertThat(response.amount()).isEqualTo(new BigDecimal("150.500"));
        assertThat(response.counterpartyName()).isEqualTo(TopUpSource.NBB_SALARY.getHolderName());
        assertThat(response.counterpartyIban()).isEqualTo(TopUpSource.NBB_SALARY.getIban());
        assertThat(response.description()).isEqualTo(TopUpSource.NBB_SALARY.getBankName());
        ArgumentCaptor<MoneyReceivedEvent> event = ArgumentCaptor.forClass(MoneyReceivedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().recipientUserId()).isEqualTo(USER_ID);
        assertThat(event.getValue().amount()).isEqualByComparingTo("150.500");
        assertThat(event.getValue().senderName()).isEqualTo(TopUpSource.NBB_SALARY.getHolderName());
        verify(auditService).record(USER_ID, AuditAction.TOP_UP_COMPLETED, AuditTargetType.WALLET_TRANSACTION, response.id(), null);
    }

    @Test
    void topUp_rejectsAmountAboveRemainingDailyLimit() {
        receivedToday("9000.000");

        assertThatThrownBy(() -> walletService.topUp(USER_ID, new TopUpRequest(TopUpSource.BBK_SAVINGS, new BigDecimal("1000.001"))))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.DAILY_TOP_UP_LIMIT_EXCEEDED);
        assertThat(wallet.getBalance()).isEqualByComparingTo("10.000");
        verify(transactionRepository, never()).saveAndFlush(any());
    }
}
