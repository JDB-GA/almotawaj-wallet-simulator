package com.almotawaj.wallet.common.service;

import com.almotawaj.wallet.common.model.FileType;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.KycConstants;
import com.almotawaj.wallet.exception.FileStorageException;
import com.almotawaj.wallet.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class FileTypeDetector {
    public FileType detect(MultipartFile file, Set<FileType> allowed) {
        if (file.isEmpty()) {
            throw new InvalidFileException(ErrorMessages.FILE_REQUIRED, ErrorCodes.FILE_REQUIRED);
        }
        if (file.getSize() > KycConstants.MAX_FILE_BYTES) {
            throw new InvalidFileException(ErrorMessages.FILE_TOO_LARGE, ErrorCodes.FILE_TOO_LARGE);
        }
        byte[] header = readHeader(file);
        return allowed.stream()
                .filter(type -> type.matches(header))
                .findFirst()
                .orElseThrow(() -> new InvalidFileException(ErrorMessages.FILE_TYPE_INVALID, ErrorCodes.FILE_TYPE_INVALID));
    }

    private static byte[] readHeader(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            return input.readNBytes(FileType.SIGNATURE_MAX_LENGTH);
        } catch (IOException e) {
            throw new FileStorageException(file.getName(), e);
        }
    }
}
