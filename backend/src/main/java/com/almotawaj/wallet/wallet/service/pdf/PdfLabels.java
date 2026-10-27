package com.almotawaj.wallet.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.context.MessageSource;

import java.util.Locale;

public record PdfLabels(MessageSource messageSource, Locale locale) {
    public String get(String key, Object... arguments) {
        return messageSource.getMessage(PdfConstants.LABEL_PREFIX + key, arguments, locale);
    }

    public String type(TransactionType type) {
        return get(PdfConstants.TYPE_LABEL_PREFIX + type.name());
    }

    public int runDirection() {
        boolean isArabic = LocaleConstants.ARABIC.getLanguage().equals(locale.getLanguage());
        return isArabic ? PdfWriter.RUN_DIRECTION_RTL : PdfWriter.RUN_DIRECTION_LTR;
    }
}
