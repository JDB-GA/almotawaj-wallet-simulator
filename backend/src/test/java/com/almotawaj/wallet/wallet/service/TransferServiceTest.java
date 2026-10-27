package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.model.request.TransferRequest;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceTest {
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final String RECIPIENT_EMAIL = "sara@example.com";

    @Mock
    private WalletProvisioner provisioner;
    @Mock
    private WalletLocker walletLocker;
    @Mock
    private RecipientResolver recipientResolver;
    @Mock
    private WalletHolderNames holderNames;
    @Mock
    private WalletTransactionRepository transactionRepository;
    @Mock
    private TransactionReferenceGenerator referenceGenerator;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AuditService auditService;

    private TransferService transferService;
    private Wallet sender;
    private Wallet receiver;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(provisioner, walletLocker, recipientResolver, holderNames, new Counterparties(holderNames),
                new DailyLimitPolicy(transactionRepository, Clock.systemUTC()), new WalletLedger(transactionRepository, referenceGenerator),
                eventPublisher, auditService);
        sender = WalletFixtures.wallet(UserRole.CLIENT, "100.000");
        receiver = WalletFixtures.wallet(UserRole.CLIENT, "5.000");
        when(recipientResolver.resolve(SENDER_ID, RECIPIENT_EMAIL)).thenReturn(receiver.getUser());
        when(provisioner.getOrCreate(SENDER_ID)).thenReturn(sender);
        when(provisioner.getOrCreate(receiver.getUser().getId())).thenReturn(receiver);
        when(walletLocker.lockPair(sender.getId(), receiver.getId())).thenReturn(new WalletLocker.LockedPair(sender, receiver));
        when(holderNames.fullName(any())).thenReturn("Holder");
        when(referenceGenerator.next()).thenReturn("TXN-20261005-AAAAAAAA", "TXN-20261005-BBBBBBBB");
        when(transactionRepository.sumAmountSince(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.saveAndFlush(any(WalletTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void transfer_movesMoneyBetweenWallets() {
        var response = transferService.transfer(SENDER_ID, new TransferRequest(RECIPIENT_EMAIL, new BigDecimal("40"), " Dinner "));

        assertThat(sender.getBalance()).isEqualByComparingTo("60.000");
        assertThat(receiver.getBalance()).isEqualByComparingTo("45.000");
        assertThat(response.type()).isEqualTo(TransactionType.TRANSFER_OUT);
        assertThat(response.description()).isEqualTo("Dinner");
        ArgumentCaptor<MoneyReceivedEvent> event = ArgumentCaptor.forClass(MoneyReceivedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().recipientUserId()).isEqualTo(receiver.getUser().getId());
        assertThat(event.getValue().amount()).isEqualByComparingTo("40.000");
        assertThat(event.getValue().senderName()).isEqualTo("Holder");
        verify(auditService).record(SENDER_ID, AuditAction.TRANSFER_COMPLETED, AuditTargetType.WALLET_TRANSACTION, response.id(), null);
    }

    @Test
    void transfer_rejectsInsufficientBalance() {
        assertThatThrownBy(() -> transferService.transfer(SENDER_ID, new TransferRequest(RECIPIENT_EMAIL, new BigDecimal("100.001"), null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.INSUFFICIENT_BALANCE);
        assertThat(receiver.getBalance()).isEqualByComparingTo("5.000");
        verify(transactionRepository, never()).saveAndFlush(any());
    }

    @Test
    void transfer_rejectsAmountAboveDailyLimit() {
        sender.setBalance(new BigDecimal("20000.000"));
        when(transactionRepository.sumAmountSince(any(), any(), any())).thenReturn(new BigDecimal("9999.000"));

        assertThatThrownBy(() -> transferService.transfer(SENDER_ID, new TransferRequest(RECIPIENT_EMAIL, new BigDecimal("2"), null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.DAILY_TRANSFER_LIMIT_EXCEEDED);
        verify(referenceGenerator, never()).next();
    }
}
