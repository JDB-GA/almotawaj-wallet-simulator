package com.almotawaj.wallet.kyc.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseTables.KYC_APPLICATIONS, indexes = {
        @Index(columnList = "user_id, status"),
        @Index(columnList = "cpr_number")
})
@Getter
@Setter
@NoArgsConstructor
public class KycApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationLimits.ENUM_COLUMN_LENGTH)
    private KycApplicationStatus status = KycApplicationStatus.PENDING;

    @Column(nullable = false, length = ValidationLimits.FULL_NAME_MAX)
    private String fullName;

    @Column(nullable = false, length = ValidationLimits.CPR_LENGTH)
    private String cprNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = ValidationLimits.NATIONALITY_LENGTH)
    private String nationality;

    @Column(nullable = false, length = ValidationLimits.ADDRESS_PART_MAX)
    private String block;

    @Column(nullable = false, length = ValidationLimits.ADDRESS_PART_MAX)
    private String road;

    @Column(nullable = false, length = ValidationLimits.ADDRESS_PART_MAX)
    private String building;

    @Column(length = ValidationLimits.ADDRESS_PART_MAX)
    private String flat;

    @Column(nullable = false, length = ValidationLimits.AREA_MAX)
    private String area;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KycDocument> documents = new ArrayList<>();

    @Column(length = ValidationLimits.REJECTION_REASON_MAX)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private Instant reviewedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public void addDocument(KycDocument document) {
        document.setApplication(this);
        documents.add(document);
    }
}
