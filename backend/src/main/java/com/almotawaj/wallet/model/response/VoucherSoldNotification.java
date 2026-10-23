package com.almotawaj.wallet.model.response;

import java.math.BigDecimal;
import java.util.UUID;

public record VoucherSoldNotification(UUID voucherId, String title, BigDecimal price, int remaining) {
}
