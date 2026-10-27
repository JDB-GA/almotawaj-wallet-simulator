package com.almotawaj.wallet.wallet.controller;

import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.WalletDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.wallet.model.request.TopUpRequest;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.model.response.TopUpOptionsResponse;
import com.almotawaj.wallet.wallet.model.response.WalletResponse;
import com.almotawaj.wallet.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = WalletDocs.TAG, description = WalletDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.WALLET)
@PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @Operation(summary = WalletDocs.MINE, description = WalletDocs.MINE_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = WalletDocs.MINE_OK)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @GetMapping
    public WalletResponse getMine(@AuthenticationPrincipal MyUserDetails userDetails) {
        return walletService.getMine(userDetails.user().getId());
    }

    @Operation(summary = WalletDocs.TRANSACTIONS, description = WalletDocs.TRANSACTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = WalletDocs.TRANSACTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = WalletDocs.TRANSACTIONS_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @GetMapping(ApiPaths.TRANSACTIONS)
    public PageResponse<WalletTransactionResponse> listTransactions(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @ParameterObject @Valid TransactionSearchRequest filter,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return walletService.listTransactions(userDetails.user().getId(), filter, pageable);
    }

    @Operation(summary = WalletDocs.TOP_UP_OPTIONS, description = WalletDocs.TOP_UP_OPTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = WalletDocs.TOP_UP_OPTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @GetMapping(ApiPaths.TOP_UP_OPTIONS)
    public TopUpOptionsResponse getTopUpOptions(@AuthenticationPrincipal MyUserDetails userDetails) {
        return walletService.getTopUpOptions(userDetails.user().getId());
    }

    @Operation(summary = WalletDocs.TOP_UP, description = WalletDocs.TOP_UP_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = WalletDocs.TOP_UP_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.TOP_UP_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.TOP_UPS)
    @ResponseStatus(HttpStatus.CREATED)
    public WalletTransactionResponse topUp(@AuthenticationPrincipal MyUserDetails userDetails,
                                           @Valid @RequestBody TopUpRequest request) {
        return walletService.topUp(userDetails.user().getId(), request);
    }
}
