package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.model.SimulationStepType;
import com.almotawaj.wallet.model.response.SimulationStep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class SimulationTrace {
    private static final ThreadLocal<Recording> CURRENT = new ThreadLocal<>();

    private SimulationTrace() {
    }

    public static void start(long originNanos) {
        CURRENT.set(new Recording(originNanos, new ArrayList<>()));
    }

    public static void mark(SimulationStepType type) {
        mark(type, null);
    }

    public static void mark(SimulationStepType type, BigDecimal value) {
        Recording recording = CURRENT.get();
        if (recording != null) {
            double atMs = (System.nanoTime() - recording.originNanos()) / SimulationConstants.NANOS_PER_MILLI;
            recording.steps().add(new SimulationStep(atMs, type, value));
        }
    }

    public static List<SimulationStep> stop() {
        Recording recording = CURRENT.get();
        CURRENT.remove();
        return recording == null ? List.of() : List.copyOf(recording.steps());
    }

    private record Recording(long originNanos, List<SimulationStep> steps) {
    }
}
