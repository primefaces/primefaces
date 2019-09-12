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
package org.primefaces.component.datatable.export;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public class DataTableXMLExporter extends DataTableExporter {

    @Override
    public void doExport(FacesContext context, DataTable table, ExportConfiguration config) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        configureResponse(externalContext, config.getOutputFileName());
        StringBuilder builder = new StringBuilder();

        if (config.getPreProcessor() != null) {
            config.getPostProcessor().invoke(context.getELContext(), new Object[]{builder});
        }

        builder.append("<?xml version=\"1.0\"?>\n");
        builder.append("<" + table.getId() + ">\n");

        if (config.isPageOnly()) {
            exportPageOnly(context, table, builder);
        }
        else if (config.isSelectionOnly()) {
            exportSelectionOnly(context, table, builder);
        }
        else {
            exportAll(context, table, builder);
        }

        builder.append("</" + table.getId() + ">");

        table.setRowIndex(-1);

        if (config.getPostProcessor() != null) {
            config.getPostProcessor().invoke(context.getELContext(), new Object[]{builder});
        }

        OutputStream os = externalContext.getResponseOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, config.getEncodingType());
        PrintWriter writer = new PrintWriter(osw);
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    @Override
    protected void preRowExport(DataTable table, Object document) {
        ((StringBuilder) document).append("\t<" + table.getVar() + ">\n");
    }

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((StringBuilder) document).append("\t</" + table.getVar() + ">\n");
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        StringBuilder builder = (StringBuilder) document;
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                String columnTag = getColumnTag(col);
                try {
                    addColumnValue(builder, col.getChildren(), columnTag, col);
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
        else if (facet != null) {
            columnTag = exportValue(FacesContext.getCurrentInstance(), facet).toLowerCase();
        }
        else {
            throw new FacesException("No suitable xml tag found for " + column);
        }

        return EscapeUtils.forXmlTag(columnTag);
    }

    protected void addColumnValue(StringBuilder builder, List<UIComponent> components, String tag, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        builder.append("\t\t<" + tag + ">");

        if (column.getExportFunction() != null) {
            builder.append(EscapeUtils.forXml(exportColumnByFunction(context, column)));
        }
        else {
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);
                    if (value != null) {
                        builder.append(EscapeUtils.forXml(value));
                    }
                }
            }
        }

        builder.append("</" + tag + ">\n");
    }

    protected void configureResponse(ExternalContext externalContext, String filename) {
        externalContext.setResponseContentType("text/xml");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", filename + ".xml"));
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
    }

}
