package com.almotawaj.wallet.identity.common.service;

import com.almotawaj.wallet.identity.common.model.FileType;
import com.almotawaj.wallet.identity.config.constants.LogMessages;
import com.almotawaj.wallet.identity.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final FileStore fileStore;

    public String store(MultipartFile file, FileType type) {
        String key = UUID.randomUUID() + type.getExtension();
        try (InputStream input = file.getInputStream()) {
            fileStore.save(key, input, file.getSize());
        } catch (IOException | RuntimeException e) {
            throw new FileStorageException(key, e);
        }
        deleteOnRollback(key);
        return key;
    }

    public Resource load(String key) {
        return fileStore.load(key);
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
            fileStore.delete(key);
        } catch (IOException | RuntimeException e) {
            log.warn(LogMessages.FILE_DELETE_FAILED, key, e);
        }
    }
}
