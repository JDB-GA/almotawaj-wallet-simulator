package com.almotawaj.wallet.config.constants.docs;

public final class SystemDocs {
    public static final String AUDIT_TAG = "Audit Log";
    public static final String AUDIT_TAG_DESCRIPTION = "Trail of account, administrative and money actions (super admin only)";
    public static final String AUDIT_LIST = "List audit entries";
    public static final String AUDIT_LIST_DESCRIPTION = "Newest first. Filter by action or by the id of the affected user, application, payment request or transaction.";
    public static final String AUDIT_LIST_OK = "Page of audit entries";

    public static final String SEED_TAG = "Seeding";
    public static final String SEED_TAG_DESCRIPTION = "Demo data for development and grading";
    public static final String SEED = "Seed demo data";
    public static final String SEED_DESCRIPTION = "Creates demo accounts for every role and every KYC state. Send `Authorization: Bearer <SEED_TOKEN>`. Safe to run repeatedly. Disabled (404) when SEED_TOKEN is not configured.";
    public static final String SEED_CREATED = "Demo data present";
    public static final String SEED_FORBIDDEN = "Missing or wrong seed token";
    public static final String SEED_NOT_FOUND = "Seeding is disabled because SEED_TOKEN is not set";
    public static final String SEED_UNPROCESSABLE = "SEED_PASSWORD is not set (SEED_PASSWORD_MISSING)";
    public static final String CLEAN_SEED_DATA = "Remove seeded demo data";
    public static final String CLEAN_SEED_DATA_DESCRIPTION = "Removes only the accounts defined by the seed configuration with their wallets, transactions,"
            + " payment requests and verification records. Requires the super-admin role.";
    public static final String CLEAN_SEED_DATA_NO_CONTENT = "Seeded demo data removed";

    private SystemDocs() {
    }
}
