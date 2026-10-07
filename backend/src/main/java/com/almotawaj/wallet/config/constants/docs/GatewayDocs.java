package com.almotawaj.wallet.config.constants.docs;

public final class GatewayDocs {
    public static final String API_KEYS_TAG = "Merchant API Keys";
    public static final String API_KEYS_TAG_DESCRIPTION = "Keys a merchant's server uses to call the payment gateway (merchant only)";
    public static final String KEYS_LIST = "List my API keys";
    public static final String KEYS_LIST_DESCRIPTION = "Active and revoked keys, newest first. Only the first characters of each key are returned.";
    public static final String KEYS_LIST_OK = "API keys";
    public static final String KEY_CREATE = "Create an API key";
    public static final String KEY_CREATE_DESCRIPTION = "The full key is returned once in `secret`; only its SHA-256 hash is stored. At most 5 active keys.";
    public static final String KEY_CREATED = "Key created";
    public static final String KEY_LIMIT = "Five keys are already active (API_KEY_LIMIT_REACHED)";
    public static final String KEY_REVOKE = "Revoke an API key";
    public static final String KEY_REVOKE_DESCRIPTION = "Idempotent. The key stops working immediately.";
    public static final String KEY_REVOKED = "Key revoked";
    public static final String KEY_NOT_FOUND = "Key not found (API_KEY_NOT_FOUND)";

    public static final String WEBHOOK_TAG = "Merchant Callback";
    public static final String WEBHOOK_TAG_DESCRIPTION = "The URL that is called when one of the merchant's checkout sessions changes status (merchant only)";
    public static final String WEBHOOK_GET = "Get my callback URL";
    public static final String WEBHOOK_GET_DESCRIPTION = "Returns the URL and the signing secret, or nulls when none is set.";
    public static final String WEBHOOK_OK = "Callback settings";
    public static final String WEBHOOK_SET = "Set my callback URL";
    public static final String WEBHOOK_SET_DESCRIPTION = "A JSON POST is sent to this URL when a session becomes PAID, CANCELLED, EXPIRED or REFUNDED. Each call carries an HMAC-SHA256 signature of the body in X-Wallet-Signature and is tried up to 3 times. The URL must be a public https address.";
    public static final String WEBHOOK_UNPROCESSABLE = "The URL is not a public https address (WEBHOOK_URL_NOT_ALLOWED)";
    public static final String WEBHOOK_DELETE = "Remove my callback URL";
    public static final String WEBHOOK_DELETED = "Callback removed";

    public static final String PAYMENTS_TAG = "Merchant Payments";
    public static final String PAYMENTS_TAG_DESCRIPTION = "Checkout sessions managed from the merchant dashboard (merchant only)";
    public static final String GATEWAY_TAG = "Payment Gateway";
    public static final String GATEWAY_TAG_DESCRIPTION = "Server-to-server API for merchants. Send the key in the `X-API-Key` header.";
    public static final String CHECKOUT_TAG = "Checkout";
    public static final String CHECKOUT_TAG_DESCRIPTION = "Paying a merchant's checkout session from a wallet (client only)";

    public static final String LIST = "List my payments";
    public static final String LIST_DESCRIPTION = "Checkout sessions with the masked payer. Filter by status. Paged and sortable (default createdAt,desc).";
    public static final String LIST_OK = "Page of checkout sessions";
    public static final String CREATE = "Create a checkout session";
    public static final String CREATE_DESCRIPTION = "Creates a PENDING session that expires after `expiresInMinutes` (1 to 30, default 30) and returns the `checkoutUrl` to send the customer to. After paying, the customer is sent to the optional `returnUrl` with `sessionId` and `orderReference` added. An order reference can be used once per merchant.";
    public static final String CREATED = "Session created";
    public static final String ORDER_EXISTS = "A session already exists for this order reference (ORDER_ALREADY_EXISTS)";
    public static final String GET = "Get a checkout session";
    public static final String GET_DESCRIPTION = "Use it to confirm the payment before fulfilling the order.";
    public static final String SESSION_OK = "Checkout session";
    public static final String NOT_FOUND = "Session not found (CHECKOUT_NOT_FOUND)";
    public static final String CANCEL = "Cancel a checkout session";
    public static final String CANCEL_DESCRIPTION = "PENDING to CANCELLED.";
    public static final String CANCEL_UNPROCESSABLE = "The session is not pending (CHECKOUT_NOT_PENDING)";
    public static final String REFUND = "Refund a paid checkout session";
    public static final String REFUND_DESCRIPTION = "PAID to REFUNDED. The full amount moves back from the merchant's wallet to the payer's wallet.";
    public static final String REFUND_UNPROCESSABLE = "Not paid (CHECKOUT_NOT_PAID) or the merchant balance is too low (INSUFFICIENT_BALANCE)";
    public static final String API_KEY_REQUIRED = "Missing, wrong or revoked API key";

    public static final String VIEW = "Get a checkout to pay";
    public static final String VIEW_DESCRIPTION = "Merchant, amount and status of a checkout session.";
    public static final String VIEW_OK = "Checkout details";
    public static final String PAY = "Pay a checkout";
    public static final String PAY_DESCRIPTION = "Moves the amount from the client's wallet to the merchant's wallet and marks the session PAID. A session can be paid once.";
    public static final String PAY_OK = "Checkout paid";
    public static final String PAY_UNPROCESSABLE = "Not pending (CHECKOUT_NOT_PENDING), expired (CHECKOUT_EXPIRED), balance too low (INSUFFICIENT_BALANCE), daily limit (DAILY_CHECKOUT_LIMIT_EXCEEDED), KYC missing (WALLET_KYC_REQUIRED) or merchant unavailable (MERCHANT_UNAVAILABLE)";

    public static final String API_KEY_SCHEME_NAME = "merchantApiKey";
    public static final String API_KEY_SCHEME_DESCRIPTION = "A key created under Merchant API Keys";

    private GatewayDocs() {
    }
}
