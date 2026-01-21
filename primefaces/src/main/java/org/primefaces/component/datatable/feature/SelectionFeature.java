/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
import org.primefaces.component.datatable.DataTableState;
import org.primefaces.util.LangUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

        // Store rowKeys for lazy tables to apply after data loading
        // For non-lazy tables or lazy tables with data loaded, apply selection immediately
        if (!table.isLazy() || table.isLazyDataLoaded()) {
            decodeSelection(context, table, rowKeys);
        }
        else {
            // Lazy table without data - store rowKeys for later application
            table.setSelectedRowKeys(rowKeys);
        }

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
        table.setSelectedRowKeys(rowKeys);

        if (table.isSingleSelectionMode()) {
            decodeSingleSelection(context, table, rowKeys);
        }
        else {
            decodeMultipleSelection(context, table, rowKeys);
        }
    }

    public void decodeSelectionRowKeys(FacesContext context, DataTable table) {
        Set<String> rowKeys = table.getSelectedRowKeys();

        // If rowKeys are already set from decode phase (lazy table scenario), resolve them to objects
        if (table.isLazy() && rowKeys != null && !rowKeys.isEmpty()) {
            decodeSelection(context, table, rowKeys);
            return;
        }

        // Otherwise, derive rowKeys from selection value expression (initial render)
        rowKeys = null;
        Map<String, Object> rowKeyToObjectMap = getSelectionMapFromValueExpression(context, table);

        if (rowKeyToObjectMap != null && !rowKeyToObjectMap.isEmpty()) {
            rowKeys = new HashSet<>(rowKeyToObjectMap.keySet());
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
        ValueExpression selectionVE = table.getValueExpression(DataTableBase.PropertyKeys.selection.toString());
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

    /**
     * Reads the selection value expression and converts it to a Map of rowKey to selected object.
     * Handles both single selection (returns map with one entry) and multiple selection (array or List).
     *
     * @param context the FacesContext
     * @param table the DataTable
     * @return a Map with rowKey as key and selected object as value, or empty map if selection value expression is null or selection is null
     * @throws FacesException if multiple selection is not an Array or List
     */
    private static Map<String, Object> getSelectionMapFromValueExpression(FacesContext context, DataTable table) {
        ValueExpression selectionVE = table.getValueExpression(DataTableBase.PropertyKeys.selection.name());
        if (selectionVE == null) {
            return Collections.emptyMap();
        }

        Object selection = selectionVE.getValue(context.getELContext());
        if (selection == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> rowKeyToObjectMap = new HashMap<>();

        if (table.isSingleSelectionMode()) {
            String rowKey = table.getRowKey(selection);
            rowKeyToObjectMap.put(rowKey, selection);
        }
        else {
            Class<?> clazz = selection.getClass();
            boolean isArray = clazz.isArray();

            if (!isArray && !List.class.isAssignableFrom(clazz)) {
                throw new FacesException("Multiple selection reference must be an Array or a List for datatable "
                        + table.getClientId());
            }

            List<Object> selectionList = isArray ? Arrays.asList((Object[]) selection) : (List<Object>) selection;
            for (Object selectedItem : selectionList) {
                String rowKey = table.getRowKey(selectedItem);
                rowKeyToObjectMap.put(rowKey, selectedItem);
            }
        }

        return rowKeyToObjectMap;
    }
}
