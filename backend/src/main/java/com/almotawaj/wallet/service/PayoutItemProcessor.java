package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.Counterparty;
import com.almotawaj.wallet.model.PayoutBatch;
import com.almotawaj.wallet.model.PayoutBatchStatus;
import com.almotawaj.wallet.model.PayoutItem;
import com.almotawaj.wallet.model.PayoutItemStatus;
import com.almotawaj.wallet.model.TransactionType;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.Wallet;
import com.almotawaj.wallet.model.WalletTransaction;
import com.almotawaj.wallet.repository.PayoutBatchRepository;
import com.almotawaj.wallet.repository.PayoutItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutItemProcessor {
    private final PayoutItemRepository itemRepository;
    private final PayoutBatchRepository batchRepository;
    private final WalletProvisioner provisioner;
    private final WalletLocker walletLocker;
    private final WalletAccessPolicy accessPolicy;
    private final WalletLedger ledger;
    private final Counterparties counterparties;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public PayoutItemStatus process(UUID itemId) {
        PayoutItem item = itemRepository.findById(itemId).orElseThrow();
        UUID batchId = item.getBatch().getId();
        User recipient = item.getRecipient();
        if (!accessPolicy.canReceive(recipient)) {
            return fail(item, batchId, WalletConstants.PAYOUT_RECIPIENT_UNAVAILABLE);
        }
        Wallet merchantWallet = provisioner.getOrCreate(item.getBatch().getMerchant().getId());
        Wallet recipientWallet = provisioner.getOrCreate(recipient.getId());
        WalletLocker.LockedPair locked = walletLocker.lockPair(merchantWallet.getId(), recipientWallet.getId());
        Wallet merchant = locked.source();
        Wallet receiver = locked.target();
        if (merchant.getBalance().compareTo(item.getAmount()) < 0) {
            return fail(item, batchId, WalletConstants.PAYOUT_INSUFFICIENT_BALANCE);
        }

        Counterparty merchantDetails = counterparties.of(merchant);
        WalletTransaction debit = ledger.debit(merchant, TransactionType.PAYOUT_SENT, item.getAmount(), counterparties.of(receiver), null);
        WalletTransaction credit = ledger.credit(receiver, TransactionType.PAYOUT_RECEIVED, item.getAmount(), merchantDetails, null);
        item.setTransaction(debit);
        item.setStatus(PayoutItemStatus.PAID);
        item.setProcessedAt(clock.instant());
        itemRepository.saveAndFlush(item);
        batchRepository.incrementPaid(batchId);
        eventPublisher.publishEvent(new MoneyReceivedEvent(recipient.getId(), item.getAmount(), merchantDetails.name(),
                credit.getReference()));
        log.info(LogMessages.PAYOUT_ITEM_PAID, itemId, batchId, Thread.currentThread().getName());
        return PayoutItemStatus.PAID;
    }

    @Transactional
    public PayoutItemStatus markCrashed(UUID itemId) {
        PayoutItem item = itemRepository.findById(itemId).orElseThrow();
        return fail(item, item.getBatch().getId(), WalletConstants.PAYOUT_PROCESSING_ERROR);
    }

    @Transactional
    public PayoutBatch complete(UUID batchId) {
        PayoutBatch batch = batchRepository.findById(batchId).orElseThrow();
        batch.setStatus(batch.getFailedCount() == 0 ? PayoutBatchStatus.COMPLETED : PayoutBatchStatus.PARTIALLY_FAILED);
        batch.setCompletedAt(clock.instant());
        UUID merchantId = batch.getMerchant().getId();
        auditService.record(merchantId, AuditAction.PAYOUT_BATCH_COMPLETED, AuditTargetType.PAYOUT_BATCH, batchId,
                batch.getStatus().name());
        return batch;
    }

    private PayoutItemStatus fail(PayoutItem item, UUID batchId, String reason) {
        item.setStatus(PayoutItemStatus.FAILED);
        item.setFailureReason(reason);
        item.setProcessedAt(clock.instant());
        itemRepository.saveAndFlush(item);
        batchRepository.incrementFailed(batchId);
        log.info(LogMessages.PAYOUT_ITEM_FAILED, item.getId(), batchId, Thread.currentThread().getName(), reason);
        return PayoutItemStatus.FAILED;
    }
}
