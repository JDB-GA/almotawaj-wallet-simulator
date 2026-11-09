package com.almotawaj.wallet.notification.email.model;

import java.util.UUID;

public record EmailRequest(
        UUID eventId,
        EmailType type,
        UUID userId,
        String email,
        String locale,
        String code,
        String kycStatus,
        String reason
) {
    public enum EmailType {
        VERIFICATION_CODE, PASSWORD_RESET, INVITATION, KYC_DECISION
    }
}
