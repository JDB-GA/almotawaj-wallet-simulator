package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.VoucherDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.VoucherPurchaseResponse;
import com.almotawaj.wallet.model.response.VoucherResponse;
import com.almotawaj.wallet.service.IdempotentVoucherPurchaseService;
import com.almotawaj.wallet.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
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
@Tag(name = VoucherDocs.TAG, description = VoucherDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.VOUCHERS)
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;
    private final IdempotentVoucherPurchaseService purchaseService;

    @Operation(summary = VoucherDocs.LIST_AVAILABLE, description = VoucherDocs.LIST_AVAILABLE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = VoucherDocs.LIST_AVAILABLE_OK)
    @GetMapping
    @PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
    public PageResponse<VoucherResponse> listAvailable(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return voucherService.listAvailable(pageable);
    }

    @Operation(summary = VoucherDocs.PURCHASE, description = VoucherDocs.PURCHASE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = VoucherDocs.PURCHASE_CREATED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = VoucherDocs.PURCHASE_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = VoucherDocs.PURCHASE_CONFLICT)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = VoucherDocs.PURCHASE_UNPROCESSABLE)
    @PostMapping(ApiPaths.VOUCHER_PURCHASE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityConstants.HAS_ROLE_CLIENT)
    public VoucherPurchaseResponse purchase(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable UUID voucherId,
            @Parameter(description = VoucherDocs.IDEMPOTENCY_KEY)
            @RequestHeader(name = WalletConstants.IDEMPOTENCY_HEADER, required = false)
            @Size(max = ValidationLimits.IDEMPOTENCY_KEY_MAX, message = ErrorMessages.IDEMPOTENCY_KEY_TOO_LONG) String idempotencyKey) {
        return purchaseService.purchase(userDetails.user().getId(), voucherId, idempotencyKey);
    }

    @Operation(summary = VoucherDocs.LIST_PURCHASES, description = VoucherDocs.LIST_PURCHASES_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = VoucherDocs.LIST_PURCHASES_OK)
    @GetMapping(ApiPaths.VOUCHER_PURCHASES)
    @PreAuthorize(SecurityConstants.HAS_ROLE_CLIENT)
    public PageResponse<VoucherPurchaseResponse> listPurchases(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return voucherService.listPurchases(userDetails.user().getId(), pageable);
    }
}
