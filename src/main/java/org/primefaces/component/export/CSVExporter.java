/**
 * Copyright 2009-2018 PrimeTek.
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

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class CSVExporter extends Exporter {

    private CSVOptions csvOptions;

    public CSVExporter(ExporterOptions options) {
        csvOptions = CSVOptions.EXCEL;
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
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {

        ExternalContext externalContext = context.getExternalContext();
        configureResponse(externalContext, filename, encodingType);
        StringBuilder builder = new StringBuilder();

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{builder});
        }

        addColumnFacets(builder, table, ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, builder);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, builder);
        }
        else {
            exportAll(context, table, builder);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(builder, table, ColumnType.FOOTER);
        }

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{builder});
        }

        Writer writer = externalContext.getResponseOutputWriter();
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    @Override
    public void export(FacesContext facesContext, List<String> clientIds, String outputFileName, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void export(FacesContext facesContext, String outputFileName, List<DataTable> tables, boolean pageOnly, boolean selectionOnly,
                       String encodingType, MethodExpression preProcessor, MethodExpression postProcessor, ExporterOptions options,
                       MethodExpression onTableRender) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
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
                if (facet != null) {
                    addColumnValue(builder, facet);
                }
                else {
                    String textValue;
                    switch (columnType) {
                        case HEADER:
                            textValue = col.getHeaderText();
                            break;

                        case FOOTER:
                            textValue = col.getFooterText();
                            break;

                        default:
                            textValue = "";
                            break;
                    }

                    addColumnValue(builder, textValue);

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

    protected void configureResponse(ExternalContext externalContext, String filename, String encodingType) {
        externalContext.setResponseContentType("text/csv; charset=" + encodingType);
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", filename + ".csv"));
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
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
}
