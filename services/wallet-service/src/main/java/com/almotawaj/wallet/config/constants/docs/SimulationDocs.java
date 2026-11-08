package com.almotawaj.wallet.config.constants.docs;

public final class SimulationDocs {
    public static final String TAG = "Concurrency Simulation";
    public static final String TAG_DESCRIPTION = "Run real races in a throw-away sandbox and see every thread's timing (super admin only)";

    public static final String VOUCHER_RACE = "Race buyers for a voucher";
    public static final String VOUCHER_RACE_DESCRIPTION = """
            Creates a temporary merchant, a voucher with the given stock and N funded buyers, then starts N threads at the \
            same instant. `SAFE` uses the real purchase (atomic stock update): winners never exceed stock. `UNSAFE` reads the \
            stock, waits, then writes it back, showing overselling. Everything is deleted afterwards.""";
    public static final String VOUCHER_RACE_OK = "Every attempt with its thread, timing and outcome";

    public static final String PAYOUT_RACE = "Run a parallel payout";
    public static final String PAYOUT_RACE_DESCRIPTION = """
            Creates a temporary merchant with the given balance and N recipients, then pays them with the real payout \
            runner (5-thread pool). Shows which thread paid which item and that the balance never goes negative. \
            Everything is deleted afterwards.""";
    public static final String PAYOUT_RACE_OK = "Every item with its thread, timing and result";

    private SimulationDocs() {
    }
}
