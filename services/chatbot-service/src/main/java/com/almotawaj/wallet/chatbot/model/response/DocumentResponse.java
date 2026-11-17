package com.almotawaj.wallet.chatbot.model.response;

import java.time.Instant;

public record DocumentResponse(String id, String name, long sizeBytes, String state, Instant createdAt) {
}
