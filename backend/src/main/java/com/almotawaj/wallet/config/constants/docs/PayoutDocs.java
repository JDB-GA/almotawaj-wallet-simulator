package com.almotawaj.wallet.config.constants.docs;

public final class PayoutDocs {
    public static final String TAG = "Bulk Payouts";
    public static final String TAG_DESCRIPTION = "Pay many wallet holders in one request, processed in parallel in the background.";

    public static final String CREATE = "Start a bulk payout";
    public static final String CREATE_DESCRIPTION = """
            Checks every recipient, saves the batch and returns `202` right away. A pool of 5 threads then pays the items \
            in parallel; each item locks your wallet, so your balance never goes below zero. Items that do not fit are \
            marked `FAILED` with the reason. Progress arrives over the notification stream as `payout-progress` and \
            `payout-completed` events.""";
    public static final String CREATE_ACCEPTED = "Batch accepted and processing";
    public static final String CREATE_NOT_FOUND = "A recipient was not found (RECIPIENT_NOT_FOUND)";
    public static final String CREATE_UNPROCESSABLE = "Duplicate recipient, yourself, or a recipient who cannot receive money";

    public static final String LIST = "List my payouts";
    public static final String LIST_DESCRIPTION = "Your payout batches, newest first, without their items.";
    public static final String LIST_OK = "Page of payout batches";

    public static final String GET = "Get a payout";
    public static final String GET_DESCRIPTION = "A batch with the result of every item.";
    public static final String GET_OK = "Payout batch with items";
    public static final String GET_NOT_FOUND = "Batch not found or not yours (PAYOUT_BATCH_NOT_FOUND)";

    private PayoutDocs() {
    }
}
