package com.almotawaj.wallet.account.client;

import java.util.UUID;

/** Account data as returned by identity-service's internal API. */
public record AccountSnapshot(
        UUID id,
        String emailAddress,
        String mobileNumber,
        String displayName,
        String role,
        String status,
        boolean kycApproved
) {
}
