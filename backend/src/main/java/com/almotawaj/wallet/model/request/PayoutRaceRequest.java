package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.SimulationConstants;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayoutRaceRequest(
        @Min(value = SimulationConstants.MIN_RECIPIENTS, message = ErrorMessages.SIMULATION_RECIPIENTS_RANGE)
        @Max(value = SimulationConstants.MAX_RECIPIENTS, message = ErrorMessages.SIMULATION_RECIPIENTS_RANGE)
        int recipients,

        @NotNull(message = ErrorMessages.SIMULATION_AMOUNT_RANGE)
        @DecimalMin(value = SimulationConstants.MIN_SIM_AMOUNT, message = ErrorMessages.SIMULATION_AMOUNT_RANGE)
        @DecimalMax(value = SimulationConstants.MAX_SIM_AMOUNT, message = ErrorMessages.SIMULATION_AMOUNT_RANGE)
        BigDecimal amount,

        @NotNull(message = ErrorMessages.SIMULATION_BALANCE_RANGE)
        @DecimalMin(value = SimulationConstants.MIN_SIM_BALANCE, message = ErrorMessages.SIMULATION_BALANCE_RANGE)
        @DecimalMax(value = SimulationConstants.MAX_SIM_BALANCE, message = ErrorMessages.SIMULATION_BALANCE_RANGE)
        BigDecimal merchantBalance
) {
}
