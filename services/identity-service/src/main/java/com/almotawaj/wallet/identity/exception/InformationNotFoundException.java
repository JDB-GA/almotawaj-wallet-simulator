package com.almotawaj.wallet.identity.exception;

import com.almotawaj.wallet.identity.config.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class InformationNotFoundException extends RuntimeException {
    private final String code;

    public InformationNotFoundException(String message) {
        this(message, ErrorCodes.NOT_FOUND);
    }

    public InformationNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }
}
