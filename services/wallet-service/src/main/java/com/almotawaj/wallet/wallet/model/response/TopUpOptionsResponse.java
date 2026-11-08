package com.almotawaj.wallet.wallet.model.response;

import java.math.BigDecimal;
import java.util.List;

public record TopUpOptionsResponse(
        List<TopUpSourceResponse> sources,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal dailyLimit,
        BigDecimal remainingToday
) {
}
