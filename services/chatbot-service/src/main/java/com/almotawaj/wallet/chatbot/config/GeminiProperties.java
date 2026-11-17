package com.almotawaj.wallet.chatbot.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(ChatbotConstants.GEMINI_PROPERTIES_PREFIX)
public record GeminiProperties(
        @NotBlank String apiKey,
        @NotBlank String baseUrl,
        @NotBlank String model,
        String fileSearchStore,
        Duration timeout) {
}
