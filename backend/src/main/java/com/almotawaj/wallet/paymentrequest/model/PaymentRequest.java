package com.almotawaj.wallet.paymentrequest.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
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
@Table(name = DatabaseTables.PAYMENT_REQUESTS, indexes = {
        @Index(columnList = "requester_id, created_at"),
        @Index(columnList = "payer_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false, updatable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payer_id", nullable = false, updatable = false)
    private User payer;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal amount;

    @Column(updatable = false, length = ValidationLimits.TRANSACTION_DESCRIPTION_MAX)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private PaymentRequestStatus status = PaymentRequestStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
