/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.ExcelOptions;

public class ExcelStylesManager {
    protected static final String DEFAULT_FONT = HSSFFont.FONT_ARIAL;

    private final Workbook wb;
    private final Locale locale;
    private final ExcelOptions options;

    //internal
    private final boolean hssf;
    private final boolean stronglyTypedCells;
    private final DecimalFormat numberFormat;
    private final DecimalFormat currencyFormat;
    private CellStyle facetStyle;
    private CellStyle defaultCellStyle;
    private CellStyle generalNumberStyle;
    private CellStyle formattedDecimalStyle;
    private CellStyle formattedIntegerStyle;
    private CellStyle currencyStyle;

    public ExcelStylesManager(Workbook wb, Locale locale, ExcelOptions options) {
        this.wb = wb;
        this.locale = locale;
        this.options = options;
        //internal vars
        this.hssf = wb instanceof HSSFWorkbook;
        this.stronglyTypedCells = options == null || options.isStronglyTypedCells();
        this.numberFormat = getNumberFormat();
        this.currencyFormat = getCurrencyFormat();
    }

    /**
     * If ExcelOptions.isStronglyTypedCells = true then for cells check:
     * <pre>
     * Numeric - String that are all numbers make them a numeric cell
     * Currency - Convert to currency cell so math can be done in Excel
     * String - fallback to just a normal string cell
     * </pre>
     * Possible future enhancement of Date cells as well.
     *
     * @param column the column from which value comes
     * @param cell the cell to operate on
     * @param value the String value to put in the cell
     */
    public void updateCell(UIColumn column, Cell cell, String value) {
        updateCell(cell, value);
        applyColumnAlignments(column, cell);
    }

    private void updateCell(Cell cell, String value) {
        boolean printed = false;
        if (stronglyTypedCells) {
            printed = setNumberValueIfAppropiate(cell, value);

            if (!printed) {
                printed = setCurrencyValueIfAppropiate(cell, value);
            }
        }

        // fall back to just printing the string value
        if (!printed) {
            cell.setCellStyle(getDefaultCellStyle());
            cell.setCellValue(createRichTextString(value));
        }
    }

    private boolean setNumberValueIfAppropiate(Cell cell, String value) {
        BigDecimal bigDecimal = BigDecimalValidator.getInstance().validate(value, numberFormat);
        if (bigDecimal != null) {
            cell.setCellValue(bigDecimal.doubleValue());
            boolean withoutThousandSeparator = value.indexOf(numberFormat.getDecimalFormatSymbols().getGroupingSeparator()) == -1;
            CellStyle style;
            if (withoutThousandSeparator) {
                style = getGeneralNumberStyle();
            }
            else {
                boolean hasDecimals = bigDecimal.stripTrailingZeros().scale() > 0;
                style = hasDecimals ? getFormattedDecimalStyle() : getFormattedIntegerStyle();
            }
            cell.setCellStyle(style);
            return true;
        }
        else if (LangUtils.isNumeric(value)) {
            double number = Double.parseDouble(value);
            cell.setCellValue(number);
            cell.setCellStyle(getGeneralNumberStyle());
            return true;
        }
        return false;
    }

    private boolean setCurrencyValueIfAppropiate(Cell cell, String value) {
        Number currency = CurrencyValidator.getInstance().validate(value, currencyFormat);
        if (currency == null) {
            return false;
        }
        else {
            cell.setCellValue(currency.doubleValue());
            cell.setCellStyle(getCurrencyStyle());
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

    public CellStyle getFacetStyle() {
        if (facetStyle == null) {
            facetStyle = createFacetStyle();
        }
        return facetStyle;
    }

    private CellStyle getDefaultCellStyle() {
        if (defaultCellStyle == null) {
            defaultCellStyle = createDefaultCellStyle();
        }
        return defaultCellStyle;
    }

    private CellStyle getGeneralNumberStyle() {
        if (generalNumberStyle == null) {
            generalNumberStyle = createGeneralNumberStyle();
        }
        return generalNumberStyle;
    }

    private CellStyle getFormattedIntegerStyle() {
        if (formattedIntegerStyle == null) {
            formattedIntegerStyle = createFormattedIntegerStyle();
        }
        return formattedIntegerStyle;
    }

    private CellStyle getFormattedDecimalStyle() {
        if (formattedDecimalStyle == null) {
            formattedDecimalStyle = createFormattedDecimalStyle();
        }
        return formattedDecimalStyle;
    }

    private CellStyle getCurrencyStyle() {
        if (currencyStyle == null) {
            currencyStyle = createCurrencyStyle();
        }
        return currencyStyle;
    }

    private CellStyle createFacetStyle() {
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

    private CellStyle createGeneralNumberStyle() {
        CellStyle style = createDefaultCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createFormattedIntegerStyle() {
        CellStyle style = createGeneralNumberStyle();
        String format = getFormattedIntegerExcelFormat();
        short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
        style.setDataFormat(dataFormat);
        return style;
    }

    private CellStyle createFormattedDecimalStyle() {
        CellStyle style = createGeneralNumberStyle();
        String format = getFormattedDecimalExcelFormat();
        short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
        style.setDataFormat(dataFormat);
        return style;
    }

    private CellStyle createCurrencyStyle() {
        CellStyle style = createDefaultCellStyle();
        String format = getCurrencyExcelFormat();
        short dataFormat = wb.getCreationHelper().createDataFormat().getFormat(format);
        style.setDataFormat(dataFormat);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void applyFacetOptions(CellStyle style) {
        if (options != null) {
            if (hssf) {
                applyHssfFacetOptions(style);
            }
            else {
                applyXssfFacetOptions(style);
            }
        }
    }

    private void applyCellOptions(CellStyle style) {
        if (options != null) {
            if (hssf) {
                applyHssfCellOptions(style);
            }
            else {
                applyXssfCellOptions(style);
            }
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

    private void applyXssfFacetOptions(CellStyle style) {
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

        String facetBackground = options.getFacetBgColor();
        if (facetBackground != null) {
            XSSFColor backgroundColor = new XSSFColor(Color.decode(facetBackground), new DefaultIndexedColorMap());
            ((XSSFCellStyle) style).setFillForegroundColor(backgroundColor);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        String facetFontColor = options.getFacetFontColor();
        if (facetFontColor != null) {
            XSSFColor facetColor = new XSSFColor(Color.decode(facetFontColor), new DefaultIndexedColorMap());
            ((XSSFFont) facetFont).setColor(facetColor);
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

    private void applyXssfCellOptions(CellStyle style) {
        Font cellFont = wb.getFontAt(style.getFontIndex());
        String cellFontColor = options.getCellFontColor();
        if (cellFontColor != null) {
            XSSFColor cellColor = new XSSFColor(Color.decode(cellFontColor), new DefaultIndexedColorMap());
            ((XSSFFont) cellFont).setColor(cellColor);
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
        if (hssf) {
            return new HSSFRichTextString(value);
        }
        else {
            return new XSSFRichTextString(value);
        }
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
}
