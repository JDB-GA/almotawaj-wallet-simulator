package com.almotawaj.wallet.simulation.model.response;

import java.math.BigDecimal;
import java.util.List;

public record PayoutRaceResult(
        int recipients,
        BigDecimal amount,
        BigDecimal startingBalance,
        BigDecimal finalBalance,
        int paid,
        int failed,
        int threadsUsed,
        double durationMs,
        List<SimulationAttempt> attempts
) {
}
