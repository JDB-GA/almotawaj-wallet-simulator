package com.almotawaj.wallet.payout.service;

import com.almotawaj.wallet.payout.model.PayoutItemStatus;
import com.almotawaj.wallet.simulation.model.response.SimulationStep;

import java.util.List;
import java.util.UUID;

@FunctionalInterface
public interface PayoutItemObserver {
    // Empty function that does nothing
    PayoutItemObserver NONE = (itemId, thread, startNanos, endNanos, status, steps) -> {
    };

    // For simulation purposes (onItemProcessed (the abstract method of this functional interface))
    void onItemProcessed(UUID itemId, String thread, long startNanos, long endNanos, PayoutItemStatus status, List<SimulationStep> steps);
}
