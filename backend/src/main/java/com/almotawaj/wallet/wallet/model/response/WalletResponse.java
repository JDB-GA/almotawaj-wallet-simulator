package com.almotawaj.wallet.wallet.model.response;

import com.almotawaj.wallet.wallet.model.Wallet;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletResponse(String holderName, String iban, BigDecimal balance, String currency, Instant createdAt) {
    public static WalletResponse from(Wallet wallet, String holderName) {
        return new WalletResponse(holderName, wallet.getIban(), wallet.getBalance(), wallet.getCurrency(), wallet.getCreatedAt());
    }
}
