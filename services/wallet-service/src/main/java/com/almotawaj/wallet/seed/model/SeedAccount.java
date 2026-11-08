package com.almotawaj.wallet.seed.model;

import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.UserRole;

import java.util.Set;

public record SeedAccount(
        String email,
        String localMobile,
        UserRole role,
        Set<Permission> permissions,
        KycApplicationStatus kycStatus,
        String fullName,
        String cprNumber
) {
}
