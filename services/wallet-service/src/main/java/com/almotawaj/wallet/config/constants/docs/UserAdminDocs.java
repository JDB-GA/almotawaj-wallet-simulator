package com.almotawaj.wallet.config.constants.docs;

public final class UserAdminDocs {
    public static final String TAG = "User Management";
    public static final String TAG_DESCRIPTION = "Managing users (requires USER_MANAGE). Admins manage clients and merchants; only the super admin manages admins.";

    public static final String LIST = "Search users";
    public static final String LIST_DESCRIPTION = "Search matches email, mobile or KYC full name. Filter by role and status. Paged and sortable (default createdAt,desc).";
    public static final String LIST_OK = "Page of users";

    public static final String CREATE = "Create a user";
    public static final String CREATE_DESCRIPTION = "Creates a CLIENT, MERCHANT or (super admin only) ADMIN and emails a 48-hour invitation code to set a password. Permissions can only be set by the super admin.";
    public static final String CREATE_CREATED = "User created; invitation sent";
    public static final String CREATE_UNPROCESSABLE = "Role cannot be assigned (ROLE_NOT_ASSIGNABLE) or permission not valid for the role (PERMISSION_NOT_GRANTABLE)";

    public static final String GET = "Get a user";
    public static final String GET_DESCRIPTION = "User details with KYC name, explicit permissions and the permissions the role can receive.";
    public static final String NOT_FOUND = "User not found";

    public static final String UPDATE = "Update contact details";
    public static final String UPDATE_DESCRIPTION = "Changes email and/or mobile. A new email must be verified again.";

    public static final String TRANSACTIONS = "List a user's transactions";
    public static final String TRANSACTIONS_DESCRIPTION = "Ledger entries of the user's wallet with the same filters as the wallet history. Paged and sortable (default createdAt,desc).";
    public static final String TRANSACTIONS_OK = "Page of transactions";
    public static final String TRANSACTIONS_BAD_REQUEST = "Invalid filter, page or sort parameter";

    public static final String PERMISSIONS_TAG = "Permissions";

    private UserAdminDocs() {
    }
}
