/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.util;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.component.export.ExcelOptions;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

public class ExcelStylesManager {
    protected static final String DEFAULT_FONT = HSSFFont.FONT_ARIAL;
    /**
     * Format: "m/d/yy"
     * @see org.apache.poi.ss.usermodel.BuiltinFormats
     */
    private static final short DEFAULT_DATE_EXCEL_FORMAT = 14;
    /**
     * Format: "h:mm"
     * @see org.apache.poi.ss.usermodel.BuiltinFormats
     */
    private static final short DEFAULT_TIME_EXCEL_FORMAT = 20;
    /**
     * Format: "m/d/yy h:mm"
     * @see org.apache.poi.ss.usermodel.BuiltinFormats
     */
    private static final short DEFAULT_DATETIME_EXCEL_FORMAT = 22;

    protected final Workbook wb;
    protected final ExcelOptions options;
    private final Locale locale;

    //internal
    private final boolean stronglyTypedCells;
    private final DecimalFormat numberFormat;
    private final DecimalFormat currencyFormat;
    private final Styles facetStyles;
    private final Styles cellStyles;

    protected ExcelStylesManager(Workbook wb, Locale locale, ExcelOptions options) {
        this.wb = wb;
        this.locale = locale;
        this.options = options;
        //internal vars
        this.stronglyTypedCells = options == null || options.isStronglyTypedCells();
        this.numberFormat = getNumberFormat();
        this.currencyFormat = getCurrencyFormat();
        this.facetStyles = new Styles(this::createDefaultFacetStyle);
        this.cellStyles = new Styles(this::createDefaultCellStyle);
    }

    public static ExcelStylesManager createExcelStylesManager(Workbook wb, Locale locale, ExcelOptions options) {
        if (wb instanceof HSSFWorkbook) {
            return new ExcelStylesManager(wb, locale, options);
        }
        else {
            return new ExcelXmlStylesManager(wb, locale, options);
        }
    }

    public void updateFacetCell(Cell cell, ColumnValue value) {
        if (value.isStringValue()) {
            updateCellStringValue(cell, Objects.toString(value, Constants.EMPTY_STRING), facetStyles);
        }
        else {
            updateCellObjectValue(cell, value.getValue(), facetStyles);
        }
    }

    /**
     * If value is an explicitly set value, then use that value to update the cell, otherwise if
     * ExcelOptions.isStronglyTypedCells = true then for cells check:
     * <pre>
     * Numeric - String that are all numbers make them a numeric cell
     * Currency - Convert to currency cell so math can be done in Excel
     * String - fallback to just a normal string cell
     * </pre>
     * Possible future enhancement of Date cells as well.
     *
     * @param column the column from which value comes
     * @param cell the cell to operate on
     * @param value the ColumnValue value to put in the cell
     */
    public void updateCell(UIColumn column, Cell cell, ColumnValue value) {
        if (value.isStringValue()) {
            updateCellStringValue(cell, Objects.toString(value, Constants.EMPTY_STRING), cellStyles);
        }
        else {
            updateCellObjectValue(cell, value.getValue(), cellStyles);
        }
        applyColumnAlignments(column, cell);
    }

    private void updateCellObjectValue(Cell cell, Object value, Styles styles) {
        if (value instanceof BigDecimal) {
            setBigDecimalValue(cell, numberFormat.format(value), (BigDecimal) value, styles);
        }
        else if (value instanceof Number) {
            setNumberValue(cell, (Number) value, styles);
        }
        else if (value instanceof LocalDate) {
            setLocalDateValue(cell, (LocalDate) value, styles);
        }
        else if (value instanceof LocalDateTime) {
            setLocalDateTimeValue(cell, (LocalDateTime) value, styles);
        }
        else if (value instanceof LocalTime) {
            setLocalTimeValue(cell, (LocalTime) value, styles);
        }
        else if (value instanceof Date) {
            setDateValue(cell, (Date) value, styles);
        }
        else {
            cell.setCellStyle(styles.getDefaultStyle());
            cell.setCellValue(createRichTextString(Objects.toString(value, Constants.EMPTY_STRING)));
        }
    }

