package com.almotawaj.wallet.wallet.repository;

import com.almotawaj.wallet.wallet.model.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(UUID userId);

    boolean existsByIban(String iban);

    @EntityGraph(attributePaths = "user")
    Optional<Wallet> findByIban(String iban);

    @Query("""
            select w from Wallet w join fetch w.user u
            where u.id <> :excludedUserId and u.status = com.almotawaj.wallet.user.model.UserStatus.ACTIVE
            and (u.role = com.almotawaj.wallet.user.model.UserRole.MERCHANT
                 or (u.role = com.almotawaj.wallet.user.model.UserRole.CLIENT
                     and u.kycStatus = com.almotawaj.wallet.kyc.model.KycStatus.APPROVED))
            and (lower(u.emailAddress) like :prefix escape '\\' or u.mobileNumber like :mobilePrefix escape '\\')
            order by u.emailAddress""")
    List<Wallet> findRecipientSuggestions(UUID excludedUserId, String prefix, String mobilePrefix, Pageable pageable);

    @Modifying
    @Query("delete from Wallet w where w.user.id in :userIds")
    void deleteAllByUserIdIn(Collection<UUID> userIds);

    @Query("select w.balance from Wallet w where w.user.id = :userId")
    BigDecimal readBalance(UUID userId);
}
