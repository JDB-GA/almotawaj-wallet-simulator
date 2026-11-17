package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.client.GeminiDocument;
import com.almotawaj.wallet.chatbot.client.GeminiDocumentClient;
import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.exception.InvalidDocumentException;
import com.almotawaj.wallet.chatbot.model.response.DocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final GeminiDocumentClient documentClient;

    public List<DocumentResponse> list() {
        return documentClient.list().stream()
                .map(DocumentService::toResponse)
                .sorted(Comparator.comparing(DocumentResponse::name))
                .toList();
    }

    public void upload(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (file.isEmpty() || name == null || !isAllowed(name)) {
            throw new InvalidDocumentException();
        }
        try {
            documentClient.upload(name, file.getBytes());
        } catch (IOException e) {
            throw new InvalidDocumentException();
        }
    }

    public void delete(String documentId) {
        documentClient.delete(documentId);
    }

    private static boolean isAllowed(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return ChatbotConstants.ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    private static DocumentResponse toResponse(GeminiDocument document) {
        String name = document.name();
        String id = name.substring(name.lastIndexOf(ChatbotConstants.DOCUMENT_NAME_SEPARATOR) + 1);
        long size = document.sizeBytes() == null ? 0 : document.sizeBytes();
        return new DocumentResponse(id, document.displayName(), size, document.state(), document.createTime());
    }
}
