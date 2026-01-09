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

import org.primefaces.component.export.ExcelOptions;

import java.awt.Color;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

public class ExcelXmlStylesManager extends ExcelStylesManager {

    protected ExcelXmlStylesManager(Workbook wb, Locale locale, ExcelOptions options) {
        super(wb, locale, options);
    }

    @Override
    protected void applyFacetOptions(CellStyle style) {
        if (options != null) {
            applyXssfFacetOptions(style);
        }
    }

    @Override
    protected void applyCellOptions(CellStyle style) {
        if (options != null) {
            applyXssfCellOptions(style);
        }
    }


    @Override
    public RichTextString createRichTextString(String value) {
        return new XSSFRichTextString(value);
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
}
