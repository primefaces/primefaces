/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;

public class DataTableXMLExporter extends DataTableExporter {

    @Override
    public void doExport(FacesContext context, DataTable table, ExportConfiguration exportConfiguration, int index) throws IOException {

        try (OutputStreamWriter osw = new OutputStreamWriter(getOutputStream(), exportConfiguration.getEncodingType());
            PrintWriter writer = new PrintWriter(osw);) {

            if (exportConfiguration.getPreProcessor() != null) {
                // PF 9 - attention: breaking change to PreProcessor (PrintWriter instead of writer)
                exportConfiguration.getPreProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            writer.append("<?xml version=\"1.0\"?>\n");
            writer.append("<" + table.getId() + ">\n");

            if (exportConfiguration.isPageOnly()) {
                exportPageOnly(context, table, writer);
            }
            else if (exportConfiguration.isSelectionOnly()) {
                exportSelectionOnly(context, table, writer);
            }
            else {
                exportAll(context, table, writer);
            }

            writer.append("</" + table.getId() + ">");

            table.setRowIndex(-1);

            if (exportConfiguration.getPostProcessor() != null) {
                // PF 9 - attention: breaking change to PostProcessor (PrintWriter instead of writer)
                exportConfiguration.getPostProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            writer.flush();
        }
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public String getFileExtension() {
        return ".xml";
    }

    @Override
    protected void preRowExport(DataTable table, Object document) {
        ((PrintWriter) document).append("\t<" + table.getVar() + ">\n");
    }

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((PrintWriter) document).append("\t</" + table.getVar() + ">\n");
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        PrintWriter writer = (PrintWriter) document;
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                String columnTag = getColumnTag(col);
                try {
                    addColumnValue(writer, table, col.getChildren(), columnTag, col);
                }
                catch (IOException ex) {
                    throw new FacesException(ex);
                }
            }
        }
    }

    protected String getColumnTag(UIColumn column) {
        String headerText = (column.getExportHeaderValue() != null) ? column.getExportHeaderValue() : column.getHeaderText();
        UIComponent facet = column.getFacet("header");
        String columnTag;

        if (headerText != null) {
            columnTag = headerText.toLowerCase();
        }
        else if (ComponentUtils.shouldRenderFacet(facet)) {
            columnTag = exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();
        }
        else {
            throw new FacesException("No suitable xml tag found for " + column);
        }

        return EscapeUtils.forXmlTag(columnTag);
    }

    protected void addColumnValue(PrintWriter writer, DataTable table, List<UIComponent> components, String tag, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        writer.append("\t\t<" + tag + ">");

        if (LangUtils.isNotBlank(column.getExportValue())) {
            writer.append(EscapeUtils.forXml(column.getExportValue()));
        }
        else if (column.getExportFunction() != null) {
            writer.append(EscapeUtils.forXml(exportColumnByFunction(context, column)));
        }
        else if (LangUtils.isNotBlank(column.getField())) {
            String value = table.getConvertedFieldValue(context, column);
            writer.append(EscapeUtils.forXml(Objects.toString(value, Constants.EMPTY_STRING)));
        }
        else {
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);
                    if (value != null) {
                        writer.append(EscapeUtils.forXml(value));
                    }
                }
            }
        }

        writer.append("</" + tag + ">\n");
    }
}
