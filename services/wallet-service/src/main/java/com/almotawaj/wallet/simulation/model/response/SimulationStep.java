package com.almotawaj.wallet.simulation.model.response;

import com.almotawaj.wallet.simulation.model.SimulationStepType;

import java.math.BigDecimal;

public record SimulationStep(double atMs, SimulationStepType type, BigDecimal value) {
}
