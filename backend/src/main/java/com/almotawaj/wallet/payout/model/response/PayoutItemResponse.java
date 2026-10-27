package com.almotawaj.wallet.payout.model.response;

import com.almotawaj.wallet.payout.model.PayoutItem;
import com.almotawaj.wallet.payout.model.PayoutItemStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PayoutItemResponse(
        UUID id,
        String recipientName,
        BigDecimal amount,
        PayoutItemStatus status,
        String failureReason,
        String transactionReference,
        Instant processedAt
) {
    public static PayoutItemResponse from(PayoutItem item, String recipientName) {
        return new PayoutItemResponse(item.getId(), recipientName, item.getAmount(), item.getStatus(), item.getFailureReason(),
                item.getTransaction() == null ? null : item.getTransaction().getReference(), item.getProcessedAt());
    }
}
