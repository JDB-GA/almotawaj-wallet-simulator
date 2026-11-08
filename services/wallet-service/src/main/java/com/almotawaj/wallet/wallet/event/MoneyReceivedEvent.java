package com.almotawaj.wallet.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record MoneyReceivedEvent(UUID recipientUserId, BigDecimal amount, String senderName, String reference) {
}
