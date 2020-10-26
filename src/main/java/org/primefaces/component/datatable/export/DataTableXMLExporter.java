/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.List;

public class DataTableXMLExporter extends DataTableExporter {

    private OutputStream outputStream;
    private ExportConfiguration exportConfiguration;

    @Override
    protected void preExport(FacesContext context, ExportConfiguration config, OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
        this.exportConfiguration = config;
    }

    @Override
    public void doExport(FacesContext context, DataTable table, ExportConfiguration config, int index) throws IOException {
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, config.getEncodingType());
            PrintWriter writer = new PrintWriter(osw);) {

            if (config.getPreProcessor() != null) {
                // PF 9 - attention: breaking change to PreProcessor (PrintWriter instead of writer)
                config.getPreProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            writer.append("<?xml version=\"1.0\"?>\n");
            writer.append("<" + table.getId() + ">\n");

            if (config.isPageOnly()) {
                exportPageOnly(context, table, writer);
            }
            else if (config.isSelectionOnly()) {
                exportSelectionOnly(context, table, writer);
            }
            else {
                exportAll(context, table, writer);
            }

            writer.append("</" + table.getId() + ">");

            table.setRowIndex(-1);

            if (config.getPostProcessor() != null) {
                // PF 9 - attention: breaking change to PostProcessor (PrintWriter instead of writer)
                config.getPostProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            writer.flush();
        }
    }

    @Override
    protected String getContentType() {
        return "text/xml; charset=" + exportConfiguration.getEncodingType();
    }

    @Override
    protected String getFileExtension() {
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
                    addColumnValue(writer, col.getChildren(), columnTag, col);
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

    protected void addColumnValue(PrintWriter writer, List<UIComponent> components, String tag, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        writer.append("\t\t<" + tag + ">");

        if (column.getExportFunction() != null) {
            writer.append(EscapeUtils.forXml(exportColumnByFunction(context, column)));
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
