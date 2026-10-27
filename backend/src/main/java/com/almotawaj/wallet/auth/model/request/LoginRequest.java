package com.almotawaj.wallet.auth.model.request;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = DocExamples.IDENTIFIER)
        @NotBlank(message = ErrorMessages.IDENTIFIER_REQUIRED)
        String identifier,

        @Schema(example = DocExamples.PASSWORD)
        @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
        String password
) {
}
