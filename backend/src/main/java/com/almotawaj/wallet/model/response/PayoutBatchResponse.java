package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.PayoutBatch;
import com.almotawaj.wallet.model.PayoutBatchStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PayoutBatchResponse(
        UUID id,
        PayoutBatchStatus status,
        int itemCount,
        int paidCount,
        int failedCount,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant completedAt,
        List<PayoutItemResponse> items
) {
    public static PayoutBatchResponse from(PayoutBatch batch, List<PayoutItemResponse> items) {
        return new PayoutBatchResponse(batch.getId(), batch.getStatus(), batch.getItemCount(), batch.getPaidCount(),
                batch.getFailedCount(), batch.getTotalAmount(), batch.getCreatedAt(), batch.getCompletedAt(), items);
    }
}
