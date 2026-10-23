package com.almotawaj.wallet.config.constants.docs;

public final class VoucherDocs {
    public static final String TAG = "Vouchers";
    public static final String TAG_DESCRIPTION = "Browse and buy limited-stock merchant vouchers. Buying is safe under concurrency: "
            + "the last unit goes to exactly one buyer.";
    public static final String MERCHANT_TAG = "Merchant Vouchers";
    public static final String MERCHANT_TAG_DESCRIPTION = "Create and edit your vouchers. Edits use optimistic locking through `version`.";

    public static final String LIST_AVAILABLE = "List vouchers on sale";
    public static final String LIST_AVAILABLE_DESCRIPTION = "Active, unexpired vouchers with stock left, newest first.";
    public static final String LIST_AVAILABLE_OK = "Page of vouchers";

    public static final String PURCHASE = "Buy a voucher";
    public static final String PURCHASE_DESCRIPTION = """
            Takes one unit of stock with a single atomic database update, then debits your wallet. When several clients \
            buy the last unit at the same moment, exactly one gets `201` and the others get `409 VOUCHER_SOLD_OUT`. \
            Send an `Idempotency-Key` to make retries safe: repeating the same key returns the first purchase and charges nothing.""";
    public static final String PURCHASE_CREATED = "Voucher bought; the response holds your voucher code";
    public static final String PURCHASE_NOT_FOUND = "Voucher not found (VOUCHER_NOT_FOUND)";
    public static final String PURCHASE_CONFLICT = "The last unit was taken by another buyer (VOUCHER_SOLD_OUT)";
    public static final String PURCHASE_UNPROCESSABLE = "Paused or expired voucher, per-client limit reached, insufficient balance, "
            + "or Idempotency-Key reused for another voucher";
    public static final String IDEMPOTENCY_KEY = "Optional unique key per attempt (max 64 characters), e.g. a UUID. "
            + "Repeating it returns the first result instead of buying again.";

    public static final String LIST_PURCHASES = "List my vouchers";
    public static final String LIST_PURCHASES_DESCRIPTION = "Vouchers you bought, newest first, with their codes.";
    public static final String LIST_PURCHASES_OK = "Page of purchases";

    public static final String LIST_MINE = "List my vouchers for sale";
    public static final String LIST_MINE_DESCRIPTION = "Every voucher you created, newest first.";
    public static final String LIST_MINE_OK = "Page of vouchers";

    public static final String CREATE = "Create a voucher";
    public static final String CREATE_DESCRIPTION = "Put a limited number of vouchers on sale. The price cannot exceed the face value.";
    public static final String CREATE_CREATED = "Voucher created";
    public static final String CREATE_UNPROCESSABLE = "Price above face value (VOUCHER_PRICE_ABOVE_FACE_VALUE)";

    public static final String UPDATE = "Edit a voucher";
    public static final String UPDATE_DESCRIPTION = """
            Change the title, price, status (`ACTIVE` or `PAUSED`) or expiry. Send the `version` you loaded; if someone \
            changed the voucher since, the edit is rejected with `409 VOUCHER_EDIT_CONFLICT` instead of overwriting.""";
    public static final String UPDATE_OK = "Voucher updated; `version` went up by one";
    public static final String UPDATE_NOT_FOUND = "Voucher not found or not yours (VOUCHER_NOT_FOUND)";
    public static final String UPDATE_CONFLICT = "Stale version (VOUCHER_EDIT_CONFLICT)";

    private VoucherDocs() {
    }
}
