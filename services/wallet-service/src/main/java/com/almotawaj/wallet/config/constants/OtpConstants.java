package com.almotawaj.wallet.config.constants;

import java.time.Duration;

public final class OtpConstants {
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final String SECRET_PROPERTY = "${otp-secret}";

    private OtpConstants() {
    }
}
