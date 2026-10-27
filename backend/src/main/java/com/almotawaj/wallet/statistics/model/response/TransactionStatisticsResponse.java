package com.almotawaj.wallet.statistics.model.response;

import java.math.BigDecimal;

public record TransactionStatisticsResponse(long totalTransactions, BigDecimal totalCredited, BigDecimal totalDebited) {
}
