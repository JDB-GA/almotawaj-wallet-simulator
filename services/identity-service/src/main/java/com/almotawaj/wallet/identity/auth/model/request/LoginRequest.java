package com.almotawaj.wallet.identity.auth.model.request;

import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.docs.DocExamples;
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
