package com.almotawaj.wallet.config.constants;

public final class ErrorCodes {
    public static final String PROPERTY = "code";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String EMAIL_ALREADY_REGISTERED = "EMAIL_ALREADY_REGISTERED";
    public static final String MOBILE_ALREADY_REGISTERED = "MOBILE_ALREADY_REGISTERED";
    public static final String DATA_CONFLICT = "DATA_CONFLICT";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
    public static final String EMAIL_ALREADY_VERIFIED = "EMAIL_ALREADY_VERIFIED";
    public static final String OTP_NOT_FOUND = "OTP_NOT_FOUND";
    public static final String OTP_INVALID = "OTP_INVALID";
    public static final String OTP_EXPIRED = "OTP_EXPIRED";
    public static final String OTP_ATTEMPTS_EXCEEDED = "OTP_ATTEMPTS_EXCEEDED";
    public static final String OTP_RESEND_COOLDOWN = "OTP_RESEND_COOLDOWN";
    public static final String KYC_ALREADY_PENDING = "KYC_ALREADY_PENDING";
    public static final String KYC_ALREADY_APPROVED = "KYC_ALREADY_APPROVED";
    public static final String CPR_ALREADY_USED = "CPR_ALREADY_USED";
    public static final String KYC_UNDERAGE = "KYC_UNDERAGE";
    public static final String DOCUMENT_EXPIRED = "DOCUMENT_EXPIRED";
    public static final String FILE_REQUIRED = "FILE_REQUIRED";
    public static final String FILE_TYPE_INVALID = "FILE_TYPE_INVALID";
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    public static final String KYC_ALREADY_REVIEWED = "KYC_ALREADY_REVIEWED";
    public static final String PERMISSION_NOT_GRANTABLE = "PERMISSION_NOT_GRANTABLE";
    public static final String SUPER_ADMIN_PERMISSIONS_FIXED = "SUPER_ADMIN_PERMISSIONS_FIXED";
    public static final String INVALID_REQUEST_PARAMETER = "INVALID_REQUEST_PARAMETER";
    public static final String CANNOT_MANAGE_SELF = "CANNOT_MANAGE_SELF";
    public static final String USER_CLOSED = "USER_CLOSED";
    public static final String INVALID_STATUS_TRANSITION = "INVALID_STATUS_TRANSITION";
    public static final String INVALID_CURRENT_PASSWORD = "INVALID_CURRENT_PASSWORD";
    public static final String PASSWORD_REUSED = "PASSWORD_REUSED";
    public static final String ROLE_NOT_ASSIGNABLE = "ROLE_NOT_ASSIGNABLE";
    public static final String SEED_PASSWORD_MISSING = "SEED_PASSWORD_MISSING";
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
    public static final String PROFILE_PICTURE_NOT_FOUND = "PROFILE_PICTURE_NOT_FOUND";
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

    private ErrorCodes() {
    }
}
