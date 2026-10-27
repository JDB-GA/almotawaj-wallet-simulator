package com.almotawaj.wallet.kyc.controller;

import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.KycDocs;
import com.almotawaj.wallet.config.constants.docs.KycReviewDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.kyc.model.request.KycRejectRequest;
import com.almotawaj.wallet.kyc.model.response.KycApplicationSummaryResponse;
import com.almotawaj.wallet.kyc.model.response.KycReviewResponse;
import com.almotawaj.wallet.kyc.service.KycReviewService;
import com.almotawaj.wallet.wallet.model.DocumentContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = KycReviewDocs.TAG, description = KycReviewDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_KYC)
@PreAuthorize(SecurityConstants.HAS_KYC_REVIEW)
@RequiredArgsConstructor
public class AdminKycController {
    private final KycReviewService kycReviewService;

    @Operation(summary = KycReviewDocs.LIST, description = KycReviewDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycReviewDocs.LIST_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = KycReviewDocs.LIST_BAD_REQUEST)
    @GetMapping
    public PageResponse<KycApplicationSummaryResponse> list(
            @RequestParam(required = false) KycApplicationStatus status,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return kycReviewService.list(status, pageable);
    }

    @Operation(summary = KycReviewDocs.GET, description = KycReviewDocs.GET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycReviewDocs.GET_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycReviewDocs.NOT_FOUND)
    @GetMapping(ApiPaths.KYC_APPLICATION)
    public KycReviewResponse get(@PathVariable UUID applicationId) {
        return kycReviewService.get(applicationId);
    }

    @Operation(summary = KycReviewDocs.DOCUMENT, description = KycReviewDocs.DOCUMENT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycDocs.DOCUMENT_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycDocs.DOCUMENT_NOT_FOUND)
    @GetMapping(ApiPaths.KYC_APPLICATION_DOCUMENT)
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID applicationId, @PathVariable UUID documentId) {
        DocumentContent content = kycReviewService.loadDocument(applicationId, documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
                .body(content.resource());
    }

    @Operation(summary = KycReviewDocs.APPROVE, description = KycReviewDocs.APPROVE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycReviewDocs.DECISION_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycReviewDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = KycReviewDocs.ALREADY_REVIEWED)
    @PostMapping(ApiPaths.KYC_APPROVE)
    public KycReviewResponse approve(@AuthenticationPrincipal MyUserDetails reviewer, @PathVariable UUID applicationId) {
        return kycReviewService.approve(applicationId, reviewer.user().getId());
    }

    @Operation(summary = KycReviewDocs.REJECT, description = KycReviewDocs.REJECT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycReviewDocs.DECISION_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycReviewDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = KycReviewDocs.ALREADY_REVIEWED)
    @PostMapping(ApiPaths.KYC_REJECT)
    public KycReviewResponse reject(@AuthenticationPrincipal MyUserDetails reviewer, @PathVariable UUID applicationId,
                                    @Valid @RequestBody KycRejectRequest request) {
        return kycReviewService.reject(applicationId, reviewer.user().getId(), request.reason());
    }
}
