package com.almotawaj.wallet.gateway.config;

import java.util.List;

public final class GatewayConstants {
    public static final String CORS_ALLOWED_ORIGINS_PROPERTY = "${cors-allowed-origins}";
    public static final List<String> CORS_ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    public static final List<String> CORS_ALLOWED_HEADERS = List.of("Content-Type", "Accept", "Authorization", "Idempotency-Key");
    public static final long CORS_MAX_AGE_SECONDS = 3600;
    public static final String ALL_PATHS = "/**";

    private GatewayConstants() {
    }
}
