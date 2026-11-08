package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.model.AccountStatus;
import com.almotawaj.wallet.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public final class WalletFixtures {
    private WalletFixtures() {
    }

    public static Wallet wallet(AccountRole role, String balance) {
        AccountHolder user = new AccountHolder();
        user.setId(UUID.randomUUID());
        user.setRole(role);
        user.setStatus(AccountStatus.ACTIVE);
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal(balance));
        return wallet;
    }
}
