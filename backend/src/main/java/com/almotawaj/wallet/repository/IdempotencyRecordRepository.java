package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {
    Optional<IdempotencyRecord> findByUserIdAndKey(UUID userId, String key);
}
