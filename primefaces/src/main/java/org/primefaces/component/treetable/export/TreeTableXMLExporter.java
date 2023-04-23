/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.ExporterUtils;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.util.EscapeUtils;

public class TreeTableXMLExporter extends TreeTableExporter<PrintWriter, ExporterOptions> {

    public TreeTableXMLExporter() {
        super(null, Collections.emptySet(), false);
    }

    @Override
    protected PrintWriter createDocument(FacesContext context) throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(os(), exportConfiguration.getEncodingType());
            return new PrintWriter(osw);
        }
        catch (UnsupportedEncodingException e) {
            throw new FacesException(e);
        }
    }

    @Override
    protected void exportTable(FacesContext context, TreeTable table, int index) throws IOException {
        String doctag = table.getExportTag() != null ? table.getExportTag() : table.getId();
        document.append("<?xml version=\"1.0\"?>\n").append("<").append(doctag).append(">\n");

        super.exportTable(context, table, index);

        document.append("</").append(doctag).append(">");
    }

    @Override
    protected void preRowExport(FacesContext context, TreeTable table) {
        String rowtag = table.getExportRowTag() != null ? table.getExportRowTag() : table.getVar();
        document.append("\t<").append(rowtag).append(">\n");
    }

    @Override
    protected void postRowExport(FacesContext context, TreeTable table) {
        String rowtag = table.getExportRowTag() != null ? table.getExportRowTag() : table.getVar();
        document.append("\t</").append(rowtag).append(">\n");

        super.postRowExport(context, table);
    }

    @Override
    protected void postExport(FacesContext context) throws IOException {
        super.postExport(context);

        if (document != null) {
            document.flush();
        }
    }

    @Override
    protected void exportCellValue(FacesContext context, TreeTable table, UIColumn col, String text, int index) {
        String tag = ExporterUtils.getColumnExportTag(context, col);
        document.append("\t\t<").append(tag).append(">")
                .append(EscapeUtils.forXml(text))
                .append("</").append(tag).append(">\n");
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public String getFileExtension() {
        return ".xml";
    }
}
