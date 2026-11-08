package com.almotawaj.wallet.account.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "account_holders")
@Getter
@Setter
@NoArgsConstructor
public class AccountHolder {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(unique = true)
    private String mobileNumber;

    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private boolean kycApproved;
}