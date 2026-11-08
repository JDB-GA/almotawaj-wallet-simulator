package com.almotawaj.wallet.wallet.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TopUpSource {
    NBB_SALARY("Gulf Payroll Services W.L.L.", "BH35NBOB00001234567801", "NBOBBHBM", "National Bank of Bahrain"),
    BBK_SAVINGS("Fatima Hasan", "BH22BBKU00002345678902", "BBKUBHBM", "Bank of Bahrain and Kuwait"),
    BISB_CURRENT("Al Noor Trading W.L.L.", "BH08BIBB00003456789003", "BIBBBHBM", "Bahrain Islamic Bank"),
    ABC_BUSINESS("Yusuf Ahmed", "BH56ABCO00004567890104", "ABCOBHBM", "Bank ABC"),
    ENBD_UAE("Sara Ali", "AE620260001015678901234", "EBILAEAD", "Emirates NBD");

    private final String holderName;
    private final String iban;
    private final String bic;
    private final String bankName;
}
