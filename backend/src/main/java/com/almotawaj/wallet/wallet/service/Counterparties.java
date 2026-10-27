package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.wallet.model.Counterparty;
import com.almotawaj.wallet.wallet.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Counterparties {
    private final WalletHolderNames holderNames;

    public Counterparty of(Wallet wallet) {
        User user = wallet.getUser();
        return new Counterparty(holderNames.fullName(user), wallet.getIban(), WalletConstants.BANK_BIC,
                holderNames.maskedEmail(user), holderNames.maskedMobile(user));
    }
}
