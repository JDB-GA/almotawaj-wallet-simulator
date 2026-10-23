package com.almotawaj.wallet.model.response;

import java.util.UUID;

public record VoucherStockNotification(UUID voucherId, int remaining) {
}
