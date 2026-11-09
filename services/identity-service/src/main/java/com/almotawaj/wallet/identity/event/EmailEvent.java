package com.almotawaj.wallet.identity.event;

import java.util.UUID;

public record EmailEvent(
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

    public static EmailEvent code(EmailType type, UUID userId, String email, String code, String locale) {
        return new EmailEvent(UUID.randomUUID(), type, userId, email, locale, code, null, null);
    }

    public static EmailEvent kycDecision(UUID userId, String email, String kycStatus, String reason) {
        return new EmailEvent(UUID.randomUUID(), EmailType.KYC_DECISION, userId, email, null, null, kycStatus, reason);
    }
}
