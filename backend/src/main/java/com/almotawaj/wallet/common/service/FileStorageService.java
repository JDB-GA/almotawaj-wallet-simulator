package com.almotawaj.wallet.common.service;

import com.almotawaj.wallet.common.model.FileType;
import com.almotawaj.wallet.config.constants.KycConstants;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value(KycConstants.STORAGE_ROOT_PROPERTY) String root) throws IOException {
        this.root = Path.of(root).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String store(MultipartFile file, FileType type) {
        String key = UUID.randomUUID() + type.getExtension();
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, resolve(key));
        } catch (IOException e) {
            throw new FileStorageException(key, e);
        }
        deleteOnRollback(key);
        return key;
    }

    public Resource load(String key) {
        return new FileSystemResource(resolve(key));
    }

    public void deleteAfterCommit(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            delete(key);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                delete(key);
            }
        });
    }

    private void deleteOnRollback(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    delete(key);
                }
            }
        });
    }

    private void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            log.warn(LogMessages.FILE_DELETE_FAILED, key, e);
        }
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();
        if (!path.getParent().equals(root)) {
            throw new FileStorageException(key, null);
        }
        return path;
    }
}
