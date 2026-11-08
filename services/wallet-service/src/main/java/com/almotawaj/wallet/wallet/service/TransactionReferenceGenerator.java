package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.WalletConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TransactionReferenceGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern(WalletConstants.TRANSACTION_REFERENCE_DATE_FORMAT);

    private final Clock clock;

    public String next() {
        String alphabet = WalletConstants.TRANSACTION_REFERENCE_ALPHABET;
        StringBuilder suffix = new StringBuilder(WalletConstants.TRANSACTION_REFERENCE_RANDOM_LENGTH);
        for (int index = 0; index < WalletConstants.TRANSACTION_REFERENCE_RANDOM_LENGTH; index++) {
            suffix.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return String.join(WalletConstants.REFERENCE_SEPARATOR, WalletConstants.TRANSACTION_REFERENCE_PREFIX, LocalDate.now(clock).format(DATE), suffix);
    }
}
