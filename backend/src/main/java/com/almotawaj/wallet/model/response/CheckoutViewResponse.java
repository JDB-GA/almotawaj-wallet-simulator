package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.CheckoutStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CheckoutViewResponse(
        UUID id,
        String merchantName,
        String orderReference,
        BigDecimal amount,
        String description,
        CheckoutStatus status,
        String returnUrl,
        Instant expiresAt
) {
}
