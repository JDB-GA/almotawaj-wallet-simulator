package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.config.constants.WalletConstants;
import com.almotawaj.wallet.model.StatementData;
import com.almotawaj.wallet.model.TransactionDirection;
import com.almotawaj.wallet.model.WalletTransaction;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StatementPdfRenderer {
    private static final List<String> COLUMNS = List.of("date", "statement.description", "reference");
    private static final List<String> AMOUNT_COLUMNS = List.of("statement.debit", "statement.credit", "statement.balance");

    private final PdfDocuments documents;
    private final PdfFonts fonts;
    private final Clock clock;

    public byte[] render(StatementData statement, PdfLabels labels) {
        List<Element> sections = new ArrayList<>();
        sections.add(documents.title(labels, labels.get("statement.title"), List.of(
                new String[]{labels.get("statement.number"), statement.statementNumber()},
                new String[]{labels.get("statement.issued"), PdfFormats.date(clock.instant())},
                new String[]{labels.get("statement.period"), period(statement, labels)})));
        sections.add(boxes(statement, labels));
        notes(statement, labels).ifPresent(sections::add);
        sections.add(entries(statement, labels));
        sections.add(endMarker(labels));
        return documents.render(labels, PageSize.A4, sections);
    }

    private PdfPTable boxes(StatementData statement, PdfLabels labels) {
        int direction = labels.runDirection();
        PdfPTable account = detailTable(direction);
        boxHeading(account, labels.get("statement.account"), direction);
        detail(account, labels.get("statement.holder"), statement.ownerName(), direction, false);
        detailData(account, labels.get("iban"), statement.iban(), direction);
        detail(account, labels.get("statement.accountType"), labels.get("statement.accountTypeValue"), direction, false);
        detailData(account, labels.get("statement.currency"), WalletConstants.CURRENCY, direction);

        PdfPTable summary = PdfCells.table(direction, PdfConstants.SUMMARY_COLUMN_WIDTHS);
        summary.setSpacingAfter(0);
        boxHeading(summary, labels.get("statement.summary"), direction);
        money(summary, labels.get("statement.opening"), statement.openingBalance(), direction, false);
        money(summary, labels.get("statement.credits", String.valueOf(statement.creditCount())), statement.totalCredited(), direction, false);
        money(summary, labels.get("statement.debits", String.valueOf(statement.debitCount())), statement.totalDebited(), direction, false);
        money(summary, labels.get("statement.closing"), statement.closingBalance(), direction, true);

        PdfPTable table = PdfCells.table(direction, PdfConstants.BOXES_COLUMN_WIDTHS);
        table.addCell(PdfCells.box(account));
        table.addCell(PdfCells.empty());
        table.addCell(PdfCells.box(summary));
        return table;
    }

    private Optional<PdfPTable> notes(StatementData statement, PdfLabels labels) {
        List<String> notes = new ArrayList<>();
        if (statement.filtered()) {
            notes.add(labels.get("statement.filtered"));
        }
        if (statement.totalTransactions() > statement.transactions().size()) {
            notes.add(labels.get("statement.shown", String.valueOf(statement.transactions().size()), String.valueOf(statement.totalTransactions())));
        }
        if (notes.isEmpty()) {
            return Optional.empty();
        }
        PdfPTable table = PdfCells.table(labels.runDirection(), 1f);
        notes.forEach(note -> table.addCell(PdfCells.shaded(PdfCells.plain(note, fonts.muted(), labels.runDirection()),
                PdfConstants.HIGHLIGHT_BACKGROUND)));
        return Optional.of(table);
    }

    private PdfPTable entries(StatementData statement, PdfLabels labels) {
        int direction = labels.runDirection();
        PdfPTable table = PdfCells.table(direction, PdfConstants.STATEMENT_COLUMN_WIDTHS);
        table.setHeaderRows(1);
        COLUMNS.forEach(column -> table.addCell(PdfCells.header(labels.get(column), fonts.tableHeader(), direction)));
        AMOUNT_COLUMNS.forEach(column -> table.addCell(PdfCells.amountHeader(labels.get(column), fonts.tableHeader(), direction)));

        String firstDate = statement.from() != null ? PdfFormats.date(statement.from())
                : statement.transactions().isEmpty() ? "" : PdfFormats.date(statement.transactions().get(0).getCreatedAt());
        balanceRow(table, labels, firstDate, labels.get("statement.broughtForward"), "", "", statement.openingBalance());

        if (statement.transactions().isEmpty()) {
            PdfPCell empty = PdfCells.text(labels.get("statement.empty"), fonts.muted(), direction);
            empty.setColspan(PdfConstants.STATEMENT_COLUMN_WIDTHS.length);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
        }
        int row = 0;
        for (WalletTransaction transaction : statement.transactions()) {
            Color background = row++ % 2 == 1 ? PdfConstants.STRIPE_BACKGROUND : null;
            boolean isCredit = transaction.getDirection() == TransactionDirection.CREDIT;
            String amount = PdfFormats.amount(transaction.getAmount());
            table.addCell(shade(PdfCells.data(PdfFormats.date(transaction.getCreatedAt()), fonts.body(), direction), background));
            table.addCell(shade(PdfCells.text(description(transaction, labels), fonts.body(), direction), background));
            table.addCell(shade(PdfCells.data(transaction.getReference(), fonts.small(), direction), background));
            table.addCell(shade(PdfCells.amount(isCredit ? "" : amount, fonts.body(), direction), background));
            table.addCell(shade(PdfCells.amount(isCredit ? amount : "", fonts.body(), direction), background));
            table.addCell(shade(PdfCells.amount(PdfFormats.amount(transaction.getBalanceAfter()), fonts.body(), direction), background));
        }

        String lastDate = statement.to() != null ? PdfFormats.date(statement.to()) : PdfFormats.date(clock.instant());
        balanceRow(table, labels, lastDate, labels.get("statement.carriedForward"), PdfFormats.amount(statement.totalDebited()),
                PdfFormats.amount(statement.totalCredited()), statement.closingBalance());
        return table;
    }

    private void balanceRow(PdfPTable table, PdfLabels labels, String date, String label, String debit, String credit, BigDecimal balance) {
        int direction = labels.runDirection();
        table.addCell(highlight(PdfCells.data(date, fonts.strong(), direction)));
        PdfPCell description = highlight(PdfCells.text(label, fonts.strong(), direction));
        description.setColspan(2);
        table.addCell(description);
        table.addCell(highlight(PdfCells.amount(debit, fonts.strong(), direction)));
        table.addCell(highlight(PdfCells.amount(credit, fonts.strong(), direction)));
        table.addCell(highlight(PdfCells.amount(PdfFormats.amount(balance), fonts.strong(), direction)));
    }

    private PdfPTable endMarker(PdfLabels labels) {
        PdfPTable table = PdfCells.table(labels.runDirection(), 1f);
        PdfPCell cell = PdfCells.plain(labels.get("statement.end"), fonts.small(), labels.runDirection());
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        return table;
    }

    private static String description(WalletTransaction transaction, PdfLabels labels) {
        String counterparty = transaction.getCounterpartyName();
        String type = labels.type(transaction.getType());
        return counterparty == null || counterparty.isBlank() ? type : type + PdfConstants.DESCRIPTION_SEPARATOR + counterparty;
    }

    private static String period(StatementData statement, PdfLabels labels) {
        if (statement.from() != null && statement.to() != null) {
            return labels.get("statement.range", PdfFormats.date(statement.from()), PdfFormats.date(statement.to()));
        }
        if (statement.from() != null) {
            return labels.get("statement.since", PdfFormats.date(statement.from()));
        }
        if (statement.to() != null) {
            return labels.get("statement.until", PdfFormats.date(statement.to()));
        }
        return labels.get("statement.allDates");
    }

    private static PdfPTable detailTable(int direction) {
        PdfPTable table = PdfCells.table(direction, PdfConstants.DETAIL_COLUMN_WIDTHS);
        table.setSpacingAfter(0);
        return table;
    }

    private void boxHeading(PdfPTable table, String title, int direction) {
        PdfPCell cell = PdfCells.plain(title, fonts.heading(), direction);
        cell.setColspan(2);
        cell.setPaddingBottom(PdfConstants.CELL_PADDING);
        table.addCell(cell);
    }

    private void detail(PdfPTable table, String label, String value, int direction, boolean strong) {
        table.addCell(PdfCells.compact(label, fonts.muted(), direction));
        table.addCell(PdfCells.compact(value, strong ? fonts.strong() : fonts.body(), direction));
    }

    private void detailData(PdfPTable table, String label, String value, int direction) {
        table.addCell(PdfCells.compact(label, fonts.muted(), direction));
        table.addCell(PdfCells.compactData(value, fonts.body(), direction));
    }

    private void money(PdfPTable table, String label, BigDecimal amount, int direction, boolean emphasise) {
        PdfPCell labelCell = PdfCells.compact(label, emphasise ? fonts.strong() : fonts.muted(), direction);
        PdfPCell valueCell = PdfCells.compact(PdfFormats.money(amount), emphasise ? fonts.strong() : fonts.body(), direction);
        valueCell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        valueCell.setHorizontalAlignment(direction == PdfWriter.RUN_DIRECTION_RTL ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
        if (emphasise) {
            PdfCells.shaded(labelCell, PdfConstants.HIGHLIGHT_BACKGROUND);
            PdfCells.shaded(valueCell, PdfConstants.HIGHLIGHT_BACKGROUND);
            labelCell.setPadding(PdfConstants.CELL_PADDING);
            valueCell.setPadding(PdfConstants.CELL_PADDING);
        }
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static PdfPCell shade(PdfPCell cell, Color background) {
        return background == null ? cell : PdfCells.shaded(cell, background);
    }

    private static PdfPCell highlight(PdfPCell cell) {
        return PdfCells.shaded(cell, PdfConstants.HIGHLIGHT_BACKGROUND);
    }
}
