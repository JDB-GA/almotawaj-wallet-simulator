package com.almotawaj.wallet.config.constants;

public final class DatabaseTables {
    public static final String USERS = "users";
    public static final String OTP_CHALLENGES = "otp_challenges";
    public static final String KYC_APPLICATIONS = "kyc_applications";
    public static final String KYC_DOCUMENTS = "kyc_documents";
    public static final String USER_PERMISSIONS = "user_permissions";
    public static final String AUDIT_LOGS = "audit_logs";
    public static final String WALLETS = "wallets";
    public static final String WALLET_TRANSACTIONS = "wallet_transactions";
    public static final String PAYMENT_REQUESTS = "payment_requests";
    public static final String MERCHANT_API_KEYS = "merchant_api_keys";
    public static final String CHECKOUT_SESSIONS = "checkout_sessions";
    public static final String MERCHANT_WEBHOOKS = "merchant_webhooks";

    private DatabaseTables() {
    }
}
