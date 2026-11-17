package com.almotawaj.wallet.chatbot.model.response;

import java.util.List;

public record ChatResponse(String answer, List<String> sources) {
}
