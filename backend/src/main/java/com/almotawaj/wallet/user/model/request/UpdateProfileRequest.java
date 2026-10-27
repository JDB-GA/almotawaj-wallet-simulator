package com.almotawaj.wallet.user.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Schema(example = DocExamples.BUSINESS_NAME)
        @NotBlank(message = ErrorMessages.DISPLAY_NAME_REQUIRED)
        @Size(min = ValidationLimits.DISPLAY_NAME_MIN, max = ValidationLimits.COUNTERPARTY_NAME_MAX,
                message = ErrorMessages.DISPLAY_NAME_LENGTH)
        @Pattern(regexp = ValidationPatterns.DISPLAY_NAME, message = ErrorMessages.DISPLAY_NAME_INVALID)
        String displayName
) {
}
