package com.almotawaj.wallet.simulation.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.SimulationConstants;
import com.almotawaj.wallet.simulation.model.SimulationMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VoucherRaceRequest(
        @NotNull(message = ErrorMessages.SIMULATION_MODE_REQUIRED)
        SimulationMode mode,

        @Min(value = SimulationConstants.MIN_BUYERS, message = ErrorMessages.SIMULATION_BUYERS_RANGE)
        @Max(value = SimulationConstants.MAX_BUYERS, message = ErrorMessages.SIMULATION_BUYERS_RANGE)
        int buyers,

        @Min(value = SimulationConstants.MIN_STOCK, message = ErrorMessages.SIMULATION_STOCK_RANGE)
        @Max(value = SimulationConstants.MAX_STOCK, message = ErrorMessages.SIMULATION_STOCK_RANGE)
        int stock
) {
}
