package com.almotawaj.wallet.payout.model.response;

import com.almotawaj.wallet.payout.model.PayoutBatchStatus;

import java.util.UUID;

public record PayoutProgressNotification(UUID batchId, PayoutBatchStatus status, int paid, int failed, int total) {
}
