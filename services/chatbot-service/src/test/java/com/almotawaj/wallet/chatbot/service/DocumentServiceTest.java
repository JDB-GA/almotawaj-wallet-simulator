package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.client.GeminiDocument;
import com.almotawaj.wallet.chatbot.client.GeminiDocumentClient;
import com.almotawaj.wallet.chatbot.exception.InvalidDocumentException;
import com.almotawaj.wallet.chatbot.model.response.DocumentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock
    private GeminiDocumentClient documentClient;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void listsDocumentsSortedWithShortIds() {
        Instant now = Instant.now();
        when(documentClient.list()).thenReturn(List.of(
                new GeminiDocument("fileSearchStores/s/documents/b", "wallet.md", 20L, "STATE_ACTIVE", now),
                new GeminiDocument("fileSearchStores/s/documents/a", "api.md", null, "STATE_PENDING", now)));

        List<DocumentResponse> result = documentService.list();

        assertThat(result).extracting(DocumentResponse::id).containsExactly("a", "b");
        assertThat(result.get(0).sizeBytes()).isZero();
    }

    @Test
    void uploadsMarkdownFile() {
        byte[] content = "# Wallet".getBytes();

        documentService.upload(new MockMultipartFile("file", "Wallet.MD", "text/markdown", content));

        verify(documentClient).upload("Wallet.MD", content);
    }

    @Test
    void rejectsUnsupportedFile() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        assertThatThrownBy(() -> documentService.upload(file)).isInstanceOf(InvalidDocumentException.class);
        verify(documentClient, never()).upload(anyString(), any());
    }
}
