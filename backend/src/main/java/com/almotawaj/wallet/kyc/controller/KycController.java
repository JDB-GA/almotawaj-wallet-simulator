package com.almotawaj.wallet.kyc.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.KycDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.kyc.model.request.KycSubmitRequest;
import com.almotawaj.wallet.kyc.model.response.KycApplicationResponse;
import com.almotawaj.wallet.kyc.service.KycService;
import com.almotawaj.wallet.wallet.model.DocumentContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = KycDocs.TAG, description = KycDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.KYC)
@PreAuthorize(SecurityConstants.HAS_ROLE_CLIENT)
@RequiredArgsConstructor
public class KycController {
    private final KycService kycService;

    @Operation(summary = KycDocs.MINE, description = KycDocs.MINE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycDocs.MINE_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycDocs.MINE_NOT_FOUND)
    @GetMapping(ApiPaths.ME)
    public KycApplicationResponse getMyApplication(@AuthenticationPrincipal MyUserDetails userDetails) {
        return kycService.getLatest(userDetails.user().getId());
    }

    @Operation(summary = KycDocs.SUBMIT, description = KycDocs.SUBMIT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = KycDocs.SUBMIT_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = KycDocs.SUBMIT_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = KycDocs.SUBMIT_CONFLICT)
    @ApiResponse(responseCode = ApiDocs.CONTENT_TOO_LARGE, description = KycDocs.SUBMIT_TOO_LARGE)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = KycDocs.SUBMIT_UNPROCESSABLE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public KycApplicationResponse submit(@AuthenticationPrincipal MyUserDetails userDetails,
                                         @Valid @ModelAttribute KycSubmitRequest request) {
        return kycService.submit(userDetails.user().getId(), request);
    }

    @Operation(summary = KycDocs.MY_DOCUMENT, description = KycDocs.MY_DOCUMENT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = KycDocs.DOCUMENT_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = KycDocs.DOCUMENT_NOT_FOUND)
    @GetMapping(ApiPaths.MY_KYC_DOCUMENT)
    public ResponseEntity<Resource> downloadMyDocument(@AuthenticationPrincipal MyUserDetails userDetails,
                                                       @PathVariable UUID documentId) {
        DocumentContent content = kycService.loadDocument(userDetails.user().getId(), documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
                .body(content.resource());
    }
}
