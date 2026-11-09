package com.almotawaj.wallet.event;

import com.almotawaj.wallet.config.constants.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public void publish(String topic, String key, Object event) {
        String payload = jsonMapper.writeValueAsString(event);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(topic, key, payload);
                }
            });
        } else {
            send(topic, key, payload);
        }
    }

    private void send(String topic, String key, String payload) {
        try {
            kafkaTemplate.send(topic, key, payload).whenComplete((result, error) -> {
                if (error != null) {
                    log.error(LogMessages.EVENT_PUBLISH_FAILED, topic, error.getMessage());
                } else {
                    log.info(LogMessages.EVENT_PUBLISHED, topic, key);
                }
            });
        } catch (RuntimeException e) {
            log.error(LogMessages.EVENT_PUBLISH_FAILED, topic, e.getMessage());
        }
    }
}
