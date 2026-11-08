package com.almotawaj.wallet.identity.config.constants.docs;

public final class VoucherDocs {
    public static final String TAG = "Vouchers";
    public static final String TAG_DESCRIPTION = "Browse and buy limited-stock merchant vouchers. Buying is safe under concurrency: "
            + "the last unit goes to exactly one buyer.";
    public static final String MERCHANT_TAG = "Merchant Vouchers";

    public static final String CREATE = "Create a voucher";
    public static final String CREATE_DESCRIPTION = "Put a limited number of vouchers on sale. The price cannot exceed the face value.";
    public static final String CREATE_CREATED = "Voucher created";
    public static final String CREATE_UNPROCESSABLE = "Price above face value (VOUCHER_PRICE_ABOVE_FACE_VALUE)";

    public static final String UPDATE = "Edit a voucher";
    public static final String UPDATE_DESCRIPTION = """
            Change the title, price, status (`ACTIVE` or `PAUSED`) or expiry. Send the `version` you loaded; if someone \
            changed the voucher since, the edit is rejected with `409 VOUCHER_EDIT_CONFLICT` instead of overwriting.""";

    private VoucherDocs() {
    }
}