    private void updateCellStringValue(Cell cell, String value, Styles styles) {
        boolean printed = false;
        if (stronglyTypedCells) {
            printed = setNumberValueIfAppropiate(cell, value, styles);

            if (!printed) {
                printed = setCurrencyValueIfAppropiate(cell, value, styles);
            }
        }

        // fall back to just printing the string value
        if (!printed) {
            cell.setCellStyle(styles.getDefaultStyle());
            cell.setCellValue(createRichTextString(value));
        }
    }

    private boolean setNumberValueIfAppropiate(Cell cell, String value, Styles styles) {
        BigDecimal bigDecimal = BigDecimalValidator.getInstance().validate(value, numberFormat);
        if (bigDecimal != null) {
            setBigDecimalValue(cell, value, bigDecimal, styles);
            return true;
        }
        else if (LangUtils.isNumeric(value)) {
            double number = Double.parseDouble(value);
            setNumberValue(cell, number, styles);
            return true;
        }
        return false;
    }

    private void setBigDecimalValue(Cell cell, String value, BigDecimal bigDecimal, Styles styles) {
        cell.setCellValue(bigDecimal.doubleValue());
        boolean withoutThousandSeparator = value.indexOf(numberFormat.getDecimalFormatSymbols().getGroupingSeparator()) == -1;
        CellStyle style;
        if (withoutThousandSeparator) {
            style = styles.getGeneralNumberStyle();
        }
        else {
            boolean hasDecimals = bigDecimal.stripTrailingZeros().scale() > 0;
            style = hasDecimals ? styles.getDecimalStyle() : styles.getIntegerStyle();
        }
        cell.setCellStyle(style);
    }

    private void setNumberValue(Cell cell, Number number, Styles styles) {
        cell.setCellValue(number.doubleValue());
        cell.setCellStyle(styles.getGeneralNumberStyle());
    }

    private void setLocalDateValue(Cell cell, LocalDate date, Styles styles) {
        cell.setCellValue(date);
        cell.setCellStyle(styles.getDateStyle());
    }

    private void setLocalDateTimeValue(Cell cell, LocalDateTime dateTime, Styles styles) {
        cell.setCellValue(dateTime);
        cell.setCellStyle(styles.getDateTimeStyle());
    }

    private void setLocalTimeValue(Cell cell, LocalTime localTime, Styles styles) {
        cell.setCellValue(CalendarUtils.convertLocalTime2Date(localTime));
        cell.setCellStyle(styles.getTimeStyle());
    }

    private void setDateValue(Cell cell, Date date, Styles styles) {
        cell.setCellValue(date);
        cell.setCellStyle(styles.getDateStyle());
    }

    private boolean setCurrencyValueIfAppropiate(Cell cell, String value, Styles styles) {
        Number currency = CurrencyValidator.getInstance().validate(value, currencyFormat);
        if (currency == null) {
            return false;
        }
        else {
            cell.setCellValue(currency.doubleValue());
            cell.setCellStyle(styles.getCurrencyStyle());
            return true;
        }
    }

