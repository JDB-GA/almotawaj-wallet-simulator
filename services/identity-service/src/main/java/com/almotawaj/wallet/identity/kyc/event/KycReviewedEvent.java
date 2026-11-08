package com.almotawaj.wallet.identity.kyc.event;

import com.almotawaj.wallet.identity.kyc.model.KycApplicationStatus;

import java.util.UUID;

public record KycReviewedEvent(UUID userId, String email, KycApplicationStatus status, String reason) {
}
