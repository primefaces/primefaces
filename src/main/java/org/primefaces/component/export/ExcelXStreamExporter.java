/*
 * Copyright 2009-2016 PrimeTek.
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

import java.io.IOException;
import javax.faces.context.ExternalContext;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Different implementation of ExcelXExporter using the POI streaming API:
 * 
 * SXSSF (package: org.apache.poi.xssf.streaming) is an API-compatible streaming extension of XSSF
 * to be used when very large spreadsheets have to be produced, and heap space is limited.
 * SXSSF achieves its low memory footprint by limiting access to the rows that are within a sliding window.
 */
public class ExcelXStreamExporter extends ExcelXExporter {

    @Override
    protected Workbook createWorkBook() {
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(100);
        sxssfWorkbook.setCompressTempFiles(true);
        return sxssfWorkbook;
    }

    @Override
    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
        super.writeExcelToResponse(externalContext, generatedExcel, filename);
        ((SXSSFWorkbook) generatedExcel).dispose();
    }
}
