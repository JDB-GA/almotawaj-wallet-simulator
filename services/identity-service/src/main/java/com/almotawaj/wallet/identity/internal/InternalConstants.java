package com.almotawaj.wallet.identity.internal;

public final class InternalConstants {
    public static final String ACCOUNTS_PATH = "/internal/accounts";
    public static final String INTERNAL_PATHS = "/internal/**";
    public static final String TOKEN_HEADER = "X-Internal-Token";
    public static final String TOKEN_PROPERTY = "${internal-api-token}";

    private InternalConstants() {
    }
}
