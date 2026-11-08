package com.almotawaj.wallet.wallet.repository;

import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID>,
        JpaSpecificationExecutor<WalletTransaction> {
    @Override
    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    Page<WalletTransaction> findAll(Specification<WalletTransaction> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"wallet", "wallet.user"})
    Optional<WalletTransaction> findByIdAndWalletUserId(UUID id, UUID userId);

    Optional<WalletTransaction> findFirstByWalletIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID walletId, Instant before);

    @Query("""
            select coalesce(sum(t.amount), 0) from WalletTransaction t
            where t.wallet.id = :walletId and t.type = :type and t.createdAt >= :since""")
    BigDecimal sumAmountSince(UUID walletId, TransactionType type, Instant since);

    @Modifying
    @Query("delete from WalletTransaction t where t.wallet.user.id in :userIds")
    void deleteAllByWalletUserIdIn(Collection<UUID> userIds);
}
