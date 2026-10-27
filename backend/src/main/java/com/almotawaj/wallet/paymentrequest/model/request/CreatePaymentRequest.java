package com.almotawaj.wallet.paymentrequest.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @Schema(example = DocExamples.EMAIL)
        @NotBlank(message = ErrorMessages.PAYER_REQUIRED)
        @Size(max = ValidationLimits.RECIPIENT_QUERY_MAX, message = ErrorMessages.RECIPIENT_TOO_LONG)
        String payer,

        @Schema(example = DocExamples.AMOUNT)
        @NotNull(message = ErrorMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = WalletLimits.TRANSFER_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.TRANSFER_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal amount,

        @Schema(example = DocExamples.TRANSFER_NOTE)
        @Size(max = ValidationLimits.TRANSACTION_DESCRIPTION_MAX, message = ErrorMessages.NOTE_TOO_LONG)
        String note
) {
}
