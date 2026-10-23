package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.PaymentRequestDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.request.CreatePaymentRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.PaymentRequestResponse;
import com.almotawaj.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.service.PaymentRequestService;
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
@Tag(name = PaymentRequestDocs.TAG, description = PaymentRequestDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.WALLET_PAYMENT_REQUESTS)
@PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
@RequiredArgsConstructor
public class PaymentRequestController {
    private final PaymentRequestService requestService;

    @Operation(summary = PaymentRequestDocs.LIST, description = PaymentRequestDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = PaymentRequestDocs.LIST_OK)
    @GetMapping
    public PageResponse<PaymentRequestResponse> list(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return requestService.list(userDetails.user().getId(), pageable);
    }

    @Operation(summary = PaymentRequestDocs.CREATE, description = PaymentRequestDocs.CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = PaymentRequestDocs.CREATE_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = PaymentRequestDocs.CREATE_UNPROCESSABLE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentRequestResponse create(@AuthenticationPrincipal MyUserDetails userDetails,
                                         @Valid @RequestBody CreatePaymentRequest request) {
        return requestService.create(userDetails.user().getId(), request);
    }

    @Operation(summary = PaymentRequestDocs.PAY, description = PaymentRequestDocs.PAY_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = PaymentRequestDocs.PAY_CREATED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = PaymentRequestDocs.PAY_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = PaymentRequestDocs.PAY_UNPROCESSABLE)
    @PostMapping(ApiPaths.PAYMENT_REQUEST_PAY)
    @ResponseStatus(HttpStatus.CREATED)
    public WalletTransactionResponse pay(@AuthenticationPrincipal MyUserDetails userDetails,
                                         @PathVariable UUID requestId) {
        return requestService.pay(userDetails.user().getId(), requestId);
    }

    @Operation(summary = PaymentRequestDocs.DECLINE, description = PaymentRequestDocs.DECLINE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = PaymentRequestDocs.DECLINE_OK)
    @PostMapping(ApiPaths.PAYMENT_REQUEST_DECLINE)
    public PaymentRequestResponse decline(@AuthenticationPrincipal MyUserDetails userDetails,
                                          @PathVariable UUID requestId) {
        return requestService.decline(userDetails.user().getId(), requestId);
    }

    @Operation(summary = PaymentRequestDocs.CANCEL, description = PaymentRequestDocs.CANCEL_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = PaymentRequestDocs.CANCEL_OK)
    @PostMapping(ApiPaths.PAYMENT_REQUEST_CANCEL)
    public PaymentRequestResponse cancel(@AuthenticationPrincipal MyUserDetails userDetails,
                                         @PathVariable UUID requestId) {
        return requestService.cancel(userDetails.user().getId(), requestId);
    }
}
