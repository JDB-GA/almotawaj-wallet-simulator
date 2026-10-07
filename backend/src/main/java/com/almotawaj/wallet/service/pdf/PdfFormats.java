package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.model.TransactionDirection;
import com.almotawaj.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class PdfFormats {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(PdfConstants.DATE_TIME_PATTERN)
            .withZone(ZoneId.of(WalletConstants.TIME_ZONE));

    private PdfFormats() {
    }

    public static String dateTime(Instant instant) {
        return DATE_TIME.format(instant);
    }

    public static String money(BigDecimal amount) {
        return amount.setScale(ValidationLimits.MONEY_SCALE, RoundingMode.HALF_UP).toPlainString() + " " + WalletConstants.CURRENCY;
    }

    public static String signedMoney(WalletTransaction transaction) {
        String sign = transaction.getDirection() == TransactionDirection.CREDIT ? PdfConstants.CREDIT_SIGN : PdfConstants.DEBIT_SIGN;
        return sign + money(transaction.getAmount());
    }

    public static String orEmpty(String value) {
        return value == null || value.isBlank() ? PdfConstants.EMPTY_VALUE : value;
    }
}
