package com.almotawaj.wallet.exception;

import com.almotawaj.wallet.config.constants.ErrorCodes;
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
