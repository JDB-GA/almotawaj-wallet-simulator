package com.almotawaj.wallet.user.model.response;

import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.UserRole;
import com.almotawaj.wallet.user.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminUserSummaryResponse(
        UUID id,
        String email,
        String mobileNumber,
        UserRole role,
        UserStatus status,
        KycStatus kycStatus,
        Instant createdAt
) {
    public static AdminUserSummaryResponse from(User user) {
        return new AdminUserSummaryResponse(user.getId(), user.getEmailAddress(), user.getMobileNumber(),
                user.getRole(), user.getStatus(), user.getKycStatus(), user.getCreatedAt());
    }
}
