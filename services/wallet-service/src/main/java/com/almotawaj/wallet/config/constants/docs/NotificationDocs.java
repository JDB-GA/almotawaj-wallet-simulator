package com.almotawaj.wallet.config.constants.docs;

public final class NotificationDocs {
    public static final String TAG = "Notifications";
    public static final String TAG_DESCRIPTION = "Live wallet notifications";
    public static final String STREAM = "Open the live notification stream";
    public static final String STREAM_DESCRIPTION = "Streams committed incoming wallet and bank transfers for the authenticated wallet holder."
            + " The stream sends heartbeat comments every 25 seconds and the browser reconnects automatically.";
    public static final String STREAM_OK = "Server-sent event stream";

    private NotificationDocs() {
    }
}
