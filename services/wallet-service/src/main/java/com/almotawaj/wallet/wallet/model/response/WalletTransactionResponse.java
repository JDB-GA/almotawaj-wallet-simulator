package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID id,
        String reference,
        TransactionType type,
        TransactionDirection direction,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String counterpartyName,
        String counterpartyIban,
        String counterpartyBic,
        String counterpartyEmail,
        String counterpartyMobile,
        String description,
        Instant createdAt
) {
    public static WalletTransactionResponse from(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getId(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getDirection(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getCounterpartyName(),
                transaction.getCounterpartyIban(),
                transaction.getCounterpartyBic(),
                transaction.getCounterpartyEmail(),
                transaction.getCounterpartyMobile(),
                transaction.getDescription(),
                transaction.getCreatedAt());
    }
}
