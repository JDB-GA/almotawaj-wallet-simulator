package com.almotawaj.wallet.voucher.model.response;

import com.almotawaj.wallet.voucher.model.VoucherPurchase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UsableVoucherResponse(UUID id, String title, String code, BigDecimal faceValue, Instant expiresAt) {
    public static UsableVoucherResponse from(VoucherPurchase purchase) {
        return new UsableVoucherResponse(purchase.getId(), purchase.getVoucher().getTitle(), purchase.getCode(),
                purchase.getVoucher().getFaceValue(), purchase.getVoucher().getExpiresAt());
    }
}
