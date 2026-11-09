package com.almotawaj.wallet.identity.event;

import java.util.UUID;

public record AccountEvent(UUID eventId, UUID userId) {
}
