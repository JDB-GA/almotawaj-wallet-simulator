package com.almotawaj.wallet.config.constants;

public final class SimulationConstants {
    public static final String EMAIL_PATTERN = "sim-%s@simulation.local";
    public static final String MOBILE_PREFIX = "+9739";
    public static final int MOBILE_RANDOM_DIGITS = 7;
    public static final String DISABLED_PASSWORD = "!simulation-account";
    public static final String MERCHANT_NAME = "Simulation Store";
    public static final String VOUCHER_TITLE = "Simulation voucher";
    public static final String VOUCHER_PRICE = "10.000";
    public static final String VOUCHER_FACE_VALUE = "12.000";
    public static final String BUYER_BALANCE = "100.000";
    public static final int VOUCHER_EXPIRY_HOURS = 1;
    public static final long UNSAFE_READ_DELAY_MS = 25;
    public static final String BUYER_THREAD_NAME = "buyer-%d";
    public static final int MIN_BUYERS = 2;
    public static final int MAX_BUYERS = 50;
    public static final int MIN_STOCK = 1;
    public static final int MAX_STOCK = 20;
    public static final int MIN_RECIPIENTS = 2;
    public static final int MAX_RECIPIENTS = 30;
    public static final String MIN_SIM_AMOUNT = "1.000";
    public static final String MAX_SIM_AMOUNT = "100.000";
    public static final String MIN_SIM_BALANCE = "0.000";
    public static final String MAX_SIM_BALANCE = "5000.000";
    public static final long RACE_TIMEOUT_SECONDS = 30;
    public static final double NANOS_PER_MILLI = 1_000_000.0;

    private SimulationConstants() {
    }
}
