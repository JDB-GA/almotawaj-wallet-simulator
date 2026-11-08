package com.almotawaj.wallet.account.repository;

import com.almotawaj.wallet.account.model.AccountHolder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from AccountHolder account where account.id = :id")
    Optional<AccountHolder> findByIdForUpdate(UUID id);

    Optional<AccountHolder> findByEmailAddressIgnoreCase(String emailAddress);

    Optional<AccountHolder> findByMobileNumber(String mobileNumber);

    List<AccountHolder> findAllByEmailAddressIn(Collection<String> emailAddresses);
}