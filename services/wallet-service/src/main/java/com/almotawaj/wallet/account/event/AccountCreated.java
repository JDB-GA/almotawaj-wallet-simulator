package com.almotawaj.wallet.account.event;

import java.util.UUID;

public record AccountCreated(UUID eventId, UUID userId) {
}
