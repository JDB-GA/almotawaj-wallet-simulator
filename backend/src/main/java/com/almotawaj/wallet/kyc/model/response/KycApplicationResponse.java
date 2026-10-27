package com.almotawaj.wallet.kyc.model.response;

import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record KycApplicationResponse(
        UUID id,
        KycApplicationStatus status,
        String fullName,
        String cprNumber,
        LocalDate dateOfBirth,
        String nationality,
        String block,
        String road,
        String building,
        String flat,
        String area,
        String rejectionReason,
        Instant submittedAt,
        Instant reviewedAt,
        List<KycDocumentResponse> documents
) {
    public static KycApplicationResponse from(KycApplication application) {
        return new KycApplicationResponse(
                application.getId(),
                application.getStatus(),
                application.getFullName(),
                application.getCprNumber(),
                application.getDateOfBirth(),
                application.getNationality(),
                application.getBlock(),
                application.getRoad(),
                application.getBuilding(),
                application.getFlat(),
                application.getArea(),
                application.getRejectionReason(),
                application.getCreatedAt(),
                application.getReviewedAt(),
                application.getDocuments().stream().map(KycDocumentResponse::from).toList()
        );
    }
}
