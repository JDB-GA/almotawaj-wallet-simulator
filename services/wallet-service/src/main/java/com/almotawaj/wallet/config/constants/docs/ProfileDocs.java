package com.almotawaj.wallet.config.constants.docs;

public final class ProfileDocs {
    public static final String TAG = "Profile";
    public static final String TAG_DESCRIPTION = "The signed-in client's or merchant's own profile and picture";
    public static final String GET = "Get my profile";
    public static final String GET_DESCRIPTION = "Name, contact details, role, verification status and whether a picture is set. The name is the verified KYC name, otherwise the business name, otherwise the email.";
    public static final String UPDATE = "Set my business name";
    public static final String UPDATE_DESCRIPTION = "Merchants only. The business name is shown to customers on payments and transfers.";

    private ProfileDocs() {
    }
}
