package com.almotawaj.wallet.exception;

import lombok.Getter;

import java.time.Duration;

@Getter
public class OtpResendCooldownException extends RuntimeException {
    private final Duration retryAfter;

    public OtpResendCooldownException(Duration retryAfter) {
        this.retryAfter = retryAfter;
    }
}
