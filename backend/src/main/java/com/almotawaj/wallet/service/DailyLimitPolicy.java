package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.model.TransactionType;
import com.almotawaj.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DailyLimitPolicy {
    public static final BigDecimal TOP_UP_LIMIT = new BigDecimal(WalletLimits.DAILY_TOP_UP_LIMIT);
    public static final BigDecimal TRANSFER_LIMIT = new BigDecimal(WalletLimits.DAILY_TRANSFER_LIMIT);
    private static final BigDecimal CHECKOUT_LIMIT = new BigDecimal(WalletLimits.DAILY_CHECKOUT_LIMIT);
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);

    private final WalletTransactionRepository transactionRepository;
    private final Clock clock;

    public BigDecimal remainingTopUp(UUID walletId) {
        return remaining(walletId, TransactionType.TOP_UP, TOP_UP_LIMIT);
    }

    public BigDecimal remainingTransfer(UUID walletId) {
        return remaining(walletId, TransactionType.TRANSFER_OUT, TRANSFER_LIMIT);
    }

    public void ensureTopUpAllowed(UUID walletId, BigDecimal amount) {
        if (amount.compareTo(remainingTopUp(walletId)) > 0) {
            throw new BusinessRuleException(ErrorMessages.DAILY_TOP_UP_LIMIT_EXCEEDED, ErrorCodes.DAILY_TOP_UP_LIMIT_EXCEEDED);
        }
    }

    public void ensureTransferAllowed(UUID walletId, BigDecimal amount) {
        if (amount.compareTo(remainingTransfer(walletId)) > 0) {
            throw new BusinessRuleException(ErrorMessages.DAILY_TRANSFER_LIMIT_EXCEEDED, ErrorCodes.DAILY_TRANSFER_LIMIT_EXCEEDED);
        }
    }

    public void ensureCheckoutAllowed(UUID walletId, BigDecimal amount) {
        if (amount.compareTo(remaining(walletId, TransactionType.PAYMENT, CHECKOUT_LIMIT)) > 0) {
            throw new BusinessRuleException(GatewayMessages.DAILY_CHECKOUT_LIMIT_EXCEEDED, ErrorCodes.DAILY_CHECKOUT_LIMIT_EXCEEDED);
        }
    }

    private BigDecimal remaining(UUID walletId, TransactionType type, BigDecimal limit) {
        BigDecimal used = transactionRepository.sumAmountSince(walletId, type, startOfToday());
        return limit.subtract(used).max(BigDecimal.ZERO);
    }

    private Instant startOfToday() {
        return LocalDate.now(clock.withZone(ZONE)).atStartOfDay(ZONE).toInstant();
    }
}
