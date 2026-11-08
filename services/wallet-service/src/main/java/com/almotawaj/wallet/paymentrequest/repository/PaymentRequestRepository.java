package com.almotawaj.wallet.paymentrequest.repository;

import com.almotawaj.wallet.paymentrequest.model.PaymentRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, UUID> {
    @EntityGraph(attributePaths = {"requester", "payer"})
    @Query("select p from PaymentRequest p where p.requester.id = :userId or p.payer.id = :userId")
    Page<PaymentRequest> findByRequesterIdOrPayerId(UUID userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentRequest p where p.id = :id and p.payer.id = :payerId")
    Optional<PaymentRequest> findByIdAndPayerIdForUpdate(UUID id, UUID payerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentRequest p where p.id = :id and p.requester.id = :requesterId")
    Optional<PaymentRequest> findByIdAndRequesterIdForUpdate(UUID id, UUID requesterId);

    @Modifying
    @Query("delete from PaymentRequest p where p.requester.id in :userIds or p.payer.id in :userIds")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
