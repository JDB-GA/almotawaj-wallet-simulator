package com.almotawaj.wallet.identity.config.constants;

public final class InternalConstants {
    public static final String SANDBOX_ACCOUNTS_PATH = "/internal/sandbox-accounts";
    public static final String SANDBOX_EMAIL = "sim-%s@simulation.local";
    public static final String SANDBOX_MOBILE_PREFIX = "+9739";
    public static final int SANDBOX_MOBILE_DIGITS = 7;
    public static final String SANDBOX_DISABLED_PASSWORD = "{noop}disabled";
    public static final String INTERNAL_PREFIX = "/internal/";
    public static final String INTERNAL_PATHS = "/internal/**";
    public static final String TOKEN_HEADER = "X-Internal-Token";
    public static final String WALLET_URL_PROPERTY = "${wallet-url}";
    public static final String WALLET_SEED_DATA_PATH = "/internal/seed-data";
    public static final String TOKEN_PROPERTY = "${internal-api-token}";

    private InternalConstants() {
    }
}
