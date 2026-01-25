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
package org.primefaces.component.datatable.export;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.EscapeUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

/**
 * InMemoryDataTableXMLExporter exists to support post-processors adjusting text inside the document.
 * The existing DataTableXMLExporter uses a PrintWriter as the backing document, which is stream-based,
 * making it resistant to editing the text of the document after it has been written.
 * InMemoryDataTableXMLExporter uses a StringBuilder as the backing document,
 * allowing a post-processor to make any necessary changes to the entire document without restriction.
 * Though be aware there may be a higher memory requirement.
 */
public class InMemoryDataTableXMLExporter extends DataTableExporter<StringBuilder, ExporterOptions> {

    public InMemoryDataTableXMLExporter() {
        super(null, Collections.emptySet(), false);
    }

    @Override
    protected StringBuilder createDocument(FacesContext context) throws IOException {
        return new StringBuilder();
    }

    @Override
    protected void exportTable(FacesContext context, DataTable table, int index) throws IOException {
        String doctag = table.getExportTag() != null ? table.getExportTag() : table.getId();
        document.append("<?xml version=\"1.0\"?>\n").append("<").append(doctag).append(">\n");

        // GitHub #14103: must be called so visible columns are cached
        getExportableColumns(table);

        super.exportTable(context, table, index);

        document.append("</").append(doctag).append(">");
    }

    @Override
    protected void preRowExport(FacesContext context, DataTable table) {
        String rowtag = table.getExportRowTag() != null ? table.getExportRowTag() : table.getVar();
        document.append("\t<").append(rowtag).append(">\n");
    }

    @Override
    protected void postRowExport(FacesContext context, DataTable table) {
        String rowtag = table.getExportRowTag() != null ? table.getExportRowTag() : table.getVar();
        document.append("\t</").append(rowtag).append(">\n");

        super.postRowExport(context, table);
    }

    @Override
    protected void postExport(FacesContext context) throws IOException {
        super.postExport(context);

        if (document != null) {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(os(), exportConfiguration.getEncodingType());
                osw.write(document.toString());
                osw.flush();
            }
            catch (UnsupportedEncodingException e) {
                throw new FacesException(e);
            }
        }
    }

    @Override
    protected void exportCellValue(FacesContext context, DataTable table, UIColumn col, ColumnValue columnValue, int index) {
        String tag = getColumnExportTag(context, col);
        document.append("\t\t<").append(tag).append(">")
                .append(EscapeUtils.forXml(columnValue.toString()))
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
