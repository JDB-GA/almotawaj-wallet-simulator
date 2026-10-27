package com.almotawaj.wallet.config.app;

import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.WalletLimits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

//Only @Async methods: emails, webhooks, KYC notifier and payouts will use the multip threading using the 4-threads
// Its name, taskExecutor, makes Spring use it for every @Async
// HTTP requests use Tomcat’s own threads  (By default, no setup needed.
// Tomcat handles many requests at once out of the box. You can tune it with server.tomcat.threads.max.)
@Configuration
@EnableAsync
public class AsyncConfig {
    // taskExecutor is the default name Spring looks for to run @Async methods
    @Bean(name = WalletConstants.ASYNC_EXECUTOR, destroyMethod = WalletConstants.EXECUTOR_SHUTDOWN_METHOD)
    public ExecutorService asyncExecutor() {
        AtomicInteger counter = new AtomicInteger();
        // An interface with one method, newThread(task).
        // The pool calls it whenever it needs a thread,
        // and you decide the name and daemon setting
        ThreadFactory threads = task -> {
            Thread thread = new Thread(task, WalletConstants.ASYNC_THREAD_NAME.formatted(counter.incrementAndGet()));
            thread.setDaemon(true);
            // A non-daemon thread still running keeps the JVM alive
            // (e.g., CTRL + C will not be shut down if non-daemon thread still running)
            return thread;
        };
        return Executors.newFixedThreadPool(WalletLimits.ASYNC_THREADS, threads);
    }
}
