package com.almotawaj.wallet.chatbot.client;

import java.time.Instant;

public record GeminiDocument(String name, String displayName, Long sizeBytes, String state, Instant createTime) {
}
