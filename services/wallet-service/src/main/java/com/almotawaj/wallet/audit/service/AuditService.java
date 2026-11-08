package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuditService {

    public void record(UUID actorId, AuditAction action, AuditTargetType targetType, UUID targetId, String details) {
        log.info("audit action={} actor={} targetType={} target={} details={}", action, actorId, targetType, targetId, details);
    }
}
