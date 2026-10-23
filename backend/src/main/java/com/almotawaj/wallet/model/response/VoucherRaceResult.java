package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.SimulationMode;

import java.util.List;

public record VoucherRaceResult(
        SimulationMode mode,
        int buyers,
        int stock,
        int winners,
        int remainingAfter,
        int oversold,
        double durationMs,
        List<SimulationAttempt> attempts
) {
}
