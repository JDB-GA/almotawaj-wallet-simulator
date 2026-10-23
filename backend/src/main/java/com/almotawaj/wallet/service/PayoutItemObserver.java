package com.almotawaj.wallet.service;

import com.almotawaj.wallet.model.PayoutItemStatus;
import com.almotawaj.wallet.model.response.SimulationStep;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface PayoutItemObserver {
    PayoutItemObserver NONE = (itemId, thread, startNanos, endNanos, status, steps) -> {
    };

    void onItemProcessed(UUID itemId, String thread, long startNanos, long endNanos, PayoutItemStatus status, List<SimulationStep> steps);
}
