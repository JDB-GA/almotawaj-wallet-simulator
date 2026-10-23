package com.almotawaj.wallet.repository;

import com.almotawaj.wallet.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @EntityGraph(attributePaths = "actor")
    Page<AuditLog> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "actor")
    Page<AuditLog> findByAction(String action, Pageable pageable);

    @EntityGraph(attributePaths = "actor")
    Page<AuditLog> findByTargetId(UUID targetId, Pageable pageable);

    @Modifying
    @Query("update AuditLog log set log.actor = null where log.actor.id in :userIds")
    void clearActorsByIdIn(Collection<UUID> userIds);

    @Modifying
    @Query("delete from AuditLog log where log.actor.id in :userIds")
    void deleteAllByActorIdIn(Collection<UUID> userIds);
}
