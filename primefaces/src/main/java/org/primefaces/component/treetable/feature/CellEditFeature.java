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
package org.primefaces.component.treetable.feature;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.model.TreeNode;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

public class CellEditFeature implements TreeTableFeature {

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable tt) throws IOException {
        TreeNode root = tt.getValue();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String[] cellInfo = params.get(clientId + "_cellInfo").split(",");
        String rowKey = cellInfo[0];
        int cellIndex = Integer.parseInt(cellInfo[1]);
        int i = -1;
        UIColumn column = null;
        for (UIColumn col : tt.getColumns()) {
            if (col.isRendered()) {
                i++;

                if (i == cellIndex) {
                    column = col;
                    break;
                }
            }
        }

        if (column == null) {
            throw new FacesException("No column found for cellIndex: " + cellIndex);
        }

        tt.setRowKey(root, rowKey);

        if (column.isDynamic()) {
            DynamicColumn dynamicColumn = (DynamicColumn) column;
            dynamicColumn.applyStatelessModel();
        }

        if (isCellEditCancelRequest(context, clientId) || isCellEditInitRequest(context, clientId)) {
            column.getCellEditor().getFacet("input").encodeAll(context);
        }
        else {
            column.getCellEditor().getFacet("output").encodeAll(context);
        }

        if (column.isDynamic()) {
            ((DynamicColumn) column).cleanStatelessModel();
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_cellInfo");
    }

    private boolean isCellEditCancelRequest(FacesContext context, String clientId) {
        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_cellEditCancel");
    }

    private boolean isCellEditInitRequest(FacesContext context, String clientId) {
        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_cellEditInit");
    }
}
