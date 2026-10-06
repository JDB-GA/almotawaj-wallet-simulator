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
@Table(name = DatabaseTables.CHECKOUT_SESSIONS,
        uniqueConstraints = @UniqueConstraint(columnNames = {"merchant_id", "order_reference"}),
        indexes = {@Index(columnList = "merchant_id, created_at"), @Index(columnList = "status, expires_at")})
@Getter
@Setter
@NoArgsConstructor
public class CheckoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, updatable = false)
    private User merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private User payer;

    @Column(name = "order_reference", nullable = false, updatable = false, length = ValidationLimits.ORDER_REFERENCE_MAX)
    private String orderReference;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal amount;

    @Column(updatable = false, length = ValidationLimits.TRANSACTION_DESCRIPTION_MAX)
    private String description;

    @Column(updatable = false, length = ValidationLimits.URL_MAX)
    private String returnUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckoutStatus status = CheckoutStatus.PENDING;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    private Instant paidAt;

    private Instant refundedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public CheckoutStatus statusAt(Instant now) {
        return status == CheckoutStatus.PENDING && !expiresAt.isAfter(now) ? CheckoutStatus.EXPIRED : status;
    }
}
