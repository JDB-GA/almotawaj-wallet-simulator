package com.almotawaj.wallet.checkout.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiKeyCreatedResponse(
        ApiKeyResponse key,
        @Schema(description = "The full key. It is shown only once and cannot be read again.")
        String secret
) {
}
