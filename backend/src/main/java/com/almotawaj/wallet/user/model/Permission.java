package com.almotawaj.wallet.user.model;

import java.util.EnumSet;
import java.util.Set;

public enum Permission {
    KYC_REVIEW(UserRole.ADMIN),
    USER_MANAGE(UserRole.ADMIN),
    STATISTICS_VIEW(UserRole.ADMIN);

    private final Set<UserRole> grantableTo;

    Permission(UserRole... grantableTo) {
        this.grantableTo = Set.of(grantableTo);
    }

    public boolean isGrantableTo(UserRole role) {
        return grantableTo.contains(role);
    }

    public static Set<Permission> effectiveFor(User user) {
        return user.getRole() == UserRole.SUPER_ADMIN ? EnumSet.allOf(Permission.class) : Set.copyOf(user.getPermissions());
    }
}
