package com.almotawaj.wallet.identity.config.constants.docs;

import com.almotawaj.wallet.identity.config.constants.WalletLimits;

public final class WalletDocs {
    public static final String TAG = "Wallet";
    public static final String TAG_DESCRIPTION = "Wallet balance, IBAN, transaction history and simulated incoming bank transfers (clients with approved KYC and merchants)";

    public static final String MINE = "Get my wallet";
    public static final String MINE_DESCRIPTION = "Returns the holder name, IBAN and balance in BHD. The wallet is opened on first access. Clients need approved KYC.";
    public static final String MINE_OK = "Wallet details";

    public static final String TRANSACTIONS = "List my transactions";
    public static final String TRANSACTIONS_DESCRIPTION = "Newest first. Optional filters: `search` (counterparty, reference or note), `type`, `direction` (CREDIT/DEBIT), `from`/`to` dates (inclusive, Bahrain time) and `minAmount`/`maxAmount`.";
    public static final String TRANSACTIONS_OK = "Page of transactions";
    public static final String TRANSACTIONS_BAD_REQUEST = "Invalid filter value, start date after end date, minimum above maximum, or unknown sort field";

    public static final String TOP_UP_OPTIONS = "Get top-up options";

    private WalletDocs() {
    }
}
