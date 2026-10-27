package com.almotawaj.wallet.kyc.model.response;

import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public record KycApplicationSummaryResponse(
        UUID id,
        KycApplicationStatus status,
        String fullName,
        String cprNumber,
        String applicantEmail,
        Instant submittedAt,
        Instant reviewedAt
) {
    public static KycApplicationSummaryResponse from(KycApplication application) {
        return new KycApplicationSummaryResponse(
                application.getId(),
                application.getStatus(),
                application.getFullName(),
                application.getCprNumber(),
                application.getUser().getEmailAddress(),
                application.getCreatedAt(),
                application.getReviewedAt()
        );
    }
}
