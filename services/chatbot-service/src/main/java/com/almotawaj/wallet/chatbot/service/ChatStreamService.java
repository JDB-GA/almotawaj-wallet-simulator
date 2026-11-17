package com.almotawaj.wallet.chatbot.service;

import com.almotawaj.wallet.chatbot.config.ChatbotConstants;
import com.almotawaj.wallet.chatbot.exception.ChatbotUnavailableException;
import com.almotawaj.wallet.chatbot.model.event.ChatDeltaEvent;
import com.almotawaj.wallet.chatbot.model.event.ChatDoneEvent;
import com.almotawaj.wallet.chatbot.model.event.ChatErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatStreamService {
    private final ChatService chatService;
    private final TaskExecutor taskExecutor;

    public SseEmitter stream(String question) {
        SseEmitter emitter = new SseEmitter(ChatbotConstants.STREAM_TIMEOUT_MS);
        taskExecutor.execute(() -> run(question, emitter));
        return emitter;
    }

    private void run(String question, SseEmitter emitter) {
        try {
            chatService.stream(question, new ChatStreamListener() {
                @Override
                public void onDelta(String text) {
                    send(emitter, ChatbotConstants.DELTA_EVENT, new ChatDeltaEvent(text));
                }

                @Override
                public void onDone(List<String> sources) {
                    send(emitter, ChatbotConstants.DONE_EVENT, new ChatDoneEvent(sources));
                }
            });
            emitter.complete();
        } catch (ChatbotUnavailableException e) {
            send(emitter, ChatbotConstants.ERROR_EVENT, new ChatErrorEvent(ChatbotConstants.CHATBOT_UNAVAILABLE, e.getMessage()));
            emitter.complete();
        } catch (UncheckedIOException e) {
            log.warn("Chat stream closed by client: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    private static void send(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event().name(event).data(data, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
