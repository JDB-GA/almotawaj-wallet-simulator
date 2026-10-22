package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
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

    public byte[] render(PdfLabels labels, Rectangle pageSize, List<? extends Element> sections) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(pageSize, PdfConstants.PAGE_MARGIN, PdfConstants.PAGE_MARGIN,
                PdfConstants.PAGE_TOP_MARGIN, PdfConstants.PAGE_BOTTOM_MARGIN);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        writer.setPageEvent(new PdfPageDecorator(fonts, labels, logo, labels.get("generatedAt", PdfFormats.dateTime(clock.instant()))));
        document.addTitle(labels.get("appName"));
        document.addCreator(labels.get("appName"));
        document.open();
        sections.forEach(document::add);
        document.close();
        return output.toByteArray();
    }

    public PdfPTable title(PdfLabels labels, String title, List<String[]> facts) {
        int direction = labels.runDirection();
        PdfPTable table = PdfCells.table(direction, PdfConstants.TITLE_COLUMN_WIDTHS);
        table.addCell(PdfCells.plain(title, fonts.title(), direction));
        PdfPTable details = PdfCells.table(direction, PdfConstants.DETAIL_COLUMN_WIDTHS);
        details.setSpacingAfter(0);
        for (String[] fact : facts) {
            details.addCell(PdfCells.compact(fact[0], fonts.muted(), direction));
            details.addCell(PdfCells.compactData(fact[1], fonts.strong(), direction));
        }
        table.addCell(PdfCells.nested(details));
        return table;
    }
}
