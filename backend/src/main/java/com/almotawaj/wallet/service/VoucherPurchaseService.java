package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.event.VoucherSoldEvent;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.ConflictException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.Counterparty;
import com.almotawaj.wallet.model.IdempotencyRecord;
import com.almotawaj.wallet.model.SimulationStepType;
import com.almotawaj.wallet.model.TransactionType;
import com.almotawaj.wallet.model.Voucher;
import com.almotawaj.wallet.model.VoucherPurchase;
import com.almotawaj.wallet.model.VoucherStatus;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.model.WalletTransaction;
import com.almotawaj.wallet.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherPurchaseService {
    private final VoucherRepository voucherRepository;
    private final VoucherPurchaseRepository purchaseRepository;
    private final IdempotencyRecordRepository idempotencyRepository;
    private final WalletProvisioner provisioner;
    private final WalletLocker walletLocker;
    private final WalletAccessPolicy accessPolicy;
    private final WalletLedger ledger;
    private final Counterparties counterparties;
    private final VoucherCodeGenerator codeGenerator;
    private final WalletHolderNames names;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public VoucherPurchaseResponse purchase(UUID buyerId, UUID voucherId, String idempotencyKey) {
        log.info(LogMessages.VOUCHER_PURCHASE_STARTED, buyerId, voucherId);
        Instant now = clock.instant();
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(VoucherService::notFound);
        SimulationTrace.mark(SimulationStepType.READ, BigDecimal.valueOf(voucher.getRemaining()));
        ensureOnSale(voucher, now);
        UUID merchantId = voucher.getMerchant().getId();
        String title = voucher.getTitle();
        BigDecimal price = voucher.getPrice();
        int perClientLimit = voucher.getPerClientLimit();

        int taken = voucherRepository.takeOne(voucherId, VoucherStatus.ACTIVE, now);
        SimulationTrace.mark(SimulationStepType.TAKE_ONE, BigDecimal.valueOf(taken));
        if (taken == 0) {
            SimulationTrace.mark(SimulationStepType.SOLD_OUT);
            log.info(LogMessages.VOUCHER_SOLD_OUT, buyerId, voucherId);
            throw new ConflictException(ErrorMessages.VOUCHER_SOLD_OUT, ErrorCodes.VOUCHER_SOLD_OUT);
        }

        Wallet buyerWallet = provisioner.getOrCreate(buyerId);
        Wallet merchantWallet = provisioner.getOrCreate(merchantId);
        if (!accessPolicy.canReceive(merchantWallet.getUser())) {
            throw new BusinessRuleException(GatewayMessages.MERCHANT_UNAVAILABLE, ErrorCodes.MERCHANT_UNAVAILABLE);
        }
        WalletLocker.LockedPair locked = walletLocker.lockPair(buyerWallet.getId(), merchantWallet.getId());
        Wallet buyer = locked.source();
        Wallet merchant = locked.target();
        SimulationTrace.mark(SimulationStepType.LOCKED);
        if (purchaseRepository.countByVoucherIdAndBuyerId(voucherId, buyerId) >= perClientLimit) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_LIMIT_REACHED, ErrorCodes.VOUCHER_LIMIT_REACHED);
        }
        if (buyer.getBalance().compareTo(price) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }

        Counterparty buyerDetails = counterparties.of(buyer);
        WalletTransaction debit = ledger.debit(buyer, TransactionType.VOUCHER_PURCHASE, price, counterparties.of(merchant), title);
        WalletTransaction credit = ledger.credit(merchant, TransactionType.VOUCHER_SALE, price, buyerDetails, title);

        Voucher sold = voucherRepository.getReferenceById(voucherId);
        VoucherPurchase purchase = new VoucherPurchase();
        purchase.setVoucher(sold);
        purchase.setBuyer(buyer.getUser());
        purchase.setTransaction(debit);
        purchase.setCode(codeGenerator.next());
        purchase.setPrice(price);
        purchaseRepository.saveAndFlush(purchase);
        if (idempotencyKey != null) {
            remember(buyer, idempotencyKey, purchase);
        }

        int remaining = sold.getRemaining();
        SimulationTrace.mark(SimulationStepType.COMMITTED, BigDecimal.valueOf(remaining));
        auditService.record(buyerId, AuditAction.VOUCHER_PURCHASED, AuditTargetType.VOUCHER, voucherId, purchase.getCode());
        eventPublisher.publishEvent(new MoneyReceivedEvent(merchantId, price, buyerDetails.name(), credit.getReference()));
        eventPublisher.publishEvent(new VoucherSoldEvent(merchantId, voucherId, title, price, remaining));
        log.info(LogMessages.VOUCHER_PURCHASED, buyerId, voucherId, purchase.getId(), remaining);
        return VoucherPurchaseResponse.from(purchase, names.fullName(merchant.getUser()));
    }

    @Transactional(readOnly = true)
    public VoucherPurchaseResponse replay(UUID buyerId, UUID voucherId, IdempotencyRecord record) {
        VoucherPurchase purchase = purchaseRepository.findById(UUID.fromString(record.getResponseBody()))
                .filter(found -> found.getVoucher().getId().equals(voucherId))
                .orElseThrow(() -> new BusinessRuleException(ErrorMessages.IDEMPOTENCY_KEY_REUSED, ErrorCodes.IDEMPOTENCY_KEY_REUSED));
        log.info(LogMessages.VOUCHER_PURCHASE_REPLAYED, buyerId, purchase.getId());
        return VoucherPurchaseResponse.from(purchase, names.fullName(purchase.getVoucher().getMerchant()));
    }

    private void remember(Wallet buyer, String key, VoucherPurchase purchase) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setUser(buyer.getUser());
        record.setKey(key);
        record.setEndpoint(WalletConstants.IDEMPOTENCY_VOUCHER_PURCHASE);
        record.setResponseStatus(WalletConstants.IDEMPOTENCY_CREATED_STATUS);
        record.setResponseBody(purchase.getId().toString());
        idempotencyRepository.saveAndFlush(record);
    }

    private static void ensureOnSale(Voucher voucher, Instant now) {
        if (voucher.getRemaining() == 0) {
            SimulationTrace.mark(SimulationStepType.SOLD_OUT);
            throw new ConflictException(ErrorMessages.VOUCHER_SOLD_OUT, ErrorCodes.VOUCHER_SOLD_OUT);
        }
        if (voucher.getStatus() != VoucherStatus.ACTIVE || !voucher.getExpiresAt().isAfter(now)) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_UNAVAILABLE, ErrorCodes.VOUCHER_UNAVAILABLE);
        }
    }
}
