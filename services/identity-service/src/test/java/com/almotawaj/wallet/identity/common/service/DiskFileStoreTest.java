package com.almotawaj.wallet.identity.common.service;

import com.almotawaj.wallet.identity.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiskFileStoreTest {
    @TempDir
    Path root;

    @Test
    void savesLoadsAndDeletesAFile() throws Exception {
        DiskFileStore store = new DiskFileStore(root.toString());
        byte[] content = "id-card".getBytes(StandardCharsets.UTF_8);

        store.save("file.pdf", new ByteArrayInputStream(content), content.length);
        assertThat(store.load("file.pdf").getContentAsByteArray()).isEqualTo(content);

        store.delete("file.pdf");
        assertThat(store.load("file.pdf").exists()).isFalse();
    }

    @Test
    void rejectsKeysOutsideTheRoot() throws Exception {
        DiskFileStore store = new DiskFileStore(root.toString());

        assertThatThrownBy(() -> store.load("../secret.pdf")).isInstanceOf(FileStorageException.class);
    }
}
