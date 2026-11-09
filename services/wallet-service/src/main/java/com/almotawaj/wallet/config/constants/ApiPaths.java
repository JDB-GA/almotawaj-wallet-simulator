package com.almotawaj.wallet.config.constants;

public final class ApiPaths {
    public static final String HEALTH = "/actuator/health/**";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String ME = "/me";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String KYC = "/api/kyc";
    public static final String KYC_APPLICATION = "/{applicationId}";
    public static final String ADMIN_USERS = "/api/admin/users";
    public static final String ADMIN_STATISTICS = "/api/admin/statistics";
    public static final String ADMIN_TRANSACTIONS = "/api/admin/transactions";
    public static final String SEED = "/api/seed/wallet";
    public static final String WALLET = "/api/wallet";
    public static final String NOTIFICATIONS_STREAM = "/api/notifications/stream";
    public static final String TRANSACTIONS = "/transactions";
    public static final String TRANSACTION_RECEIPT = "/transactions/{transactionId}/receipt";
    public static final String TRANSACTIONS_STATEMENT = "/transactions/statement";
    public static final String TOP_UPS = "/top-ups";
    public static final String TOP_UP_OPTIONS = "/top-ups/options";
    public static final String TRANSFERS = "/transfers";
    public static final String TRANSFER_OPTIONS = "/transfers/options";
    public static final String RECIPIENTS = "/recipients";
    public static final String RECIPIENT_SUGGESTIONS = "/recipients/suggestions";
    public static final String PAYMENT_REQUESTS = "/requests";
    public static final String PAYMENT_REQUEST_PAY = "/{requestId}/pay";
    public static final String PAYMENT_REQUEST_DECLINE = "/{requestId}/decline";
    public static final String PAYMENT_REQUEST_CANCEL = "/{requestId}/cancel";
    public static final String WALLET_TRANSACTION_RECEIPTS = WALLET + "/transactions/*/receipt";
    public static final String WALLET_TRANSACTIONS_STATEMENT = WALLET + TRANSACTIONS_STATEMENT;
    public static final String WALLET_TRANSFERS = WALLET + TRANSFERS;
    public static final String WALLET_RECIPIENTS = WALLET + RECIPIENTS;
    public static final String WALLET_TOP_UPS = WALLET + TOP_UPS;
    public static final String WALLET_PAYMENT_REQUESTS = WALLET + PAYMENT_REQUESTS;
    public static final String MERCHANT_API_KEYS = "/api/merchant/api-keys";
    public static final String API_KEY_BY_ID = "/{keyId}";
    public static final String MERCHANT_WEBHOOK = "/api/merchant/webhook";
    public static final String MERCHANT_CHECKOUT_SESSIONS = "/api/merchant/checkout-sessions";
    public static final String GATEWAY = "/api/gateway";
    public static final String GATEWAY_CHECKOUT_SESSIONS = GATEWAY + "/checkout-sessions";
    public static final String CHECKOUT = "/api/checkout";
    public static final String CHECKOUT_SESSION = "/{sessionId}";
    public static final String CHECKOUT_SESSION_PAY = "/{sessionId}/pay";
    public static final String CHECKOUT_SESSION_VOUCHERS = "/{sessionId}/vouchers";
    public static final String CHECKOUT_SESSION_CANCEL = "/{sessionId}/cancel";
    public static final String CHECKOUT_SESSION_REFUND = "/{sessionId}/refund";
    public static final String VOUCHERS = "/api/vouchers";
    public static final String VOUCHER_PURCHASE = "/{voucherId}/purchase";
    public static final String VOUCHER_PURCHASES = "/purchases";
    public static final String MERCHANT_VOUCHERS = "/api/merchant/vouchers";
    public static final String VOUCHER_BY_ID = "/{voucherId}";
    public static final String MERCHANT_PAYOUTS = "/api/merchant/payouts";
    public static final String PAYOUT_BY_ID = "/{batchId}";
    public static final String ADMIN_SIMULATIONS = "/api/admin/simulations";
    public static final String SIMULATION_VOUCHER_RACE = "/voucher-race";
    public static final String SIMULATION_PAYOUT_RACE = "/payout-race";
    public static final String PROFILE = "/api/profile";
    public static final String USER_TRANSACTIONS = "/{userId}/transactions";
    public static final String TRANSACTION_STATISTICS = "/transactions";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String API_DOCS = "/v3/api-docs/**";
    public static final String ERROR = "/error";
    public static final String ALL = "/**";

    public static final String INTERNAL_PREFIX = "/internal/";
    public static final String INTERNAL_PATHS = "/internal/**";
    public static final String INTERNAL_SEED_DATA = "/internal/seed-data";
    public static final String INTERNAL_SANDBOX_ACCOUNTS = "/internal/sandbox-accounts";

    private ApiPaths() {
    }
}
