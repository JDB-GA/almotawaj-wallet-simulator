package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.PayoutDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.request.CreatePayoutRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.PayoutBatchResponse;
import com.almotawaj.wallet.service.PayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = PayoutDocs.TAG, description = PayoutDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.MERCHANT_PAYOUTS)
@PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
@RequiredArgsConstructor
public class PayoutController {
    private final PayoutService payoutService;

    @Operation(summary = PayoutDocs.CREATE, description = PayoutDocs.CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.ACCEPTED, description = PayoutDocs.CREATE_ACCEPTED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = PayoutDocs.CREATE_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = PayoutDocs.CREATE_UNPROCESSABLE)
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PayoutBatchResponse create(@AuthenticationPrincipal MyUserDetails userDetails,
                                      @Valid @RequestBody CreatePayoutRequest request) {
        return payoutService.create(userDetails.user().getId(), request);
    }

    @Operation(summary = PayoutDocs.LIST, description = PayoutDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = PayoutDocs.LIST_OK)
    @GetMapping
    public PageResponse<PayoutBatchResponse> list(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return payoutService.list(userDetails.user().getId(), pageable);
    }

    @Operation(summary = PayoutDocs.GET, description = PayoutDocs.GET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = PayoutDocs.GET_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = PayoutDocs.GET_NOT_FOUND)
    @GetMapping(ApiPaths.PAYOUT_BY_ID)
    public PayoutBatchResponse get(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable UUID batchId) {
        return payoutService.get(userDetails.user().getId(), batchId);
    }
}
