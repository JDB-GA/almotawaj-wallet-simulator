package com.almotawaj.wallet.statistics.service;

import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.statistics.model.response.TransactionStatisticsResponse;
import com.almotawaj.wallet.wallet.model.DirectionTotal;
import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.request.TransactionSearchRequest;
import com.almotawaj.wallet.wallet.model.response.AdminTransactionResponse;
import com.almotawaj.wallet.wallet.repository.TransactionTotalsRepository;
import com.almotawaj.wallet.wallet.repository.WalletTransactionRepository;
import com.almotawaj.wallet.wallet.repository.specification.WalletTransactionSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);

    private final WalletTransactionRepository transactionRepository;
    private final TransactionTotalsRepository totalsRepository;

    @Transactional(readOnly = true)
    public TransactionStatisticsResponse getTransactionStatistics(TransactionSearchRequest filter) {
        List<DirectionTotal> totals = totalsRepository.totalsByDirection(WalletTransactionSpecifications.forSystem(filter, ZONE));
        long totalTransactions = totals.stream().mapToLong(DirectionTotal::count).sum();
        return new TransactionStatisticsResponse(totalTransactions, DirectionTotal.amountOf(totals, TransactionDirection.CREDIT),
                DirectionTotal.amountOf(totals, TransactionDirection.DEBIT));
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminTransactionResponse> listTransactions(TransactionSearchRequest filter, Pageable pageable) {
        return PageResponse.from(
                transactionRepository.findAll(WalletTransactionSpecifications.forSystem(filter, ZONE), pageable),
                AdminTransactionResponse::from);
    }
}
