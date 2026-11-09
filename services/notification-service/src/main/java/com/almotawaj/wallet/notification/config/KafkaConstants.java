package com.almotawaj.wallet.notification.config;

public final class KafkaConstants {
    public static final String EMAIL_EVENTS = "email-events";
    public static final long RETRY_INTERVAL_MS = 2_000;
    public static final long MAX_RETRIES = 3;
    public static final int REMEMBERED_EVENTS = 10_000;

    private KafkaConstants() {
    }
}
