package com.almotawaj.wallet.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.LocaleConstants;
import com.almotawaj.wallet.wallet.model.StatementData;
import com.almotawaj.wallet.wallet.model.TransactionDirection;
import com.almotawaj.wallet.wallet.model.TransactionType;
import com.almotawaj.wallet.wallet.model.Wallet;
import com.almotawaj.wallet.wallet.model.WalletTransaction;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionPdfRendererTest {
    private static final Instant NOW = Instant.parse("2026-10-06T10:00:00Z");

    private final PdfFonts fonts = new PdfFonts();
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final PdfDocuments documents = new PdfDocuments(fonts, clock);

    @Test
    void receipt_containsTheTransactionInEnglish() throws IOException {
        byte[] pdf = new ReceiptPdfRenderer(documents, fonts).render(transaction(), "Sara Ali", labels(LocaleConstants.ENGLISH));

        assertThat(textOf(pdf)).contains("Transaction receipt", "TXN-20261006-AAAAAAAA", "+12.500 BHD", "Money received", "Sara Ali",
                "2026-10-06 13:00");
    }

    @Test
    void statement_listsEntriesAndBalancesInArabic() throws IOException {
        StatementData statement = statement(List.of(transaction()), new BigDecimal("50.000"), new BigDecimal("62.500"),
                1, new BigDecimal("12.500"), 0, BigDecimal.ZERO);

        byte[] pdf = new StatementPdfRenderer(documents, fonts, clock).render(statement, labels(LocaleConstants.ARABIC));

        assertThat(new PdfReader(pdf).getNumberOfPages()).isEqualTo(1);
        assertThat(textOf(pdf, 1)).contains("TXN-20261006-AAAAAAAA", "06/10/2026", "50.000", "12.500", "62.500", "STM-202610061300-000001");
    }

    @Test
    void statement_showsBankStyleSectionsInEnglish() throws IOException {
        StatementData statement = statement(List.of(transaction()), new BigDecimal("50.000"), new BigDecimal("62.500"),
                1, new BigDecimal("12.500"), 0, BigDecimal.ZERO);

        String text = textOf(new StatementPdfRenderer(documents, fonts, clock).render(statement, labels(LocaleConstants.ENGLISH)), 1);

        assertThat(text).contains("Account Statement", "Opening balance", "Total credits (1)", "Closing balance",
                "Balance brought forward", "Debit", "Credit", "Page 1 of", "End of statement");
    }

    @Test
    void statement_spansSeveralPagesWithEveryTransactionAndPageNumbers() throws IOException {
        List<WalletTransaction> transactions = new ArrayList<>();
        BigDecimal balance = BigDecimal.ZERO;
        for (int index = 0; index < 200; index++) {
            balance = balance.add(BigDecimal.ONE);
            WalletTransaction transaction = transaction();
            transaction.setReference("TXN-20261006-%08d".formatted(index));
            transaction.setAmount(BigDecimal.ONE);
            transaction.setBalanceAfter(balance);
            transactions.add(transaction);
        }
        StatementData statement = statement(transactions, BigDecimal.ZERO, balance, 200, balance, 0, BigDecimal.ZERO);

        byte[] pdf = new StatementPdfRenderer(documents, fonts, clock).render(statement, labels(LocaleConstants.ENGLISH));
        int pages = new PdfReader(pdf).getNumberOfPages();

        assertThat(pages).isGreaterThan(1);
        assertThat(textOf(pdf, pages)).contains("TXN-20261006-00000199", "Closing balance", "200.000");
        assertThat(textOf(pdf, 2)).contains("Page 2 of", "Description");
    }

    @Test
    void statement_explainsWhenFiltersNarrowTheList() throws IOException {
        StatementData statement = new StatementData("Sara Ali", "BH02ALMT00000000000001", "STM-1", null, null, List.of(), 0,
                BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO, true);

        String text = textOf(new StatementPdfRenderer(documents, fonts, clock).render(statement, labels(LocaleConstants.ENGLISH)), 1);

        assertThat(text).contains("Filtered statement", "No transactions in this period.");
    }

    private static StatementData statement(List<WalletTransaction> transactions, BigDecimal opening, BigDecimal closing,
                                           long credits, BigDecimal credited, long debits, BigDecimal debited) {
        return new StatementData("سارة علي", "BH02ALMT00000000000001", "STM-202610061300-000001", null, null, transactions,
                transactions.size(), opening, closing, credits, credited, debits, debited, false);
    }

    private static PdfLabels labels(Locale locale) {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("messages");
        messages.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messages.setFallbackToSystemLocale(false);
        return new PdfLabels(messages, locale);
    }

    private static String textOf(byte[] pdf) throws IOException {
        return textOf(pdf, 1);
    }

    private static String textOf(byte[] pdf, int page) throws IOException {
        return new PdfTextExtractor(new PdfReader(pdf)).getTextFromPage(page);
    }

    private static WalletTransaction transaction() {
        Wallet wallet = new Wallet();
        wallet.setIban("BH02ALMT00000000000001");
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setReference("TXN-20261006-AAAAAAAA");
        transaction.setType(TransactionType.TRANSFER_IN);
        transaction.setDirection(TransactionDirection.CREDIT);
        transaction.setAmount(new BigDecimal("12.500"));
        transaction.setBalanceAfter(new BigDecimal("62.500"));
        transaction.setCounterpartyName("Illia");
        transaction.setCreatedAt(NOW);
        return transaction;
    }
}
