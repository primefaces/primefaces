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
package org.primefaces.component.datatable.feature;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.util.LangUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

public class SelectionFeature implements DataTableFeature {

    private static final String ALL_SELECTOR = "@all";

    @Override
    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        Object originalValue = table.getValue();
        boolean allEligibleToSelection = table.isFilteringCurrentlyActive() && !table.isSelectAllFilteredOnly();

        String selection = params.get(clientId + "_selection");
        Set<String> rowKeys = Collections.emptySet();

        if (LangUtils.isNotBlank(selection)) {
            rowKeys = LangUtils.newLinkedHashSet(selection.split(","));
            table.setSelectAll(ALL_SELECTOR.equals(selection));
        }
        else {
            table.setSelectAll(false);
        }

        if (allEligibleToSelection) {
            table.setValue(null);
        }

        decodeSelection(context, table, rowKeys);

        if (allEligibleToSelection) {
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
            decodeSingleSelection(context, table, rowKeys);
        }
        else {
            decodeMultipleSelection(context, table, rowKeys);
        }
    }

    public void decodeSelectionRowKeys(FacesContext context, DataTable table) {
        Set<String> rowKeys = null;
        ValueExpression selectionVE = table.getValueExpression(DataTable.PropertyKeys.selection.name());
        if (selectionVE != null) {
            Object selection = selectionVE.getValue(context.getELContext());

            if (selection != null) {
                rowKeys = new HashSet<>();

                if (table.isSingleSelectionMode()) {
                    rowKeys.add(table.getRowKey(selection));
                }
                else {
                    Class<?> clazz = selection.getClass();
                    boolean isArray = clazz.isArray();

                    if (!isArray && !List.class.isAssignableFrom(clazz)) {
                        throw new FacesException("Multiple selection reference must be an Array or a List for datatable " + table.getClientId());
                    }

                    List<Object> selectionTmp = isArray ? Arrays.asList((Object[]) selection) : (List<Object>) selection;
                    for (int i = 0; i < selectionTmp.size(); i++) {
                        Object o = selectionTmp.get(i);
                        rowKeys.add(table.getRowKey(o));
                    }
                }
            }
        }
        table.setSelectedRowKeys(rowKeys);
    }

    protected void decodeSingleSelection(FacesContext context, DataTable table, Set<String> rowKeys) {
        if (rowKeys.size() > 1) {
            throw new IllegalArgumentException("DataTable '" + table.getClientId(context)
                    + "' is configured for single selection while multiple rows have been selected");
        }

        if (rowKeys.isEmpty()) {
            setSelection(context, table, false, new ArrayList<>(), new HashSet<>());
        }
        else {
            String rowKey = rowKeys.iterator().next();
            Object o = table.getRowData(rowKey);
            if (o != null) {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                String var = table.getVar();

                List<Object> selectionTmp = Collections.emptyList();
                Set<String> rowKeysTmp = Collections.emptySet();
                if (isSelectable(table, var, requestMap, o)) {
                    selectionTmp = new ArrayList<>(1);
                    selectionTmp.add(o);
                    rowKeysTmp = new HashSet<>(1);
                    rowKeysTmp.add(rowKey);
                }

                setSelection(context, table, false, selectionTmp, rowKeysTmp);
            }
            else {
                setSelection(context, table, false, new ArrayList<>(), new HashSet<>());
            }
        }
    }

    protected void decodeMultipleSelection(FacesContext context, DataTable table, Set<String> rowKeys) {
        if (rowKeys.isEmpty()) {
            setSelection(context, table, true, new ArrayList<>(), new HashSet<>());
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            String var = table.getVar();

            List<Object> selection = new ArrayList<>();

            Set<String> rowKeysTmp = new HashSet<>();
            if (!rowKeys.isEmpty() && ALL_SELECTOR.equals(rowKeys.iterator().next())) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.setRowIndex(i);
                    Object rowData = table.getRowData();
                    String rowKey = table.getRowKey(rowData);
                    if (rowKey != null) {
                        rowKeysTmp.add(rowKey);
                        if (rowData != null && isSelectable(table, var, requestMap, rowData)) {
                            selection.add(rowData);
                        }
                    }
                }
            }
            else {
                for (String rowKey : rowKeys) {
                    Object rowData = table.getRowData(rowKey);
                    if (rowData != null) {
                        rowKeysTmp.add(rowKey);
                        if (isSelectable(table, var, requestMap, rowData)) {
                            selection.add(rowData);
                        }
                    }
                }
            }

            setSelection(context, table, true, selection, rowKeysTmp);
        }
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
        boolean containsVar = requestMap.containsKey(var);
        if (!containsVar) {
            requestMap.put(var, o);
        }

        boolean selectable = table.isSelectionEnabled() && !table.isSelectionDisabled();

        if (!containsVar) {
            requestMap.remove(var);
        }

        return selectable;
    }

    protected void setSelection(FacesContext context, DataTable table, boolean multiple, List<Object> selected, Set<String> rowKeys) {
        ValueExpression selectionVE = table.getValueExpression(DataTable.PropertyKeys.selection.toString());
        Class<?> clazz = selectionVE == null ? null : selectionVE.getType(context.getELContext());
        boolean isArray = clazz != null && clazz.isArray();

        if (multiple && clazz != null && !isArray && !List.class.isAssignableFrom(clazz)) {
            throw new FacesException("Multiple selection reference must be an Array or a List for DataTable " + table.getClientId());
        }

        Object selection;
        if (selected.isEmpty()) {
            if (multiple) {
                selection = isArray
                        ? Array.newInstance(clazz.getComponentType(), 0)
                        : new ArrayList<>();
            }
            else {
                selection = null;
            }
        }
        else {
            if (multiple) {
                if (isArray) {
                    Object arr = Array.newInstance(clazz.getComponentType(), selected.size());
                    selection = selected.toArray((Object[]) arr);
                }
                else {
                    selection = selected;
                }
            }
            else {
                selection = selected.get(0);
            }
        }

        table.setSelectedRowKeys(rowKeys);

        if (selectionVE != null) {
            selectionVE.setValue(context.getELContext(), selection);
        }
        else {
            table.setSelection(selection);
        }
    }
}
