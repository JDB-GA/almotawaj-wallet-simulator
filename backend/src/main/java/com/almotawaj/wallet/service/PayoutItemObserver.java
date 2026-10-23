package com.almotawaj.wallet.service;

import com.almotawaj.wallet.model.PayoutItemStatus;

import java.util.UUID;

@FunctionalInterface
public interface PayoutItemObserver {
    PayoutItemObserver NONE = (itemId, thread, startNanos, endNanos, status) -> {
    };

    void onItemProcessed(UUID itemId, String thread, long startNanos, long endNanos, PayoutItemStatus status);
}
