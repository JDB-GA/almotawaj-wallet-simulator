package com.almotawaj.wallet.checkout.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WebhookPayload(
        String event,
        UUID sessionId,
        String orderReference,
        BigDecimal amount,
        CheckoutStatus status,
        Instant occurredAt
) {
}
