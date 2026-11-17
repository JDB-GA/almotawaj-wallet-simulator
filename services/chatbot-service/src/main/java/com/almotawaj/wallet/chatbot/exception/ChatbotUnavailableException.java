package com.almotawaj.wallet.chatbot.exception;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;

public class ChatbotUnavailableException extends RuntimeException {
    public ChatbotUnavailableException() {
        super(ChatbotConstants.CHATBOT_UNAVAILABLE_MESSAGE);
    }
}
