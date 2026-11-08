package com.almotawaj.wallet.identity.config.constants;

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
    public static final int COUNTERPARTY_NAME_MAX = 70;
    public static final int DISPLAY_NAME_MIN = 2;
    public static final int ENUM_COLUMN_LENGTH = 20;

    private ValidationLimits() {
    }
}
