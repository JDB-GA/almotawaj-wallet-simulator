package com.almotawaj.wallet.exception;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import lombok.Getter;

@Getter
public class InformationExistException extends RuntimeException {
    private final String code;

    public InformationExistException(String message) {
        this(message, ErrorCodes.DATA_CONFLICT);
    }

    public InformationExistException(String message, String code) {
        super(message);
        this.code = code;
    }
}
