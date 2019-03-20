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
package org.primefaces.component.export;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.datatable.DataTable;

public class ExcelExportVisitCallback implements VisitCallback {

    private final ExcelExporter exporter;
    private final boolean pageOnly;
    private final boolean selectionOnly;
    private final Workbook workbook;

    public ExcelExportVisitCallback(ExcelExporter exporter, Workbook workbook, boolean pageOnly, boolean selectionOnly) {
        this.exporter = exporter;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.workbook = workbook;
    }

    @Override
    public VisitResult visit(VisitContext context, UIComponent target) {
        DataTable dt = (DataTable) target;
        FacesContext facesContext = context.getFacesContext();
        String sheetName = exporter.getSheetName(facesContext, dt);
        if (sheetName == null) {
            sheetName = dt.getClientId().replaceAll(":", "_");
        }

        Sheet sheet = workbook.createSheet(sheetName);
        exporter.exportTable(facesContext, dt, sheet, pageOnly, selectionOnly);
        return VisitResult.ACCEPT;
    }

}
