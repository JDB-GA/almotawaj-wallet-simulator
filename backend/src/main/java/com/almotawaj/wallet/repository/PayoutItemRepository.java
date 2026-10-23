package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.PayoutItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayoutItemRepository extends JpaRepository<PayoutItem, UUID> {
    @EntityGraph(attributePaths = {"recipient"})
    List<PayoutItem> findByBatchId(UUID batchId);
}
