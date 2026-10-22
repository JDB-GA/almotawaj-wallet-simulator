package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.config.constants.ValidationLimits;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.model.TransactionDirection;
import com.almotawaj.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PdfFormats {
    private static final ZoneId ZONE = ZoneId.of(WalletConstants.TIME_ZONE);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(PdfConstants.DATE_TIME_PATTERN).withZone(ZONE);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern(PdfConstants.DATE_PATTERN).withZone(ZONE);

    private PdfFormats() {
    }

    public static String dateTime(Instant instant) {
        return DATE_TIME.format(instant);
    }

    public static String date(Instant instant) {
        return DATE.format(instant);
    }

    public static String date(LocalDate date) {
        return DATE.format(date);
    }

    public static String amount(BigDecimal amount) {
        DecimalFormat format = new DecimalFormat(PdfConstants.AMOUNT_PATTERN, DecimalFormatSymbols.getInstance(Locale.ROOT));
        return format.format(amount.setScale(ValidationLimits.MONEY_SCALE, RoundingMode.HALF_UP));
    }

    public static String money(BigDecimal amount) {
        return amount(amount) + " " + WalletConstants.CURRENCY;
    }

    public static String signedMoney(WalletTransaction transaction) {
        String sign = transaction.getDirection() == TransactionDirection.CREDIT ? PdfConstants.CREDIT_SIGN : PdfConstants.DEBIT_SIGN;
        return sign + money(transaction.getAmount());
    }

    public static String orEmpty(String value) {
        return value == null || value.isBlank() ? PdfConstants.EMPTY_VALUE : value;
    }
}
