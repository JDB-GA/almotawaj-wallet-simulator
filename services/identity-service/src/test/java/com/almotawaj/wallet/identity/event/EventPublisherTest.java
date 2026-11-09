package com.almotawaj.wallet.identity.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventPublisherTest {
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
    private final EventPublisher publisher = new EventPublisher(kafkaTemplate, JsonMapper.builder().build());
    private final AccountEvent event = new AccountEvent(UUID.randomUUID(), UUID.randomUUID());

    @AfterEach
    void clearTransaction() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void outsideTransaction_sendsRightAway() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(new CompletableFuture<>());

        publisher.publish("account-events", "key", event);

        verify(kafkaTemplate).send(eq("account-events"), eq("key"), any());
    }

    @Test
    void insideTransaction_sendsOnlyAfterCommit() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(new CompletableFuture<>());
        TransactionSynchronizationManager.initSynchronization();

        publisher.publish("account-events", "key", event);
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(kafkaTemplate).send(eq("account-events"), eq("key"), any());
    }
}
