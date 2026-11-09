package com.almotawaj.wallet.config.constants;

public final class KafkaConstants {
    public static final String ACCOUNT_EVENTS = "account-events";
    public static final String AUDIT_EVENTS = "audit-events";
    public static final long RETRY_INTERVAL_MS = 2_000;
    public static final long MAX_RETRIES = 3;

    private KafkaConstants() {
    }
}
