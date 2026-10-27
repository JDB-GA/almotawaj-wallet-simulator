package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public final class WalletFixtures {
    private WalletFixtures() {
    }

    public static Wallet wallet(UserRole role, String balance) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(role);
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal(balance));
        return wallet;
    }
}
