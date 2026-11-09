package com.almotawaj.wallet.config.constants;

import com.almotawaj.wallet.seed.model.SeedVoucher;

import java.util.List;

public final class SeedConstants {
    public static final String SEED_TOKEN_PROPERTY = "${seed-token:}";

    public static final String MERCHANT_EMAIL = "merchant@almotawaj.com";
    public static final int VOUCHER_EXPIRY_DAYS = 30;
    public static final List<SeedVoucher> VOUCHERS = List.of(
            new SeedVoucher("Coffee Gift Card", "9.000", "10.000", 50, 2),
            new SeedVoucher("Dinner for Two", "25.000", "35.000", 10, 1),
            new SeedVoucher("Cinema Ticket", "4.000", "5.000", 100, 4),
            new SeedVoucher("Spa Day - Last One", "30.000", "45.000", 1, 1));

    private SeedConstants() {
    }
}
