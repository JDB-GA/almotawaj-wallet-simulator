package com.almotawaj.wallet.config.constants;

import java.time.Duration;

public final class OtpConstants {
    public static final int CODE_BOUND = 1_000_000;
    public static final String CODE_FORMAT = "%06d";
    public static final Duration EXPIRY = Duration.ofMinutes(10);
    public static final Duration INVITATION_EXPIRY = Duration.ofHours(48);
    public static final int MAX_ATTEMPTS = 5;
    public static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final String HASH_INPUT_SEPARATOR = ":";
    public static final String SECRET_PROPERTY = "${otp-secret}";

    private OtpConstants() {
    }
}
