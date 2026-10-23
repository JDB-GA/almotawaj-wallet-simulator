package com.almotawaj.wallet.config.constants;

public final class WalletConstants {
    public static final String CURRENCY = "BHD";
    public static final String COUNTRY_CODE = "BH";
    public static final String BANK_CODE = "ALMT";
    public static final String BANK_BIC = "ALMTBHBM";
    public static final String EMAIL_MASK = "***";
    public static final String EMAIL_MARKER = "@";
    public static final String NAME_INITIAL_SUFFIX = ".";
    public static final int ACCOUNT_NUMBER_LENGTH = 14;
    public static final int IBAN_LENGTH = 22;
    public static final int IBAN_MIN_LENGTH = 15;
    public static final int IBAN_CHECK_MODULUS = 97;
    public static final int IBAN_CHECK_REMAINDER = 1;
    public static final String IBAN_PLACEHOLDER_CHECK_DIGITS = "00";
    public static final String IBAN_PATTERN = "^[A-Z]{2}\\d{2}[A-Z0-9]+$";
    public static final String TRANSACTION_REFERENCE_PREFIX = "TXN";
    public static final String TRANSACTION_REFERENCE_DATE_FORMAT = "yyyyMMdd";
    public static final String TRANSACTION_REFERENCE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    public static final int TRANSACTION_REFERENCE_RANDOM_LENGTH = 8;
    public static final int IBAN_GENERATION_ATTEMPTS = 5;
    public static final int SUGGESTION_MIN_QUERY = 3;
    public static final int SUGGESTION_LIMIT = 5;
    public static final int MOBILE_VISIBLE_DIGITS = 4;
    public static final String LIKE_SUFFIX = "%";
    public static final String TIME_ZONE = "Asia/Bahrain";
    public static final String VOUCHER_CODE_PREFIX = "VCH";
    public static final int VOUCHER_CODE_RANDOM_LENGTH = 12;
    public static final int VOUCHER_CODE_GROUP_SIZE = 4;
    public static final String VOUCHER_SOLD_OUT_REASON = "VOUCHER_SOLD_OUT";
    public static final String PAYOUT_INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
    public static final String PAYOUT_RECIPIENT_UNAVAILABLE = "RECIPIENT_UNAVAILABLE";
    public static final String PAYOUT_PROCESSING_ERROR = "PROCESSING_ERROR";
    public static final String PAYOUT_THREAD_NAME = "payout-%d";
    public static final String PAYOUT_EXECUTOR = "payoutExecutor";
    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    public static final String IDEMPOTENCY_VOUCHER_PURCHASE = "VOUCHER_PURCHASE";
    public static final int IDEMPOTENCY_CREATED_STATUS = 201;

    private WalletConstants() {
    }
}
