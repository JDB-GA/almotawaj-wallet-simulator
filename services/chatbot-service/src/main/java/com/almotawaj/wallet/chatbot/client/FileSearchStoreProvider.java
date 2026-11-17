package com.almotawaj.wallet.chatbot.client;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.config.GeminiProperties;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Component
public class FileSearchStoreProvider {
    private final RestClient geminiRestClient;
    private volatile String store;

    public FileSearchStoreProvider(RestClient geminiRestClient, GeminiProperties properties) {
        this.geminiRestClient = geminiRestClient;
        this.store = StringUtils.hasText(properties.fileSearchStore()) ? properties.fileSearchStore() : null;
    }

    public Optional<String> current() {
        return Optional.ofNullable(store);
    }

    public synchronized String getOrCreate() {
        if (store != null) {
            return store;
        }
        try {
            GeminiStore created = geminiRestClient.post()
                    .uri(ChatbotConstants.STORES_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GeminiStore(null, ChatbotConstants.STORE_DISPLAY_NAME))
                    .retrieve()
                    .body(GeminiStore.class);
            if (created == null || created.name() == null) {
                throw new ChatbotUnavailableException();
            }
            store = created.name();
            log.warn("Created file search store. Save it in .env: GEMINI_FILE_SEARCH_STORE={}", store);
            return store;
        } catch (RestClientException e) {
            log.error("Gemini store creation failed: {}", e.getMessage());
            throw new ChatbotUnavailableException();
        }
    }
}