    private void applyColumnAlignments(UIColumn column, Cell cell) {
        String[] styles = new String[] {column.getStyle(), column.getStyleClass()};
        if (LangUtils.containsIgnoreCase(styles, "right")) {
            CellUtil.setAlignment(cell, HorizontalAlignment.RIGHT);
        }
        else if (LangUtils.containsIgnoreCase(styles, "center")) {
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        }
        else if (LangUtils.containsIgnoreCase(styles, "left")) {
            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);
        }
    }

    private CellStyle createDefaultFacetStyle() {
        CellStyle style = wb.createCellStyle();
        style.setFont(createFont());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        applyFacetOptions(style);
        return style;
    }

    private CellStyle createDefaultCellStyle() {
        CellStyle style = wb.createCellStyle();
        style.setFont(createFont());
        applyCellOptions(style);
        return style;
    }

    protected void applyFacetOptions(CellStyle style) {
        if (options != null) {
            applyHssfFacetOptions(style);
        }
    }

    protected void applyCellOptions(CellStyle style) {
        if (options != null) {
            applyHssfCellOptions(style);
        }
    }

    private void applyHssfFacetOptions(CellStyle style) {
        Font facetFont = wb.getFontAt(style.getFontIndex());
        String facetFontStyle = options.getFacetFontStyle();
        if (facetFontStyle != null) {
            if ("BOLD".equalsIgnoreCase(facetFontStyle)) {
                facetFont.setBold(true);
            }
            if ("ITALIC".equalsIgnoreCase(facetFontStyle)) {
                facetFont.setItalic(true);
            }
        }

        HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
        Color color;

        String facetBackground = options.getFacetBgColor();
        if (facetBackground != null) {
            color = Color.decode(facetBackground);
            HSSFColor backgroundColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
            style.setFillForegroundColor(backgroundColor.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        String facetFontColor = options.getFacetFontColor();
        if (facetFontColor != null) {
            color = Color.decode(facetFontColor);
            HSSFColor facetColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
            facetFont.setColor(facetColor.getIndex());
        }

        String facetFontSize = options.getFacetFontSize();
        if (facetFontSize != null) {
            facetFont.setFontHeightInPoints(Short.parseShort(facetFontSize));
        }
    }


    private void applyHssfCellOptions(CellStyle style) {
        Font cellFont = wb.getFontAt(style.getFontIndex());
        String cellFontColor = options.getCellFontColor();
        if (cellFontColor != null) {
            HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
            Color color = Color.decode(cellFontColor);
            HSSFColor cellColor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
            cellFont.setColor(cellColor.getIndex());
        }

        String cellFontSize = options.getCellFontSize();
        if (cellFontSize != null) {
            cellFont.setFontHeightInPoints(Short.parseShort(cellFontSize));
        }

        String cellFontStyle = options.getCellFontStyle();
        if (cellFontStyle != null) {
            if ("BOLD".equalsIgnoreCase(cellFontStyle)) {
                cellFont.setBold(true);
            }
            if ("ITALIC".equalsIgnoreCase(cellFontStyle)) {
                cellFont.setItalic(true);
            }
        }
    }


    private Font createFont() {
        Font font = wb.createFont();

        if (options != null) {
            String fontName = LangUtils.isBlank(options.getFontName()) ? DEFAULT_FONT : options.getFontName();
            font.setFontName(fontName);
        }
        else {
            font.setFontName(DEFAULT_FONT);
        }
        return font;
    }

    public RichTextString createRichTextString(String value) {
        return new HSSFRichTextString(value);
    }

    protected String getCurrencyExcelFormat() {
        String pattern = currencyFormat.toLocalizedPattern();
        String[] patterns = pattern.split(";");
        return toExcelPattern(patterns[0], currencyFormat.getDecimalFormatSymbols());
    }

    protected String getFormattedIntegerExcelFormat() {
        if (options == null || options.getNumberFormat() == null) {
            return BuiltinFormats.getBuiltinFormat(3);
        }
        else {
            return toExcelPattern(numberFormat.toLocalizedPattern(), numberFormat.getDecimalFormatSymbols());
        }
    }

    protected String getFormattedDecimalExcelFormat() {
        if (options == null || options.getNumberFormat() == null) {
            return "#,##0.###";
        }
        else {
            return toExcelPattern(numberFormat.toLocalizedPattern(), numberFormat.getDecimalFormatSymbols());
        }
    }

    private String toExcelPattern(String pattern, DecimalFormatSymbols symbols) {

        StringBuilder buffer = new StringBuilder(pattern.length());
        for (int i = 0; i < pattern.length(); i++) {
            char character = pattern.charAt(i);
            if (character == symbols.getDecimalSeparator()) {
                buffer.append(".");
            }
            else if (character == symbols.getGroupingSeparator()) {
                buffer.append(",");
            }
            else if (character == symbols.getPatternSeparator()) {
                buffer.append(";");
            }
            else if (character == CurrencyValidator.CURRENCY_SYMBOL) {
                buffer.append("\"").append(symbols.getCurrencySymbol()).append("\"");
            }
            else {
                buffer.append(character);
            }
        }

        return buffer.toString();
    }

    private DecimalFormat getNumberFormat() {
        if (options != null && options.getNumberFormat() != null) {
            return options.getNumberFormat();
        }
        else {
            return (DecimalFormat) DecimalFormat.getInstance(locale);
        }
    }

    private DecimalFormat getCurrencyFormat() {
        if (options != null && options.getCurrencyFormat() != null) {
            return options.getCurrencyFormat();
        }
        else {
            return (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        }
    }

    private class Styles {
        private final Supplier<CellStyle> defaultStyleSupplier;
        private CellStyle defaultStyle;
        private CellStyle generalNumberStyle;
        private CellStyle decimalStyle;
        private CellStyle integerStyle;
        private CellStyle currencyStyle;
        private CellStyle dateStyle;
        private CellStyle timeStyle;
        private CellStyle dateTimeStyle;

        public Styles(Supplier<CellStyle> defaultStyleSupplier) {
            this.defaultStyleSupplier = defaultStyleSupplier;
        }

        public CellStyle getDefaultStyle() {
            if (defaultStyle == null) {
                defaultStyle = defaultStyleSupplier.get();
            }
            return defaultStyle;
        }

        public CellStyle getGeneralNumberStyle() {
            if (generalNumberStyle == null) {
                generalNumberStyle = createGeneralNumberStyle();
            }
            return generalNumberStyle;
        }

        private CellStyle getIntegerStyle() {
            if (integerStyle == null) {
                integerStyle = createIntegerStyle();
            }
            return integerStyle;
        }

        private CellStyle getDecimalStyle() {
            if (decimalStyle == null) {
                decimalStyle = createDecimalStyle();
            }
            return decimalStyle;
        }

        private CellStyle getCurrencyStyle() {
            if (currencyStyle == null) {
                currencyStyle = createCurrencyStyle();
            }
            return currencyStyle;
        }

        private CellStyle getDateStyle() {
            if (dateStyle == null) {
                dateStyle = createDateStyle();
            }
            return dateStyle;
        }

        private CellStyle getDateTimeStyle() {
            if (dateTimeStyle == null) {
                dateTimeStyle = createDateTimeStyle();
            }
            return dateTimeStyle;
        }

        private CellStyle getTimeStyle() {
            if (timeStyle == null) {
                timeStyle = createTimeStyle();
            }
            return timeStyle;
        }

        private CellStyle createDateTimeStyle() {
            CellStyle style = defaultStyleSupplier.get();
            short dataFormat;
            if (options.getExcelDateTimeFormat() != null) {
                String format = options.getExcelDateTimeFormat();
                dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            }
            else {
                dataFormat = DEFAULT_DATETIME_EXCEL_FORMAT;
            }
            style.setDataFormat(dataFormat);
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }

        private CellStyle createTimeStyle() {
            CellStyle style = defaultStyleSupplier.get();
            short dataFormat;
            if (options.getExcelTimeFormat() != null) {
                String format = options.getExcelTimeFormat();
                dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            }
            else {
                dataFormat = DEFAULT_TIME_EXCEL_FORMAT;
            }
            style.setDataFormat(dataFormat);
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }

        private CellStyle createGeneralNumberStyle() {
            CellStyle style = defaultStyleSupplier.get();
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }

        private CellStyle createIntegerStyle() {
            CellStyle style = createGeneralNumberStyle();
            String format = getFormattedIntegerExcelFormat();
            short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            style.setDataFormat(dataFormat);
            return style;
        }

        private CellStyle createDecimalStyle() {
            CellStyle style = defaultStyleSupplier.get();
            String format = getFormattedDecimalExcelFormat();
            short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            style.setDataFormat(dataFormat);
            return style;
        }

        private CellStyle createCurrencyStyle() {
            CellStyle style = defaultStyleSupplier.get();
            String format = getCurrencyExcelFormat();
            short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            style.setDataFormat(dataFormat);
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }

        private CellStyle createDateStyle() {
            CellStyle style = defaultStyleSupplier.get();
            short dataFormat;
            if (options.getExcelDateFormat() != null) {
                String format = options.getExcelDateFormat();
                dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
            }
            else {
                dataFormat = DEFAULT_DATE_EXCEL_FORMAT;
            }
            style.setDataFormat(dataFormat);
            style.setAlignment(HorizontalAlignment.RIGHT);
            return style;
        }
    }
}
