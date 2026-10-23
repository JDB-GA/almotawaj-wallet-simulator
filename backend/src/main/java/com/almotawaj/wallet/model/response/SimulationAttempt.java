package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.SimulationOutcome;

import java.util.List;

public record SimulationAttempt(int index, String thread, double startMs, double endMs, SimulationOutcome outcome, String detail,
                                List<SimulationStep> steps) {
}
