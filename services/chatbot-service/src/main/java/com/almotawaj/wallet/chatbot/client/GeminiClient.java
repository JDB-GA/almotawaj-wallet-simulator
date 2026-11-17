package com.almotawaj.wallet.chatbot.client;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.config.GeminiProperties;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final RestClient geminiRestClient;
    private final GeminiProperties properties;
    private final FileSearchStoreProvider storeProvider;

    public GeminiResponse ask(String question) {
        GeminiRequest request = new GeminiRequest(
                new GeminiRequest.Content(null, List.of(new GeminiRequest.Part(ChatbotConstants.SYSTEM_INSTRUCTION))),
                List.of(new GeminiRequest.Content(ChatbotConstants.USER_ROLE, List.of(new GeminiRequest.Part(question)))),
                List.of(new GeminiRequest.Tool(new GeminiRequest.FileSearch(List.of(storeProvider.current().orElseThrow(ChatbotUnavailableException::new))))));
        try {
            return geminiRestClient.post()
                    .uri(ChatbotConstants.GENERATE_CONTENT_PATH, properties.model())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientException e) {
            log.error("Gemini request failed: {}", e.getMessage());
            throw new ChatbotUnavailableException();
        }
    }
}
