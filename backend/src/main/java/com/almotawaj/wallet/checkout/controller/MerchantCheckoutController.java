package com.almotawaj.wallet.checkout.controller;

import com.almotawaj.wallet.checkout.model.CheckoutStatus;
import com.almotawaj.wallet.checkout.model.request.CreateCheckoutSessionRequest;
import com.almotawaj.wallet.checkout.model.response.CheckoutSessionResponse;
import com.almotawaj.wallet.checkout.service.CheckoutRefundService;
import com.almotawaj.wallet.checkout.service.CheckoutSessionService;
import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.GatewayDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
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
@Tag(name = GatewayDocs.PAYMENTS_TAG, description = GatewayDocs.PAYMENTS_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.MERCHANT_CHECKOUT_SESSIONS)
@PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
@RequiredArgsConstructor
public class MerchantCheckoutController {
    private final CheckoutSessionService sessionService;
    private final CheckoutRefundService refundService;

    @Operation(summary = GatewayDocs.LIST, description = GatewayDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.LIST_OK)
    @GetMapping
    public PageResponse<CheckoutSessionResponse> list(
            @AuthenticationPrincipal MyUserDetails merchant,
            @RequestParam(required = false) CheckoutStatus status,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return sessionService.list(merchant.user().getId(), status, pageable);
    }

    @Operation(summary = GatewayDocs.CREATE, description = GatewayDocs.CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = GatewayDocs.CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.CONFLICT, description = GatewayDocs.ORDER_EXISTS)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutSessionResponse create(@AuthenticationPrincipal MyUserDetails merchant,
                                          @Valid @RequestBody CreateCheckoutSessionRequest request) {
        return sessionService.create(merchant.user().getId(), request);
    }

    @Operation(summary = GatewayDocs.CANCEL, description = GatewayDocs.CANCEL_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.SESSION_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = GatewayDocs.CANCEL_UNPROCESSABLE)
    @PostMapping(ApiPaths.CHECKOUT_SESSION_CANCEL)
    public CheckoutSessionResponse cancel(@AuthenticationPrincipal MyUserDetails merchant, @PathVariable UUID sessionId) {
        return sessionService.cancel(merchant.user().getId(), sessionId);
    }

    @Operation(summary = GatewayDocs.REFUND, description = GatewayDocs.REFUND_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.SESSION_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = GatewayDocs.REFUND_UNPROCESSABLE)
    @PostMapping(ApiPaths.CHECKOUT_SESSION_REFUND)
    public CheckoutSessionResponse refund(@AuthenticationPrincipal MyUserDetails merchant, @PathVariable UUID sessionId) {
        return refundService.refund(merchant.user().getId(), sessionId);
    }
}
