package com.almotawaj.wallet.user.model.response;

import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record AdminUserResponse(
        UUID id,
        String email,
        String mobileNumber,
        String fullName,
        UserRole role,
        UserStatus status,
        KycStatus kycStatus,
        boolean emailVerified,
        Set<Permission> permissions,
        Set<Permission> grantablePermissions,
        Instant createdAt,
        Instant updatedAt
) {
    public static AdminUserResponse from(User user, String fullName) {
        Set<Permission> grantable = Arrays.stream(Permission.values())
                .filter(permission -> permission.isGrantableTo(user.getRole()))
                .collect(Collectors.toSet());
        return new AdminUserResponse(user.getId(), user.getEmailAddress(), user.getMobileNumber(), fullName,
                user.getRole(), user.getStatus(), user.getKycStatus(), user.isEmailVerified(),
                Permission.effectiveFor(user), grantable, user.getCreatedAt(), user.getUpdatedAt());
    }
}
