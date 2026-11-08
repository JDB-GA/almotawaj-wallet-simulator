package com.almotawaj.wallet.statistics.service;

import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.statistics.model.response.TransactionStatisticsResponse;
import com.almotawaj.wallet.statistics.model.response.UserStatisticsResponse;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.repository.UserRepository;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);

    private final UserRepository userRepository;
    private final WalletTransactionRepository transactionRepository;
    private final TransactionTotalsRepository totalsRepository;

    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        Map<UserRole, Long> usersByRole = new EnumMap<>(UserRole.class);
        for (UserRole role : UserRole.values()) {
            usersByRole.put(role, 0L);
        }
        userRepository.countByRole().forEach(count -> usersByRole.put(count.role(), count.total()));
        long totalUsers = usersByRole.values().stream().mapToLong(Long::longValue).sum();
        return new UserStatisticsResponse(totalUsers, usersByRole);
    }

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
