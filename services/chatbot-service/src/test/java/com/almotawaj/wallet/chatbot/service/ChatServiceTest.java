package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.client.GeminiClient;
import com.almotawaj.wallet.chatbot.client.GeminiResponse;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import com.almotawaj.wallet.chatbot.model.response.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private GeminiClient geminiClient;

    @InjectMocks
    private ChatService chatService;

    @Test
    void returnsAnswerAndDistinctSources() {
        GeminiResponse response = new GeminiResponse(List.of(new GeminiResponse.Candidate(
                new GeminiResponse.Content(List.of(new GeminiResponse.Part("Open Request, "), new GeminiResponse.Part("enter the amount."))),
                new GeminiResponse.GroundingMetadata(List.of(
                        chunk("api.md"), chunk("api.md"), chunk("services.md"), new GeminiResponse.GroundingChunk(null))))));
        when(geminiClient.ask("How do I request money?")).thenReturn(response);

        ChatResponse result = chatService.ask("  How do I request money?  ");

        assertThat(result.answer()).isEqualTo("Open Request, enter the amount.");
        assertThat(result.sources()).containsExactly("api.md", "services.md");
    }

    @Test
    void returnsNoSourcesWhenNotGrounded() {
        GeminiResponse response = new GeminiResponse(List.of(new GeminiResponse.Candidate(
                new GeminiResponse.Content(List.of(new GeminiResponse.Part("I don't know."))), null)));
        when(geminiClient.ask("Weather?")).thenReturn(response);

        assertThat(chatService.ask("Weather?").sources()).isEmpty();
    }

    @Test
    void failsWhenGeminiReturnsNoCandidates() {
        when(geminiClient.ask("Hi")).thenReturn(new GeminiResponse(List.of()));

        assertThatThrownBy(() -> chatService.ask("Hi")).isInstanceOf(ChatbotUnavailableException.class);
    }

    private static GeminiResponse.GroundingChunk chunk(String title) {
        return new GeminiResponse.GroundingChunk(new GeminiResponse.RetrievedContext(title));
    }
}
