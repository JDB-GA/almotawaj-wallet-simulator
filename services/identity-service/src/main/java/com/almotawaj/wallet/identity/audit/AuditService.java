package com.almotawaj.wallet.identity.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Records audit events. The audit log is owned by audit-service; until the Kafka
 * publisher is added (Day 3) events are only logged here.
 */
@Slf4j
@Service
public class AuditService {

    public void record(UUID actorId, AuditAction action, AuditTargetType targetType, UUID targetId, String details) {
        log.info("audit action={} actor={} targetType={} target={} details={}", action, actorId, targetType, targetId, details);
    }
}
