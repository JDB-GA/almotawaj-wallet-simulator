package com.almotawaj.wallet.auth.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @Schema(example = DocExamples.OTP_CODE)
        @NotBlank(message = ErrorMessages.OTP_REQUIRED)
        @Pattern(regexp = ValidationPatterns.OTP_CODE, message = ErrorMessages.OTP_FORMAT_INVALID)
        String code
) {
}
