package com.almotawaj.wallet.config.constants;

import java.time.Duration;

public final class RateLimitConstants {
    public static final long CAPACITY = 10;
    public static final Duration WINDOW = Duration.ofMinutes(1);
    public static final Duration BUCKET_EXPIRY = Duration.ofMinutes(10);
    public static final long MAX_TRACKED_CLIENTS = 100_000;
    public static final String KEY_SEPARATOR = ":";

    private RateLimitConstants() {
    }
}
