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
    public static final String USER_OK = "User details";
    public static final String NOT_FOUND = "User not found";

    public static final String UPDATE = "Update contact details";
    public static final String UPDATE_DESCRIPTION = "Changes email and/or mobile. A new email must be verified again.";
    public static final String DUPLICATE_CONTACT = "Email or mobile already registered";

    public static final String SUSPEND = "Suspend a user";
    public static final String SUSPEND_DESCRIPTION = "ACTIVE or LOCKED to SUSPENDED. The user is signed out immediately.";
    public static final String REACTIVATE = "Reactivate a user";
    public static final String REACTIVATE_DESCRIPTION = "SUSPENDED or LOCKED to ACTIVE.";
    public static final String CLOSE = "Close a user (soft delete)";
    public static final String CLOSE_DESCRIPTION = "Marks the account CLOSED. Data is kept; the account can no longer sign in or be changed.";
    public static final String CLOSE_NO_CONTENT = "Account closed";
    public static final String STATUS_UNPROCESSABLE = "Invalid transition (INVALID_STATUS_TRANSITION), closed account (USER_CLOSED) or own account (CANNOT_MANAGE_SELF)";

    public static final String TRANSACTIONS = "List a user's transactions";
    public static final String TRANSACTIONS_DESCRIPTION = "Ledger entries of the user's wallet with the same filters as the wallet history. Paged and sortable (default createdAt,desc).";
    public static final String TRANSACTIONS_OK = "Page of transactions";
    public static final String TRANSACTIONS_BAD_REQUEST = "Invalid filter, page or sort parameter";

    public static final String PERMISSIONS_TAG = "Permissions";
    public static final String PERMISSIONS_TAG_DESCRIPTION = "Granting and revoking permissions (super admin only)";
    public static final String GRANT = "Grant a permission";
    public static final String GRANT_DESCRIPTION = "Idempotent. Every permission can only be granted to admins.";
    public static final String REVOKE = "Revoke a permission";
    public static final String REVOKE_DESCRIPTION = "Idempotent. Takes effect on the user's next request.";
    public static final String PERMISSION_UNPROCESSABLE = "Not grantable to the role (PERMISSION_NOT_GRANTABLE) or target is a super admin (SUPER_ADMIN_PERMISSIONS_FIXED)";

    private UserAdminDocs() {
    }
}
