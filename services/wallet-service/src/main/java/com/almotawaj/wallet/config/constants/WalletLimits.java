package com.almotawaj.wallet.config.constants;

public final class WalletLimits {
    public static final String TOP_UP_MIN = "0.100";
    public static final String TOP_UP_MAX = "5000.000";
    public static final String DAILY_TOP_UP_LIMIT = "10000.000";
    public static final String TRANSFER_MIN = "0.100";
    public static final String TRANSFER_MAX = "5000.000";
    public static final String DAILY_TRANSFER_LIMIT = "10000.000";
    public static final String CHECKOUT_MIN = "0.100";
    public static final String CHECKOUT_MAX = "5000.000";
    public static final String DAILY_CHECKOUT_LIMIT = "10000.000";
    public static final String VOUCHER_PRICE_MIN = "0.100";
    public static final String VOUCHER_PRICE_MAX = "5000.000";
    public static final int VOUCHER_QUANTITY_MIN = 1;
    public static final int VOUCHER_QUANTITY_MAX = 10000;
    public static final int VOUCHER_PER_CLIENT_MIN = 1;
    public static final int VOUCHER_PER_CLIENT_MAX = 100;
    public static final String PAYOUT_MIN = "0.100";
    public static final String PAYOUT_MAX = "5000.000";
    public static final int PAYOUT_ITEMS_MAX = 100;
    public static final int PAYOUT_THREADS = 5;
    public static final int ASYNC_THREADS = 4;

    private WalletLimits() {
    }
}
