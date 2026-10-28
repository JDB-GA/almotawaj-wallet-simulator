package com.almotawaj.wallet.voucher.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.ConflictException;
import com.almotawaj.wallet.simulation.model.SimulationStepType;
import com.almotawaj.wallet.simulation.service.SimulationTrace;
import com.almotawaj.wallet.voucher.event.VoucherSoldEvent;
import com.almotawaj.wallet.voucher.model.IdempotencyRecord;
import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.model.VoucherPurchase;
import com.almotawaj.wallet.voucher.model.VoucherStatus;
import com.almotawaj.wallet.voucher.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.voucher.repository.IdempotencyRecordRepository;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.Counterparty;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.almotawaj.wallet.wallet.service.Counterparties;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import com.almotawaj.wallet.wallet.service.WalletLedger;
import com.almotawaj.wallet.wallet.service.WalletLocker;
import com.almotawaj.wallet.wallet.service.WalletProvisioner;
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
        // Logging the Buyer {X} is buying voucher {Y}
        log.info(LogMessages.VOUCHER_PURCHASE_STARTED, buyerId, voucherId);

        // Current time in UTC (from ClockConfig)
        Instant now = clock.instant();

        // Find the voucher row
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(VoucherService::notFound);

        // Records a READ step with the stock it saw, during simulations only
        SimulationTrace.mark(SimulationStepType.READ, BigDecimal.valueOf(voucher.getRemaining()));

        // Throws if the voucher is sold out, inactive, or its sale has ended.
        ensureOnSale(voucher, now);

        UUID merchantId = voucher.getMerchant().getId();
        String title = voucher.getTitle();
        BigDecimal price = voucher.getPrice();
        int perClientLimit = voucher.getPerClientLimit();


        /*
         * It reserves one voucher safely. Even if 20 buyers hit the last voucher at once, only one gets 1 back;
         * the rest get 0 and a 409
         */
        int taken = voucherRepository.takeOne(voucherId, VoucherStatus.ACTIVE, now);
        SimulationTrace.mark(SimulationStepType.TAKE_ONE, BigDecimal.valueOf(taken));

        if (taken == 0) {
            SimulationTrace.mark(SimulationStepType.SOLD_OUT);
            log.info(LogMessages.VOUCHER_SOLD_OUT, buyerId, voucherId);
            // 409 Conflict
            throw new ConflictException(ErrorMessages.VOUCHER_SOLD_OUT, ErrorCodes.VOUCHER_SOLD_OUT);
        }

        // Get or create a wallet for the buyer and merchant
        Wallet buyerWallet = provisioner.getOrCreate(buyerId);
        Wallet merchantWallet = provisioner.getOrCreate(merchantId);

        // Check the merchant if he can receive or not
        if (!accessPolicy.canReceive(merchantWallet.getUser())) {
            throw new BusinessRuleException(GatewayMessages.MERCHANT_UNAVAILABLE, ErrorCodes.MERCHANT_UNAVAILABLE);
        }

        // Lock the two wallets the sender and buyer
        WalletLocker.LockedPair locked = walletLocker.lockPair(buyerWallet.getId(), merchantWallet.getId());
        Wallet buyer = locked.source();
        Wallet merchant = locked.target();

        SimulationTrace.mark(SimulationStepType.LOCKED);

        // Count By Voucher
        if (purchaseRepository.countByVoucherIdAndBuyerId(voucherId, buyerId) >= perClientLimit) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_LIMIT_REACHED, ErrorCodes.VOUCHER_LIMIT_REACHED);
        }

        // Check the balance
        if (buyer.getBalance().compareTo(price) < 0) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_BALANCE, ErrorCodes.INSUFFICIENT_BALANCE);
        }

        // Getting the buyer details
        Counterparty buyerDetails = counterparties.of(buyer);

        // withdraw from buyer
        WalletTransaction debit = ledger.debit(buyer, TransactionType.VOUCHER_PURCHASE, price, counterparties.of(merchant), title);

        // deposit to the merchant
        WalletTransaction credit = ledger.credit(merchant, TransactionType.VOUCHER_SALE, price, buyerDetails, title);

        // Prepare the voucher purchase record and save it
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


        // Get the remaining and mark for simulation
        int remaining = sold.getRemaining();
        SimulationTrace.mark(SimulationStepType.COMMITTED, BigDecimal.valueOf(remaining));

        // Auditing the transaction of voucher purchase
        auditService.record(buyerId, AuditAction.VOUCHER_PURCHASED, AuditTargetType.VOUCHER, voucherId, purchase.getCode());

        // Notify the merchant after commit (money received, voucher sold) -> NotificationListener File
        eventPublisher.publishEvent(new MoneyReceivedEvent(merchantId, price, buyerDetails.name(), credit.getReference()));
        eventPublisher.publishEvent(new VoucherSoldEvent(merchantId, voucherId, title, price, remaining));

        log.info(LogMessages.VOUCHER_PURCHASED, buyerId, voucherId, purchase.getId(), remaining);
        return VoucherPurchaseResponse.from(purchase, names.fullName(merchant.getUser()));
    }

    /*
     * It loads the original purchase saved under this key. If the purchase was for a different voucher,
     * it rejects the reused key. Otherwise, it returns the same response again, with no new charge
     */
    @Transactional(readOnly = true)
    public VoucherPurchaseResponse replay(UUID buyerId, UUID voucherId, IdempotencyRecord record) {
        VoucherPurchase purchase = purchaseRepository.findById(UUID.fromString(record.getResponseBody()))
                .filter(found -> found.getVoucher().getId().equals(voucherId))
                .orElseThrow(() -> new BusinessRuleException(ErrorMessages.IDEMPOTENCY_KEY_REUSED, ErrorCodes.IDEMPOTENCY_KEY_REUSED));
        log.info(LogMessages.VOUCHER_PURCHASE_REPLAYED, buyerId, purchase.getId());
        return VoucherPurchaseResponse.from(purchase, names.fullName(purchase.getVoucher().getMerchant()));
    }

    /*
     * Saves the idempotency key with its user, endpoint and purchase ID.
     * Replay: if the same key comes again (e.g. a retry after a network error),
     * we return this saved purchase instead of buying again, so the buyer is charged once.
     */
    private void remember(Wallet buyer, String key, VoucherPurchase purchase) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setUser(buyer.getUser());
        record.setKey(key);
        record.setEndpoint(WalletConstants.IDEMPOTENCY_VOUCHER_PURCHASE);
        record.setResponseStatus(WalletConstants.IDEMPOTENCY_CREATED_STATUS);
        record.setResponseBody(purchase.getId().toString());
        idempotencyRepository.saveAndFlush(record);
    }

    // Checks the remaining and availability of the voucher
    private static void ensureOnSale(Voucher voucher, Instant now) {
        if (voucher.getRemaining() == 0) {
            SimulationTrace.mark(SimulationStepType.SOLD_OUT);
            throw new ConflictException(ErrorMessages.VOUCHER_SOLD_OUT, ErrorCodes.VOUCHER_SOLD_OUT);
        }
        if (voucher.getStatus() != VoucherStatus.ACTIVE || !voucher.getSaleEndsAt().isAfter(now)) {
            throw new BusinessRuleException(ErrorMessages.VOUCHER_UNAVAILABLE, ErrorCodes.VOUCHER_UNAVAILABLE);
        }
    }
}
