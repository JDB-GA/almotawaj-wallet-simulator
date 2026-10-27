package com.almotawaj.wallet.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.TransferDocs;
import com.almotawaj.wallet.config.constants.docs.WalletDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.wallet.model.request.TransferRequest;
import com.almotawaj.wallet.wallet.model.response.RecipientResponse;
import com.almotawaj.wallet.wallet.model.response.RecipientSuggestionResponse;
import com.almotawaj.wallet.wallet.model.response.TransferOptionsResponse;
import com.almotawaj.wallet.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.wallet.service.RecipientSuggestionService;
import com.almotawaj.wallet.wallet.service.TransferService;
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

@RestController
@Tag(name = TransferDocs.TAG, description = TransferDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.WALLET)
@PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final RecipientSuggestionService suggestionService;

    @Operation(summary = TransferDocs.RECIPIENT, description = TransferDocs.RECIPIENT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = TransferDocs.RECIPIENT_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = TransferDocs.RECIPIENT_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = TransferDocs.RECIPIENT_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @GetMapping(ApiPaths.RECIPIENTS)
    public RecipientResponse findRecipient(@AuthenticationPrincipal MyUserDetails userDetails, @RequestParam String query) {
        return transferService.findRecipient(userDetails.user().getId(), query);
    }

    @Operation(summary = TransferDocs.SUGGESTIONS, description = TransferDocs.SUGGESTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = TransferDocs.SUGGESTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @GetMapping(ApiPaths.RECIPIENT_SUGGESTIONS)
    public List<RecipientSuggestionResponse> suggestRecipients(@AuthenticationPrincipal MyUserDetails userDetails,
                                                               @RequestParam String query) {
        return suggestionService.suggest(userDetails.user().getId(), query);
    }

    @Operation(summary = TransferDocs.OPTIONS, description = TransferDocs.OPTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = TransferDocs.OPTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @GetMapping(ApiPaths.TRANSFER_OPTIONS)
    public TransferOptionsResponse getOptions(@AuthenticationPrincipal MyUserDetails userDetails) {
        return transferService.getOptions(userDetails.user().getId());
    }

    @Operation(summary = TransferDocs.TRANSFER, description = TransferDocs.TRANSFER_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.CREATED, description = TransferDocs.TRANSFER_CREATED)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = ApiDocs.VALIDATION_FAILED)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = TransferDocs.RECIPIENT_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = TransferDocs.TRANSFER_UNPROCESSABLE)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @PostMapping(ApiPaths.TRANSFERS)
    @ResponseStatus(HttpStatus.CREATED)
    public WalletTransactionResponse transfer(@AuthenticationPrincipal MyUserDetails userDetails,
                                              @Valid @RequestBody TransferRequest request) {
        return transferService.transfer(userDetails.user().getId(), request);
    }
}
