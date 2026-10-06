package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.CheckoutStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CheckoutSessionResponse(
        UUID id,
        String orderReference,
        BigDecimal amount,
        String description,
        CheckoutStatus status,
        String payerName,
        String checkoutUrl,
        String returnUrl,
        Instant expiresAt,
        Instant paidAt,
        Instant refundedAt,
        Instant createdAt
) {
}
