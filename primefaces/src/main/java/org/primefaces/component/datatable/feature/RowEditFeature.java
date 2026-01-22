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

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.visit.ResetInputVisitCallback;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;

public class RowEditFeature implements DataTableFeature {

    @Override
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        if (table.isSelectionEnabled()) {
            DataTableFeatures.selectionFeature().decodeSelectionRowKeys(context, table);
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        int editedRowId = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
        table.setRowIndex(editedRowId);

        if (table.isRowEditRequest(context)) {
            String action = params.get(clientId + "_rowEditAction");

            if ("cancel".equals(action)) {
                VisitContext visitContext = null;

                for (UIColumn column : table.getColumns()) {
                    for (UIComponent grandkid : column.getChildren()) {
                        if (grandkid instanceof CellEditor) {
                            UIComponent inputFacet = grandkid.getFacet("input");

                            if (inputFacet instanceof EditableValueHolder) {
                                ((EditableValueHolder) inputFacet).resetValue();
                            }
                            else {
                                if (visitContext == null) {
                                    visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                                }
                                inputFacet.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                            }
                        }
                    }
                }
            }
        }

        if (table.isRowAvailable()) {
            renderer.encodeRow(context, table, editedRowId);
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return table.isRowEditRequest(context) || table.isRowEditInitRequest(context);
    }
}
