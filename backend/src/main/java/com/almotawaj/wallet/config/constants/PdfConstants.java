package com.almotawaj.wallet.config.constants;

import java.awt.Color;

public final class PdfConstants {
    public static final String REGULAR_FONT = "pdf/fonts/IBMPlexSansArabic-Regular.ttf";
    public static final String BOLD_FONT = "pdf/fonts/IBMPlexSansArabic-SemiBold.ttf";
    public static final String LOGO = "pdf/logo.png";
    public static final float LOGO_SIZE = 30f;
    public static final float TITLE_SIZE = 15f;
    public static final float HEADING_SIZE = 10f;
    public static final float BODY_SIZE = 8.5f;
    public static final float SMALL_SIZE = 7f;
    public static final float PAGE_MARGIN = 36f;
    public static final float PAGE_TOP_MARGIN = 82f;
    public static final float PAGE_BOTTOM_MARGIN = 58f;
    public static final float HEADER_LOGO_Y_OFFSET = 52f;
    public static final float HEADER_TEXT_Y_OFFSET = 34f;
    public static final float HEADER_SUBTEXT_Y_OFFSET = 46f;
    public static final float HEADER_RULE_Y_OFFSET = 62f;
    public static final float FOOTER_RULE_Y = 44f;
    public static final float FOOTER_TEXT_Y = 30f;
    public static final float FOOTER_SUBTEXT_Y = 20f;
    public static final float RULE_WIDTH = 0.6f;
    public static final float HEADER_RULE_WIDTH = 1.5f;
    public static final float PAGE_TOTAL_WIDTH = 16f;
    public static final float PAGE_LABEL_GAP = 2f;
    public static final float PAGE_TOTAL_HEIGHT = 14f;
    public static final float PAGE_TOTAL_BASELINE = 3f;
    public static final float CELL_PADDING = 4.5f;
    public static final float BOX_PADDING = 8f;
    public static final float LINE_HEIGHT = 1.35f;
    public static final float SECTION_SPACING = 12f;
    public static final float FULL_WIDTH = 100f;
    public static final float BOX_GAP_WIDTH = 0.08f;
    public static final float[] DETAIL_COLUMN_WIDTHS = {1f, 2f};
    public static final float[] SUMMARY_COLUMN_WIDTHS = {1.3f, 1f};
    public static final float[] TITLE_COLUMN_WIDTHS = {1.2f, 1f};
    public static final float[] BOXES_COLUMN_WIDTHS = {1f, BOX_GAP_WIDTH, 1f};
    public static final float[] STATEMENT_COLUMN_WIDTHS = {1.55f, 4.2f, 2.7f, 1.7f, 1.7f, 1.85f};
    public static final Color BRAND_COLOR = new Color(15, 39, 71);
    public static final Color TEXT_COLOR = new Color(30, 41, 59);
    public static final Color MUTED_COLOR = new Color(100, 116, 139);
    public static final Color BORDER_COLOR = new Color(203, 213, 225);
    public static final Color HEADER_BACKGROUND = new Color(15, 39, 71);
    public static final Color HEADER_TEXT_COLOR = Color.WHITE;
    public static final Color STRIPE_BACKGROUND = new Color(246, 248, 251);
    public static final Color HIGHLIGHT_BACKGROUND = new Color(234, 239, 246);
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final String AMOUNT_PATTERN = "#,##0.000";
    public static final String CREDIT_SIGN = "+";
    public static final String DEBIT_SIGN = "-";
    public static final String EMPTY_VALUE = "-";
    public static final String DESCRIPTION_SEPARATOR = " · ";
    public static final String STATEMENT_NUMBER_PATTERN = "STM-%s-%s";
    public static final String STATEMENT_NUMBER_DATE_PATTERN = "yyyyMMddHHmm";
    public static final int STATEMENT_NUMBER_IBAN_DIGITS = 6;
    public static final int STATEMENT_MAX_ROWS = 5000;
    public static final String RECEIPT_FILE_NAME = "receipt-%s.pdf";
    public static final String STATEMENT_FILE_NAME = "statement-%s.pdf";
    public static final String LABEL_PREFIX = "pdf.";
    public static final String TYPE_LABEL_PREFIX = "type.";

    private PdfConstants() {
    }
}
