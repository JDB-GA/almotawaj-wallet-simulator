package com.almotawaj.wallet.config.constants.docs;

public final class KycReviewDocs {
    public static final String TAG = "KYC Review";
    public static final String TAG_DESCRIPTION = "Reviewing KYC applications (requires the KYC_REVIEW permission)";

    public static final String LIST = "List applications";
    public static final String LIST_DESCRIPTION = "Paged list, optionally filtered by status. Supports page, size (max 100) and sort (default createdAt,desc).";
    public static final String LIST_OK = "Page of applications";
    public static final String LIST_BAD_REQUEST = "Unknown status or sort property (INVALID_REQUEST_PARAMETER)";

    public static final String GET = "Get an application";
    public static final String GET_DESCRIPTION = "Application with documents, applicant contact details and reviewer.";
    public static final String GET_OK = "Application details";
    public static final String NOT_FOUND = "Application not found";

    public static final String DOCUMENT = "Download an application document";
    public static final String DOCUMENT_DESCRIPTION = "Streams a document; it must belong to the given application.";

    public static final String APPROVE = "Approve an application";
    public static final String APPROVE_DESCRIPTION = "Marks a PENDING application and its user APPROVED and emails the client (English and Arabic).";
    public static final String REJECT = "Reject an application";
    public static final String REJECT_DESCRIPTION = "Marks a PENDING application REJECTED with a required reason (max 500 characters) and emails the client.";
    public static final String DECISION_OK = "Updated application";
    public static final String ALREADY_REVIEWED = "Application is no longer PENDING (KYC_ALREADY_REVIEWED)";

    private KycReviewDocs() {
    }
}
