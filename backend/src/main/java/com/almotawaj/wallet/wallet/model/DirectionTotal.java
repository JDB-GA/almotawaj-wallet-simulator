package com.almotawaj.wallet.wallet.model;

import java.math.BigDecimal;
import java.util.List;

public record DirectionTotal(TransactionDirection direction, Long count, BigDecimal amount) {
    public static BigDecimal amountOf(List<DirectionTotal> totals, TransactionDirection direction) {
        return totals.stream()
                .filter(total -> total.direction() == direction)
                .map(DirectionTotal::amount)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    public static long countOf(List<DirectionTotal> totals, TransactionDirection direction) {
        return totals.stream()
                .filter(total -> total.direction() == direction)
                .map(DirectionTotal::count)
                .findFirst()
                .orElse(0L);
    }
}
