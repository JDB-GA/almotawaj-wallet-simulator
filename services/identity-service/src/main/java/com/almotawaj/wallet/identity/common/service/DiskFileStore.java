package com.almotawaj.wallet.identity.common.service;

import com.almotawaj.wallet.identity.exception.FileStorageException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiskFileStore implements FileStore {
    private final Path root;

    public DiskFileStore(String root) throws IOException {
        this.root = Path.of(root).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    @Override
    public void save(String key, InputStream input, long size) throws IOException {
        Files.copy(input, resolve(key));
    }

    @Override
    public Resource load(String key) {
        return new FileSystemResource(resolve(key));
    }

    @Override
    public void delete(String key) throws IOException {
        Files.deleteIfExists(resolve(key));
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();
        if (!path.getParent().equals(root)) {
            throw new FileStorageException(key, null);
        }
        return path;
    }
}
