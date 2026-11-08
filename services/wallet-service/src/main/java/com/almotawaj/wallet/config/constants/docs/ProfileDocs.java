package com.almotawaj.wallet.config.constants.docs;

public final class ProfileDocs {
    public static final String TAG = "Profile";
    public static final String TAG_DESCRIPTION = "The signed-in client's or merchant's own profile and picture";
    public static final String GET = "Get my profile";
    public static final String GET_DESCRIPTION = "Name, contact details, role, verification status and whether a picture is set. The name is the verified KYC name, otherwise the business name, otherwise the email.";
    public static final String PROFILE_OK = "Profile";
    public static final String UPDATE = "Set my business name";
    public static final String UPDATE_DESCRIPTION = "Merchants only. The business name is shown to customers on payments and transfers.";
    public static final String PICTURE_GET = "Get my profile picture";
    public static final String PICTURE_OK = "The picture file";
    public static final String PICTURE_NOT_FOUND = "No picture uploaded (PROFILE_PICTURE_NOT_FOUND)";
    public static final String PICTURE_PUT = "Upload or replace my profile picture";
    public static final String PICTURE_PUT_DESCRIPTION = "JPEG or PNG up to 5 MB, checked by content. Replaces the previous picture.";
    public static final String PICTURE_BAD_REQUEST = "Missing file (FILE_REQUIRED) or not a real JPEG/PNG (FILE_TYPE_INVALID)";
    public static final String PICTURE_TOO_LARGE = "File larger than 5 MB (FILE_TOO_LARGE)";
    public static final String PICTURE_DELETE = "Remove my profile picture";
    public static final String PICTURE_DELETED = "Picture removed";

    private ProfileDocs() {
    }
}
