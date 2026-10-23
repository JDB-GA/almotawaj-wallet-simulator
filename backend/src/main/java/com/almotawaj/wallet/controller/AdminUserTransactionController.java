package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.config.constants.docs.UserAdminDocs;
import com.almotawaj.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.service.UserTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = UserAdminDocs.TAG, description = UserAdminDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_USERS)
@PreAuthorize(SecurityConstants.HAS_USER_MANAGE)
@RequiredArgsConstructor
public class AdminUserTransactionController {
    private final UserTransactionService userTransactionService;

    @Operation(summary = UserAdminDocs.TRANSACTIONS, description = UserAdminDocs.TRANSACTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = UserAdminDocs.TRANSACTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = UserAdminDocs.TRANSACTIONS_BAD_REQUEST)
    @ApiResponse(responseCode = ApiDocs.NOT_FOUND, description = UserAdminDocs.NOT_FOUND)
    @GetMapping(ApiPaths.USER_TRANSACTIONS)
    public PageResponse<WalletTransactionResponse> list(
            @PathVariable UUID userId,
            @ParameterObject @Valid TransactionSearchRequest filter,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return userTransactionService.list(userId, filter, pageable);
    }
}
