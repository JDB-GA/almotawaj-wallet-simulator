package com.almotawaj.wallet.payout.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.PAYOUT_BATCHES, indexes = {
        @Index(columnList = "merchant_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class PayoutBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, updatable = false)
    private User merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private PayoutBatchStatus status = PayoutBatchStatus.PROCESSING;

    @Column(nullable = false, updatable = false)
    private int itemCount;

    @Column(nullable = false)
    private int paidCount;

    @Column(nullable = false)
    private int failedCount;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant completedAt;
}