package com.almotawaj.wallet.identity.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final String code;

    public BusinessRuleException(String message, String code) {
        super(message);
        this.code = code;
    }
}
