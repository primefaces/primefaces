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

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.model.TreeNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.visit.ResetInputVisitCallback;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;

public class RowEditFeature implements TreeTableFeature {

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable tt) throws IOException {
        TreeNode root = tt.getValue();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String editedRowKey = params.get(clientId + "_rowEditIndex");
        String action = params.get(clientId + "_rowEditAction");

        tt.setRowKey(root, editedRowKey);
        TreeNode node = tt.getRowNode();

        if ("cancel".equals(action)) {
            VisitContext visitContext = null;

            for (UIColumn column : tt.getColumns()) {
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

        renderer.encodeNode(context, tt, root, node);
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_rowEditAction");
    }
}
