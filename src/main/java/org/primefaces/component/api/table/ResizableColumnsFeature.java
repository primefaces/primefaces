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
package org.primefaces.component.api.table;

import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UITable;

import org.primefaces.util.LangUtils;

public class ResizableColumnsFeature extends UITableFeature {

    public static final ResizableColumnsFeature INSTANCE = new ResizableColumnsFeature();

    @Override
    public boolean shouldDecode(FacesContext context, UITable table) {
        return isRequestParameterAvailable(context, table.getClientId(context) + "_resizableColumnState");
    }

    @Override
    public void decode(FacesContext context, UITable table) {
        String columnResizeStateParam = getRequestParameter(context, table.getClientId(context) + "_resizableColumnState");

        Map<String, ColumnDisplayState> displayStates = table.getColumnDisplayState();
        displayStates.values().stream().forEach(s -> s.setWidth(null));

        String tableWidth = null;

        if (!LangUtils.isValueBlank(columnResizeStateParam)) {
            String[] columnStates = columnResizeStateParam.split(",");
            for (String columnState : columnStates) {
                if (LangUtils.isValueBlank(columnState)) {
                    continue;
                }

                if (columnState.equals(table.getClientId(context) + "_tableWidthState")) {
                    tableWidth = columnState;
                    table.setWidth(tableWidth);
                }
                else {
                    int seperatorIndex = columnState.lastIndexOf('_');
                    String columnKey = columnState.substring(0, seperatorIndex);
                    String width = columnState.substring(seperatorIndex + 1);

                    ColumnDisplayState state = displayStates.computeIfAbsent(columnKey, k -> new ColumnDisplayState(k));
                    state.setWidth(width);
                }
            }
        }

        if (table.isMultiViewState()) {
            UITableState state = (UITableState) table.getMultiViewState(true);
            state.setWidth(tableWidth);
            state.setColumnDiplayState(displayStates);
        }
    }



}
