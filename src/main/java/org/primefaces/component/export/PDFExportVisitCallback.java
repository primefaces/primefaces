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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.view.facelets.FaceletException;
import org.primefaces.component.datatable.DataTable;

public class PDFExportVisitCallback implements VisitCallback {
    
    private PDFExporter exporter;
    private Document document;
    private boolean pageOnly;
    private boolean selectionOnly;
    private String encoding;

    public PDFExportVisitCallback(PDFExporter exporter, Document document, boolean pageOnly, boolean selectionOnly, String encoding) {
        this.exporter = exporter;
        this.document = document;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.encoding = encoding;
    }

    public VisitResult visit(VisitContext context, UIComponent target) {
        DataTable dt = (DataTable) target;
        try {
            document.add(exporter.exportPDFTable(context.getFacesContext(), dt, pageOnly, selectionOnly, encoding));
            
            Paragraph preface = new Paragraph();
            exporter.addEmptyLine(preface, 3);
            document.add(preface);
            
        } catch (DocumentException e) {
            throw new FaceletException(e.getMessage());
        }
        
        return VisitResult.ACCEPT;
    }
    
}
