package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminTransactionResponse(
        UUID id,
        String walletOwnerEmail,
        String reference,
        TransactionType type,
        TransactionDirection direction,
        BigDecimal amount,
        String counterpartyName,
        Instant createdAt
) {
    public static AdminTransactionResponse from(WalletTransaction transaction) {
        return new AdminTransactionResponse(
                transaction.getId(),
                transaction.getWallet().getUser().getEmailAddress(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getDirection(),
                transaction.getAmount(),
                transaction.getCounterpartyName(),
                transaction.getCreatedAt());
    }
}
