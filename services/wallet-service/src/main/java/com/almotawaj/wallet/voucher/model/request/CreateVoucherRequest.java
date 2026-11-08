package com.almotawaj.wallet.voucher.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletLimits;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherRequest(
        @NotBlank(message = ErrorMessages.VOUCHER_TITLE_REQUIRED)
        @Size(max = ValidationLimits.VOUCHER_TITLE_MAX, message = ErrorMessages.VOUCHER_TITLE_TOO_LONG)
        String title,

        @NotNull(message = ErrorMessages.VOUCHER_PRICE_REQUIRED)
        @DecimalMin(value = WalletLimits.VOUCHER_PRICE_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.VOUCHER_PRICE_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal price,

        @NotNull(message = ErrorMessages.VOUCHER_FACE_VALUE_REQUIRED)
        @DecimalMin(value = WalletLimits.VOUCHER_PRICE_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.VOUCHER_PRICE_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal faceValue,

        @NotNull(message = ErrorMessages.VOUCHER_QUANTITY_REQUIRED)
        @Min(value = WalletLimits.VOUCHER_QUANTITY_MIN, message = ErrorMessages.VOUCHER_QUANTITY_TOO_SMALL)
        @Max(value = WalletLimits.VOUCHER_QUANTITY_MAX, message = ErrorMessages.VOUCHER_QUANTITY_TOO_LARGE)
        Integer totalQuantity,

        @NotNull(message = ErrorMessages.VOUCHER_LIMIT_REQUIRED)
        @Min(value = WalletLimits.VOUCHER_PER_CLIENT_MIN, message = ErrorMessages.VOUCHER_LIMIT_TOO_SMALL)
        @Max(value = WalletLimits.VOUCHER_PER_CLIENT_MAX, message = ErrorMessages.VOUCHER_LIMIT_TOO_LARGE)
        Integer perClientLimit,

        @NotNull(message = ErrorMessages.VOUCHER_SALE_END_REQUIRED)
        @Future(message = ErrorMessages.VOUCHER_SALE_END_IN_PAST)
        Instant saleEndsAt,

        @Future(message = ErrorMessages.VOUCHER_EXPIRY_IN_PAST)
        Instant expiresAt
) {
}
