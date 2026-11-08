package com.almotawaj.wallet.config.constants;

public final class DatabaseTables {
    public static final String USERS = "users";
    public static final String WALLETS = "wallets";
    public static final String WALLET_TRANSACTIONS = "wallet_transactions";
    public static final String PAYMENT_REQUESTS = "payment_requests";
    public static final String MERCHANT_API_KEYS = "merchant_api_keys";
    public static final String CHECKOUT_SESSIONS = "checkout_sessions";
    public static final String MERCHANT_WEBHOOKS = "merchant_webhooks";
    public static final String VOUCHERS = "vouchers";
    public static final String VOUCHER_PURCHASES = "voucher_purchases";
    public static final String PAYOUT_BATCHES = "payout_batches";
    public static final String PAYOUT_ITEMS = "payout_items";
    public static final String IDEMPOTENCY_RECORDS = "idempotency_records";
    public static final String TEXT_COLUMN = "text";

    private DatabaseTables() {
    }
}
