package com.almotawaj.wallet.config.constants;

import java.awt.Color;

public final class PdfConstants {
    public static final String REGULAR_FONT = "net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf";
    public static final String BOLD_FONT = "net/sf/jasperreports/fonts/dejavu/DejaVuSans-Bold.ttf";
    public static final String LOGO = "pdf/logo.png";
    public static final float LOGO_SIZE = 40f;
    public static final float[] HEADER_COLUMN_WIDTHS = {1f, 9f};
    public static final float TITLE_SIZE = 16f;
    public static final float HEADING_SIZE = 11f;
    public static final float BODY_SIZE = 9f;
    public static final float PAGE_MARGIN = 36f;
    public static final float CELL_PADDING = 6f;
    public static final float LINE_HEIGHT = 1.3f;
    public static final float SECTION_SPACING = 14f;
    public static final float FULL_WIDTH = 100f;
    public static final float[] DETAIL_COLUMN_WIDTHS = {1f, 2f};
    public static final float[] STATEMENT_COLUMN_WIDTHS = {2.2f, 3f, 2.4f, 3.4f, 2.2f, 2.2f};
    public static final Color MUTED_COLOR = new Color(100, 116, 139);
    public static final Color BORDER_COLOR = new Color(226, 232, 240);
    public static final Color HEADER_BACKGROUND = new Color(241, 245, 249);
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String CREDIT_SIGN = "+";
    public static final String DEBIT_SIGN = "-";
    public static final String EMPTY_VALUE = "-";
    public static final int STATEMENT_MAX_ROWS = 500;
    public static final String RECEIPT_FILE_NAME = "receipt-%s.pdf";
    public static final String STATEMENT_FILE_NAME = "statement-%s.pdf";
    public static final String LABEL_PREFIX = "pdf.";
    public static final String TYPE_LABEL_PREFIX = "type.";

    private PdfConstants() {
    }
}
