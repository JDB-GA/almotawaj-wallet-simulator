package com.almotawaj.wallet.identity.exception;

import lombok.Getter;

import java.time.Duration;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final Duration retryAfter;

    public RateLimitExceededException(Duration retryAfter) {
        this.retryAfter = retryAfter;
    }
}
