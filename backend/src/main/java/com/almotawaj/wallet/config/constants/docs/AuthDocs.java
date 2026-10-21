package com.almotawaj.wallet.config.constants.docs;

public final class AuthDocs {
    public static final String TAG = "Authentication";
    public static final String TAG_DESCRIPTION = "Registration, login, email verification and passwords";

    public static final String REGISTER = "Register a client account";
    public static final String REGISTER_DESCRIPTION = "Creates a CLIENT account, emails a 6-digit verification code and signs the user in (sets the access_token cookie). The mobile number is 8 digits; +973 is added by the server.";
    public static final String REGISTER_CREATED = "Account created and session cookie set";
    public static final String REGISTER_CONFLICT = "Email (EMAIL_ALREADY_REGISTERED) or mobile (MOBILE_ALREADY_REGISTERED) already registered";

    public static final String LOGIN = "Log in";
    public static final String LOGIN_DESCRIPTION = "Signs in with an email or an 8-digit mobile number. Sets the httpOnly access_token cookie. Unverified users receive a new code if none is active.";
    public static final String LOGIN_OK = "Signed in; session cookie set";
    public static final String LOGIN_UNAUTHORIZED = "Wrong credentials (INVALID_CREDENTIALS), or correct credentials for a suspended/closed (ACCOUNT_DEACTIVATED) or locked (ACCOUNT_LOCKED) account";

    public static final String LOGOUT = "Log out";
    public static final String LOGOUT_DESCRIPTION = "Clears the session cookie.";
    public static final String LOGOUT_NO_CONTENT = "Cookie cleared";

    public static final String ME = "Get the current user";
    public static final String ME_DESCRIPTION = "Returns the signed-in user with role, statuses and effective permissions.";
    public static final String ME_OK = "Current user";

    public static final String VERIFY_EMAIL = "Verify email";
    public static final String VERIFY_EMAIL_DESCRIPTION = "Confirms the email with the 6-digit code. Codes expire after 10 minutes and allow 5 attempts.";
    public static final String VERIFY_EMAIL_OK = "Email verified";
    public static final String VERIFY_EMAIL_BAD_REQUEST = "Invalid, expired or exhausted code (OTP_INVALID, OTP_EXPIRED, OTP_ATTEMPTS_EXCEEDED, OTP_NOT_FOUND)";
    public static final String ALREADY_VERIFIED = "Email already verified (EMAIL_ALREADY_VERIFIED)";

    public static final String RESEND = "Resend the verification code";
    public static final String RESEND_DESCRIPTION = "Emails a new code. Allowed once every 60 seconds.";
    public static final String RESEND_NO_CONTENT = "Code sent";

    public static final String FORGOT = "Request a password reset code";
    public static final String FORGOT_DESCRIPTION = "Emails a 6-digit reset code when an active account exists. Always returns 204 so it cannot be used to discover accounts.";
    public static final String FORGOT_NO_CONTENT = "Request accepted";

    public static final String RESET = "Reset the password";
    public static final String RESET_DESCRIPTION = "Sets a new password using the emailed code (also used to accept invitations). Verifies the email and signs out every existing session.";
    public static final String RESET_NO_CONTENT = "Password changed";
    public static final String RESET_BAD_REQUEST = "Invalid or expired code, or invalid fields";

    public static final String CHANGE = "Change the password";
    public static final String CHANGE_DESCRIPTION = "Changes the password of the signed-in user. Other sessions are signed out; this one receives a fresh cookie.";
    public static final String CHANGE_OK = "Password changed and a new session cookie set";
    public static final String CHANGE_UNPROCESSABLE = "Current password is wrong (INVALID_CURRENT_PASSWORD) or the new one equals it (PASSWORD_REUSED)";

    private AuthDocs() {
    }
}
