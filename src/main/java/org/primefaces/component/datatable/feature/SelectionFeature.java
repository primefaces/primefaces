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
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.datatable.TableState;
import org.primefaces.util.LangUtils;

public class SelectionFeature implements DataTableFeature {

    private static final String ALL_SELECTOR = "@all";

    @Override
    public void decode(FacesContext context, DataTable table) {
        String clientId = table.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String selection = params.get(clientId + "_selection");
        Object originalValue = table.getValue();
        Object filteredValue = table.getFilteredValue();
        boolean isFiltered = (filteredValue != null);

        if (isFiltered) {
            table.setValue(null);
        }

        if (table.isSingleSelectionMode()) {
            decodeSingleSelection(table, selection);
        }
        else {
            decodeMultipleSelection(context, table, selection);
        }

        if (isFiltered) {
            table.setValue(originalValue);
        }

        if (table.isMultiViewState()) {
            TableState ts = table.getTableState(true);
            table.findSelectedRowKeys();
            ts.setRowKeys(table.getSelectedRowKeys());
        }
    }

    void decodeSingleSelection(DataTable table, String selection) {
        if (LangUtils.isValueBlank(selection)) {
            table.setSelection(null);
        }
        else {
            table.setSelection(table.getRowData(selection));
        }
    }

    void decodeMultipleSelection(FacesContext context, DataTable table, String selection) {
        ValueExpression selectionByVE = table.getValueExpression(DataTable.PropertyKeys.selection.toString());
        Class<?> clazz = selectionByVE == null ? null : selectionByVE.getType(context.getELContext());
        boolean isArray = clazz == null ? false : clazz.isArray();

        if (clazz != null && !isArray && !List.class.isAssignableFrom(clazz)) {
            throw new FacesException("Multiple selection reference must be an Array or a List for datatable " + table.getClientId());
        }

        if (LangUtils.isValueBlank(selection)) {
            if (isArray) {
                table.setSelection(Array.newInstance(clazz.getComponentType(), 0));
            }
            else {
                table.setSelection(new ArrayList<>());
            }
        }
        else {
            List selectionList = new ArrayList();

            if (selection.equals(ALL_SELECTOR)) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.setRowIndex(i);
                    selectionList.add(table.getRowData());
                }
            }
            else {
                String[] rowKeys = selection.split(",");
                for (int i = 0; i < rowKeys.length; i++) {
                    Object rowData = table.getRowData(rowKeys[i]);

                    if (rowData != null) {
                        selectionList.add(rowData);
                    }
                }
            }

            if (isArray) {
                Object selectionArray = Array.newInstance(clazz.getComponentType(), selectionList.size());
                table.setSelection(selectionList.toArray((Object[]) selectionArray));
            }
            else {
                table.setSelection(selectionList);
            }
        }
    }

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        throw new RuntimeException("SelectFeature should not encode.");
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return table.isSelectionEnabled();
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return false;
    }

}
