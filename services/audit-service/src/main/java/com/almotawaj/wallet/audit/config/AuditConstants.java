package com.almotawaj.wallet.audit.config;

public final class AuditConstants {
    public static final String AUDIT_LOGS_TABLE = "audit_logs";
    public static final int ACTION_MAX = 40;
    public static final int DETAILS_MAX = 500;
    public static final int EMAIL_MAX = 254;
    public static final String AUDIT_LOGS_PATH = "/api/admin/audit-logs";
    public static final String HAS_ROLE_SUPER_ADMIN = "hasRole('SUPER_ADMIN')";
    public static final String CREATED_AT = "createdAt";
    public static final String AUTH_COOKIE_NAME = "access_token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORITIES_CLAIM = "authorities";
    public static final String JWT_SECRET_PROPERTY = "${jwt-secret}";

    public static final String AUDIT_EVENTS = "audit-events";
    public static final long RETRY_INTERVAL_MS = 2_000;
    public static final long MAX_RETRIES = 3;
    public static final String HEALTH_PATHS = "/actuator/health/**";
    public static final String ERROR_PATH = "/error";

    private AuditConstants() {
    }
}
