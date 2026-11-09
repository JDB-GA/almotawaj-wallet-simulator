package com.almotawaj.wallet.audit.model;

import com.almotawaj.wallet.audit.config.AuditConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = AuditConstants.AUDIT_LOGS_TABLE, indexes = {
        @Index(columnList = "created_at"),
        @Index(columnList = "action"),
        @Index(columnList = "target_id")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID eventId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(length = AuditConstants.EMAIL_MAX)
    private String actorEmail;

    @Column(nullable = false, length = AuditConstants.ACTION_MAX)
    private String action;

    @Column(nullable = false, length = AuditConstants.ACTION_MAX)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(length = AuditConstants.DETAILS_MAX)
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
