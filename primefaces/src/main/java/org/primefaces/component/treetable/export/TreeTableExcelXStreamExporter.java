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
package org.primefaces.component.treetable.export;

import org.primefaces.component.export.ExcelOptions;

import java.io.IOException;

import jakarta.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Different implementation of ExcelXExporter using the POI streaming API:
 *
 * SXSSF (package: org.apache.poi.xssf.streaming) is an API-compatible streaming extension of XSSF
 * to be used when very large spreadsheets have to be produced, and heap space is limited.
 * SXSSF achieves its low memory footprint by limiting access to the rows that are within a sliding window.
 */
public class TreeTableExcelXStreamExporter extends TreeTableExcelXExporter {

    @Override
    protected Workbook createDocument(FacesContext context) throws IOException {
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        sxssfWorkbook.setCompressTempFiles(true);
        return sxssfWorkbook;
    }

    @Override
    protected void postExport(FacesContext context) throws IOException {
        super.postExport(context);
        SXSSFWorkbook sxssfWorkbook = ((SXSSFWorkbook) document);
        sxssfWorkbook.close();
    }

    @Override
    protected void applyOptions(Sheet sheet) {
        super.applyOptions(sheet);
        SXSSFSheet sxssfSheet = (SXSSFSheet) sheet;
        ExcelOptions options = (ExcelOptions) exportConfiguration.getOptions();
        if (options == null || options.isAutoSizeColumn()) {
            sxssfSheet.trackAllColumnsForAutoSizing();
        }
        else {
            sxssfSheet.untrackAllColumnsForAutoSizing();
        }
    }
}
