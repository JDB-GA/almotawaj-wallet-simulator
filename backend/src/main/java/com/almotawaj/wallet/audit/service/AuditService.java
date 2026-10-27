package com.almotawaj.wallet.audit.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditLog;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.model.response.AuditLogResponse;
import com.almotawaj.wallet.audit.repository.AuditLogRepository;
import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public void record(UUID actorId, AuditAction action, AuditTargetType targetType, UUID targetId, String details) {
        AuditLog log = new AuditLog();
        log.setActor(actorId == null ? null : userRepository.getReferenceById(actorId));
        log.setAction(action.name());
        log.setTargetType(targetType.name());
        log.setTargetId(targetId);
        log.setDetails(details == null ? null : truncate(details));
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
            page = auditLogRepository.findAllBy(pageable);
        }
        return PageResponse.from(page, AuditLogResponse::from);
    }

    private static String truncate(String details) {
        return details.length() <= ValidationLimits.AUDIT_DETAILS_MAX
                ? details
                : details.substring(0, ValidationLimits.AUDIT_DETAILS_MAX);
    }
}
