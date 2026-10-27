package com.almotawaj.wallet.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
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
@Table(name = DatabaseTables.WALLETS)
@Getter
@Setter
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Column(nullable = false, unique = true, updatable = false, length = WalletConstants.IBAN_LENGTH)
    private String iban;

    @Column(nullable = false, precision = ValidationLimits.MONEY_PRECISION, scale = ValidationLimits.MONEY_SCALE)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false, length = 3)
    private String currency = WalletConstants.CURRENCY;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
