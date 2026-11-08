package com.almotawaj.wallet.checkout.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.checkout.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.checkout.model.CheckoutSession;
import com.almotawaj.wallet.checkout.model.CheckoutStatus;
import com.almotawaj.wallet.checkout.model.response.CheckoutSessionResponse;
import com.almotawaj.wallet.checkout.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.voucher.model.VoucherPurchaseStatus;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.Counterparty;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.service.Counterparties;
import com.almotawaj.wallet.wallet.service.WalletLedger;
import com.almotawaj.wallet.wallet.service.WalletLocker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutRefundService {
    private final CheckoutSessionRepository sessionRepository;
    private final WalletRepository walletRepository;
    private final VoucherPurchaseRepository purchaseRepository;
    private final WalletLocker walletLocker;
    private final WalletLedger ledger;
    private final Counterparties counterparties;
    private final CheckoutSessionMapper mapper;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public CheckoutSessionResponse refund(UUID merchantId, UUID sessionId) {
        CheckoutSession session = sessionRepository.findByIdForUpdate(sessionId)
                .filter(found -> found.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new InformationNotFoundException(GatewayMessages.CHECKOUT_NOT_FOUND, ErrorCodes.CHECKOUT_NOT_FOUND));
        if (session.getStatus() != CheckoutStatus.PAID) {
            throw new BusinessRuleException(GatewayMessages.CHECKOUT_NOT_PAID, ErrorCodes.CHECKOUT_NOT_PAID);
        }
        WalletLocker.LockedPair locked = walletLocker.lockPair(walletId(merchantId), walletId(session.getPayer().getId()));
        Wallet merchantWallet = locked.source();
        Wallet payerWallet = locked.target();
        BigDecimal discount = session.getVoucherDiscount() == null ? BigDecimal.ZERO : session.getVoucherDiscount();
        BigDecimal amount = session.getAmount().subtract(discount);
        if (merchantWallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }

        Counterparty merchantDetails = counterparties.of(merchantWallet);
        if (amount.signum() > 0) {
            WalletTransaction refunded = ledger.credit(payerWallet, TransactionType.REFUND, amount, merchantDetails,
                    session.getOrderReference());
            ledger.debit(merchantWallet, TransactionType.REFUND_ISSUED, amount, counterparties.of(payerWallet), session.getOrderReference());
            eventPublisher.publishEvent(new MoneyReceivedEvent(session.getPayer().getId(), amount, merchantDetails.name(),
                    refunded.getReference()));
        }
        purchaseRepository.release(sessionId, VoucherPurchaseStatus.UNUSED);
        session.setStatus(CheckoutStatus.REFUNDED);
        session.setRefundedAt(clock.instant());

        auditService.record(merchantId, AuditAction.CHECKOUT_REFUNDED, AuditTargetType.CHECKOUT_SESSION, sessionId,
                session.getOrderReference());
        eventPublisher.publishEvent(new CheckoutStatusChangedEvent(sessionId));
        return mapper.toResponse(session);
    }

    private UUID walletId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.NOT_FOUND))
                .getId();
    }
}
