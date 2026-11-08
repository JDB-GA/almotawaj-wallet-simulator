package com.almotawaj.wallet.config.constants;

public final class LogMessages {
    public static final String INVALID_JWT = "Invalid JWT: {}";
    public static final String TOKEN_USER_NOT_FOUND = "Token belongs to a user that no longer exists: {}";
    public static final String EMAIL_SEND_FAILED = "Failed to send email to user {}";
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
    public static final String SSE_DELIVERY_FAILED = "Dropped notification stream for user {}";
    public static final String WEBHOOK_FAILED = "Callback for checkout session {} was not delivered after {} attempts";
    public static final String SEED_COMPLETED = "Basic database seeding completed";
    public static final String SEED_DATA_CLEANED = "Removed {} seeded accounts and their dependent data";
    public static final String SEED_TOKEN_REJECTED = "Rejected seed request with an invalid token";
    public static final String VOUCHER_CREATED = "Merchant {} created voucher {} with {} units";
    public static final String VOUCHER_UPDATED = "Merchant {} updated voucher {} to version {}";
    public static final String VOUCHER_EDIT_CONFLICT = "Rejected stale edit of voucher {} by merchant {}: sent version {}, current {}";
    public static final String VOUCHER_PURCHASE_STARTED = "Buyer {} is buying voucher {}";
    public static final String VOUCHER_PURCHASED = "Buyer {} bought voucher {} as purchase {}; {} left";
    public static final String VOUCHER_SOLD_OUT = "Buyer {} lost the race for voucher {}: sold out";
    public static final String VOUCHER_PURCHASE_REPLAYED = "Buyer {} repeated Idempotency-Key for purchase {}";
    public static final String PAYOUT_BATCH_CREATED = "Merchant {} created payout batch {} with {} items";
    public static final String PAYOUT_BATCH_STARTED = "Payout batch {} started on {} threads";
    public static final String PAYOUT_ITEM_PAID = "Payout item {} of batch {} paid on {}";
    public static final String PAYOUT_ITEM_FAILED = "Payout item {} of batch {} failed on {}: {}";
    public static final String PAYOUT_BATCH_FINISHED = "Payout batch {} finished as {}: {} paid, {} failed in {} ms";
    public static final String PAYOUT_ITEM_CRASHED = "Payout item {} of batch {} crashed";
    public static final String SIMULATION_STARTED = "Simulation {} started: {} threads";
    public static final String SIMULATION_FINISHED = "Simulation {} finished in {} ms: {}";

    private LogMessages() {
    }
}
