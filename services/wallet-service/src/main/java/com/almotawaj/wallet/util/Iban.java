package com.almotawaj.wallet.util;

import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;

import java.util.Locale;

public final class Iban {
    private static final int COUNTRY_AND_CHECK_LENGTH = 4;
    private static final int LETTER_OFFSET = 10;
    private static final int CHECK_DIGITS_BASE = 98;

    private Iban() {
    }

    public static String normalize(String value) {
        return value == null ? null : value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    public static boolean isValid(String value) {
        String iban = normalize(value);
        return iban != null
                && iban.length() >= WalletConstants.IBAN_MIN_LENGTH
                && iban.length() <= ValidationLimits.IBAN_MAX
                && iban.matches(WalletConstants.IBAN_PATTERN)
                && mod97(rearrange(iban)) == WalletConstants.IBAN_CHECK_REMAINDER;
    }

    public static String of(String countryCode, String bban) {
        String placeholder = countryCode + WalletConstants.IBAN_PLACEHOLDER_CHECK_DIGITS + bban;
        int checkDigits = CHECK_DIGITS_BASE - mod97(rearrange(placeholder));
        return countryCode + String.format("%02d", checkDigits) + bban;
    }

    private static String rearrange(String iban) {
        return iban.substring(COUNTRY_AND_CHECK_LENGTH) + iban.substring(0, COUNTRY_AND_CHECK_LENGTH);
    }

    private static int mod97(String value) {
        int remainder = 0;
        for (char character : value.toCharArray()) {
            int number = Character.isDigit(character) ? character - '0' : character - 'A' + LETTER_OFFSET;
            remainder = (remainder * (number < LETTER_OFFSET ? 10 : 100) + number) % WalletConstants.IBAN_CHECK_MODULUS;
        }
        return remainder;
    }
}
