package com.almotawaj.wallet.service.pdf;

import com.almotawaj.wallet.config.constants.PdfConstants;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

class PdfPageDecorator extends PdfPageEventHelper {
    private final PdfFonts fonts;
    private final PdfLabels labels;
    private final Image logo;
    private final String generatedAt;
    private PdfTemplate pageTotal;

    PdfPageDecorator(PdfFonts fonts, PdfLabels labels, Image logo, String generatedAt) {
        this.fonts = fonts;
        this.labels = labels;
        this.logo = logo;
        this.generatedAt = generatedAt;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pageTotal = writer.getDirectContent().createTemplate(PdfConstants.PAGE_TOTAL_WIDTH, PdfConstants.PAGE_TOTAL_HEIGHT);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();
        header(canvas, document);
        footer(canvas, document, writer.getPageNumber());
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        pageTotal.beginText();
        pageTotal.setFontAndSize(fonts.regularBase(), PdfConstants.SMALL_SIZE);
        pageTotal.setColorFill(PdfConstants.MUTED_COLOR);
        String total = String.valueOf(writer.getPageNumber() - 1);
        float width = fonts.regularBase().getWidthPoint(total, PdfConstants.SMALL_SIZE);
        pageTotal.setTextMatrix(isRtl() ? PdfConstants.PAGE_TOTAL_WIDTH - width : 0, PdfConstants.PAGE_TOTAL_BASELINE);
        pageTotal.showText(total);
        pageTotal.endText();
    }

    private void header(PdfContentByte canvas, Document document) {
        float top = document.getPageSize().getHeight();
        boolean rtl = isRtl();
        float start = rtl ? document.right() : document.left();
        float end = rtl ? document.left() : document.right();
        float logoX = rtl ? start - PdfConstants.LOGO_SIZE : start;

        Image mark = Image.getInstance(logo);
        mark.setAbsolutePosition(logoX, top - PdfConstants.HEADER_LOGO_Y_OFFSET);
        canvas.addImage(mark);

        float textStart = rtl ? logoX - PdfConstants.CELL_PADDING * 2 : start + PdfConstants.LOGO_SIZE + PdfConstants.CELL_PADDING * 2;
        int startAlign = rtl ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
        int endAlign = rtl ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT;
        show(canvas, startAlign, new Phrase(labels.get("appName"), fonts.heading()), textStart, top - PdfConstants.HEADER_TEXT_Y_OFFSET);
        show(canvas, startAlign, new Phrase(labels.get("address"), fonts.small()), textStart, top - PdfConstants.HEADER_SUBTEXT_Y_OFFSET);
        show(canvas, endAlign, new Phrase(labels.get("website"), fonts.small()), end, top - PdfConstants.HEADER_TEXT_Y_OFFSET);
        show(canvas, endAlign, new Phrase(generatedAt, fonts.small()), end, top - PdfConstants.HEADER_SUBTEXT_Y_OFFSET);

        rule(canvas, document, top - PdfConstants.HEADER_RULE_Y_OFFSET, PdfConstants.HEADER_RULE_WIDTH, true);
    }

    private void footer(PdfContentByte canvas, Document document, int pageNumber) {
        boolean rtl = isRtl();
        float start = rtl ? document.right() : document.left();
        float end = rtl ? document.left() : document.right();
        int startAlign = rtl ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;

        rule(canvas, document, PdfConstants.FOOTER_RULE_Y, PdfConstants.RULE_WIDTH, false);
        show(canvas, startAlign, new Phrase(labels.get("disclaimer"), fonts.small()), start, PdfConstants.FOOTER_TEXT_Y);
        show(canvas, startAlign, new Phrase(labels.get("simulation"), fonts.small()), start, PdfConstants.FOOTER_SUBTEXT_Y);

        float totalX = rtl ? end : end - PdfConstants.PAGE_TOTAL_WIDTH;
        float labelX = rtl ? end + PdfConstants.PAGE_TOTAL_WIDTH + PdfConstants.PAGE_LABEL_GAP : totalX - PdfConstants.PAGE_LABEL_GAP;
        show(canvas, rtl ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT, new Phrase(labels.get("page", String.valueOf(pageNumber)), fonts.small()),
                labelX, PdfConstants.FOOTER_TEXT_Y);
        canvas.addTemplate(pageTotal, totalX, PdfConstants.FOOTER_TEXT_Y - PdfConstants.PAGE_TOTAL_BASELINE);
    }

    private void rule(PdfContentByte canvas, Document document, float y, float width, boolean brand) {
        canvas.saveState();
        canvas.setLineWidth(width);
        canvas.setColorStroke(brand ? PdfConstants.BRAND_COLOR : PdfConstants.BORDER_COLOR);
        canvas.moveTo(document.left(), y);
        canvas.lineTo(document.right(), y);
        canvas.stroke();
        canvas.restoreState();
    }

    private void show(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y) {
        ColumnText.showTextAligned(canvas, alignment, phrase, x, y, 0, labels.runDirection(), 0);
    }

    private boolean isRtl() {
        return labels.runDirection() == PdfWriter.RUN_DIRECTION_RTL;
    }
}
