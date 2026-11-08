package com.almotawaj.wallet.config.constants.docs;

import com.almotawaj.wallet.config.constants.WalletLimits;

public final class TransferDocs {
    public static final String TAG = "Transfers";
    public static final String TAG_DESCRIPTION = "Send money to another wallet by email, mobile number or IBAN";

    public static final String RECIPIENT = "Find a recipient";
    public static final String RECIPIENT_DESCRIPTION = "Looks up a wallet holder by email, 8-digit mobile number or IBAN and returns a masked preview to confirm before sending.";
    public static final String RECIPIENT_OK = "Masked recipient preview";
    public static final String RECIPIENT_NOT_FOUND = "No wallet holder matches (RECIPIENT_NOT_FOUND)";
    public static final String RECIPIENT_UNPROCESSABLE = "Sending to yourself (SELF_TRANSFER_NOT_ALLOWED) or recipient cannot receive yet (RECIPIENT_UNAVAILABLE)";

    public static final String SUGGESTIONS = "Suggest recipients";
    public static final String SUGGESTIONS_DESCRIPTION = "Autocomplete by case-insensitive email or mobile prefix (at least 3 characters). Returns up to 5 masked wallet holders who can receive money; select one to send by its IBAN.";
    public static final String SUGGESTIONS_OK = "Up to 5 masked suggestions (empty for short queries)";

    public static final String OPTIONS = "Get transfer options";
    public static final String OPTIONS_DESCRIPTION = "Current balance, per-transfer limits, the daily sending limit and how much can still be sent today.";
    public static final String OPTIONS_OK = "Balance and transfer limits";

    public static final String TRANSFER = "Send money";
    public static final String TRANSFER_DESCRIPTION = "Moves money to another wallet. Amount " + WalletLimits.TRANSFER_MIN + " – "
            + WalletLimits.TRANSFER_MAX + " BHD per transfer, at most " + WalletLimits.DAILY_TRANSFER_LIMIT
            + " BHD sent per day (Bahrain time). Both wallets are locked in a fixed order to prevent double spending.";
    public static final String TRANSFER_CREATED = "Transfer completed; returns the sender's transaction";
    public static final String TRANSFER_UNPROCESSABLE = "INSUFFICIENT_BALANCE, DAILY_TRANSFER_LIMIT_EXCEEDED, SELF_TRANSFER_NOT_ALLOWED, RECIPIENT_UNAVAILABLE or WALLET_KYC_REQUIRED";

    private TransferDocs() {
    }
}
