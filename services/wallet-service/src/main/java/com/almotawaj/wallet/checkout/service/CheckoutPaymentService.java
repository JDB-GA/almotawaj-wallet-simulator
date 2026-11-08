package com.almotawaj.wallet.checkout.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.checkout.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.checkout.model.CheckoutSession;
import com.almotawaj.wallet.checkout.model.CheckoutStatus;
import com.almotawaj.wallet.checkout.model.response.CheckoutViewResponse;
import com.almotawaj.wallet.checkout.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.voucher.model.VoucherPurchaseStatus;
import com.almotawaj.wallet.voucher.model.response.UsableVoucherResponse;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.Counterparty;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.service.Counterparties;
import com.almotawaj.wallet.wallet.service.DailyLimitPolicy;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import com.almotawaj.wallet.wallet.service.WalletLedger;
import com.almotawaj.wallet.wallet.service.WalletLocker;
import com.almotawaj.wallet.wallet.service.WalletProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutPaymentService {
    private final CheckoutSessionRepository sessionRepository;
    private final VoucherPurchaseRepository purchaseRepository;
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

    @Transactional(readOnly = true)
    public List<UsableVoucherResponse> usableVouchers(UUID payerId, UUID sessionId) {
        CheckoutSession session = sessionRepository.findById(sessionId).orElseThrow(() ->
                new InformationNotFoundException(GatewayMessages.CHECKOUT_NOT_FOUND, ErrorCodes.CHECKOUT_NOT_FOUND));
        return purchaseRepository.findUsable(payerId, session.getMerchant().getId(), VoucherPurchaseStatus.UNUSED, clock.instant()).stream()
                .map(UsableVoucherResponse::from)
                .toList();
    }

    @Transactional
    public CheckoutViewResponse pay(UUID payerId, UUID sessionId, UUID voucherId) {
        // Pessimistic lock on the session (SELECT ... FOR UPDATE), so two tabs can't both pay it
        CheckoutSession session = sessionRepository.findByIdForUpdate(sessionId).orElseThrow(() ->
                new InformationNotFoundException(GatewayMessages.CHECKOUT_NOT_FOUND, ErrorCodes.CHECKOUT_NOT_FOUND));
        Instant now = clock.instant();
        ensurePayable(session, now);
        User merchant = session.getMerchant();
        // Lock the payer's and merchant's wallets (smaller ID first) before checking the balance
        WalletLocker.LockedPair locked = walletLocker.lockPair(provisioner.getOrCreate(payerId).getId(),
                provisioner.getOrCreate(merchant.getId()).getId());
        Wallet payerWallet = locked.source();
        Wallet merchantWallet = locked.target();
        BigDecimal discount = voucherId == null ? BigDecimal.ZERO : redeem(voucherId, payerId, session, now);
        BigDecimal amount = session.getAmount().subtract(discount);
        if (payerWallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }

        Counterparty payerDetails = counterparties.of(payerWallet);
        if (amount.signum() > 0) {
            dailyLimitPolicy.ensureCheckoutAllowed(payerWallet.getId(), amount);
            WalletTransaction received = ledger.credit(merchantWallet, TransactionType.PAYMENT_RECEIVED, amount, payerDetails,
                    session.getOrderReference());
            ledger.debit(payerWallet, TransactionType.PAYMENT, amount, counterparties.of(merchantWallet), session.getOrderReference());
            eventPublisher.publishEvent(new MoneyReceivedEvent(merchant.getId(), amount, payerDetails.name(), received.getReference()));
        }
        session.setVoucherDiscount(discount.signum() > 0 ? discount : null);
        session.setPayer(payerWallet.getUser());
        session.setStatus(CheckoutStatus.PAID);
        session.setPaidAt(now);

        auditService.record(payerId, AuditAction.CHECKOUT_PAID, AuditTargetType.CHECKOUT_SESSION, sessionId, session.getOrderReference());
        eventPublisher.publishEvent(new CheckoutStatusChangedEvent(sessionId));
        return mapper.toView(session);
    }

    private BigDecimal redeem(UUID voucherId, UUID payerId, CheckoutSession session, Instant now) {
        // Atomic update (WHERE status = 'UNUSED'), so a voucher can only be redeemed once; 0 rows means already used
        int redeemed = purchaseRepository.redeem(voucherId, payerId, session.getMerchant().getId(), session, now,
                VoucherPurchaseStatus.UNUSED, VoucherPurchaseStatus.REDEEMED);
        if (redeemed == 0) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_NOT_USABLE, ErrorCodes.VOUCHER_NOT_USABLE);
        }
        BigDecimal faceValue = purchaseRepository.findById(voucherId).orElseThrow().getVoucher().getFaceValue();
        return faceValue.min(session.getAmount());
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
