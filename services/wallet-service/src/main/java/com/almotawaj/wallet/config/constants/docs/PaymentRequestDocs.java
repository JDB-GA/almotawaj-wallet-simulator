package com.almotawaj.wallet.config.constants.docs;

public final class PaymentRequestDocs {
    public static final String TAG = "Payment Requests";
    public static final String TAG_DESCRIPTION = "Request money and respond to requests";

    public static final String LIST = "List payment requests";
    public static final String LIST_DESCRIPTION = "List incoming and outgoing requests.";
    public static final String LIST_OK = "Page of payment requests";

    public static final String CREATE = "Request money";
    public static final String CREATE_DESCRIPTION = "Create a request for money.";
    public static final String CREATE_CREATED = "Request created";
    public static final String CREATE_UNPROCESSABLE = "Self transfer or unverified recipient";

    public static final String PAY = "Pay a request";
    public static final String PAY_DESCRIPTION = "Accept and pay an incoming money request.";
    public static final String PAY_CREATED = "Payment completed";
    public static final String PAY_NOT_FOUND = "Request not found";
    public static final String PAY_UNPROCESSABLE = "Not pending or insufficient balance";

    public static final String DECLINE = "Decline a request";
    public static final String DECLINE_DESCRIPTION = "Decline an incoming money request.";
    public static final String DECLINE_OK = "Request declined";

    public static final String CANCEL = "Cancel a request";
    public static final String CANCEL_DESCRIPTION = "Cancel an outgoing money request.";
    public static final String CANCEL_OK = "Request cancelled";

    private PaymentRequestDocs() {
    }
}
