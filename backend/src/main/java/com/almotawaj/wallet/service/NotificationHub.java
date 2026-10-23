package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.NotificationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class NotificationHub {
    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(NotificationConstants.EMITTER_TIMEOUT_MS);
        emitters.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(emitter::complete);
        emitter.onError(error -> remove(userId, emitter));
        return emitter;
    }

    public void send(UUID userId, String eventName, Object payload) {
        emitters.getOrDefault(userId, List.of()).forEach(emitter ->
                deliver(userId, emitter, SseEmitter.event().name(eventName).data(payload)));
    }

    public void broadcast(String eventName, Object payload) {
        emitters.forEach((userId, userEmitters) -> userEmitters.forEach(emitter ->
                deliver(userId, emitter, SseEmitter.event().name(eventName).data(payload))));
    }

    @Scheduled(fixedRate = NotificationConstants.HEARTBEAT_INTERVAL_MS)
    public void heartbeat() {
        emitters.forEach((userId, userEmitters) -> userEmitters.forEach(emitter ->
                deliver(userId, emitter, SseEmitter.event().comment(NotificationConstants.HEARTBEAT_COMMENT))));
    }

    private void deliver(UUID userId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException | IllegalStateException e) {
            log.debug(LogMessages.SSE_DELIVERY_FAILED, userId);
            remove(userId, emitter);
        }
    }

    private void remove(UUID userId, SseEmitter emitter) {
        emitters.computeIfPresent(userId, (id, userEmitters) -> {
            userEmitters.remove(emitter);
            return userEmitters.isEmpty() ? null : userEmitters;
        });
    }
}
