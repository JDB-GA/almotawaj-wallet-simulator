package com.almotawaj.wallet.identity.kyc.model.response;

import com.almotawaj.wallet.identity.kyc.model.KycApplication;
import com.almotawaj.wallet.identity.user.model.User;

public record KycReviewResponse(
        KycApplicationResponse application,
        String applicantEmail,
        String applicantMobileNumber,
        String reviewedByEmail
) {
    public static KycReviewResponse from(KycApplication application) {
        User reviewer = application.getReviewedBy();
        return new KycReviewResponse(
                KycApplicationResponse.from(application),
                application.getUser().getEmailAddress(),
                application.getUser().getMobileNumber(),
                reviewer == null ? null : reviewer.getEmailAddress()
        );
    }
}
