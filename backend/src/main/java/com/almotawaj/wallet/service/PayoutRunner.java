package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.NotificationConstants;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.event.PayoutBatchCreatedEvent;
import com.almotawaj.wallet.model.PayoutBatch;
import com.almotawaj.wallet.model.PayoutBatchStatus;
import com.almotawaj.wallet.model.PayoutItem;
import com.almotawaj.wallet.model.PayoutItemStatus;
import com.almotawaj.wallet.model.response.PayoutProgressNotification;
import com.almotawaj.wallet.repository.PayoutBatchRepository;
import com.almotawaj.wallet.repository.PayoutItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class PayoutRunner {
    private final PayoutItemRepository itemRepository;
    private final PayoutBatchRepository batchRepository;
    private final PayoutItemProcessor processor;
    private final NotificationHub hub;
    private final ExecutorService executor;

    public PayoutRunner(PayoutItemRepository itemRepository, PayoutBatchRepository batchRepository, PayoutItemProcessor processor,
                        NotificationHub hub, @Qualifier(WalletConstants.PAYOUT_EXECUTOR) ExecutorService executor) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
        this.processor = processor;
        this.hub = hub;
        this.executor = executor;
    }

    @Async
    @TransactionalEventListener
    public void onBatchCreated(PayoutBatchCreatedEvent event) {
        run(event.batchId());
    }

    public PayoutBatch run(UUID batchId) {
        return run(batchId, PayoutItemObserver.NONE);
    }

    public PayoutBatch run(UUID batchId, PayoutItemObserver observer) {
        long started = System.currentTimeMillis();
        UUID merchantId = batchRepository.findById(batchId).orElseThrow().getMerchant().getId();
        List<UUID> itemIds = itemRepository.findByBatchId(batchId).stream().map(PayoutItem::getId).toList();
        int total = itemIds.size();
        AtomicInteger paid = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        log.info(LogMessages.PAYOUT_BATCH_STARTED, batchId, WalletLimits.PAYOUT_THREADS);

        List<CompletableFuture<PayoutItemStatus>> futures = itemIds.stream()
                .map(itemId -> CompletableFuture
                        .supplyAsync(() -> payOne(batchId, itemId, merchantId, total, paid, failed, observer), executor)
                        .exceptionally(error -> crashed(batchId, itemId, merchantId, total, paid, failed, error)))
                .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        PayoutBatch batch = processor.complete(batchId);
        hub.send(merchantId, NotificationConstants.PAYOUT_COMPLETED,
                new PayoutProgressNotification(batchId, batch.getStatus(), paid.get(), failed.get(), total));
        log.info(LogMessages.PAYOUT_BATCH_FINISHED, batchId, batch.getStatus(), paid.get(), failed.get(),
                System.currentTimeMillis() - started);
        return batch;
    }

    private PayoutItemStatus payOne(UUID batchId, UUID itemId, UUID merchantId, int total,
                                    AtomicInteger paid, AtomicInteger failed, PayoutItemObserver observer) {
        long startNanos = System.nanoTime();
        if (observer != PayoutItemObserver.NONE) {
            SimulationTrace.start(startNanos);
        }
        PayoutItemStatus status = processor.process(itemId);
        observer.onItemProcessed(itemId, Thread.currentThread().getName(), startNanos, System.nanoTime(), status,
                SimulationTrace.stop());
        report(batchId, merchantId, total, status == PayoutItemStatus.PAID ? paid.incrementAndGet() : paid.get(),
                status == PayoutItemStatus.FAILED ? failed.incrementAndGet() : failed.get());
        return status;
    }

    private PayoutItemStatus crashed(UUID batchId, UUID itemId, UUID merchantId, int total, AtomicInteger paid,
                                     AtomicInteger failed, Throwable cause) {
        log.error(LogMessages.PAYOUT_ITEM_CRASHED, itemId, batchId, cause);
        processor.markCrashed(itemId);
        report(batchId, merchantId, total, paid.get(), failed.incrementAndGet());
        return PayoutItemStatus.FAILED;
    }

    private void report(UUID batchId, UUID merchantId, int total, int paidCount, int failedCount) {
        hub.send(merchantId, NotificationConstants.PAYOUT_PROGRESS,
                new PayoutProgressNotification(batchId, PayoutBatchStatus.PROCESSING, paidCount, failedCount, total));
    }
}
