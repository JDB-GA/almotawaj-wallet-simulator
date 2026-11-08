package com.almotawaj.wallet.audit.controller;

import com.almotawaj.wallet.audit.common.PageResponse;
import com.almotawaj.wallet.audit.config.AuditConstants;
import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.response.AuditLogResponse;
import com.almotawaj.wallet.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(AuditConstants.AUDIT_LOGS_PATH)
@PreAuthorize(AuditConstants.HAS_ROLE_SUPER_ADMIN)
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditService auditService;

    @GetMapping
    public PageResponse<AuditLogResponse> list(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) UUID targetId,
            @PageableDefault(sort = AuditConstants.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.list(action, targetId, pageable);
    }
}
