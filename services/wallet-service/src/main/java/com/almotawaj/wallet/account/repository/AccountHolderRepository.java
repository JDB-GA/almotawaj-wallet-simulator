package com.almotawaj.wallet.account.repository;

import com.almotawaj.wallet.account.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, UUID> {
    Optional<AccountHolder> findByEmailAddressIgnoreCase(String emailAddress);

    Optional<AccountHolder> findByMobileNumber(String mobileNumber);
}