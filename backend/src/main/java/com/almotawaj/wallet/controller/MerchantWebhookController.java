package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.GatewayDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.model.request.UpdateWebhookRequest;
import com.almotawaj.wallet.model.response.WebhookResponse;
import com.almotawaj.wallet.service.MerchantWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = GatewayDocs.WEBHOOK_TAG, description = GatewayDocs.WEBHOOK_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.MERCHANT_WEBHOOK)
@PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
@RequiredArgsConstructor
public class MerchantWebhookController {
    private final MerchantWebhookService webhookService;

    @Operation(summary = GatewayDocs.WEBHOOK_GET, description = GatewayDocs.WEBHOOK_GET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.WEBHOOK_OK)
    @GetMapping
    public WebhookResponse get(@AuthenticationPrincipal MyUserDetails merchant) {
        return webhookService.get(merchant.user().getId());
    }

    @Operation(summary = GatewayDocs.WEBHOOK_SET, description = GatewayDocs.WEBHOOK_SET_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.WEBHOOK_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = GatewayDocs.WEBHOOK_UNPROCESSABLE)
    @PutMapping
    public WebhookResponse set(@AuthenticationPrincipal MyUserDetails merchant, @Valid @RequestBody UpdateWebhookRequest request) {
        return webhookService.set(merchant.user().getId(), request);
    }

    @Operation(summary = GatewayDocs.WEBHOOK_DELETE)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = GatewayDocs.WEBHOOK_DELETED)
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal MyUserDetails merchant) {
        webhookService.remove(merchant.user().getId());
    }
}
