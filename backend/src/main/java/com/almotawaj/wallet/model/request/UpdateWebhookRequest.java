package com.almotawaj.wallet.model.request;

import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.ValidationPatterns;
import com.almotawaj.wallet.config.constants.docs.DocExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookRequest(
        @Schema(example = DocExamples.WEBHOOK_URL)
        @NotBlank(message = GatewayMessages.WEBHOOK_URL_REQUIRED)
        @Size(max = ValidationLimits.URL_MAX, message = GatewayMessages.URL_TOO_LONG)
        @Pattern(regexp = ValidationPatterns.HTTP_URL, message = GatewayMessages.WEBHOOK_URL_INVALID)
        String url
) {
}
