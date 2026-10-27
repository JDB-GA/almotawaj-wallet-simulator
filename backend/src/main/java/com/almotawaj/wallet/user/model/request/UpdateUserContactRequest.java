package com.almotawaj.wallet.user.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserContactRequest(
        @Schema(example = DocExamples.EMAIL)
        @Email(message = ErrorMessages.EMAIL_INVALID)
        @Size(max = ValidationLimits.EMAIL_MAX, message = ErrorMessages.EMAIL_TOO_LONG)
        String email,

        @Schema(example = DocExamples.MOBILE)
        @Pattern(regexp = ValidationPatterns.MOBILE_NUMBER, message = ErrorMessages.MOBILE_INVALID)
        String mobileNumber
) {
}
