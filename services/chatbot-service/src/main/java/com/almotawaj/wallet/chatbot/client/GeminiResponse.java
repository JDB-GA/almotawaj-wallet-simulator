package com.almotawaj.wallet.chatbot.client;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {
    public record Candidate(Content content, GroundingMetadata groundingMetadata) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record GroundingMetadata(List<GroundingChunk> groundingChunks) {
    }

    public record GroundingChunk(RetrievedContext retrievedContext) {
    }

    public record RetrievedContext(String title) {
    }
}
