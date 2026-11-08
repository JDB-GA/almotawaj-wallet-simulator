package com.almotawaj.wallet.checkout.model;

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
@Table(name = DatabaseTables.MERCHANT_API_KEYS, indexes = @Index(columnList = "merchant_id, created_at"))
@Getter
@Setter
@NoArgsConstructor
public class MerchantApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, updatable = false)
    private User merchant;

    @Column(nullable = false, updatable = false, length = ValidationLimits.API_KEY_NAME_MAX)
    private String name;

    @Column(nullable = false, updatable = false, length = ValidationLimits.API_KEY_PREFIX_LENGTH)
    private String keyPrefix;

    @Column(nullable = false, unique = true, updatable = false, length = ValidationLimits.API_KEY_HASH_LENGTH)
    private String keyHash;

    private Instant lastUsedAt;

    private Instant revokedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
