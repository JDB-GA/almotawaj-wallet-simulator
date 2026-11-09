package com.almotawaj.wallet.account.model;

import com.almotawaj.wallet.config.constants.DatabaseTables;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = DatabaseTables.USERS)
@Getter
@Setter
@NoArgsConstructor
public class AccountHolder {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    private String displayName;

    @Formula(DatabaseTables.APPROVED_KYC_NAME)
    private String kycFullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private String kycStatus;

    public boolean isKycApproved() {
        return DatabaseTables.KYC_APPROVED.equals(kycStatus);
    }

    public void setKycApproved(boolean approved) {
        kycStatus = approved ? DatabaseTables.KYC_APPROVED : DatabaseTables.KYC_NOT_APPROVED;
    }
}
