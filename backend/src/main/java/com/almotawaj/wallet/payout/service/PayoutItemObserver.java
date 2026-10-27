package com.almotawaj.wallet.payout.service;

import com.almotawaj.wallet.payout.model.PayoutItemStatus;
import com.almotawaj.wallet.simulation.model.response.SimulationStep;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface PayoutItemObserver {
    PayoutItemObserver NONE = (itemId, thread, startNanos, endNanos, status, steps) -> {
    };

    void onItemProcessed(UUID itemId, String thread, long startNanos, long endNanos, PayoutItemStatus status, List<SimulationStep> steps);
}
