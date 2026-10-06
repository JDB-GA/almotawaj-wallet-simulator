package com.almotawaj.wallet.event;

import java.util.UUID;

public record CheckoutStatusChangedEvent(UUID sessionId) {
}
