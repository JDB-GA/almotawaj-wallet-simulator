package com.almotawaj.wallet.config.constants;

import java.time.Duration;
import java.util.List;

public final class GatewayConstants {
    public static final String API_KEY_PREFIX = "almt_";
    public static final int API_KEY_RANDOM_BYTES = 32;
    public static final String API_KEY_HASH_ALGORITHM = "SHA-256";
    public static final int MAX_ACTIVE_API_KEYS = 5;
    public static final int SESSION_LIFETIME_MIN_MINUTES = 1;
    public static final int SESSION_LIFETIME_MAX_MINUTES = 30;
    public static final Duration SESSION_LIFETIME = Duration.ofMinutes(SESSION_LIFETIME_MAX_MINUTES);
    public static final long EXPIRY_SWEEP_INTERVAL_MS = 60_000L;
    public static final String CHECKOUT_PAGE_PATH = "/checkout/";
    public static final String RETURN_SESSION_PARAMETER = "sessionId";
    public static final String RETURN_ORDER_PARAMETER = "orderReference";
    public static final long REQUESTS_PER_MINUTE = 120;
    public static final String WEBHOOK_ALLOW_PRIVATE_HOSTS_PROPERTY = "${webhook-allow-private-hosts:false}";
    public static final String WEBHOOK_SECRET_LABEL = "webhook:";
    public static final String WEBHOOK_SECRET_PREFIX = "whsec_";
    public static final String WEBHOOK_SIGNATURE_HEADER = "X-Wallet-Signature";
    public static final String WEBHOOK_EVENT_HEADER = "X-Wallet-Event";
    public static final String WEBHOOK_EVENT_PREFIX = "checkout.";
    public static final String WEBHOOK_SECURE_SCHEME = "https";
    public static final Duration WEBHOOK_TIMEOUT = Duration.ofSeconds(5);
    public static final List<Duration> WEBHOOK_RETRY_DELAYS = List.of(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(10));

    private GatewayConstants() {
    }
}
