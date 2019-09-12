/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.datatable.export;

import java.awt.Color;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.ComponentUtils;

public class DataTableExcelXExporter extends DataTableExcelExporter {

    @Override
    protected Workbook createWorkBook() {
        return new XSSFWorkbook();
    }

    @Override
    protected RichTextString createRichTextString(String value) {
        return new XSSFRichTextString(value);
    }

    @Override
    protected String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    protected String getContentDisposition(String filename) {
        return ComponentUtils.createContentDisposition("attachment", filename + ".xlsx");
    }

    @Override
    protected void applyFacetOptions(Workbook wb, ExporterOptions options, CellStyle facetStyle) {
        Font facetFont = getFont(wb, options);

        if (options != null) {
            String facetFontStyle = options.getFacetFontStyle();
            if (facetFontStyle != null) {
                if (facetFontStyle.equalsIgnoreCase("BOLD")) {
                    facetFont.setBold(true);
                }
                if (facetFontStyle.equalsIgnoreCase("ITALIC")) {
                    facetFont.setItalic(true);
                }
            }

            String facetBackground = options.getFacetBgColor();
            if (facetBackground != null) {
                XSSFColor backgroundColor = new XSSFColor(Color.decode(facetBackground));
                ((XSSFCellStyle) facetStyle).setFillForegroundColor(backgroundColor);
                facetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            String facetFontColor = options.getFacetFontColor();
            if (facetFontColor != null) {
                XSSFColor facetColor = new XSSFColor(Color.decode(facetFontColor));
                ((XSSFFont) facetFont).setColor(facetColor);
            }

            String facetFontSize = options.getFacetFontSize();
            if (facetFontSize != null) {
                facetFont.setFontHeightInPoints(Short.valueOf(facetFontSize));
            }
        }

        facetStyle.setFont(facetFont);
    }

    @Override
    protected void applyCellOptions(Workbook wb, ExporterOptions options, CellStyle cellStyle) {
        Font cellFont = getFont(wb, options);

        if (options != null) {
            String cellFontColor = options.getCellFontColor();
            if (cellFontColor != null) {
                XSSFColor cellColor = new XSSFColor(Color.decode(cellFontColor));
                ((XSSFFont) cellFont).setColor(cellColor);
            }

            String cellFontSize = options.getCellFontSize();
            if (cellFontSize != null) {
                cellFont.setFontHeightInPoints(Short.valueOf(cellFontSize));
            }

            String cellFontStyle = options.getCellFontStyle();
            if (cellFontStyle != null) {
                if (cellFontStyle.equalsIgnoreCase("BOLD")) {
                    cellFont.setBold(true);
                }
                if (cellFontStyle.equalsIgnoreCase("ITALIC")) {
                    cellFont.setItalic(true);
                }
            }
        }

        cellStyle.setFont(cellFont);
    }
}
