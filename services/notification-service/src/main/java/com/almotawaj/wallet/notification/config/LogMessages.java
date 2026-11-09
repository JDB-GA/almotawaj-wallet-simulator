package com.almotawaj.wallet.notification.config;

public final class LogMessages {
    public static final String EMAIL_SEND_FAILED = "Failed to send email to user {}";

    public static final String EMAIL_SENT = "Sent {} email for user {}";
    public static final String DUPLICATE_EMAIL_SKIPPED = "Skipped duplicate email event {}";
    public static final String EMAIL_NOT_SENT = "Email to user %s was not sent";

    private LogMessages() {
    }
}
