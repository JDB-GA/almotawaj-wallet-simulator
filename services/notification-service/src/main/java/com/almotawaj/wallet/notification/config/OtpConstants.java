package com.almotawaj.wallet.notification.config;

import java.time.Duration;

public final class OtpConstants {
    public static final Duration EXPIRY = Duration.ofMinutes(10);
    public static final Duration INVITATION_EXPIRY = Duration.ofHours(48);

    private OtpConstants() {
    }
}
