package com.almotawaj.wallet.voucher.model;

import com.almotawaj.wallet.checkout.model.CheckoutSession;
import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.VOUCHER_PURCHASES, indexes = {
        @Index(columnList = "voucher_id, buyer_id"),
        @Index(columnList = "buyer_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class VoucherPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false, updatable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false, updatable = false)
    private User buyer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = false, unique = true)
    private WalletTransaction transaction;

    @Column(nullable = false, updatable = false, unique = true, length = ValidationLimits.VOUCHER_CODE_LENGTH)
    private String code;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'UNUSED'")
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private VoucherPurchaseStatus status = VoucherPurchaseStatus.UNUSED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeemed_checkout_id")
    private CheckoutSession redeemedCheckout;

    private Instant redeemedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}