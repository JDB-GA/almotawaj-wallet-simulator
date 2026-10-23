package com.almotawaj.wallet.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.USERS)
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = ValidationLimits.EMAIL_MAX)
    private String emailAddress;

    @Column(nullable = false, unique = true, length = ValidationLimits.MOBILE_MAX)
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private UserRole role = UserRole.CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_SUBMITTED'")
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;

    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    @CollectionTable(name = DatabaseTables.USER_PERMISSIONS, joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "permission"}))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 40)
    private Set<Permission> permissions = new HashSet<>();

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean mobileVerified = true;

    private Instant credentialsChangedAt;

    @Column(length = ValidationLimits.COUNTERPARTY_NAME_MAX)
    private String displayName;

    @Column(length = ValidationLimits.STORAGE_KEY_MAX)
    private String pictureKey;

    @Column(length = ValidationLimits.CONTENT_TYPE_MAX)
    private String pictureContentType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
