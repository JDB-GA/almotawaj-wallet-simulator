package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.audit.common.PageResponse;
import com.almotawaj.wallet.audit.config.AuditConstants;
import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditEvent;
import com.almotawaj.wallet.audit.model.AuditLog;
import com.almotawaj.wallet.audit.model.response.AuditLogResponse;
import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void record(AuditEvent event) {
        if (auditLogRepository.existsByEventId(event.eventId())) {
            return;
        }
        AuditLog log = new AuditLog();
        log.setEventId(event.eventId());
        log.setActorId(event.actorId());
        log.setActorEmail(event.actorEmail());
        log.setAction(event.action());
        log.setTargetType(event.targetType());
        log.setTargetId(event.targetId());
        log.setDetails(event.details() == null ? null : truncate(event.details()));
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(AuditAction action, UUID targetId, Pageable pageable) {
        Page<AuditLog> page;
        if (action != null) {
            page = auditLogRepository.findByAction(action.name(), pageable);
        } else if (targetId != null) {
            page = auditLogRepository.findByTargetId(targetId, pageable);
        } else {
            page = auditLogRepository.findAll(pageable);
        }
        return PageResponse.from(page, AuditLogResponse::from);
    }

    private static String truncate(String details) {
        return details.length() <= AuditConstants.DETAILS_MAX
                ? details
                : details.substring(0, AuditConstants.DETAILS_MAX);
    }
}
