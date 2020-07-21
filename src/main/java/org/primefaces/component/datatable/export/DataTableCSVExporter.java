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

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.CSVOptions;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class DataTableCSVExporter extends DataTableExporter {

    private CSVOptions csvOptions;
    private StringBuilder sb;

    protected StringBuilder createStringBuilder() {
        return new StringBuilder();
    }

    protected StringBuilder getStringBuilder() {
        return sb;
    }

    @Override
    protected void preExport(FacesContext context, ExportConfiguration config) throws IOException {
        csvOptions = CSVOptions.EXCEL;
        ExporterOptions options = config.getOptions();
        if (options != null) {
            if (options instanceof CSVOptions) {
                csvOptions = (CSVOptions) options;
            }
            else {
                throw new IllegalArgumentException("Options must be an instance of CSVOptions.");
            }
        }
    }

    @Override
    public void doExport(FacesContext context, DataTable table, ExportConfiguration config, int index) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        configureResponse(context, config.getOutputFileName(), config.getEncodingType());
        sb = createStringBuilder();

        if (config.getPreProcessor() != null) {
            config.getPreProcessor().invoke(context.getELContext(), new Object[]{sb});
        }

        addColumnFacets(sb, table, ColumnType.HEADER);

        if (config.isPageOnly()) {
            exportPageOnly(context, table, sb);
        }
        else if (config.isSelectionOnly()) {
            exportSelectionOnly(context, table, sb);
        }
        else {
            exportAll(context, table, sb);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(sb, table, ColumnType.FOOTER);
        }

        if (config.getPostProcessor() != null) {
            config.getPostProcessor().invoke(context.getELContext(), new Object[]{sb});
        }

        Writer writer = externalContext.getResponseOutputWriter();
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    protected void addColumnFacets(StringBuilder builder, DataTable table, ColumnType columnType) throws IOException {
        boolean firstCellWritten = false;

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    builder.append(csvOptions.getDelimiterChar());
                }

                UIComponent facet = col.getFacet(columnType.facet());
                String textValue;
                switch (columnType) {
                    case HEADER:
                        textValue = col.getExportHeaderValue() != null ? col.getExportHeaderValue() : col.getHeaderText();
                        break;
                    case FOOTER:
                        textValue = col.getExportFooterValue() != null ? col.getExportFooterValue() : col.getFooterText();
                        break;
                    default:
                        textValue = null;
                        break;
                }

                if (textValue != null) {
                    addColumnValue(builder, textValue);
                }
                else if (ComponentUtils.shouldRenderFacet(facet)) {
                    addColumnValue(builder, facet);
                }
                else {
                    addColumnValue(builder, Constants.EMPTY_STRING);
                }

                firstCellWritten = true;
            }
        }

        builder.append(csvOptions.getEndOfLineSymbols());
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        StringBuilder builder = (StringBuilder) document;
        boolean firstCellWritten = false;

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    builder.append(csvOptions.getDelimiterChar());
                }

                try {
                    addColumnValue(builder, col.getChildren(), col);
                }
                catch (IOException ex) {
                    throw new FacesException(ex);
                }

                firstCellWritten = true;
            }
        }
    }

    protected void configureResponse(FacesContext context, String filename, String encodingType) {
        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseContentType("text/csv; charset=" + encodingType);
        setResponseHeader(externalContext, ComponentUtils.createContentDisposition("attachment", filename + ".csv"));
        addResponseCookie(context);
    }

    protected void addColumnValues(StringBuilder builder, List<UIColumn> columns) throws IOException {
        for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext(); ) {
            UIColumn col = iterator.next();
            addColumnValue(builder, col.getChildren(), col);

            if (iterator.hasNext()) {
                builder.append(csvOptions.getDelimiterChar());
            }
        }
    }

    protected void addColumnValue(StringBuilder builder, UIComponent component) throws IOException {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

        addColumnValue(builder, value);
    }

    protected void addColumnValue(StringBuilder builder, String value) throws IOException {
        value = (value == null) ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

        builder.append(csvOptions.getQuoteChar()).append(value).append(csvOptions.getQuoteChar());
    }

    protected void addColumnValue(StringBuilder builder, List<UIComponent> components, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        builder.append(csvOptions.getQuoteChar());

        if (column.getExportFunction() != null) {
            String value = exportColumnByFunction(context, column);
            //escape double quotes
            value = value == null ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

            builder.append(value);
        }
        else {
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);

                    //escape double quotes
                    value = value == null ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

                    builder.append(value);
                }
            }
        }

        builder.append(csvOptions.getQuoteChar());
    }

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((StringBuilder) document).append(csvOptions.getEndOfLineSymbols());
    }

    @Override
    protected void postExport(FacesContext context, ExportConfiguration config) throws IOException {
        super.postExport(context, config);
        sb = null;
    }
}
