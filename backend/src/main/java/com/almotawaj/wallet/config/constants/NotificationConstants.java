package com.almotawaj.wallet.config.constants;

import java.time.Duration;

public final class NotificationConstants {
    public static final long EMITTER_TIMEOUT_MS = Duration.ofMinutes(30).toMillis();
    public static final long HEARTBEAT_INTERVAL_MS = 25_000L;
    public static final String HEARTBEAT_COMMENT = "heartbeat";
    public static final String MONEY_RECEIVED = "money-received";
    public static final String PAYMENT_REQUESTED = "payment-requested";
    public static final String PAYMENT_REQUEST_UPDATED = "payment-request-updated";
    public static final String VOUCHER_STOCK_CHANGED = "voucher-stock-changed";
    public static final String VOUCHER_SOLD = "voucher-sold";
    public static final String PAYOUT_PROGRESS = "payout-progress";
    public static final String PAYOUT_COMPLETED = "payout-completed";

    private NotificationConstants() {
    }
}
