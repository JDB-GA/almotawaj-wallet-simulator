package com.almotawaj.wallet.checkout.model.response;

import com.almotawaj.wallet.checkout.model.CheckoutStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CheckoutViewResponse(
        UUID id,
        String merchantName,
        String orderReference,
        BigDecimal amount,
        BigDecimal voucherDiscount,
        String description,
        CheckoutStatus status,
        String returnUrl,
        Instant expiresAt
) {
}
