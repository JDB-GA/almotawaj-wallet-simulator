package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.PayoutBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PayoutBatchRepository extends JpaRepository<PayoutBatch, UUID> {
    Page<PayoutBatch> findByMerchantId(UUID merchantId, Pageable pageable);

    Optional<PayoutBatch> findByIdAndMerchantId(UUID id, UUID merchantId);

    @Modifying
    @Query("update PayoutBatch b set b.paidCount = b.paidCount + 1 where b.id = :id")
    void incrementPaid(UUID id);

    @Modifying
    @Query("update PayoutBatch b set b.failedCount = b.failedCount + 1 where b.id = :id")
    void incrementFailed(UUID id);

    @Modifying
    @Query("delete from PayoutBatch b where b.merchant.id in :userIds")
    void deleteAllByMerchantIdIn(Collection<UUID> userIds);
}
