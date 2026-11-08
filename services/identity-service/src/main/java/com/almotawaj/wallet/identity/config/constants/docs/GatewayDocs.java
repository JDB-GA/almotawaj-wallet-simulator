package com.almotawaj.wallet.identity.config.constants.docs;

public final class GatewayDocs {
    public static final String API_KEYS_TAG = "Merchant API Keys";

    public static final String WEBHOOK_TAG = "Merchant Callback";

    public static final String PAYMENTS_TAG = "Merchant Payments";
    public static final String GATEWAY_TAG = "Payment Gateway";
    public static final String CHECKOUT_TAG = "Checkout";

    public static final String LIST = "List my payments";
    public static final String LIST_DESCRIPTION = "Checkout sessions with the masked payer. Filter by status. Paged and sortable (default createdAt,desc).";
    public static final String LIST_OK = "Page of checkout sessions";
    public static final String CREATE = "Create a checkout session";
    public static final String CREATE_DESCRIPTION = "Creates a PENDING session that expires after `expiresInMinutes` (1 to 30, default 30) and returns the `checkoutUrl` to send the customer to. After paying, the customer is sent to the optional `returnUrl` with `sessionId` and `orderReference` added. An order reference can be used once per merchant.";
    public static final String CREATED = "Session created";
    public static final String GET = "Get a checkout session";
    public static final String GET_DESCRIPTION = "Use it to confirm the payment before fulfilling the order.";
    public static final String NOT_FOUND = "Session not found (CHECKOUT_NOT_FOUND)";
    public static final String CANCEL = "Cancel a checkout session";
    public static final String CANCEL_DESCRIPTION = "PENDING to CANCELLED.";

    public static final String PAY = "Pay a checkout";
    public static final String PAY_DESCRIPTION = "Moves the amount from the client's wallet to the merchant's wallet and marks the session PAID. A session can be paid once. An optional voucherId of an unused voucher from this merchant is redeemed in the same transaction and its face value is taken off the amount.";
    public static final String PAY_UNPROCESSABLE = "Not pending (CHECKOUT_NOT_PENDING), expired (CHECKOUT_EXPIRED), balance too low (INSUFFICIENT_BALANCE), daily limit (DAILY_CHECKOUT_LIMIT_EXCEEDED), KYC missing (WALLET_KYC_REQUIRED) merchant unavailable (MERCHANT_UNAVAILABLE) or voucher already used or not from this merchant (VOUCHER_NOT_USABLE)";
    public static final String VOUCHERS = "List vouchers usable for a checkout";

    public static final String API_KEY_SCHEME_NAME = "merchantApiKey";
    public static final String API_KEY_SCHEME_DESCRIPTION = "A key created under Merchant API Keys";

    private GatewayDocs() {
    }
}
