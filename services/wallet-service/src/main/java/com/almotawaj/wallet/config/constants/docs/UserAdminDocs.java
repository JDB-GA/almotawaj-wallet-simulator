package com.almotawaj.wallet.config.constants.docs;

public final class UserAdminDocs {
    public static final String TAG = "User Management";
    public static final String TAG_DESCRIPTION = "Managing users (requires USER_MANAGE). Admins manage clients and merchants; only the super admin manages admins.";

    public static final String NOT_FOUND = "User not found";

    public static final String TRANSACTIONS = "List a user's transactions";
    public static final String TRANSACTIONS_DESCRIPTION = "Ledger entries of the user's wallet with the same filters as the wallet history. Paged and sortable (default createdAt,desc).";
    public static final String TRANSACTIONS_OK = "Page of transactions";
    public static final String TRANSACTIONS_BAD_REQUEST = "Invalid filter, page or sort parameter";

    private UserAdminDocs() {
    }
}
