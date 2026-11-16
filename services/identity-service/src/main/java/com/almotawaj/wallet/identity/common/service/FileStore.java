package com.almotawaj.wallet.identity.common.service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public interface FileStore {
    void save(String key, InputStream input, long size) throws IOException;

    Resource load(String key);

    void delete(String key) throws IOException;
}
