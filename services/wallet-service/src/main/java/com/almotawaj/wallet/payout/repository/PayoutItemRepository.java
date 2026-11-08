package com.almotawaj.wallet.payout.repository;

import com.almotawaj.wallet.payout.model.PayoutItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PayoutItemRepository extends JpaRepository<PayoutItem, UUID> {
    @EntityGraph(attributePaths = {"recipient"})
    List<PayoutItem> findByBatchId(UUID batchId);

    @Modifying
    @Query("""
            delete from PayoutItem i
            where i.recipient.id in :userIds or i.batch.id in (select b.id from PayoutBatch b where b.merchant.id in :userIds)""")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
