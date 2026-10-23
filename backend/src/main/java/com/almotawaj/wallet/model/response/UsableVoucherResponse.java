package com.almotawaj.wallet.model.response;

import com.almotawaj.wallet.model.VoucherPurchase;

import java.math.BigDecimal;
import java.util.UUID;

public record UsableVoucherResponse(UUID id, String title, String code, BigDecimal faceValue) {
    public static UsableVoucherResponse from(VoucherPurchase purchase) {
        return new UsableVoucherResponse(purchase.getId(), purchase.getVoucher().getTitle(), purchase.getCode(),
                purchase.getVoucher().getFaceValue());
    }
}
