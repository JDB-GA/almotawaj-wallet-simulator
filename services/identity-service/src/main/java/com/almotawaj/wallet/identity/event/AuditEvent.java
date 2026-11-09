package com.almotawaj.wallet.identity.event;

import java.time.Instant;
import java.util.UUID;

public record AuditEvent(
        UUID eventId,
        UUID actorId,
        String actorEmail,
        String action,
        String targetType,
        UUID targetId,
        String details,
        Instant occurredAt
) {
}
