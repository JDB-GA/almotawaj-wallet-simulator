package com.almotawaj.wallet.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.SortFields;
import com.almotawaj.wallet.config.constants.docs.AdminStatisticsDocs;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.model.response.AdminTransactionResponse;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.service.AdminStatisticsService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = AdminStatisticsDocs.TAG, description = AdminStatisticsDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_TRANSACTIONS)
@PreAuthorize(SecurityConstants.HAS_STATISTICS_VIEW)
@RequiredArgsConstructor
public class AdminTransactionController {
    private final AdminStatisticsService statisticsService;

    @Operation(summary = AdminStatisticsDocs.LIST, description = AdminStatisticsDocs.LIST_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AdminStatisticsDocs.LIST_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = AdminStatisticsDocs.BAD_REQUEST)
    @GetMapping
    public PageResponse<AdminTransactionResponse> list(
            @ParameterObject @Valid TransactionSearchRequest filter,
            @PageableDefault(sort = SortFields.CREATED_AT, direction = Sort.Direction.DESC) Pageable pageable) {
        return statisticsService.listTransactions(filter, pageable);
    }
}
