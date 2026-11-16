package com.almotawaj.wallet.config.constants;

public final class ErrorCodes {
    public static final String PROPERTY = "code";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ACCOUNT_DEACTIVATED = "ACCOUNT_DEACTIVATED";
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String DATA_CONFLICT = "DATA_CONFLICT";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    public static final String INVALID_REQUEST_PARAMETER = "INVALID_REQUEST_PARAMETER";
    public static final String WALLET_KYC_REQUIRED = "WALLET_KYC_REQUIRED";
    public static final String DAILY_TOP_UP_LIMIT_EXCEEDED = "DAILY_TOP_UP_LIMIT_EXCEEDED";
    public static final String DAILY_TRANSFER_LIMIT_EXCEEDED = "DAILY_TRANSFER_LIMIT_EXCEEDED";
    public static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String SELF_TRANSFER_NOT_ALLOWED = "SELF_TRANSFER_NOT_ALLOWED";
    public static final String RECIPIENT_NOT_FOUND = "RECIPIENT_NOT_FOUND";
    public static final String RECIPIENT_UNAVAILABLE = "RECIPIENT_UNAVAILABLE";
    public static final String TRANSACTION_NOT_FOUND = "TRANSACTION_NOT_FOUND";
    public static final String PAYMENT_REQUEST_NOT_FOUND = "PAYMENT_REQUEST_NOT_FOUND";
    public static final String PAYMENT_REQUEST_NOT_PENDING = "PAYMENT_REQUEST_NOT_PENDING";
    public static final String API_KEY_NOT_FOUND = "API_KEY_NOT_FOUND";
    public static final String API_KEY_LIMIT_REACHED = "API_KEY_LIMIT_REACHED";
    public static final String WEBHOOK_URL_NOT_ALLOWED = "WEBHOOK_URL_NOT_ALLOWED";
    public static final String CHECKOUT_NOT_FOUND = "CHECKOUT_NOT_FOUND";
    public static final String CHECKOUT_NOT_PENDING = "CHECKOUT_NOT_PENDING";
    public static final String CHECKOUT_EXPIRED = "CHECKOUT_EXPIRED";
    public static final String CHECKOUT_NOT_PAID = "CHECKOUT_NOT_PAID";
    public static final String ORDER_ALREADY_EXISTS = "ORDER_ALREADY_EXISTS";
    public static final String MERCHANT_UNAVAILABLE = "MERCHANT_UNAVAILABLE";
    public static final String DAILY_CHECKOUT_LIMIT_EXCEEDED = "DAILY_CHECKOUT_LIMIT_EXCEEDED";
    public static final String VOUCHER_NOT_FOUND = "VOUCHER_NOT_FOUND";
    public static final String VOUCHER_SOLD_OUT = "VOUCHER_SOLD_OUT";
    public static final String VOUCHER_UNAVAILABLE = "VOUCHER_UNAVAILABLE";
    public static final String VOUCHER_LIMIT_REACHED = "VOUCHER_LIMIT_REACHED";
    public static final String VOUCHER_EDIT_CONFLICT = "VOUCHER_EDIT_CONFLICT";
    public static final String VOUCHER_PRICE_ABOVE_FACE_VALUE = "VOUCHER_PRICE_ABOVE_FACE_VALUE";
    public static final String VOUCHER_EXPIRY_BEFORE_SALE_END = "VOUCHER_EXPIRY_BEFORE_SALE_END";
    public static final String VOUCHER_NOT_USABLE = "VOUCHER_NOT_USABLE";
    public static final String PAYOUT_BATCH_NOT_FOUND = "PAYOUT_BATCH_NOT_FOUND";
    public static final String PAYOUT_RECIPIENT_DUPLICATE = "PAYOUT_RECIPIENT_DUPLICATE";
    public static final String IDEMPOTENCY_KEY_REUSED = "IDEMPOTENCY_KEY_REUSED";

    private ErrorCodes() {
    }
}
