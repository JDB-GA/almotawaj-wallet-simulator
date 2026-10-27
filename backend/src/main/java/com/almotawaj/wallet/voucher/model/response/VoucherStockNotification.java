package com.almotawaj.wallet.voucher.model.response;

import java.util.UUID;

public record VoucherStockNotification(UUID voucherId, int remaining) {
}
