package com.almotawaj.wallet.account.client;

import java.util.UUID;

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
