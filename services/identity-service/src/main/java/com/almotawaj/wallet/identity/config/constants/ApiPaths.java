package com.almotawaj.wallet.identity.config.constants;

public final class ApiPaths {
    public static final String HEALTH = "/actuator/health/**";
    public static final String AUTH_USERS = "/auth/users";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String ME = "/me";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String RESEND_VERIFICATION = "/verify-email/resend";
    public static final String AUTH_REGISTER = AUTH_USERS + REGISTER;
    public static final String AUTH_LOGIN = AUTH_USERS + LOGIN;
    public static final String AUTH_LOGOUT = AUTH_USERS + LOGOUT;
    public static final String AUTH_ME = AUTH_USERS + ME;
    public static final String AUTH_VERIFY_EMAIL = AUTH_USERS + VERIFY_EMAIL;
    public static final String AUTH_RESEND_VERIFICATION = AUTH_USERS + RESEND_VERIFICATION;
    public static final String FORGOT_PASSWORD = "/password/forgot";
    public static final String RESET_PASSWORD = "/password/reset";
    public static final String CHANGE_PASSWORD = "/password/change";
    public static final String AUTH_FORGOT_PASSWORD = AUTH_USERS + FORGOT_PASSWORD;
    public static final String AUTH_RESET_PASSWORD = AUTH_USERS + RESET_PASSWORD;
    public static final String AUTH_CHANGE_PASSWORD = AUTH_USERS + CHANGE_PASSWORD;
    public static final String KYC = "/api/kyc";
    public static final String MY_KYC_DOCUMENT = "/me/documents/{documentId}";
    public static final String ADMIN_KYC = "/api/admin/kyc";
    public static final String KYC_APPLICATION = "/{applicationId}";
    public static final String KYC_APPLICATION_DOCUMENT = "/{applicationId}/documents/{documentId}";
    public static final String KYC_APPROVE = "/{applicationId}/approve";
    public static final String KYC_REJECT = "/{applicationId}/reject";
    public static final String ADMIN_USERS = "/api/admin/users";
    public static final String ADMIN_SEED_DATA = "/api/admin/seed-data";
    public static final String ADMIN_STATISTICS = "/api/admin/statistics";
    public static final String SEED = "/api/seed";
    public static final String TRANSACTIONS = "/transactions";
    public static final String TOP_UP_OPTIONS = "/top-ups/options";
    public static final String PAYMENT_REQUESTS = "/requests";
    public static final String MERCHANT_API_KEYS = "/api/merchant/api-keys";
    public static final String CHECKOUT_SESSION = "/{sessionId}";
    public static final String VOUCHERS = "/api/vouchers";
    public static final String VOUCHER_PURCHASES = "/purchases";
    public static final String PROFILE = "/api/profile";
    public static final String PROFILE_PICTURE = "/picture";
    public static final String USER_BY_ID = "/{userId}";
    public static final String USER_SUSPEND = "/{userId}/suspend";
    public static final String USER_REACTIVATE = "/{userId}/reactivate";
    public static final String USER_PERMISSION = "/{userId}/permissions/{permission}";
    public static final String USER_STATISTICS = "/users";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String API_DOCS = "/v3/api-docs/**";
    public static final String ERROR = "/error";
    public static final String ALL = "/**";

    private ApiPaths() {
    }
}
