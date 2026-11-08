package com.almotawaj.wallet.identity.auth.model.request;

import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.ValidationLimits;
import com.almotawaj.wallet.identity.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @Schema(example = DocExamples.PASSWORD)
        @NotBlank(message = ErrorMessages.CURRENT_PASSWORD_REQUIRED)
        String currentPassword,

        @Schema(example = DocExamples.NEW_PASSWORD)
        @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
        @Size(min = ValidationLimits.PASSWORD_MIN, max = ValidationLimits.PASSWORD_MAX, message = ErrorMessages.PASSWORD_LENGTH)
        String newPassword
) {
}
