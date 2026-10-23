package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.PayoutBatchStatus;

import java.util.UUID;

public record PayoutProgressNotification(UUID batchId, PayoutBatchStatus status, int paid, int failed, int total) {
}
