package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.CheckoutStatus;
import com.almotawaj.wallet.model.Counterparty;
import com.almotawaj.wallet.model.TransactionType;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.model.WalletTransaction;
import com.almotawaj.wallet.model.response.CheckoutViewResponse;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutPaymentService {
    private final CheckoutSessionRepository sessionRepository;
    private final WalletProvisioner provisioner;
    private final WalletLocker walletLocker;
    private final WalletAccessPolicy accessPolicy;
    private final DailyLimitPolicy dailyLimitPolicy;
    private final WalletLedger ledger;
    private final Counterparties counterparties;
    private final CheckoutSessionMapper mapper;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public CheckoutViewResponse pay(UUID payerId, UUID sessionId) {
        CheckoutSession session = sessionRepository.findByIdForUpdate(sessionId).orElseThrow(() ->
                new InformationNotFoundException(GatewayMessages.CHECKOUT_NOT_FOUND, ErrorCodes.CHECKOUT_NOT_FOUND));
        Instant now = clock.instant();
        ensurePayable(session, now);
        User merchant = session.getMerchant();
        WalletLocker.LockedPair locked = walletLocker.lockPair(provisioner.getOrCreate(payerId).getId(),
                provisioner.getOrCreate(merchant.getId()).getId());
        Wallet payerWallet = locked.source();
        Wallet merchantWallet = locked.target();
        BigDecimal amount = session.getAmount();
        if (payerWallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }
        dailyLimitPolicy.ensureCheckoutAllowed(payerWallet.getId(), amount);

        Counterparty payerDetails = counterparties.of(payerWallet);
        WalletTransaction received = ledger.credit(merchantWallet, TransactionType.PAYMENT_RECEIVED, amount, payerDetails,
                session.getOrderReference());
        ledger.debit(payerWallet, TransactionType.PAYMENT, amount, counterparties.of(merchantWallet), session.getOrderReference());
        session.setPayer(payerWallet.getUser());
        session.setStatus(CheckoutStatus.PAID);
        session.setPaidAt(now);

        auditService.record(payerId, AuditAction.CHECKOUT_PAID, AuditTargetType.CHECKOUT_SESSION, sessionId, session.getOrderReference());
        eventPublisher.publishEvent(new MoneyReceivedEvent(merchant.getId(), amount, payerDetails.name(), received.getReference()));
        eventPublisher.publishEvent(new CheckoutStatusChangedEvent(sessionId));
        return mapper.toView(session);
    }

    private void ensurePayable(CheckoutSession session, Instant now) {
        CheckoutStatus status = session.statusAt(now);
        if (status == CheckoutStatus.EXPIRED) {
            throw new BusinessRuleException(GatewayMessages.CHECKOUT_EXPIRED, ErrorCodes.CHECKOUT_EXPIRED);
        }
        if (status != CheckoutStatus.PENDING) {
            throw new BusinessRuleException(GatewayMessages.CHECKOUT_NOT_PENDING, ErrorCodes.CHECKOUT_NOT_PENDING);
        }
        if (!accessPolicy.canReceive(session.getMerchant())) {
            throw new BusinessRuleException(GatewayMessages.MERCHANT_UNAVAILABLE, ErrorCodes.MERCHANT_UNAVAILABLE);
        }
    }
}
