package com.almotawaj.wallet.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FileType {
    PDF("application/pdf", ".pdf", new byte[]{'%', 'P', 'D', 'F', '-'}),
    JPEG("image/jpeg", ".jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
    PNG("image/png", ".png", new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});

    public static final int SIGNATURE_MAX_LENGTH = 8;

    private final String contentType;
    private final String extension;
    private final byte[] signature;

    public boolean matches(byte[] header) {
        return header.length >= signature.length
                && Arrays.equals(Arrays.copyOf(header, signature.length), signature);
    }
}
