package com.almotawaj.wallet.chatbot.client;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiDocumentClient {
    private final RestClient geminiRestClient;
    private final FileSearchStoreProvider storeProvider;

    public List<GeminiDocument> list() {
        Optional<String> store = storeProvider.current();
        if (store.isEmpty()) {
            return List.of();
        }
        return call(() -> Optional.ofNullable(geminiRestClient.get()
                        .uri(ChatbotConstants.LIST_DOCUMENTS_PATH.formatted(store.get()), ChatbotConstants.DOCUMENTS_PAGE_SIZE)
                        .retrieve()
                        .body(GeminiDocumentPage.class))
                .map(GeminiDocumentPage::documents)
                .orElse(List.of()));
    }

    public void upload(String displayName, byte[] content) {
        call(() -> {
            String uploadUrl = geminiRestClient.post()
                    .uri(ChatbotConstants.UPLOAD_PATH.formatted(storeProvider.getOrCreate()))
                    .header(ChatbotConstants.UPLOAD_PROTOCOL_HEADER, ChatbotConstants.UPLOAD_PROTOCOL)
                    .header(ChatbotConstants.UPLOAD_COMMAND_HEADER, ChatbotConstants.UPLOAD_START)
                    .header(ChatbotConstants.UPLOAD_LENGTH_HEADER, String.valueOf(content.length))
                    .header(ChatbotConstants.UPLOAD_TYPE_HEADER, ChatbotConstants.DOCUMENT_MIME_TYPE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GeminiUploadMetadata(displayName, ChatbotConstants.DOCUMENT_MIME_TYPE))
                    .retrieve()
                    .toBodilessEntity()
                    .getHeaders()
                    .getFirst(ChatbotConstants.UPLOAD_URL_HEADER);
            if (uploadUrl == null) {
                throw new ChatbotUnavailableException();
            }
            return geminiRestClient.post()
                    .uri(URI.create(uploadUrl))
                    .header(ChatbotConstants.UPLOAD_OFFSET_HEADER, ChatbotConstants.UPLOAD_OFFSET)
                    .header(ChatbotConstants.UPLOAD_COMMAND_HEADER, ChatbotConstants.UPLOAD_FINISH)
                    .body(content)
                    .retrieve()
                    .toBodilessEntity();
        });
    }

    public void delete(String documentId) {
        call(() -> geminiRestClient.delete()
                .uri(ChatbotConstants.DOCUMENT_PATH.formatted(storeProvider.current().orElseThrow(ChatbotUnavailableException::new)), documentId)
                .retrieve()
                .toBodilessEntity());
    }

    private static <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientException e) {
            log.error("Gemini document request failed: {}", e.getMessage());
            throw new ChatbotUnavailableException();
        }
    }
}
