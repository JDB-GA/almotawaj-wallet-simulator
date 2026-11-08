package com.almotawaj.wallet.config.constants.docs;

public final class AdminStatisticsDocs {
    public static final String TAG = "Admin Statistics";
    public static final String TAG_DESCRIPTION = "System totals and every wallet transaction (requires STATISTICS_VIEW)";

    public static final String USERS = "Get user statistics";
    public static final String USERS_DESCRIPTION = "Total number of accounts and the number of accounts per role.";
    public static final String USERS_OK = "User statistics";

    public static final String TRANSACTIONS = "Get transaction statistics";
    public static final String TRANSACTIONS_DESCRIPTION = "Number of ledger entries and the credited and debited totals that match the filters.";
    public static final String TRANSACTIONS_OK = "Transaction statistics";

    public static final String LIST = "List all transactions";
    public static final String LIST_DESCRIPTION = "Every wallet's ledger entries with the owner's email. Search also matches the owner's email. Paged and sortable (default createdAt,desc).";
    public static final String LIST_OK = "Page of transactions";
    public static final String BAD_REQUEST = "Invalid filter, page or sort parameter";

    private AdminStatisticsDocs() {
    }
}
