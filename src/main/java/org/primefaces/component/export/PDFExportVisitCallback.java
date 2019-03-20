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
import javax.faces.view.facelets.FaceletException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import org.primefaces.component.datatable.DataTable;

public class PDFExportVisitCallback implements VisitCallback {

    private final PDFExporter exporter;
    private final Document document;
    private final boolean pageOnly;
    private final boolean selectionOnly;
    private final String encoding;

    public PDFExportVisitCallback(PDFExporter exporter, Document document, boolean pageOnly, boolean selectionOnly, String encoding) {
        this.exporter = exporter;
        this.document = document;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.encoding = encoding;
    }

    @Override
    public VisitResult visit(VisitContext context, UIComponent target) {
        DataTable dt = (DataTable) target;
        try {
            document.add(exporter.exportPDFTable(context.getFacesContext(), dt, pageOnly, selectionOnly, encoding));

            Paragraph preface = new Paragraph();
            exporter.addEmptyLine(preface, 3);
            document.add(preface);

        }
        catch (DocumentException e) {
            throw new FaceletException(e.getMessage());
        }

        return VisitResult.ACCEPT;
    }

}
