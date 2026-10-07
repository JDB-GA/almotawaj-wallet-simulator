package com.almotawaj.wallet.config.constants;

public final class GatewayMessages {
    public static final String API_KEY_NAME_REQUIRED = "Name is required";
    public static final String API_KEY_NAME_TOO_LONG = "Name must be at most {max} characters";
    public static final String API_KEY_NOT_FOUND = "API key not found";
    public static final String API_KEY_LIMIT_REACHED = "Revoke an existing API key before creating another one";
    public static final String WEBHOOK_URL_REQUIRED = "Enter the callback URL";
    public static final String WEBHOOK_URL_INVALID = "Enter a full address that starts with https://";
    public static final String URL_TOO_LONG = "URL must be at most {max} characters";
    public static final String URL_INVALID = "Enter a full URL that starts with http:// or https://";
    public static final String WEBHOOK_URL_NOT_ALLOWED = "The callback URL must be a public https address";
    public static final String ORDER_REFERENCE_REQUIRED = "Order reference is required";
    public static final String ORDER_REFERENCE_TOO_LONG = "Order reference must be at most {max} characters";
    public static final String ORDER_REFERENCE_INVALID = "Order reference can only contain letters, digits, dots, dashes and underscores";
    public static final String EXPIRY_OUT_OF_RANGE = "Expiry must be between {min} and {max} minutes";
    public static final String ORDER_ALREADY_EXISTS = "A payment already exists for this order reference";
    public static final String CHECKOUT_NOT_FOUND = "Payment not found";
    public static final String CHECKOUT_NOT_PENDING = "This payment is no longer waiting to be paid";
    public static final String CHECKOUT_EXPIRED = "This payment has expired";
    public static final String CHECKOUT_NOT_PAID = "Only a paid payment can be refunded";
    public static final String MERCHANT_UNAVAILABLE = "This merchant cannot accept payments right now";
    public static final String DAILY_CHECKOUT_LIMIT_EXCEEDED = "This payment exceeds your daily payment limit";

    private GatewayMessages() {
    }
}
