package com.almotawaj.wallet.chatbot.exception;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;

public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException() {
        super(ChatbotConstants.INVALID_DOCUMENT_MESSAGE);
    }
}
