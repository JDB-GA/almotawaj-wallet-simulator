package com.almotawaj.wallet.config.constants;

public final class ValidationPatterns {
    public static final String MOBILE_NUMBER = "^\\d{8}$";
    public static final String MOBILE_COUNTRY_CODE = "+973";
    public static final String EMAIL_MARKER = "@";
    public static final String OTP_CODE = "^\\d{6}$";
    public static final String FULL_NAME = "^[\\p{L} .'-]+$";
    public static final String CPR_NUMBER = "^\\d{9}$";
    public static final String NATIONALITY = "^[A-Z]{2}$";
    public static final String BLOCK = "^\\d{1,4}$";
    public static final String ROAD = "^\\d{1,5}$";
    public static final String BUILDING = "^[0-9A-Za-z]{1,6}$";
    public static final String FLAT = "^[0-9A-Za-z]{0,6}$";
    public static final String DISPLAY_NAME = "^[\\p{L}\\p{N} .,&'-]+$";
    public static final String HTTP_URL = "^https?://[^\\s]+$";
    public static final String ORDER_REFERENCE = "^[A-Za-z0-9._-]+$";

    private ValidationPatterns() {
    }
}
