package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.audit.config.AuditConstants;
import com.almotawaj.wallet.audit.model.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class AuditEventListener {
    private final AuditService auditService;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = AuditConstants.AUDIT_EVENTS)
    public void onAudit(String payload) {
        auditService.record(jsonMapper.readValue(payload, AuditEvent.class));
    }
}
