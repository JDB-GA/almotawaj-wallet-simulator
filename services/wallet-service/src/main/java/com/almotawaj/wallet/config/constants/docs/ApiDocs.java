package com.almotawaj.wallet.config.constants.docs;

public final class ApiDocs {
    public static final String OK = "200";
    public static final String CREATED = "201";
    public static final String ACCEPTED = "202";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String CONFLICT = "409";
    public static final String CONTENT_TOO_LARGE = "413";
    public static final String UNPROCESSABLE = "422";
    public static final String TOO_MANY_REQUESTS = "429";
    public static final String SERVER_ERROR = "500";
    public static final String CLIENT_ERROR_PREFIX = "4";
    public static final String SERVER_ERROR_PREFIX = "5";
    public static final String PROBLEM_JSON = "application/problem+json";
    public static final String PROBLEM_SCHEMA = "ProblemDetail";
    public static final String PROBLEM_SCHEMA_REF = "#/components/schemas/" + PROBLEM_SCHEMA;

    public static final String VALIDATION_FAILED = "Validation failed; `errors` maps each invalid field to its message";
    public static final String UNAUTHORIZED_DESCRIPTION = "Missing, invalid, expired or revoked session cookie";
    public static final String FORBIDDEN_DESCRIPTION = "Signed in but not allowed (wrong role, missing permission or unverified email)";
    public static final String RATE_LIMITED = "Too many requests from this IP (10 per minute); see the Retry-After header";
    public static final String SERVER_ERROR_DESCRIPTION = "Unexpected server error";

    public static final String TITLE = "Digital Wallet API";
    public static final String VERSION = "1.0.0";
    public static final String DESCRIPTION = """
            REST API for the Almotawaj Digital Wallet: authentication, identity verification (KYC), \
            wallets and transactions, merchant payments, KYC review, user management, permissions and audit logs.

            **How to authenticate in Swagger:** call `POST /auth/users/login`. The server sets an httpOnly \
            `access_token` cookie that the browser sends automatically with every following request.

            Errors use RFC 9457 Problem Details with a machine-readable `code` property.""";
    public static final String SECURITY_SCHEME_NAME = "cookieAuth";
    public static final String SEED_SCHEME_NAME = "seedToken";
    public static final String SEED_SCHEME_DESCRIPTION = "Paste the SEED_TOKEN value only (Swagger adds the Bearer prefix)";
    public static final String BEARER = "bearer";
    public static final String SECURITY_SCHEME_DESCRIPTION = "JWT stored in the httpOnly access_token cookie, set by the login endpoint";

    private ApiDocs() {
    }
}
