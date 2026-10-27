package com.almotawaj.wallet.statistics.controller;

import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.config.constants.docs.AdminStatisticsDocs;
import com.almotawaj.wallet.config.constants.docs.ApiDocs;
import com.almotawaj.wallet.statistics.model.response.TransactionStatisticsResponse;
import com.almotawaj.wallet.statistics.model.response.UserStatisticsResponse;
import com.almotawaj.wallet.statistics.service.AdminStatisticsService;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = AdminStatisticsDocs.TAG, description = AdminStatisticsDocs.TAG_DESCRIPTION)
@RequestMapping(ApiPaths.ADMIN_STATISTICS)
@PreAuthorize(SecurityConstants.HAS_STATISTICS_VIEW)
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final AdminStatisticsService statisticsService;

    @Operation(summary = AdminStatisticsDocs.USERS, description = AdminStatisticsDocs.USERS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AdminStatisticsDocs.USERS_OK)
    @GetMapping(ApiPaths.USER_STATISTICS)
    public UserStatisticsResponse getUserStatistics() {
        return statisticsService.getUserStatistics();
    }

    @Operation(summary = AdminStatisticsDocs.TRANSACTIONS, description = AdminStatisticsDocs.TRANSACTIONS_DESCRIPTION)
    @ApiResponse(responseCode = ApiDocs.OK, description = AdminStatisticsDocs.TRANSACTIONS_OK)
    @ApiResponse(responseCode = ApiDocs.BAD_REQUEST, description = AdminStatisticsDocs.BAD_REQUEST)
    @GetMapping(ApiPaths.TRANSACTION_STATISTICS)
    public TransactionStatisticsResponse getTransactionStatistics(@ParameterObject @Valid TransactionSearchRequest filter) {
        return statisticsService.getTransactionStatistics(filter);
    }
}
