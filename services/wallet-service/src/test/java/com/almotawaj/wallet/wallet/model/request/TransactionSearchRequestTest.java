package com.almotawaj.wallet.wallet.model.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionSearchRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static TransactionSearchRequest request(LocalDate from, LocalDate to, String min, String max) {
        return new TransactionSearchRequest(null, null, null, from, to,
                min == null ? null : new BigDecimal(min), max == null ? null : new BigDecimal(max));
    }

    @Test
    void acceptsEmptyAndOrderedRanges() {
        assertThat(validator.validate(request(null, null, null, null))).isEmpty();
        assertThat(validator.validate(request(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 1), "5", "5"))).isEmpty();
    }

    @Test
    void rejectsReversedRangesAndNegativeAmounts() {
        assertThat(validator.validate(request(LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1), null, null)))
                .extracting(violation -> violation.getPropertyPath().toString()).containsExactly("dateRangeValid");
        assertThat(validator.validate(request(null, null, "10", "9")))
                .extracting(violation -> violation.getPropertyPath().toString()).containsExactly("amountRangeValid");
        assertThat(validator.validate(request(null, null, "-1", null))).hasSize(1);
    }
}
