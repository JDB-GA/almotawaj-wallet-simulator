package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.kyc.model.KycStatus;
import com.almotawaj.wallet.identity.user.model.User;

import java.util.UUID;

/** The part of a user that other services may copy. Never includes the password. */
public record AccountSnapshot(
        UUID id,
        String emailAddress,
        String mobileNumber,
        String displayName,
        String role,
        String status,
        boolean kycApproved
) {
    public static AccountSnapshot from(User user, String displayName) {
        return new AccountSnapshot(user.getId(), user.getEmailAddress(), user.getMobileNumber(), displayName,
                user.getRole().name(), user.getStatus().name(), user.getKycStatus() == KycStatus.APPROVED);
    }
}
