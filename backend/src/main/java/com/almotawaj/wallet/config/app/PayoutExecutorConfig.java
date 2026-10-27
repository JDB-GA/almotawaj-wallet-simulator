package com.almotawaj.wallet.config.app;

import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.WalletLimits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class PayoutExecutorConfig {
    // ExecutorService
    // A thread pool. You hand it tasks, and its threads run them
    // It reuses threads instead of creating a new one per task
    @Bean(name = WalletConstants.PAYOUT_EXECUTOR, destroyMethod = WalletConstants.EXECUTOR_SHUTDOWN_METHOD)
    public ExecutorService payoutExecutor() {
        AtomicInteger counter = new AtomicInteger();
        // task is runnable (The code the new thread will run)
        ThreadFactory threads = task -> {
            Thread thread = new Thread(task, WalletConstants.PAYOUT_THREAD_NAME.formatted(counter.incrementAndGet()));
            thread.setDaemon(true);
            return thread;
        };
        return Executors.newFixedThreadPool(WalletLimits.PAYOUT_THREADS, threads);
    }
}
