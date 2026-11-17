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
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final RestClient geminiRestClient;
    private final GeminiProperties properties;
    private final FileSearchStoreProvider storeProvider;
    private final JsonMapper jsonMapper;

    public GeminiResponse ask(String question) {
        try {
            return geminiRestClient.post()
                    .uri(ChatbotConstants.GENERATE_CONTENT_PATH, properties.model())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request(question))
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientException e) {
            log.error("Gemini request failed: {}", e.getMessage());
            throw new ChatbotUnavailableException();
        }
    }

    public void stream(String question, Consumer<GeminiResponse> onChunk) {
        GeminiRequest request = request(question);
        try {
            geminiRestClient.post()
                    .uri(ChatbotConstants.STREAM_CONTENT_PATH, properties.model())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .exchange((clientRequest, response) -> {
                        if (response.getStatusCode().isError()) {
                            log.error("Gemini stream failed: {}", response.getStatusCode());
                            throw new ChatbotUnavailableException();
                        }
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith(ChatbotConstants.SSE_DATA_PREFIX)) {
                                    onChunk.accept(jsonMapper.readValue(line.substring(ChatbotConstants.SSE_DATA_PREFIX.length()).strip(), GeminiResponse.class));
                                }
                            }
                        }
                        return null;
                    });
        } catch (RestClientException | JacksonException e) {
            log.error("Gemini stream failed: {}", e.getMessage());
            throw new ChatbotUnavailableException();
        }
    }

    private GeminiRequest request(String question) {
        return new GeminiRequest(
                new GeminiRequest.Content(null, List.of(new GeminiRequest.Part(ChatbotConstants.SYSTEM_INSTRUCTION))),
                List.of(new GeminiRequest.Content(ChatbotConstants.USER_ROLE, List.of(new GeminiRequest.Part(question)))),
                List.of(new GeminiRequest.Tool(new GeminiRequest.FileSearch(List.of(storeProvider.current().orElseThrow(ChatbotUnavailableException::new))))));
    }
}
