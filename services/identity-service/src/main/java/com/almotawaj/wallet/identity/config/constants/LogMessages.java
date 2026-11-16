package com.almotawaj.wallet.identity.config.constants;

public final class LogMessages {
    public static final String INVALID_JWT = "Invalid JWT: {}";
    public static final String TOKEN_USER_NOT_FOUND = "Token belongs to a user that no longer exists: {}";
    public static final String KYC_SUBMITTED = "User {} submitted KYC application {}";
    public static final String KYC_REVIEWED = "Admin {} marked KYC application {} as {}";
    public static final String PERMISSION_GRANTED = "Super admin {} granted {} to user {}";
    public static final String PERMISSION_REVOKED = "Super admin {} revoked {} from user {}";
    public static final String USER_STATUS_CHANGED = "Actor {} changed user {} status from {} to {}";
    public static final String USER_CONTACT_UPDATED = "Actor {} updated contact details of user {}";
    public static final String USER_CREATED = "Actor {} created user {} with role {}";
    public static final String PASSWORD_RESET_REQUESTED = "Password reset code issued for user {}";
    public static final String PASSWORD_RESET = "User {} reset their password";
    public static final String PASSWORD_CHANGED = "User {} changed their password";
    public static final String FILE_DELETE_FAILED = "Failed to delete stored file {}";
    public static final String SEED_COMPLETED = "Basic database seeding completed";
    public static final String SEED_DATA_CLEANED = "Removed {} seeded accounts and their dependent data";
    public static final String SEED_TOKEN_REJECTED = "Rejected seed request with an invalid token";

    public static final String EVENT_PUBLISHED = "Published to {} key={}";
    public static final String EVENT_PUBLISH_FAILED = "Failed to publish to {}: {}";

    private LogMessages() {
    }
}
