package com.almotawaj.wallet.audit.repository;

import com.almotawaj.wallet.audit.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByAction(String action, Pageable pageable);

    Page<AuditLog> findByTargetId(UUID targetId, Pageable pageable);
}
