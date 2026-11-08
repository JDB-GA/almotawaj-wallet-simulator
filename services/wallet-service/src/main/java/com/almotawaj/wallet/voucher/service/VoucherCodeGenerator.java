package com.almotawaj.wallet.voucher.service;

import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.voucher.repository.VoucherPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class VoucherCodeGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final VoucherPurchaseRepository purchaseRepository;

    public String next() {
        String code;
        do {
            code = generate();
        } while (purchaseRepository.existsByCode(code));
        return code;
    }

    private static String generate() {
        String alphabet = WalletConstants.TRANSACTION_REFERENCE_ALPHABET;
        StringBuilder code = new StringBuilder(WalletConstants.VOUCHER_CODE_PREFIX);
        for (int index = 0; index < WalletConstants.VOUCHER_CODE_RANDOM_LENGTH; index++) {
            if (index % WalletConstants.VOUCHER_CODE_GROUP_SIZE == 0) {
                code.append(WalletConstants.REFERENCE_SEPARATOR);
            }
            code.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return code.toString();
    }
}
