package com.almotawaj.wallet.wallet.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.TransactionType;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionSearchRequest(
        @Parameter(description = "Matches counterparty name, reference or note (case-insensitive)")
        @Size(max = ValidationLimits.TRANSACTION_SEARCH_MAX, message = ErrorMessages.SEARCH_TOO_LONG)
        String search,

        TransactionType type,

        TransactionDirection direction,

        @Parameter(description = "First day to include (Bahrain time), e.g. 2026-10-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @Parameter(description = "Last day to include (Bahrain time), e.g. 2026-10-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        @PositiveOrZero(message = ErrorMessages.AMOUNT_FILTER_NEGATIVE)
        BigDecimal minAmount,

        @PositiveOrZero(message = ErrorMessages.AMOUNT_FILTER_NEGATIVE)
        BigDecimal maxAmount
) {
    @AssertTrue(message = ErrorMessages.DATE_RANGE_INVALID)
    @Parameter(hidden = true)
    public boolean isDateRangeValid() {
        return from == null || to == null || !from.isAfter(to);
    }

    @AssertTrue(message = ErrorMessages.AMOUNT_RANGE_INVALID)
    @Parameter(hidden = true)
    public boolean isAmountRangeValid() {
        return minAmount == null || maxAmount == null || minAmount.compareTo(maxAmount) <= 0;
    }
}
