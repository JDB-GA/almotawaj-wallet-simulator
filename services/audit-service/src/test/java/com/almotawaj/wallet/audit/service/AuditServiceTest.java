package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.audit.model.AuditEvent;
import com.almotawaj.wallet.audit.model.AuditLog;
import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditServiceTest {
    private final AuditLogRepository repository = mock(AuditLogRepository.class);
    private final AuditService service = new AuditService(repository);
    private final AuditEvent event = new AuditEvent(UUID.randomUUID(), UUID.randomUUID(), "a@b.com", "USER_REGISTERED",
            "USER", UUID.randomUUID(), null, Instant.now());

    @Test
    void newEvent_isSaved() {
        service.record(event);

        ArgumentCaptor<AuditLog> saved = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getEventId()).isEqualTo(event.eventId());
        assertThat(saved.getValue().getActorEmail()).isEqualTo("a@b.com");
    }

    @Test
    void duplicateEvent_isIgnored() {
        when(repository.existsByEventId(event.eventId())).thenReturn(true);

        service.record(event);

        verify(repository, never()).save(any());
    }
}
