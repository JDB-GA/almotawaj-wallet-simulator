package com.almotawaj.wallet.checkout.controller;

import com.almotawaj.wallet.checkout.model.request.CreateApiKeyRequest;
import com.almotawaj.wallet.checkout.model.response.ApiKeyCreatedResponse;
import com.almotawaj.wallet.checkout.model.response.ApiKeyResponse;
import com.almotawaj.wallet.checkout.service.MerchantApiKeyService;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.GatewayDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = GatewayDocs.API_KEYS_TAG, description = GatewayDocs.API_KEYS_TAG_DESCRIPTION)
@RequestMapping(ApiPaths.MERCHANT_API_KEYS)
@PreAuthorize(SecurityConstants.HAS_ROLE_MERCHANT)
@RequiredArgsConstructor
public class MerchantApiKeyController {
    private final MerchantApiKeyService apiKeyService;

    @Operation(summary = GatewayDocs.KEYS_LIST, description = GatewayDocs.KEYS_LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = GatewayDocs.KEYS_LIST_OK)
    @GetMapping
    public List<ApiKeyResponse> list(@AuthenticationPrincipal MyUserDetails merchant) {
        return apiKeyService.list(merchant.user().getId());
    }

    @Operation(summary = GatewayDocs.KEY_CREATE, description = GatewayDocs.KEY_CREATE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = GatewayDocs.KEY_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = GatewayDocs.KEY_LIMIT)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiKeyCreatedResponse create(@AuthenticationPrincipal MyUserDetails merchant, @Valid @RequestBody CreateApiKeyRequest request) {
        return apiKeyService.create(merchant.user().getId(), request);
    }

    @Operation(summary = GatewayDocs.KEY_REVOKE, description = GatewayDocs.KEY_REVOKE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.NO_CONTENT, description = GatewayDocs.KEY_REVOKED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = GatewayDocs.KEY_NOT_FOUND)
    @DeleteMapping(ApiPaths.API_KEY_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@AuthenticationPrincipal MyUserDetails merchant, @PathVariable UUID keyId) {
        apiKeyService.revoke(merchant.user().getId(), keyId);
    }
}
