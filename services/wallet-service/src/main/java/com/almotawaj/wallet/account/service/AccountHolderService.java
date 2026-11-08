package com.almotawaj.wallet.account.service;

import com.almotawaj.wallet.account.client.AccountSnapshot;
import com.almotawaj.wallet.account.client.IdentityClient;
import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.account.model.AccountStatus;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
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
    private final IdentityClient identityClient;
    private final WalletRepository walletRepository;
    private final IbanGenerator ibanGenerator;

    @Transactional
    public Optional<AccountHolder> findOrFetch(UUID id) {
        return repository.findById(id).or(() -> identityClient.findById(id).map(this::save));
    }

    @Transactional
    public Optional<AccountHolder> findByIdentifier(String identifier) {
        String value = identifier.trim();
        Optional<AccountHolder> local = value.contains("@")
                ? repository.findByEmailAddressIgnoreCase(value)
                : repository.findByMobileNumber(LoginIdentifier.normalizeMobile(value));
        return local.or(() -> identityClient.lookup(value).map(this::save));
    }

    @Transactional
    public AccountHolder save(AccountSnapshot snapshot) {
        boolean isNew = !repository.existsById(snapshot.id());
        AccountHolder account = repository.findById(snapshot.id()).orElseGet(AccountHolder::new);
        account.setId(snapshot.id());
        account.setEmailAddress(snapshot.emailAddress());
        account.setMobileNumber(snapshot.mobileNumber());
        account.setDisplayName(snapshot.displayName());
        account.setRole(AccountRole.valueOf(snapshot.role()));
        account.setStatus(AccountStatus.valueOf(snapshot.status()));
        account.setKycApproved(snapshot.kycApproved());
        AccountHolder saved = repository.save(account);
        if (isNew && (saved.getRole() == AccountRole.CLIENT || saved.getRole() == AccountRole.MERCHANT)) {
            createWallet(saved);
        }
        return saved;
    }

    private void createWallet(AccountHolder account) {
        Wallet wallet = new Wallet();
        wallet.setUser(account);
        wallet.setIban(ibanGenerator.generate());
        walletRepository.save(wallet);
    }
}
