package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class PdfCells {
    private PdfCells() {
    }

    public static PdfPTable table(int runDirection, float... widths) {
        PdfPTable table = new PdfPTable(runDirection == PdfWriter.RUN_DIRECTION_RTL ? reversed(widths) : widths);
        table.setWidthPercentage(PdfConstants.FULL_WIDTH);
        table.setRunDirection(runDirection);
        table.setSpacingAfter(PdfConstants.SECTION_SPACING);
        return table;
    }

    public static PdfPCell text(String value, Font font, int runDirection) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setRunDirection(runDirection);
        cell.setPadding(PdfConstants.CELL_PADDING);
        cell.setLeading(0f, PdfConstants.LINE_HEIGHT);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(PdfConstants.BORDER_COLOR);
        return cell;
    }

    public static PdfPCell data(String value, Font font, int tableDirection) {
        PdfPCell cell = text(value, font, PdfWriter.RUN_DIRECTION_LTR);
        cell.setHorizontalAlignment(tableDirection == PdfWriter.RUN_DIRECTION_RTL ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        return cell;
    }

    public static PdfPCell header(String value, Font font, int runDirection) {
        PdfPCell cell = text(value, font, runDirection);
        cell.setBackgroundColor(PdfConstants.HEADER_BACKGROUND);
        return cell;
    }

    public static PdfPCell plain(String value, Font font, int runDirection) {
        PdfPCell cell = text(value, font, runDirection);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static Image loadImage(String path, float size) {
        try {
            Image image = Image.getInstance(new ClassPathResource(path).getContentAsByteArray());
            image.scaleToFit(size, size);
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static PdfPCell image(Image image) {
        PdfPCell cell = new PdfPCell(image, false);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    public static PdfPCell nested(PdfPTable table) {
        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private static float[] reversed(float[] widths) {
        float[] result = new float[widths.length];
        for (int index = 0; index < widths.length; index++) {
            result[index] = widths[widths.length - 1 - index];
        }
        return result;
    }
}
