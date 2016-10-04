/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.export;

import java.awt.Color;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelXExporter extends ExcelExporter {

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
        return "attachment;filename="+ filename + ".xlsx";
    }

    @Override
    protected void applyFacetOptions(Workbook wb, ExporterOptions options, CellStyle facetStyle) {
        Font facetFont = wb.createFont();
        facetFont.setFontName("Arial");
        
        if(options != null) {
            String facetFontStyle = options.getFacetFontStyle();
            if(facetFontStyle != null) {
                if(facetFontStyle.equalsIgnoreCase("BOLD")) {
                    facetFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                }
                if(facetFontStyle.equalsIgnoreCase("ITALIC")) {
                    facetFont.setItalic(true);
                }
            }

            String facetBackground = options.getFacetBgColor();
            if(facetBackground != null) {
                XSSFColor backgroundColor = new XSSFColor(Color.decode(facetBackground));
                ((XSSFCellStyle) facetStyle).setFillForegroundColor(backgroundColor);
                facetStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            }

            String facetFontColor = options.getFacetFontColor();
            if(facetFontColor != null) {
                XSSFColor facetColor = new XSSFColor(Color.decode(facetFontColor));
                ((XSSFFont) facetFont).setColor(facetColor);
            }

            String facetFontSize = options.getFacetFontSize();
            if(facetFontSize != null) {
                facetFont.setFontHeightInPoints(Short.valueOf(facetFontSize));
            }
        }
        
        facetStyle.setFont(facetFont);
    }
    
    @Override
    protected void applyCellOptions(Workbook wb, ExporterOptions options, CellStyle cellStyle) { 
        Font cellFont = wb.createFont();
        cellFont.setFontName("Arial");
        
        if(options != null) {
            String cellFontColor = options.getCellFontColor();
            if(cellFontColor != null) {
                XSSFColor cellColor = new XSSFColor(Color.decode(cellFontColor));
                ((XSSFFont) cellFont).setColor(cellColor);
            }

            String cellFontSize = options.getCellFontSize();
            if(cellFontSize != null) {
                cellFont.setFontHeightInPoints(Short.valueOf(cellFontSize));
            }

            String cellFontStyle = options.getCellFontStyle();
            if(cellFontStyle != null) {
                if(cellFontStyle.equalsIgnoreCase("BOLD")) {
                    cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                }
                if(cellFontStyle.equalsIgnoreCase("ITALIC")) {
                    cellFont.setItalic(true);
                }
            }
        }
        
        cellStyle.setFont(cellFont);
    }
}
