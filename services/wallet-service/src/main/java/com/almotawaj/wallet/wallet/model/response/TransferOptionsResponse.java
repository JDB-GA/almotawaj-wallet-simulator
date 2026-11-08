package com.almotawaj.wallet.wallet.model.response;

import java.math.BigDecimal;

public record TransferOptionsResponse(
        BigDecimal balance,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal dailyLimit,
        BigDecimal remainingToday
) {
}
