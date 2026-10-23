package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletLimits;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PayoutItemRequest(
        @NotBlank(message = ErrorMessages.RECIPIENT_REQUIRED)
        @Size(max = ValidationLimits.RECIPIENT_QUERY_MAX, message = ErrorMessages.RECIPIENT_TOO_LONG)
        String recipient,

        @NotNull(message = ErrorMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = WalletLimits.PAYOUT_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.PAYOUT_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal amount
) {
}
