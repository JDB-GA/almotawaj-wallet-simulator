package com.almotawaj.wallet.wallet.service.pdf;

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

import java.awt.Color;
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
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    public static PdfPCell data(String value, Font font, int tableDirection) {
        PdfPCell cell = text(value, font, PdfWriter.RUN_DIRECTION_LTR);
        cell.setHorizontalAlignment(isRtl(tableDirection) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        return cell;
    }

    public static PdfPCell amount(String value, Font font, int tableDirection) {
        PdfPCell cell = text(value, font, PdfWriter.RUN_DIRECTION_LTR);
        cell.setHorizontalAlignment(isRtl(tableDirection) ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
        return cell;
    }

    public static PdfPCell header(String value, Font font, int runDirection) {
        PdfPCell cell = text(value, font, runDirection);
        cell.setBackgroundColor(PdfConstants.HEADER_BACKGROUND);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell amountHeader(String value, Font font, int runDirection) {
        PdfPCell cell = header(value, font, runDirection);
        cell.setHorizontalAlignment(isRtl(runDirection) ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT);
        return cell;
    }

    public static PdfPCell plain(String value, Font font, int runDirection) {
        PdfPCell cell = text(value, font, runDirection);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell compact(String value, Font font, int runDirection) {
        PdfPCell cell = plain(value, font, runDirection);
        cell.setPaddingTop(1f);
        cell.setPaddingBottom(1f);
        return cell;
    }

    public static PdfPCell compactData(String value, Font font, int tableDirection) {
        PdfPCell cell = data(value, font, tableDirection);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(1f);
        cell.setPaddingBottom(1f);
        return cell;
    }

    public static PdfPCell shaded(PdfPCell cell, Color background) {
        cell.setBackgroundColor(background);
        return cell;
    }

    public static PdfPCell box(PdfPTable content) {
        PdfPCell cell = new PdfPCell(content);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(PdfConstants.BORDER_COLOR);
        cell.setPadding(PdfConstants.BOX_PADDING);
        return cell;
    }

    public static PdfPCell empty() {
        PdfPCell cell = new PdfPCell();
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

    public static PdfPCell nested(PdfPTable table) {
        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private static boolean isRtl(int direction) {
        return direction == PdfWriter.RUN_DIRECTION_RTL;
    }

    private static float[] reversed(float[] widths) {
        float[] result = new float[widths.length];
        for (int index = 0; index < widths.length; index++) {
            result[index] = widths[widths.length - 1 - index];
        }
        return result;
    }
}
