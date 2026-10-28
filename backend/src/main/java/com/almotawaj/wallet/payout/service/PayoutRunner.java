package com.almotawaj.wallet.payout.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.NotificationConstants;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.notification.service.NotificationHub;
import com.almotawaj.wallet.payout.event.PayoutBatchCreatedEvent;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutBatchStatus;
import com.almotawaj.wallet.payout.model.PayoutItem;
import com.almotawaj.wallet.payout.model.PayoutItemStatus;
import com.almotawaj.wallet.payout.model.response.PayoutProgressNotification;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.simulation.service.SimulationTrace;
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
                        NotificationHub hub,
                        // To use the configuration of payoutExecuterConfig
                        @Qualifier(WalletConstants.PAYOUT_EXECUTOR) ExecutorService executor) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
        this.processor = processor;
        this.hub = hub;
        this.executor = executor;
    }

    @Async
    @TransactionalEventListener // Detects Payout Batch Operation
    public void onBatchCreated(PayoutBatchCreatedEvent event) {
        run(event.batchId());
    }

    // For REAL Payouts (we use this to pass NONE which a function that will do nothing since I do not record the REAL payment thread timing)
    public void run(UUID batchId) {
        run(batchId, PayoutItemObserver.NONE);
    }

    // This will be triggered (Simulation or the other run "run(UUID batchId"))
    public PayoutBatch run(UUID batchId, PayoutItemObserver observer) {

        // Starting time
        long started = System.currentTimeMillis();

        // Merchant ID for this batch
        UUID merchantId = batchRepository.findById(batchId).orElseThrow().getMerchant().getId();

        // PayoutItems list of IDs
        List<UUID> itemIds = itemRepository.findByBatchId(batchId).stream().map(PayoutItem::getId).toList();

        // How many items?
        int total = itemIds.size();

        // Counters for failed and paid operations
        AtomicInteger paid = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        // Logging batchId & how many threads
        log.info(LogMessages.PAYOUT_BATCH_STARTED, batchId, WalletLimits.PAYOUT_THREADS);

        // Loop over every payout item ->
        List<CompletableFuture<PayoutItemStatus>> futures = itemIds
                .stream()
                .map(itemId -> CompletableFuture
                        // Provide the function that I want it to run  in the background
                        .supplyAsync(() -> payOne(batchId, itemId, merchantId, total, paid, failed, observer),
                                // Executer is the one that will have the thread factory and will generate the threads
                                executor)
                        // If payOne throws, mark the item as failed
                        .exceptionally(error -> crashed(batchId, itemId, merchantId, total, paid, failed, error)))
                .toList();


        /*
         * Waits for the threads to finish their work (the background work)
         * Note: CompletableFuture[]::new is same as size -> new CompletableFuture[size]
         */
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        // Sets the final status: COMPLETED or PARTIALLY_FAILED
        PayoutBatch batch = processor.complete(batchId);

        // Sends the notification (sync)
        hub.send(merchantId, NotificationConstants.PAYOUT_COMPLETED,
                new PayoutProgressNotification(batchId, batch.getStatus(), paid.get(), failed.get(), total));

        // Logs that the process is done
        log.info(LogMessages.PAYOUT_BATCH_FINISHED, batchId, batch.getStatus(), paid.get(), failed.get(),
                System.currentTimeMillis() - started);

        // Returns the batch object
        return batch;
    }


    private PayoutItemStatus payOne(UUID batchId, UUID itemId, UUID merchantId, int total,
                                    AtomicInteger paid, AtomicInteger failed, PayoutItemObserver observer) {
        // Start time
        long startNanos = System.nanoTime();

        // Check if the passed observer is NONE, if NONE then we do not start the simulation trace
        if (observer != PayoutItemObserver.NONE) {
            SimulationTrace.start(startNanos);
        }

        // Pay this one item in its own transaction and get its status (PAID or FAILED)
        PayoutItemStatus status = processor.process(itemId);

        // Tell the observer this item finished (records thread and timing in simulations, does nothing otherwise)
        observer.onItemProcessed(itemId, Thread.currentThread().getName(), startNanos, System.nanoTime(), status,
                SimulationTrace.stop());

        // Increase the paid or failed counter, then send live progress to the merchant
        report(batchId, merchantId, total, status == PayoutItemStatus.PAID ? paid.incrementAndGet() : paid.get(),
                status == PayoutItemStatus.FAILED ? failed.incrementAndGet() : failed.get());

        //  PENDING, PAID or FAILED
        return status;
    }

    /*
     * Runs when payOne throws an unexpected error
     * Logs it, marks the item as failed, increases the failed count and reports progress
     */
    private PayoutItemStatus crashed(UUID batchId, UUID itemId, UUID merchantId, int total, AtomicInteger paid,
                                     AtomicInteger failed, Throwable cause) {
        log.error(LogMessages.PAYOUT_ITEM_CRASHED, itemId, batchId, cause);
        processor.markCrashed(itemId);
        report(batchId, merchantId, total, paid.get(), failed.incrementAndGet());
        return PayoutItemStatus.FAILED;
    }

    /*
     * Sends live payout progress to the merchant over SSE
     * The batch is still PROCESSING, with the current paid and failed counts
     */
    private void report(UUID batchId, UUID merchantId, int total, int paidCount, int failedCount) {
        hub.send(merchantId, NotificationConstants.PAYOUT_PROGRESS,
                new PayoutProgressNotification(batchId, PayoutBatchStatus.PROCESSING, paidCount, failedCount, total));
    }
}
