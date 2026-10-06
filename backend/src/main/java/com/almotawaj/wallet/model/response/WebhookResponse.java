package com.almotawaj.wallet.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record WebhookResponse(
        @Schema(description = "Null when no callback URL is set")
        String url,
        @Schema(description = "Secret used to sign every callback; null when no callback URL is set")
        String signingSecret,
        Instant updatedAt
) {
    public static WebhookResponse none() {
        return new WebhookResponse(null, null, null);
    }
}
