package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.VoucherPurchase;
import com.almotawaj.wallet.model.VoucherPurchaseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VoucherPurchaseResponse(
        UUID id,
        UUID voucherId,
        String title,
        String merchantName,
        String code,
        BigDecimal price,
        BigDecimal faceValue,
        String transactionReference,
        VoucherPurchaseStatus status,
        Instant redeemedAt,
        Instant createdAt
) {
    public static VoucherPurchaseResponse from(VoucherPurchase purchase, String merchantName) {
        return new VoucherPurchaseResponse(purchase.getId(), purchase.getVoucher().getId(), purchase.getVoucher().getTitle(),
                merchantName, purchase.getCode(), purchase.getPrice(), purchase.getVoucher().getFaceValue(),
                purchase.getTransaction().getReference(), purchase.getStatus(), purchase.getRedeemedAt(), purchase.getCreatedAt());
    }
}
