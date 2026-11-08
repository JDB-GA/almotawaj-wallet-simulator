package com.almotawaj.wallet.config.constants.docs;

public final class KycReviewDocs {
    public static final String TAG = "KYC Review";
    public static final String TAG_DESCRIPTION = "Reviewing KYC applications (requires the KYC_REVIEW permission)";

    public static final String LIST = "List applications";
    public static final String LIST_DESCRIPTION = "Paged list, optionally filtered by status. Supports page, size (max 100) and sort (default createdAt,desc).";
    public static final String LIST_OK = "Page of applications";

    public static final String GET = "Get an application";
    public static final String GET_DESCRIPTION = "Application with documents, applicant contact details and reviewer.";
    public static final String GET_OK = "Application details";
    public static final String NOT_FOUND = "Application not found";

    private KycReviewDocs() {
    }
}
