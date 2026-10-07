package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.Clock;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PdfDocuments {
    private final Image logo = PdfCells.loadImage(PdfConstants.LOGO, PdfConstants.LOGO_SIZE);
    private final PdfFonts fonts;
    private final Clock clock;

    public byte[] render(PdfLabels labels, String title, Rectangle pageSize, List<PdfPTable> sections) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(pageSize, PdfConstants.PAGE_MARGIN, PdfConstants.PAGE_MARGIN,
                PdfConstants.PAGE_MARGIN, PdfConstants.PAGE_MARGIN);
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(header(labels, title));
        sections.forEach(document::add);
        document.add(footer(labels));
        document.close();
        return output.toByteArray();
    }

    private PdfPTable header(PdfLabels labels, String title) {
        PdfPTable heading = PdfCells.table(labels.runDirection(), 1f);
        heading.addCell(PdfCells.plain(labels.get("appName"), fonts.muted(), labels.runDirection()));
        heading.addCell(PdfCells.plain(title, fonts.title(), labels.runDirection()));
        PdfPTable table = PdfCells.table(labels.runDirection(), PdfConstants.HEADER_COLUMN_WIDTHS);
        table.addCell(PdfCells.image(logo));
        table.addCell(PdfCells.nested(heading));
        return table;
    }

    private PdfPTable footer(PdfLabels labels) {
        PdfPTable table = PdfCells.table(labels.runDirection(), 1f);
        String generatedAt = labels.get("generatedAt", PdfFormats.dateTime(clock.instant()));
        table.addCell(PdfCells.plain(generatedAt, fonts.muted(), labels.runDirection()));
        return table;
    }
}
