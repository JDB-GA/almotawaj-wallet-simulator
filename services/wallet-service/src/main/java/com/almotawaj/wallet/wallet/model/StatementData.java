package com.almotawaj.wallet.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StatementData(
        String ownerName,
        String iban,
        String statementNumber,
        LocalDate from,
        LocalDate to,
        List<WalletTransaction> transactions,
        long totalTransactions,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        long creditCount,
        BigDecimal totalCredited,
        long debitCount,
        BigDecimal totalDebited,
        boolean filtered
) {
}
