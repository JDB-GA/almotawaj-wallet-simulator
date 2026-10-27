package com.almotawaj.wallet.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.WALLET_TRANSACTIONS, indexes = @Index(columnList = "wallet_id, created_at"))
@Getter
@Setter
@NoArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @Column(nullable = false, unique = true, updatable = false, length = ValidationLimits.TRANSACTION_REFERENCE_MAX)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 10)
    private TransactionDirection direction;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal balanceAfter;

    @Column(nullable = false, updatable = false, length = ValidationLimits.COUNTERPARTY_NAME_MAX)
    private String counterpartyName;

    @Column(updatable = false, length = ValidationLimits.IBAN_MAX)
    private String counterpartyIban;

    @Column(updatable = false, length = ValidationLimits.BIC_MAX)
    private String counterpartyBic;

    @Column(updatable = false, length = ValidationLimits.EMAIL_MAX)
    private String counterpartyEmail;

    @Column(updatable = false, length = ValidationLimits.MOBILE_MAX)
    private String counterpartyMobile;

    @Column(updatable = false, length = ValidationLimits.TRANSACTION_DESCRIPTION_MAX)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
