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

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.ExcelOptions;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Locale;

public class ExcelStylesManager {
    protected static final String DEFAULT_FONT = HSSFFont.FONT_ARIAL;

    private final Workbook wb;
    private final Locale locale;
    private final ExcelOptions options;

    //internal
    private final boolean hssf;
    private final boolean stronglyTypedCells;
    private final DecimalFormat decimalFormat;
    private CellStyle facetStyle;
    private CellStyle defaultCellStyle;
    private CellStyle numberStyle;
    private CellStyle currencyStyle;

    public ExcelStylesManager(Workbook wb, Locale locale, ExcelOptions options) {
        this.wb = wb;
        this.locale = locale;
        this.options = options;
        //internal vars
        this.hssf = wb instanceof HSSFWorkbook;
        this.stronglyTypedCells = options == null || options.isStronglyTypedCells();
        this.decimalFormat = getDecimalFormat();
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
            printed = applyNumberStyleIfApropiate(cell, value);

            if (!printed) {
                printed = applyCurrencyStyleIfApropiate(cell, value);
            }
        }

        // fall back to just printing the string value
        if (!printed) {
            cell.setCellStyle(getDefaultCellStyle());
            cell.setCellValue(createRichTextString(value));
        }
    }

    private boolean applyNumberStyleIfApropiate(Cell cell, String value) {
        if (LangUtils.isNumeric(value)) {
            cell.setCellValue(Double.parseDouble(value));
            cell.setCellStyle(getNumberStyle());
            return true;
        }
        else {
            return false;
        }
    }

    private boolean applyCurrencyStyleIfApropiate(Cell cell, String value) {
        Number currency = CurrencyValidator.getInstance().validate(value, decimalFormat);
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

    private CellStyle getNumberStyle() {
        if (numberStyle == null) {
            numberStyle = createNumberStyle();
        }
        return numberStyle;
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

    private CellStyle createNumberStyle() {
        CellStyle style = createDefaultCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        applyCellOptions(style);
        return style;
    }

    private CellStyle createCurrencyStyle() {
        CellStyle style = createNumberStyle();
        String pattern = CurrencyValidator.getInstance().getExcelPattern(decimalFormat);
        short currencyPattern = wb.getCreationHelper().createDataFormat().getFormat(pattern);
        style.setDataFormat(currencyPattern);
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
            facetStyle.setFillForegroundColor(backgroundColor.getIndex());
            facetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
            ((XSSFCellStyle) facetStyle).setFillForegroundColor(backgroundColor);
            facetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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

    private DecimalFormat getDecimalFormat() {
        if (options != null && options.getDecimalFormat() != null) {
            return options.getDecimalFormat();
        }
        else {
            return (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        }
    }
}
