package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.client.GeminiClient;
import com.almotawaj.wallet.chatbot.client.GeminiResponse;
import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import com.almotawaj.wallet.chatbot.model.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public void stream(String question, ChatStreamListener listener) {
        Set<String> sources = new LinkedHashSet<>();
        boolean[] answered = {false};
        geminiClient.stream(question.strip(), chunk -> {
            if (chunk == null || chunk.candidates() == null || chunk.candidates().isEmpty()) {
                return;
            }
            GeminiResponse.Candidate candidate = chunk.candidates().get(0);
            String text = text(candidate);
            if (!text.isEmpty()) {
                answered[0] = true;
                listener.onDelta(text);
            }
            sources.addAll(sources(candidate));
        });
        if (!answered[0]) {
            throw new ChatbotUnavailableException();
        }
        listener.onDone(sources.stream().limit(ChatbotConstants.SOURCES_MAX).toList());
    }

    private static String text(GeminiResponse.Candidate candidate) {
        if (candidate.content() == null || candidate.content().parts() == null) {
            return "";
        }
        return candidate.content().parts().stream()
                .map(GeminiResponse.Part::text)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());
    }

    private static GeminiResponse.Candidate firstCandidate(GeminiResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new ChatbotUnavailableException();
        }
        return response.candidates().get(0);
    }

    private static String answer(GeminiResponse.Candidate candidate) {
        String text = text(candidate);
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
