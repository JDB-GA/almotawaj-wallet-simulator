package com.almotawaj.wallet.identity.seed;

import com.almotawaj.wallet.identity.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.identity.user.model.Permission;
import com.almotawaj.wallet.identity.user.model.UserRole;

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
