package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.SimulationStepType;

import java.math.BigDecimal;

public record SimulationStep(double atMs, SimulationStepType type, BigDecimal value) {
}
