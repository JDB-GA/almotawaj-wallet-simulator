package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCheckoutSessionRequest(
        @Schema(example = DocExamples.ORDER_REFERENCE)
        @NotBlank(message = GatewayMessages.ORDER_REFERENCE_REQUIRED)
        @Size(max = ValidationLimits.ORDER_REFERENCE_MAX, message = GatewayMessages.ORDER_REFERENCE_TOO_LONG)
        @Pattern(regexp = ValidationPatterns.ORDER_REFERENCE, message = GatewayMessages.ORDER_REFERENCE_INVALID)
        String orderReference,

        @Schema(example = DocExamples.AMOUNT)
        @NotNull(message = ErrorMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = WalletLimits.CHECKOUT_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.CHECKOUT_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal amount,

        @Schema(example = DocExamples.ORDER_DESCRIPTION)
        @Size(max = ValidationLimits.TRANSACTION_DESCRIPTION_MAX, message = ErrorMessages.NOTE_TOO_LONG)
        String description,

        @Schema(example = DocExamples.RETURN_URL)
        @Size(max = ValidationLimits.URL_MAX, message = GatewayMessages.URL_TOO_LONG)
        @Pattern(regexp = ValidationPatterns.HTTP_URL, message = GatewayMessages.URL_INVALID)
        String returnUrl,

        @Schema(example = DocExamples.EXPIRES_IN_MINUTES, description = "Defaults to 30")
        @Min(value = GatewayConstants.SESSION_LIFETIME_MIN_MINUTES, message = GatewayMessages.EXPIRY_OUT_OF_RANGE)
        @Max(value = GatewayConstants.SESSION_LIFETIME_MAX_MINUTES, message = GatewayMessages.EXPIRY_OUT_OF_RANGE)
        Integer expiresInMinutes
) {
}
