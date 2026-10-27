package com.almotawaj.wallet.audit.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.AUDIT_LOGS, indexes = {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(nullable = false, length = ValidationLimits.AUDIT_ACTION_MAX)
    private String action;

    @Column(nullable = false, length = ValidationLimits.AUDIT_ACTION_MAX)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(length = ValidationLimits.AUDIT_DETAILS_MAX)
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
