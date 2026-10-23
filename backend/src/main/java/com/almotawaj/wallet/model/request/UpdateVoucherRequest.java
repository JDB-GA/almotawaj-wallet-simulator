package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletLimits;
import com.almotawaj.wallet.model.VoucherStatus;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateVoucherRequest(
        @NotBlank(message = ErrorMessages.VOUCHER_TITLE_REQUIRED)
        @Size(max = ValidationLimits.VOUCHER_TITLE_MAX, message = ErrorMessages.VOUCHER_TITLE_TOO_LONG)
        String title,

        @NotNull(message = ErrorMessages.VOUCHER_PRICE_REQUIRED)
        @DecimalMin(value = WalletLimits.VOUCHER_PRICE_MIN, message = ErrorMessages.AMOUNT_TOO_SMALL)
        @DecimalMax(value = WalletLimits.VOUCHER_PRICE_MAX, message = ErrorMessages.AMOUNT_TOO_LARGE)
        @Digits(integer = ValidationLimits.MONEY_INTEGER_DIGITS, fraction = ValidationLimits.MONEY_SCALE,
                message = ErrorMessages.AMOUNT_PRECISION)
        BigDecimal price,

        @NotNull(message = ErrorMessages.VOUCHER_STATUS_REQUIRED)
        VoucherStatus status,

        @NotNull(message = ErrorMessages.VOUCHER_EXPIRY_REQUIRED)
        @Future(message = ErrorMessages.VOUCHER_EXPIRY_IN_PAST)
        Instant expiresAt,

        @NotNull(message = ErrorMessages.VOUCHER_VERSION_REQUIRED)
        Long version
) {
    @AssertTrue(message = ErrorMessages.VOUCHER_STATUS_INVALID)
    @Parameter(hidden = true)
    public boolean isStatusEditable() {
        return status == null || status == VoucherStatus.ACTIVE || status == VoucherStatus.PAUSED;
    }
}
