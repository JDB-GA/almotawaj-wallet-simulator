package com.almotawaj.wallet.chatbot.model.request;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = ChatbotConstants.QUESTION_REQUIRED)
        @Size(max = ChatbotConstants.QUESTION_MAX, message = ChatbotConstants.QUESTION_TOO_LONG)
        String question) {
}
