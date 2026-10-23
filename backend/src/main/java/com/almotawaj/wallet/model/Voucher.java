package com.almotawaj.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.VOUCHERS, indexes = {
        @Index(columnList = "merchant_id, created_at"),
        @Index(columnList = "status, expires_at")
}, check = @CheckConstraint(name = "vouchers_remaining_not_negative", constraint = "remaining >= 0"))
@Getter
@Setter
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, updatable = false)
    private User merchant;

    @Column(nullable = false, length = ValidationLimits.VOUCHER_TITLE_MAX)
    private String title;

    @Column(nullable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal price;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal faceValue;

    @Column(nullable = false, updatable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remaining;

    @Column(nullable = false)
    private int perClientLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private VoucherStatus status = VoucherStatus.ACTIVE;

    @Column(nullable = false)
    private Instant expiresAt;

    @Version
    private long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}