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

import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.util.ComponentUtils;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.*;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class DataTableExporter implements Exporter<DataTable> {

    protected enum ColumnType {
        HEADER("header"),
        FOOTER("footer");

        private final String facet;

        ColumnType(String facet) {
            this.facet = facet;
        }

        public String facet() {
            return facet;
        }

        @Override
        public String toString() {
            return facet;
        }
    }

    protected List<UIColumn> getColumnsToExport(UIData table) {
        return table.getChildren().stream()
                .filter(UIColumn.class::isInstance)
                .map(UIColumn.class::cast)
                .collect(Collectors.toList());
    }

    protected boolean hasColumnFooter(List<UIColumn> columns) {
        return columns.stream().anyMatch(c -> c.getFooter() != null);
    }

    protected String exportColumnByFunction(FacesContext context, org.primefaces.component.api.UIColumn column) {
        MethodExpression exportFunction = column.getExportFunction();

        if (exportFunction != null) {
            return (String) exportFunction.invoke(context.getELContext(), new Object[]{column});
        }

        return "";
    }

    public String exportValue(FacesContext context, UIComponent component) {

        if (component instanceof HtmlCommandLink) {  //support for PrimeFaces and standard HtmlCommandLink
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) {
                return String.valueOf(value);
            }
            else {
                //export first value holder
                for (UIComponent child : link.getChildren()) {
                    if (child instanceof ValueHolder) {
                        return exportValue(context, child);
                    }
                }

                return "";
            }
        }
        else if (component instanceof ValueHolder) {
            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null) {
                return "";
            }

            Converter converter = valueHolder.getConverter();
            if (converter == null) {
                Class valueType = value.getClass();
                converter = context.getApplication().createConverter(valueType);
            }

            if (converter != null) {
                if (component instanceof UISelectMany) {
                    StringBuilder builder = new StringBuilder();
                    List collection = null;

                    if (value instanceof List) {
                        collection = (List) value;
                    }
                    else if (value.getClass().isArray()) {
                        collection = Arrays.asList(value);
                    }
                    else {
                        throw new FacesException("Value of " + component.getClientId(context) + " must be a List or an Array.");
                    }

                    int collectionSize = collection.size();
                    for (int i = 0; i < collectionSize; i++) {
                        Object object = collection.get(i);
                        builder.append(converter.getAsString(context, component, object));

                        if (i < (collectionSize - 1)) {
                            builder.append(",");
                        }
                    }

                    String valuesAsString = builder.toString();
                    builder.setLength(0);

                    return valuesAsString;
                }
                else {
                    return converter.getAsString(context, component, value);
                }
            }
            else {
                return value.toString();
            }
        }
        else if (component instanceof CellEditor) {
            return exportValue(context, component.getFacet("output"));
        }
        else if (component instanceof HtmlGraphicImage) {
            return (String) component.getAttributes().get("alt");
        }
        else if (component instanceof OverlayPanel) {
            return "";
        }
        else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null) {
                return value.trim();
            }
            else {
                return "";
            }
        }
    }

    protected void exportPageOnly(FacesContext context, DataTable table, Object document) {
        int first = table.getFirst();
        int rows = table.getRows();
        if (rows == 0) {
            rows = table.getRowCount();
        }

        int rowsToExport = first + rows;

        for (int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {
            exportRow(table, document, rowIndex);
        }
    }

    protected void exportAll(FacesContext context, DataTable table, Object document) {
        int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();

        if (lazy) {
            if (rowCount > 0) {
                table.setFirst(0);
                table.setRows(rowCount);
                table.clearLazyCache();
                table.loadLazyData();
            }

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                exportRow(table, document, rowIndex);
            }

            //restore
            table.setFirst(first);
            table.setRows(rows);
            table.setRowIndex(-1);
            table.clearLazyCache();
            table.loadLazyData();
        }
        else {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                exportRow(table, document, rowIndex);
            }

            //restore
            table.setFirst(first);
        }
    }

    protected void exportRow(DataTable table, Object document, int rowIndex) {
        table.setRowIndex(rowIndex);
        if (!table.isRowAvailable()) {
            return;
        }

        preRowExport(table, document);
        exportCells(table, document);
        postRowExport(table, document);
    }

    protected void exportRow(DataTable table, Object document) {
        preRowExport(table, document);
        exportCells(table, document);
        postRowExport(table, document);
    }

    protected void exportSelectionOnly(FacesContext context, DataTable table, Object document) {
        Object selection = table.getSelection();
        String var = table.getVar();

        if (selection != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (selection.getClass().isArray()) {
                int size = Array.getLength(selection);

                for (int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    exportRow(table, document);
                }
            }
            else if (Collection.class.isAssignableFrom(selection.getClass())) {
                for (Object obj : (Collection) selection) {
                    requestMap.put(var, obj);
                    exportRow(table, document);
                }
            }
            else {
                requestMap.put(var, selection);
                exportCells(table, document);
            }
        }
    }

    protected void preExport(FacesContext context, ExportConfiguration config) throws IOException {
        // NOOP
    }

    protected void postExport(FacesContext context, ExportConfiguration config) throws IOException {
        // NOOP
    }

    protected void preRowExport(DataTable table, Object document) {
        // NOOP
    }

    protected void postRowExport(DataTable table, Object document) {
        // NOOP
    }

    protected abstract void exportCells(DataTable table, Object document);

    @Override
    public void export(FacesContext context, List<DataTable> tables, ExportConfiguration config) throws IOException {
        preExport(context,  config);

        for (DataTable table : tables) {
            ComponentUtils.invokeOnClosestIteratorParent(table, p -> {
                VisitContext visitContext = VisitContext.createVisitContext(context);
                VisitCallback visitCallback = new DataTableVisitCallBack(table, config);
                p.visitTree(visitContext, visitCallback);
            }, true);
        }

        postExport(context, config);
    }

    protected abstract void doExport(FacesContext facesContext, DataTable table, ExportConfiguration config) throws IOException;

    private class DataTableVisitCallBack implements VisitCallback {

        private ExportConfiguration config;

        private DataTable target;

        public DataTableVisitCallBack(DataTable target, ExportConfiguration config) {
            this.target = target;
            this.config = config;
        }

        @Override
        public VisitResult visit(VisitContext context, UIComponent component) {
            if (target == component) {
                try {
                    doExport(context.getFacesContext(), target, config);
                }
                catch (IOException e) {
                    throw new FacesException(e);
                }
            }
            return VisitResult.ACCEPT;
        }
    }
}
