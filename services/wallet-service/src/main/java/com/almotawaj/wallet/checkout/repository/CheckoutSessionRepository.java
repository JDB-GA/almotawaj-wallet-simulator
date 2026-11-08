package com.almotawaj.wallet.checkout.repository;

import com.almotawaj.wallet.checkout.model.CheckoutSession;
import com.almotawaj.wallet.checkout.model.CheckoutStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, UUID> {
    @EntityGraph(attributePaths = "payer")
    @Query("select s from CheckoutSession s where s.merchant.id = :merchantId and (:status is null or s.status = :status)")
    Page<CheckoutSession> findByMerchant(UUID merchantId, CheckoutStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "payer")
    Optional<CheckoutSession> findByIdAndMerchantId(UUID id, UUID merchantId);

    @EntityGraph(attributePaths = "merchant")
    Optional<CheckoutSession> findWithMerchantById(UUID id);

    // Locks to prevent paying twice
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CheckoutSession s where s.id = :id")
    Optional<CheckoutSession> findByIdForUpdate(UUID id);

    boolean existsByMerchantIdAndOrderReference(UUID merchantId, String orderReference);

    List<CheckoutSession> findAllByStatusAndExpiresAtBefore(CheckoutStatus status, Instant instant);

    @Modifying
    @Query("delete from CheckoutSession s where s.merchant.id in :userIds or s.payer.id in :userIds")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
