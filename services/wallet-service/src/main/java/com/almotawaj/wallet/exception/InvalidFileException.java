package com.almotawaj.wallet.exception;

import lombok.Getter;

@Getter
public class InvalidFileException extends RuntimeException {
    private final String code;

    public InvalidFileException(String message, String code) {
        super(message);
        this.code = code;
    }
}
