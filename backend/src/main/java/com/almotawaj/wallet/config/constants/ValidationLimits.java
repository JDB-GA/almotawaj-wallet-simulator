package com.almotawaj.wallet.config.constants;

public final class ValidationLimits {
    public static final int EMAIL_MAX = 254;
    public static final int MOBILE_MAX = 12;
    public static final int PASSWORD_MIN = 8;
    public static final int PASSWORD_MAX = 72;
    public static final int FULL_NAME_MAX = 150;
    public static final int CPR_LENGTH = 9;
    public static final int NATIONALITY_LENGTH = 2;
    public static final int ADDRESS_PART_MAX = 10;
    public static final int AREA_MAX = 100;
    public static final int REJECTION_REASON_MAX = 500;
    public static final int STORAGE_KEY_MAX = 255;
    public static final int CONTENT_TYPE_MAX = 50;
    public static final int AUDIT_ACTION_MAX = 40;
    public static final int AUDIT_DETAILS_MAX = 500;
    public static final int IBAN_MAX = 34;
    public static final int BIC_MAX = 11;
    public static final int COUNTERPARTY_NAME_MAX = 70;
    public static final int TRANSACTION_DESCRIPTION_MAX = 140;
    public static final int RECIPIENT_QUERY_MAX = 254;
    public static final int TRANSACTION_SEARCH_MAX = 100;
    public static final int TRANSACTION_REFERENCE_MAX = 24;
    public static final int DISPLAY_NAME_MIN = 2;
    public static final int API_KEY_NAME_MAX = 50;
    public static final int API_KEY_PREFIX_LENGTH = 12;
    public static final int API_KEY_HASH_LENGTH = 64;
    public static final int ORDER_REFERENCE_MAX = 64;
    public static final int URL_MAX = 500;
    public static final int MONEY_PRECISION = 19;
    public static final int MONEY_SCALE = 3;
    public static final int MONEY_INTEGER_DIGITS = 16;

    private ValidationLimits() {
    }
}
