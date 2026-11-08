package com.almotawaj.wallet.config.constants.docs;

public final class KycDocs {
    public static final String TAG = "KYC";
    public static final String TAG_DESCRIPTION = "Client identity verification (CLIENT role, verified email)";

    public static final String MINE = "Get my latest application";
    public static final String MINE_DESCRIPTION = "Returns the client's most recent KYC application with its documents.";
    public static final String MINE_OK = "Latest application";

    public static final String DOCUMENT_NOT_FOUND = "Document not found or not owned by the caller";

    private KycDocs() {
    }
}
