package com.almotawaj.wallet.checkout.controller;

import com.almotawaj.wallet.checkout.model.request.PayCheckoutRequest;
import com.almotawaj.wallet.checkout.model.response.CheckoutViewResponse;
import com.almotawaj.wallet.checkout.service.CheckoutPaymentService;
import com.almotawaj.wallet.checkout.service.CheckoutSessionService;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.GatewayDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.voucher.model.response.UsableVoucherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = GatewayDocs.CHECKOUT_TAG, description = GatewayDocs.CHECKOUT_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.CHECKOUT)
@PreAuthorize(SecurityConstants.HAS_ROLE_CLIENT)
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutSessionService sessionService;
    private final CheckoutPaymentService paymentService;

    @Operation(summary = GatewayDocs.VIEW, description = GatewayDocs.VIEW_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.VIEW_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.NOT_FOUND)
    @GetMapping(ApiPaths.CHECKOUT_SESSION)
    public CheckoutViewResponse view(@PathVariable UUID sessionId) {
        return sessionService.view(sessionId);
    }

    @Operation(summary = GatewayDocs.VOUCHERS, description = GatewayDocs.VOUCHERS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.VOUCHERS_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.NOT_FOUND)
    @GetMapping(ApiPaths.CHECKOUT_SESSION_VOUCHERS)
    public List<UsableVoucherResponse> vouchers(@AuthenticationPrincipal MyUserDetails client, @PathVariable UUID sessionId) {
        return paymentService.usableVouchers(client.user().getId(), sessionId);
    }

    @Operation(summary = GatewayDocs.PAY, description = GatewayDocs.PAY_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.PAY_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = GatewayDocs.PAY_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.CHECKOUT_SESSION_PAY)
    public CheckoutViewResponse pay(@AuthenticationPrincipal MyUserDetails client, @PathVariable UUID sessionId,
                                    @RequestBody(required = false) PayCheckoutRequest request) {
        return paymentService.pay(client.user().getId(), sessionId, request == null ? null : request.voucherId());
    }
}
