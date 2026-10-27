package com.almotawaj.wallet.user.model.response;

import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.user.model.Permission;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String mobileNumber,
        UserRole role,
        UserStatus status,
        KycStatus kycStatus,
        Set<Permission> permissions,
        boolean emailVerified,
        boolean mobileVerified
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmailAddress(),
                user.getMobileNumber(),
                user.getRole(),
                user.getStatus(),
                user.getKycStatus(),
                Permission.effectiveFor(user),
                user.isEmailVerified(),
                user.isMobileVerified()
        );
    }
}
