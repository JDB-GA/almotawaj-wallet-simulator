package com.almotawaj.wallet.seed.service;

import com.almotawaj.wallet.account.service.AccountHolderService;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.voucher.service.VoucherSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSeedService {
    private final AccountHolderService accountHolderService;
    private final VoucherSeeder voucherSeeder;

    @Transactional
    public void seedBasicData() {
        SeedConstants.ACCOUNT_EMAILS.forEach(accountHolderService::findByIdentifier);
        var merchant = accountHolderService.findByIdentifier(SeedConstants.MERCHANT_EMAIL)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.SEED_ACCOUNTS_MISSING));
        voucherSeeder.seed(merchant);
        log.info(LogMessages.SEED_COMPLETED);
    }
}
