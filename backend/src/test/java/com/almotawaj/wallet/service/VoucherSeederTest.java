package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.repository.UserRepository;
import com.almotawaj.wallet.repository.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "seed-password=test-only-seed-password")
@Transactional
class VoucherSeederTest {
    @Autowired
    private DatabaseSeedService seedService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    @Test
    void seeding_givesTheDemoMerchantVouchersOnce() {
        seedService.seedBasicData();
        seedService.seedBasicData();

        UUID merchantId = userRepository.findByEmailAddress(SeedConstants.MERCHANT_EMAIL).orElseThrow().getId();
        assertThat(voucherRepository.findAll()).hasSize(SeedConstants.VOUCHERS.size())
                .allMatch(voucher -> voucher.getMerchant().getId().equals(merchantId))
                .anyMatch(voucher -> voucher.getRemaining() == 1);
    }
}
