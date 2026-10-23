package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.KycReviewDocs;
import com.almotawaj.wallet.config.constants.docs.SystemDocs;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.response.AuditLogResponse;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = SystemDocs.AUDIT_TAG, description = SystemDocs.AUDIT_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_AUDIT_LOGS)
@PreAuthorize(SecurityConstants.HAS_ROLE_SUPER_ADMIN)
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditService auditService;

    @Operation(summary = SystemDocs.AUDIT_LIST, description = SystemDocs.AUDIT_LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = SystemDocs.AUDIT_LIST_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = KycReviewDocs.LIST_BAD_REQUEST)
    @GetMapping
    public PageResponse<AuditLogResponse> list(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) UUID targetId,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.list(action, targetId, pageable);
    }
}
