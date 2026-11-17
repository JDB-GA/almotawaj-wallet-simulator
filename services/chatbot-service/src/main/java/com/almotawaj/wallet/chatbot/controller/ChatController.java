package com.almotawaj.wallet.chatbot.controller;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.model.request.ChatRequest;
import com.almotawaj.wallet.chatbot.model.response.ChatResponse;
import com.almotawaj.wallet.chatbot.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ChatbotConstants.CHAT_PATH)
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request) {
        return chatService.ask(request.question());
    }
}
