package com.almotawaj.wallet.account.service;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.util.LoginIdentifier;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.repository.WalletRepository;
import com.almotawaj.wallet.wallet.service.IbanGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountHolderService {
    private final AccountHolderRepository repository;
    private final WalletRepository walletRepository;
    private final IbanGenerator ibanGenerator;

    @Transactional(readOnly = true)
    public Optional<AccountHolder> find(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AccountHolder> findByIdentifier(String identifier) {
        String value = identifier.trim();
        return value.contains(WalletConstants.EMAIL_MARKER)
                ? repository.findByEmailAddressIgnoreCase(value)
                : repository.findByMobileNumber(LoginIdentifier.normalizeMobile(value));
    }

    @Transactional
    public void createWalletIfMissing(UUID userId) {
        repository.findById(userId)
                .filter(account -> account.getRole() == AccountRole.CLIENT || account.getRole() == AccountRole.MERCHANT)
                .filter(account -> walletRepository.findByUserId(userId).isEmpty())
                .ifPresent(account -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(account);
                    wallet.setIban(ibanGenerator.generate());
                    walletRepository.save(wallet);
                });
    }
}
