package com.almotawaj.wallet.auth.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record
RegisterRequest(
        @Schema(example = DocExamples.EMAIL)
        @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
        @Email(message = ErrorMessages.EMAIL_INVALID)
        @Size(max = ValidationLimits.EMAIL_MAX, message = ErrorMessages.EMAIL_TOO_LONG)
        String email,

        @Schema(example = DocExamples.MOBILE)
        @NotBlank(message = ErrorMessages.MOBILE_REQUIRED)
        @Pattern(regexp = ValidationPatterns.MOBILE_NUMBER, message = ErrorMessages.MOBILE_INVALID)
        String mobileNumber,

        @Schema(example = DocExamples.PASSWORD)
        @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
        @Size(min = ValidationLimits.PASSWORD_MIN, max = ValidationLimits.PASSWORD_MAX, message = ErrorMessages.PASSWORD_LENGTH)
        String password
) {
}
