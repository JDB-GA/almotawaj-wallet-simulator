package com.almotawaj.wallet.config.constants.docs;

import com.almotawaj.wallet.config.constants.WalletLimits;

public final class WalletDocs {
    public static final String TAG = "Wallet";
    public static final String TAG_DESCRIPTION = "Wallet balance, IBAN, transaction history and simulated incoming bank transfers (clients with approved KYC and merchants)";

    public static final String MINE = "Get my wallet";
    public static final String MINE_DESCRIPTION = "Returns the holder name, IBAN and balance in BHD. The wallet is opened on first access. Clients need approved KYC.";
    public static final String MINE_OK = "Wallet details";
    public static final String KYC_REQUIRED = "Client identity is not verified yet (WALLET_KYC_REQUIRED)";

    public static final String TRANSACTIONS = "List my transactions";
    public static final String TRANSACTIONS_DESCRIPTION = "Newest first. Optional filters: `search` (counterparty, reference or note), `type`, `direction` (CREDIT/DEBIT), `from`/`to` dates (inclusive, Bahrain time) and `minAmount`/`maxAmount`.";
    public static final String TRANSACTIONS_OK = "Page of transactions";
    public static final String TRANSACTIONS_BAD_REQUEST = "Invalid filter value, start date after end date, minimum above maximum, or unknown sort field";

    public static final String RECEIPT = "Download a transaction receipt";
    public static final String RECEIPT_DESCRIPTION = "A PDF receipt of one of my transactions, in English or Arabic according to the Accept-Language header.";
    public static final String RECEIPT_OK = "PDF receipt";
    public static final String RECEIPT_NOT_FOUND = "The transaction does not exist or belongs to another wallet (TRANSACTION_NOT_FOUND)";
    public static final String STATEMENT = "Download a statement";
    public static final String STATEMENT_DESCRIPTION = "A PDF statement of the transactions that match the same filters as the transaction list, with money in and money out totals. Contains at most the 500 newest matching transactions.";
    public static final String STATEMENT_OK = "PDF statement";
    public static final String TOP_UP = "Receive a bank transfer (simulated)";
    public static final String TOP_UP_DESCRIPTION = "Simulates an incoming transfer from one of the demo external accounts (see top-up options). "
            + "Only the source and amount are sent; the sender name, IBAN and BIC come from the server. Amount "
            + WalletLimits.TOP_UP_MIN + " – " + WalletLimits.TOP_UP_MAX + " BHD per transfer, at most "
            + WalletLimits.DAILY_TOP_UP_LIMIT + " BHD received per day (Bahrain time).";
    public static final String TOP_UP_CREATED = "Transfer credited; returns the transaction";
    public static final String TOP_UP_UNPROCESSABLE = "Identity not verified (WALLET_KYC_REQUIRED) or daily receiving limit exceeded (DAILY_TOP_UP_LIMIT_EXCEEDED)";

    public static final String TOP_UP_OPTIONS = "Get top-up options";
    public static final String TOP_UP_OPTIONS_DESCRIPTION = "Demo external accounts to receive from, per-transfer limits, the daily limit and how much can still be received today.";
    public static final String TOP_UP_OPTIONS_OK = "Top-up sources and limits";

    private WalletDocs() {
    }
}
