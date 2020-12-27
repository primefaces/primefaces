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
package org.primefaces.component.datatable.feature;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableBase;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.util.LangUtils;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

public class SelectionFeature implements DataTableFeature {

    private static final String ALL_SELECTOR = "@all";

    @Override
    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        Object originalValue = table.getValue();
        Object filteredValue = table.getFilteredValue();
        boolean isFiltered = (filteredValue != null);

        String selection = params.get(clientId + "_selection");
        Set<String> rowKeys = LangUtils.newLinkedHashSet(selection.split(","));

        if (isFiltered) {
            table.setValue(null);
        }

        decodeSelection(context, table, rowKeys);

        if (isFiltered) {
            table.setValue(originalValue);
        }

        if (table.isMultiViewState()) {
            DataTableState ts = table.getMultiViewState(true);
            ts.setSelectedRowKeys(table.getSelectedRowKeys());
        }
    }

    public void decodeSelection(FacesContext context, DataTable table, Set<String> rowKeys) {
        table.setSelection(null);
        table.setSelectedRowKeys(null);

        if (table.isSingleSelectionMode()) {
            if (rowKeys.size() > 1) {
                throw new IllegalArgumentException("DataTable '" + table.getClientId(context)
                        + "' is configured for single selection while multiple rows have been selected");
            }

            if (!rowKeys.isEmpty()) {
                decodeSingleSelection(context, table, rowKeys.iterator().next());
            }
        }
        else {
            decodeMultipleSelection(context, table, rowKeys);
        }
    }

    public void decodeSelectionRowKeys(FacesContext context, DataTable table) {
        ValueExpression selectionByVE = table.getValueExpression(DataTableBase.PropertyKeys.selection.name());
        if (selectionByVE != null) {
            Object selection = selectionByVE.getValue(context.getELContext());

            if (selection != null) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                String var = table.getVar();
                Set<String> rowKeys = new HashSet<>(table.getSelectedRowKeys());

                // check if a row can be selectable
                Consumer<Object> selectableRow = o -> {
                    String rowKey = table.getRowKey(o);
                    if (LangUtils.isNotBlank(rowKey) && !table.isDisabledSelection()) {
                        rowKeys.add(rowKey);
                    }
                };

                if (table.isSingleSelectionMode()) {
                    selectableRow.accept(selection);
                }
                else {
                    Class<?> clazz = selection.getClass();
                    boolean isArray = clazz != null && clazz.isArray();

                    if (clazz != null && !isArray && !List.class.isAssignableFrom(clazz)) {
                        throw new FacesException("Multiple selection reference must be an Array or a List for datatable " + table.getClientId());
                    }

                    List<Object> selectionTmp = isArray ? Arrays.asList((Object[]) selection) : (List<Object>) selection;
                    for (int i = 0; i < selectionTmp.size(); i++) {
                        Object o = selectionTmp.get(i);
                        selectableRow.accept(o);
                    }
                }

                requestMap.remove(var);

                table.setSelectedRowKeys(rowKeys);
            }
        }
    }

    protected void decodeSingleSelection(FacesContext context, DataTable table, String rowKey) {
        if (LangUtils.isNotBlank(rowKey)) {
            Object o = table.getRowData(rowKey);
            if (o != null) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                String var = table.getVar();
                if (isSelectable(table, var, requestMap, o)) {
                    setSelection(context, table, false, Collections.singletonList(o));
                }
                table.setSelectedRowKeys(Collections.singleton(rowKey));
            }
        }
    }

    protected void decodeMultipleSelection(FacesContext context, DataTable table, Set<String> rowKeys) {
        if (rowKeys.isEmpty()) {
            setSelection(context, table, true, Collections.emptyList());
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            String var = table.getVar();

            List<Object> selection = new ArrayList<>();

            if (!rowKeys.isEmpty() && ALL_SELECTOR.equals(rowKeys.iterator().next())) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.setRowIndex(i);
                    String rowKey = table.getRowKey(table.getRowData());
                    if (rowKey != null) {
                        Object rowData = table.getRowData();
                        rowKeys.add(rowKey);
                        if (rowData != null && isSelectable(table, var, requestMap, rowData)) {
                            selection.add(table.getRowData());
                        }
                    }
                }
            }
            else {
                for (String rowKey : rowKeys) {
                    Object rowData = table.getRowData(rowKey);
                    if (rowData != null && isSelectable(table, var, requestMap, rowData)) {
                        selection.add(rowData);
                    }
                }
            }

            setSelection(context, table, true, selection);

            requestMap.remove(var);
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new FacesException("SelectFeature should not encode.");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return table.isSelectionEnabled();
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }

    protected boolean isSelectable(DataTable table, String var, Map<String, Object> requestMap, Object o) {
        requestMap.put(var, o);
        return !table.isDisabledSelection();
    }

    protected void setSelection(FacesContext context, DataTable table, boolean multiple, List<Object> o) {
        ValueExpression selectionVE = table.getValueExpression(DataTableBase.PropertyKeys.selection.toString());
        Class<?> clazz = selectionVE == null ? null : selectionVE.getType(context.getELContext());
        boolean isArray = clazz != null && clazz.isArray();

        if (multiple && clazz != null && !isArray && !List.class.isAssignableFrom(clazz)) {
            throw new FacesException("Multiple selection reference must be an Array or a List for datatable " + table.getClientId());
        }

        Object selection = null;
        if (o.isEmpty()) {
            if (multiple) {
                selection = isArray ? LangUtils.EMPTY_OBJECT_ARRAY : Collections.emptyList();
            }
        }
        else {
            if (multiple) {
                selection = isArray ? Array.newInstance(clazz.getComponentType(), o.size()) : o;
            }
            else {
                selection = o.get(0);
            }
        }

        if (selectionVE != null) {
            selectionVE.setValue(context.getELContext(), selection);
        }
        else {
            table.setSelection(selection);
        }
    }
}