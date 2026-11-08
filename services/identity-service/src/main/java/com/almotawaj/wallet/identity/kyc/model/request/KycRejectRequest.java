package com.almotawaj.wallet.identity.kyc.model.request;

import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.ValidationLimits;
import com.almotawaj.wallet.identity.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KycRejectRequest(
        @Schema(example = DocExamples.REJECTION_REASON)
        @NotBlank(message = ErrorMessages.REJECTION_REASON_REQUIRED)
        @Size(max = ValidationLimits.REJECTION_REASON_MAX, message = ErrorMessages.REJECTION_REASON_TOO_LONG)
        String reason
) {
}
