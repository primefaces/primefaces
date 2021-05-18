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
package org.primefaces.component.export;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.model.ColumnMeta;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public abstract class TableExporter<T extends UIComponent & UITable> extends Exporter<T> {

    // Because more than 1 table can be exported we cache each one for performance
    private Map<UITable, List<UIColumn>> exportableColumns = new HashMap<>();

    protected void exportColumn(FacesContext context, T table, UIColumn column, List<UIComponent> components,
            boolean joinComponents, Consumer<String> callback) {

        if (LangUtils.isNotBlank(column.getExportValue())) {
            callback.accept(column.getExportValue());
        }
        else if (column.getExportFunction() != null) {
            MethodExpression exportFunction = column.getExportFunction();
            callback.accept((String) exportFunction.invoke(context.getELContext(), new Object[]{column}));
        }
        else if (LangUtils.isNotBlank(column.getField())) {
            String value = table.getConvertedFieldValue(context, column);
            callback.accept(Objects.toString(value, Constants.EMPTY_STRING));
        }
        else {
            StringBuilder sb = null;
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);
                    if (joinComponents) {
                        if (value != null) {
                            if (sb == null) {
                                sb = new StringBuilder();
                            }
                            sb.append(value);
                        }
                    }
                    else {
                        callback.accept(value);
                    }
                }
            }
            if (joinComponents) {
                callback.accept(sb == null ? null : sb.toString());
            }
        }
    }

    /**
     * Gets and caches the list of UIColumns that are exportable="true" and rendered="true".
     * Orders them by displayPriority so they match the UI display of the columns.
     *
     * @param table the Table with columns to export
     * @return the List<UIColumn> that are exportable
     */
    protected List<UIColumn> getExportableColumns(UITable table) {
        if (exportableColumns.containsKey(table)) {
            return exportableColumns.get(table);
        }

        List<UIColumn> allColumns = table.getColumns();
        List<UIColumn> exportcolumns = new ArrayList<>(allColumns.size());
        Map<String, ColumnMeta> columnMetadata = table.getColumnMeta();

        if (columnMetadata == null || columnMetadata.isEmpty()) {
            for (UIColumn col : allColumns) {
                if (col instanceof DynamicColumn) {
                    ((DynamicColumn) col).applyStatelessModel();
                }
                if (col.isRendered() && col.isExportable()) {
                    exportcolumns.add(col);
                }
            }
        }
        else {
            // sort by display priority
            Comparator<Integer> sortIntegersNaturallyWithNullsLast = Comparator.nullsLast(Comparator.naturalOrder());
            List<ColumnMeta> columnMetas = columnMetadata.values().stream().
                        sorted(Comparator.comparing(ColumnMeta::getDisplayPriority, sortIntegersNaturallyWithNullsLast))
                        .collect(Collectors.toList());

            META: for (ColumnMeta meta : columnMetas) {
                String columnKey = meta.getColumnKey();
                for (UIColumn col : allColumns) {
                    if (col instanceof DynamicColumn) {
                        ((DynamicColumn) col).applyStatelessModel();
                    }
                    if (col.getColumnKey().equals(columnKey)) {
                        if (col.isRendered() && col.isExportable()) {
                            exportcolumns.add(col);
                        }
                        continue META;
                    }
                }
            }
        }

        exportableColumns.put(table, exportcolumns);

        return exportcolumns;
    }
}
