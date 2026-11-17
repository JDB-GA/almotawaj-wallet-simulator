package com.almotawaj.wallet.chatbot.client;

import java.util.List;

public record GeminiRequest(Content systemInstruction, List<Content> contents, List<Tool> tools) {
    public record Content(String role, List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record Tool(FileSearch fileSearch) {
    }

    public record FileSearch(List<String> fileSearchStoreNames) {
    }
}
