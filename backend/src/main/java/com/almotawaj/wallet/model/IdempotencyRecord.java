package com.almotawaj.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.IDEMPOTENCY_RECORDS, uniqueConstraints = {
        @UniqueConstraint(name = "idempotency_records_user_key", columnNames = {"user_id", "idempotency_key"})
})
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = ValidationLimits.IDEMPOTENCY_KEY_MAX)
    private String key;

    @Column(nullable = false, updatable = false, length = ValidationLimits.IDEMPOTENCY_ENDPOINT_MAX)
    private String endpoint;

    @Column(nullable = false, updatable = false)
    private int responseStatus;

    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String responseBody;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
