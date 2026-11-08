package com.almotawaj.wallet.voucher.repository;

import com.almotawaj.wallet.voucher.model.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {
    Optional<IdempotencyRecord> findByUserIdAndKey(UUID userId, String key);

    @Modifying
    @Query("delete from IdempotencyRecord r where r.user.id in :userIds")
    void deleteAllByUserIdIn(Collection<UUID> userIds);
}
