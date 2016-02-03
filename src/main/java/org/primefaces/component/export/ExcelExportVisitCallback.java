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

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.datatable.DataTable;

public class ExcelExportVisitCallback implements VisitCallback {
	
    private ExcelExporter exporter;
    private boolean pageOnly;
    private boolean selectionOnly;
    private Workbook workbook;

    public ExcelExportVisitCallback(ExcelExporter exporter, Workbook workbook, boolean pageOnly, boolean selectionOnly) {
        this.exporter = exporter;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.workbook = workbook;
    }

    public VisitResult visit(VisitContext context, UIComponent target) {
        DataTable dt = (DataTable) target;
        FacesContext facesContext = context.getFacesContext();
        String sheetName = exporter.getSheetName(facesContext, dt);
        if(sheetName == null) {
            sheetName = dt.getClientId().replaceAll(":", "_");
        }
        
        Sheet sheet = workbook.createSheet(sheetName);
        exporter.exportTable(facesContext, dt, sheet, pageOnly, selectionOnly);
        return VisitResult.ACCEPT;
    }
    
}