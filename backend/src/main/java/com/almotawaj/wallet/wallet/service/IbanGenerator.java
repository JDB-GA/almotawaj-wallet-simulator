package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.util.Iban;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class IbanGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final WalletRepository walletRepository;

    public String generate() {
        for (int attempt = 0; attempt < WalletConstants.IBAN_GENERATION_ATTEMPTS; attempt++) {
            String iban = Iban.of(WalletConstants.COUNTRY_CODE, WalletConstants.BANK_CODE + accountNumber());
            if (!walletRepository.existsByIban(iban)) {
                return iban;
            }
        }
        throw new IllegalStateException(ErrorMessages.IBAN_GENERATION_FAILED);
    }

    private static String accountNumber() {
        StringBuilder digits = new StringBuilder(WalletConstants.ACCOUNT_NUMBER_LENGTH);
        for (int index = 0; index < WalletConstants.ACCOUNT_NUMBER_LENGTH; index++) {
            digits.append(RANDOM.nextInt(10));
        }
        return digits.toString();
    }
}
