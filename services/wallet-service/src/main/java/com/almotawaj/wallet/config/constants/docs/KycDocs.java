package com.almotawaj.wallet.config.constants.docs;

public final class KycDocs {
    public static final String TAG = "KYC";
    public static final String TAG_DESCRIPTION = "Client identity verification (CLIENT role, verified email)";

    public static final String MINE = "Get my latest application";
    public static final String MINE_DESCRIPTION = "Returns the client's most recent KYC application with its documents.";
    public static final String MINE_OK = "Latest application";
    public static final String MINE_NOT_FOUND = "The client has not submitted an application yet";

    public static final String SUBMIT = "Submit an application";
    public static final String SUBMIT_DESCRIPTION = "Multipart form with personal details, address, CPR and passport (PDF, max 5 MB) and a personal photo (JPEG/PNG, max 5 MB). File types are checked by content. Applicant must be 18+ and both documents unexpired.";
    public static final String SUBMIT_CREATED = "Application submitted; status PENDING";
    public static final String SUBMIT_BAD_REQUEST = "Invalid fields or files (VALIDATION_FAILED, FILE_TYPE_INVALID, FILE_REQUIRED, FILE_TOO_LARGE)";
    public static final String SUBMIT_CONFLICT = "Already pending (KYC_ALREADY_PENDING), already approved (KYC_ALREADY_APPROVED) or CPR used by another account (CPR_ALREADY_USED)";
    public static final String SUBMIT_UNPROCESSABLE = "Under 18 (KYC_UNDERAGE) or an expired document (DOCUMENT_EXPIRED)";
    public static final String SUBMIT_TOO_LARGE = "Upload exceeds 5 MB per file or 16 MB per request";

    public static final String MY_DOCUMENT = "Download one of my documents";
    public static final String MY_DOCUMENT_DESCRIPTION = "Streams the file inline. Only documents belonging to the signed-in client are returned.";
    public static final String DOCUMENT_OK = "The file content";
    public static final String DOCUMENT_NOT_FOUND = "Document not found or not owned by the caller";

    private KycDocs() {
    }
}
