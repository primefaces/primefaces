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
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.CSVOptions;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public class DataTableCSVExporter extends DataTableExporter {

    private CSVOptions csvOptions;

    @Override
    protected void preExport(FacesContext context, ExportConfiguration exportConfiguration) throws IOException {
        csvOptions = CSVOptions.EXCEL;
        ExporterOptions options = exportConfiguration.getOptions();
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
    public void doExport(FacesContext context, DataTable table, ExportConfiguration exportConfiguration, int index) throws IOException {
        try (OutputStreamWriter osw = new OutputStreamWriter(getOutputStream(), exportConfiguration.getEncodingType());
            PrintWriter writer = new PrintWriter(osw);) {

            if (exportConfiguration.getPreProcessor() != null) {
                // PF 9 - attention: breaking change to PreProcessor (PrintWriter instead of writer)
                exportConfiguration.getPreProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            addColumnFacets(writer, table, ColumnType.HEADER);

            if (exportConfiguration.isPageOnly()) {
                exportPageOnly(context, table, writer);
            }
            else if (exportConfiguration.isSelectionOnly()) {
                exportSelectionOnly(context, table, writer);
            }
            else {
                exportAll(context, table, writer);
            }

            if (table.hasFooterColumn()) {
                addColumnFacets(writer, table, ColumnType.FOOTER);
            }

            if (exportConfiguration.getPostProcessor() != null) {
                // PF 9 - attention: breaking change to PostProcessor (PrintWriter instead of writer)
                exportConfiguration.getPostProcessor().invoke(context.getELContext(), new Object[]{writer});
            }

            writer.flush();
        }
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public String getFileExtension() {
        return ".csv";
    }

    protected void addColumnFacets(PrintWriter writer, DataTable table, ColumnType columnType) throws IOException {
        boolean firstCellWritten = false;

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    writer.append(csvOptions.getDelimiterChar());
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
                    addColumnValue(writer, textValue);
                }
                else if (ComponentUtils.shouldRenderFacet(facet)) {
                    addColumnValue(writer, facet);
                }
                else {
                    addColumnValue(writer, Constants.EMPTY_STRING);
                }

                firstCellWritten = true;
            }
        }

        writer.append(csvOptions.getEndOfLineSymbols());
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        PrintWriter writer = (PrintWriter) document;
        boolean firstCellWritten = false;

        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                if (firstCellWritten) {
                    writer.append(csvOptions.getDelimiterChar());
                }

                try {
                    addColumnValue(writer, col.getChildren(), col);
                }
                catch (IOException ex) {
                    throw new FacesException(ex);
                }

                firstCellWritten = true;
            }
        }
    }

    protected void addColumnValues(PrintWriter writer, List<UIColumn> columns) throws IOException {
        for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext(); ) {
            UIColumn col = iterator.next();
            addColumnValue(writer, col.getChildren(), col);

            if (iterator.hasNext()) {
                writer.append(csvOptions.getDelimiterChar());
            }
        }
    }

    protected void addColumnValue(PrintWriter writer, UIComponent component) throws IOException {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

        addColumnValue(writer, value);
    }

    protected void addColumnValue(PrintWriter writer, String value) throws IOException {
        value = (value == null) ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

        writer.append(csvOptions.getQuoteChar()).append(value).append(csvOptions.getQuoteChar());
    }

    protected void addColumnValue(PrintWriter writer, List<UIComponent> components, UIColumn column) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        writer.append(csvOptions.getQuoteChar());

        if (LangUtils.isNotBlank(column.getExportValue())) {
            String value = column.getExportValue();
            //escape double quotes
            value = value == null ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

            writer.append(value);
        }
        else if (column.getExportFunction() != null) {
            String value = exportColumnByFunction(context, column);
            //escape double quotes
            value = value == null ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

            writer.append(value);
        }
        else {
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);

                    //escape double quotes
                    value = value == null ? "" : value.replace(csvOptions.getQuoteString(), csvOptions.getDoubleQuoteString());

                    writer.append(value);
                }
            }
        }

        writer.append(csvOptions.getQuoteChar());
    }

    @Override
    protected void postRowExport(DataTable table, Object document) {
        ((PrintWriter) document).append(csvOptions.getEndOfLineSymbols());
    }
}
