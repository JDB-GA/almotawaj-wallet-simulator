package com.almotawaj.wallet.auth.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.OTP_CHALLENGES, indexes = @Index(columnList = "user_id, purpose"))
@Getter
@Setter
@NoArgsConstructor
public class OtpChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpPurpose purpose;

    @Column(nullable = false, length = 64)
    private String codeHash;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant consumedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
