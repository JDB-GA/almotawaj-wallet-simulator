package com.almotawaj.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.KYC_DOCUMENTS,
        uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "type"}))
@Getter
@Setter
@NoArgsConstructor
public class KycDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private KycApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private KycDocumentType type;

    private LocalDate expiryDate;

    @Column(nullable = false, unique = true, length = ValidationLimits.STORAGE_KEY_MAX)
    private String storageKey;

    @Column(nullable = false, length = ValidationLimits.CONTENT_TYPE_MAX)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
