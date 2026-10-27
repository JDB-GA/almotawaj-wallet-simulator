package com.almotawaj.wallet.kyc.event;

import com.almotawaj.wallet.kyc.model.KycApplicationStatus;

import java.util.UUID;

public record KycReviewedEvent(UUID userId, String email, KycApplicationStatus status, String reason) {
}
