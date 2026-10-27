package com.almotawaj.wallet.voucher.service;

import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.seed.model.SeedVoucher;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.voucher.model.Voucher;
import com.almotawaj.wallet.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VoucherSeeder {
    private final VoucherRepository voucherRepository;
    private final Clock clock;

    public void seed(User merchant) {
        for (SeedVoucher seed : SeedConstants.VOUCHERS) {
            if (!voucherRepository.existsByMerchantIdAndTitle(merchant.getId(), seed.title())) {
                voucherRepository.save(voucher(merchant, seed));
            }
        }
    }

    private Voucher voucher(User merchant, SeedVoucher seed) {
        Voucher voucher = new Voucher();
        voucher.setMerchant(merchant);
        voucher.setTitle(seed.title());
        voucher.setPrice(new BigDecimal(seed.price()));
        voucher.setFaceValue(new BigDecimal(seed.faceValue()));
        voucher.setTotalQuantity(seed.quantity());
        voucher.setRemaining(seed.quantity());
        voucher.setPerClientLimit(seed.perClientLimit());
        voucher.setSaleEndsAt(clock.instant().plus(Duration.ofDays(SeedConstants.VOUCHER_EXPIRY_DAYS)));
        return voucher;
    }
}
