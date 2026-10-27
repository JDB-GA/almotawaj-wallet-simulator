package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.ConflictException;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutItemStatus;
import com.almotawaj.wallet.payout.service.PayoutRunner;
import com.almotawaj.wallet.simulation.model.SimulationMode;
import com.almotawaj.wallet.simulation.model.SimulationOutcome;
import com.almotawaj.wallet.simulation.model.request.PayoutRaceRequest;
import com.almotawaj.wallet.simulation.model.request.VoucherRaceRequest;
import com.almotawaj.wallet.simulation.model.response.PayoutRaceResult;
import com.almotawaj.wallet.simulation.model.response.SimulationAttempt;
import com.almotawaj.wallet.simulation.model.response.SimulationStep;
import com.almotawaj.wallet.simulation.model.response.VoucherRaceResult;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.voucher.service.VoucherPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {
    private final SimulationSandbox sandbox;
    private final VoucherPurchaseService purchaseService;
    private final UnsafeVoucherBuyer unsafeBuyer;
    private final PayoutRunner payoutRunner;

    public VoucherRaceResult voucherRace(VoucherRaceRequest request) throws InterruptedException {
        User merchant = sandbox.createMerchant(BigDecimal.ZERO);
        List<User> buyers = sandbox.createClients(request.buyers(), new BigDecimal(SimulationConstants.BUYER_BALANCE));
        List<UUID> userIds = Stream.concat(Stream.of(merchant), buyers.stream()).map(User::getId).toList();
        try {
            UUID voucherId = sandbox.createVoucher(merchant, request.stock());
            log.info(LogMessages.SIMULATION_STARTED, request.mode(), request.buyers());
            long origin = System.nanoTime();
            List<SimulationAttempt> attempts = race(buyers.size(), index -> attempt(request.mode(), buyers.get(index).getId(), voucherId), origin);
            int winners = (int) attempts.stream().filter(attempt -> attempt.outcome() == SimulationOutcome.WON).count();
            VoucherRaceResult result = new VoucherRaceResult(request.mode(), request.buyers(), request.stock(), winners,
                    sandbox.remaining(voucherId), Math.max(0, winners - request.stock()), millis(origin, System.nanoTime()), attempts);
            log.info(LogMessages.SIMULATION_FINISHED, request.mode(), result.durationMs(), result.winners());
            return result;
        } finally {
            sandbox.cleanUp(userIds);
        }
    }

    public PayoutRaceResult payoutRace(PayoutRaceRequest request) {
        User merchant = sandbox.createMerchant(request.merchantBalance());
        List<User> recipients = sandbox.createClients(request.recipients(), BigDecimal.ZERO);
        List<UUID> userIds = Stream.concat(Stream.of(merchant), recipients.stream()).map(User::getId).toList();
        try {
            List<UUID> ids = sandbox.createPayoutBatch(merchant, recipients, request.amount());
            UUID batchId = ids.get(0);
            Map<UUID, Integer> indexes = new ConcurrentHashMap<>();
            for (int index = 1; index < ids.size(); index++) {
                indexes.put(ids.get(index), index - 1);
            }
            List<SimulationAttempt> attempts = new ArrayList<>();
            long origin = System.nanoTime();
            log.info(LogMessages.SIMULATION_STARTED, batchId, request.recipients());
            PayoutBatch batch = payoutRunner.run(batchId, (itemId, thread, start, end, status, steps) -> {
                synchronized (attempts) {
                    attempts.add(new SimulationAttempt(indexes.get(itemId), thread, millis(origin, start), millis(origin, end),
                            status == PayoutItemStatus.PAID ? SimulationOutcome.PAID : SimulationOutcome.FAILED, null,
                            shift(steps, millis(origin, start))));
                }
            });
            attempts.sort(Comparator.comparingInt(SimulationAttempt::index));
            int threads = (int) attempts.stream().map(SimulationAttempt::thread).distinct().count();
            PayoutRaceResult result = new PayoutRaceResult(request.recipients(), request.amount(), request.merchantBalance(),
                    sandbox.balance(merchant.getId()), batch.getPaidCount(), batch.getFailedCount(), threads,
                    millis(origin, System.nanoTime()), attempts);
            log.info(LogMessages.SIMULATION_FINISHED, batchId, result.durationMs(), result.paid());
            return result;
        } finally {
            sandbox.cleanUp(userIds);
        }
    }

    private Callable<SimulationOutcome> attempt(SimulationMode mode, UUID buyerId, UUID voucherId) {
        return () -> {
            if (mode == SimulationMode.UNSAFE) {
                return unsafeBuyer.buy(voucherId);
            }
            try {
                purchaseService.purchase(buyerId, voucherId, null);
                return SimulationOutcome.WON;
            } catch (ConflictException e) {
                return SimulationOutcome.SOLD_OUT;
            } catch (BusinessRuleException e) {
                return SimulationOutcome.FAILED;
            }
        };
    }

    private List<SimulationAttempt> race(int count, AttemptFactory factory, long origin) throws InterruptedException {
        AtomicInteger threadNumber = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(count,
                task -> new Thread(task, SimulationConstants.BUYER_THREAD_NAME.formatted(threadNumber.incrementAndGet())));
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<SimulationAttempt>> futures = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                int position = index;
                Callable<SimulationOutcome> task = factory.create(position);
                futures.add(pool.submit(() -> {
                    start.await();
                    long begin = System.nanoTime();
                    SimulationTrace.start(origin);
                    SimulationOutcome outcome;
                    try {
                        outcome = task.call();
                    } catch (Exception e) {
                        SimulationTrace.stop();
                        throw e;
                    }
                    return new SimulationAttempt(position, Thread.currentThread().getName(), millis(origin, begin),
                            millis(origin, System.nanoTime()), outcome, null, SimulationTrace.stop());
                }));
            }
            start.countDown();
            List<SimulationAttempt> attempts = new ArrayList<>();
            for (int index = 0; index < futures.size(); index++) {
                attempts.add(collect(futures.get(index), index));
            }
            return attempts;
        } finally {
            pool.shutdownNow();
        }
    }

    private static SimulationAttempt collect(Future<SimulationAttempt> future, int index) throws InterruptedException {
        try {
            return future.get(SimulationConstants.RACE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            return new SimulationAttempt(index, null, 0, 0, SimulationOutcome.ERROR, e.getClass().getSimpleName(), List.of());
        }
    }

    private static List<SimulationStep> shift(List<SimulationStep> steps, double offsetMs) {
        return steps.stream().map(step -> new SimulationStep(step.atMs() + offsetMs, step.type(), step.value())).toList();
    }

    private static double millis(long origin, long nanos) {
        return (nanos - origin) / SimulationConstants.NANOS_PER_MILLI;
    }

    @FunctionalInterface
    private interface AttemptFactory {
        Callable<SimulationOutcome> create(int index);
    }
}
