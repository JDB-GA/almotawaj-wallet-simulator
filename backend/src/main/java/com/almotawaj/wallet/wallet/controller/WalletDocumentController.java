package com.almotawaj.wallet.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.WalletDocs;
import com.almotawaj.wallet.config.security.MyUserDetails;
import com.almotawaj.wallet.wallet.model.PdfFile;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.service.TransactionDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

@RestController
@Tag(name = WalletDocs.TAG, description = WalletDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.WALLET)
@PreAuthorize(SecurityConstants.HAS_WALLET_ROLE)
@RequiredArgsConstructor
public class WalletDocumentController {
    private final TransactionDocumentService documentService;

    @Operation(summary = WalletDocs.RECEIPT, description = WalletDocs.RECEIPT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = WalletDocs.RECEIPT_OK)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = WalletDocs.RECEIPT_NOT_FOUND)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @GetMapping(ApiPaths.TRANSACTION_RECEIPT)
    public ResponseEntity<byte[]> receipt(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable UUID transactionId,
                                          Locale locale) {
        return download(documentService.receipt(userDetails.user().getId(), transactionId, locale));
    }

    @Operation(summary = WalletDocs.STATEMENT, description = WalletDocs.STATEMENT_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = WalletDocs.STATEMENT_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = WalletDocs.TRANSACTIONS_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.UNPROCESSABLE, description = WalletDocs.KYC_REQUIRED)
    @ApiResponse(responseCode = ApiDocs.TOO_MANY_REQUESTS, description = ApiDocs.RATE_LIMITED)
    @GetMapping(ApiPaths.TRANSACTIONS_STATEMENT)
    public ResponseEntity<byte[]> statement(@AuthenticationPrincipal MyUserDetails userDetails,
                                            @ParameterObject @Valid TransactionSearchRequest filter, Locale locale) {
        return download(documentService.statement(userDetails.user().getId(), filter, locale));
    }

    private static ResponseEntity<byte[]> download(PdfFile file) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.fileName()).build().toString())
                .body(file.content());
    }
}
