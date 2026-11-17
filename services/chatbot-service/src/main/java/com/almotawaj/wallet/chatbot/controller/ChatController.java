package com.almotawaj.wallet.chatbot.controller;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.model.request.ChatRequest;
import com.almotawaj.wallet.chatbot.model.response.ChatResponse;
import com.almotawaj.wallet.chatbot.service.ChatService;
import com.almotawaj.wallet.chatbot.service.ChatStreamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(ChatbotConstants.CHAT_PATH)
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatStreamService chatStreamService;

    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request) {
        return chatService.ask(request.question());
    }

    @PostMapping(path = ChatbotConstants.STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@Valid @RequestBody ChatRequest request) {
        return chatStreamService.stream(request.question());
    }
}
