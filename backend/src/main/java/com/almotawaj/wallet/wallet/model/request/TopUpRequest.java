package com.almotawaj.wallet.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import com.almotawaj.wallet.wallet.model.TopUpSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TopUpRequest(
        @Schema(example = DocExamples.TOP_UP_SOURCE)
        @NotNull(message = ErrorMessages.TOP_UP_SOURCE_REQUIRED)
        TopUpSource source,

        @Schema(example = DocExamples.AMOUNT)
        @NotNull(message = ErrorMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = WalletLimits.TOP_UP_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.TOP_UP_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal amount
) {
}
