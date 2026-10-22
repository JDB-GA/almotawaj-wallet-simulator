package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.almotawaj.wallet.model.TransactionDirection;
import com.almotawaj.wallet.model.WalletTransaction;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReceiptPdfRenderer {
    private final PdfDocuments documents;
    private final PdfFonts fonts;

    public byte[] render(WalletTransaction transaction, String ownerName, PdfLabels labels) {
        boolean isCredit = transaction.getDirection() == TransactionDirection.CREDIT;
        PdfPTable summary = details(labels);
        heading(summary, labels, "receipt.details");
        row(summary, labels, "amount", PdfFormats.signedMoney(transaction));
        localized(summary, labels, "type", labels.type(transaction.getType()));
        row(summary, labels, "reference", transaction.getReference());
        row(summary, labels, "date", PdfFormats.dateTime(transaction.getCreatedAt()));
        row(summary, labels, "balanceAfter", PdfFormats.money(transaction.getBalanceAfter()));
        localized(summary, labels, "note", PdfFormats.orEmpty(transaction.getDescription()));

        PdfPTable owner = details(labels);
        heading(owner, labels, isCredit ? "receiver" : "sender");
        localized(owner, labels, "name", ownerName);
        row(owner, labels, "iban", transaction.getWallet().getIban());

        PdfPTable counterparty = details(labels);
        heading(counterparty, labels, isCredit ? "sender" : "receiver");
        localized(counterparty, labels, "name", transaction.getCounterpartyName());
        row(counterparty, labels, "iban", PdfFormats.orEmpty(transaction.getCounterpartyIban()));
        row(counterparty, labels, "bic", PdfFormats.orEmpty(transaction.getCounterpartyBic()));
        row(counterparty, labels, "email", PdfFormats.orEmpty(transaction.getCounterpartyEmail()));
        row(counterparty, labels, "mobile", PdfFormats.orEmpty(transaction.getCounterpartyMobile()));

        PdfPTable title = documents.title(labels, labels.get("receipt.title"), List.of(
                new String[]{labels.get("reference"), transaction.getReference()},
                new String[]{labels.get("date"), PdfFormats.dateTime(transaction.getCreatedAt())}));
        return documents.render(labels, PageSize.A4, List.of(title, summary, owner, counterparty));
    }

    private static PdfPTable details(PdfLabels labels) {
        return PdfCells.table(labels.runDirection(), PdfConstants.DETAIL_COLUMN_WIDTHS);
    }

    private void heading(PdfPTable table, PdfLabels labels, String key) {
        PdfPCell cell = PdfCells.header(labels.get(key), fonts.tableHeader(), labels.runDirection());
        cell.setColspan(2);
        table.addCell(cell);
    }

    private void row(PdfPTable table, PdfLabels labels, String key, String value) {
        table.addCell(PdfCells.text(labels.get(key), fonts.muted(), labels.runDirection()));
        table.addCell(PdfCells.data(value, fonts.strong(), labels.runDirection()));
    }

    private void localized(PdfPTable table, PdfLabels labels, String key, String value) {
        table.addCell(PdfCells.text(labels.get(key), fonts.muted(), labels.runDirection()));
        table.addCell(PdfCells.text(value, fonts.strong(), labels.runDirection()));
    }
}
