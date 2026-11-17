package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.client.GeminiClient;
import com.almotawaj.wallet.chatbot.client.GeminiResponse;
import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import com.almotawaj.wallet.chatbot.model.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final GeminiClient geminiClient;

    public ChatResponse ask(String question) {
        GeminiResponse response = geminiClient.ask(question.strip());
        GeminiResponse.Candidate candidate = firstCandidate(response);
        return new ChatResponse(answer(candidate), sources(candidate));
    }

    private static GeminiResponse.Candidate firstCandidate(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new ChatbotUnavailableException();
        }
        return response.candidates().get(0);
    }

    private static String answer(GeminiResponse.Candidate candidate) {
        if (candidate.content() == null || candidate.content().parts() == null) {
            throw new ChatbotUnavailableException();
        }
        String text = candidate.content().parts().stream()
                .map(GeminiResponse.Part::text)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());
        if (text.isBlank()) {
            throw new ChatbotUnavailableException();
        }
        return text.strip();
    }

    private static List<String> sources(GeminiResponse.Candidate candidate) {
        if (candidate.groundingMetadata() == null || candidate.groundingMetadata().groundingChunks() == null) {
            return List.of();
        }
        return candidate.groundingMetadata().groundingChunks().stream()
                .map(GeminiResponse.GroundingChunk::retrievedContext)
                .filter(Objects::nonNull)
                .map(GeminiResponse.RetrievedContext::title)
                .filter(Objects::nonNull)
                .distinct()
                .limit(ChatbotConstants.SOURCES_MAX)
                .toList();
    }
}
