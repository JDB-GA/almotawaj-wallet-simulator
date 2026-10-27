package com.almotawaj.wallet.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletProvisioner {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletAccessPolicy accessPolicy;
    private final IbanGenerator ibanGenerator;

    public Wallet getOrCreate(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND));
        accessPolicy.ensureCanUseWallet(user);
        return walletRepository.findByUserId(userId).orElseGet(() -> create(user));
    }

    private Wallet create(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setIban(ibanGenerator.generate());
        return walletRepository.saveAndFlush(wallet);
    }
}
