package com.almotawaj.wallet.checkout.event;

import java.util.UUID;

public record CheckoutStatusChangedEvent(UUID sessionId) {
}
