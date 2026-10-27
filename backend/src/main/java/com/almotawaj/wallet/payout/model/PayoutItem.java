package com.almotawaj.wallet.payout.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.PAYOUT_ITEMS, indexes = {
        @Index(columnList = "batch_id")
})
@Getter
@Setter
@NoArgsConstructor
public class PayoutItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false, updatable = false)
    private PayoutBatch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, updatable = false)
    private User recipient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", unique = true)
    private WalletTransaction transaction;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private PayoutItemStatus status = PayoutItemStatus.PENDING;

    @Column(length = ValidationLimits.PAYOUT_FAILURE_REASON_MAX)
    private String failureReason;

    private Instant processedAt;
}