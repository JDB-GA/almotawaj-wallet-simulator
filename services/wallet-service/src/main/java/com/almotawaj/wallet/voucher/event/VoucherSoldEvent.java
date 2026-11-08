package com.almotawaj.wallet.voucher.event;

import java.math.BigDecimal;
import java.util.UUID;

public record VoucherSoldEvent(UUID merchantId, UUID voucherId, String title, BigDecimal price, int remaining) {
}
