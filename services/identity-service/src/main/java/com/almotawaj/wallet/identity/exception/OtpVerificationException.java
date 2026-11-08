package com.almotawaj.wallet.identity.exception;

import lombok.Getter;

@Getter
public class OtpVerificationException extends RuntimeException {
    private final String code;

    public OtpVerificationException(String message, String code) {
        super(message);
        this.code = code;
    }
}
