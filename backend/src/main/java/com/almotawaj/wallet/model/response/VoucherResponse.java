package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.Voucher;
import com.almotawaj.wallet.model.VoucherStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VoucherResponse(
        UUID id,
        String merchantName,
        String title,
        BigDecimal price,
        BigDecimal faceValue,
        int totalQuantity,
        int remaining,
        int perClientLimit,
        VoucherStatus status,
        Instant saleEndsAt,
        Instant expiresAt,
        long version,
        Instant createdAt
) {
    public static VoucherResponse from(Voucher voucher, String merchantName, Instant now) {
        return new VoucherResponse(voucher.getId(), merchantName, voucher.getTitle(), voucher.getPrice(), voucher.getFaceValue(),
                voucher.getTotalQuantity(), voucher.getRemaining(), voucher.getPerClientLimit(), statusAt(voucher, now),
                voucher.getSaleEndsAt(), voucher.getExpiresAt(), voucher.getVersion(), voucher.getCreatedAt());
    }

    private static VoucherStatus statusAt(Voucher voucher, Instant now) {
        if (voucher.getRemaining() == 0) {
            return VoucherStatus.SOLD_OUT;
        }
        if (!voucher.getSaleEndsAt().isAfter(now)) {
            return VoucherStatus.EXPIRED;
        }
        return voucher.getStatus();
    }
}
