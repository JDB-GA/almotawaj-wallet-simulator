package com.almotawaj.wallet.util;

import com.almotawaj.wallet.config.constants.ValidationPatterns;

import java.util.Locale;

public final class LoginIdentifier {
    private LoginIdentifier() {
    }

    public static boolean isEmail(String identifier) {
        return identifier.contains(ValidationPatterns.EMAIL_MARKER);
    }

    public static String normalize(String identifier) {
        String trimmed = identifier.trim();
        return isEmail(trimmed) ? normalizeEmail(trimmed) : normalizeMobile(trimmed);
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizeMobile(String localNumber) {
        return ValidationPatterns.MOBILE_COUNTRY_CODE + localNumber.trim();
    }
}
