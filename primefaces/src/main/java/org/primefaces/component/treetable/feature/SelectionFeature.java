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

import org.primefaces.PrimeFaces;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.model.TreeNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

public class SelectionFeature implements TreeTableFeature {

    private static final String SB_DECODE_SELECTION = TreeTableRenderer.class.getName() + "#decodeSelection";

    @Override
    public void decode(FacesContext context, TreeTable table) {
        boolean multiple = table.isMultipleSelectionMode();
        Class<?> selectionType = table.getSelectionType();
        boolean isArray = selectionType != null && selectionType.isArray();
        TreeNode root = table.getValue();

        if (multiple && selectionType != null && !isArray && !List.class.isAssignableFrom(selectionType)) {
            throw new FacesException("Multiple selection reference must be an Array or a List for TreeTable " + table.getClientId());
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);

        //decode selection
        String selectionValue = params.get(table.getClientId(context) + "_selection");
        if (LangUtils.isBlank(selectionValue)) {
            if (multiple) {
                table.setSelection(isArray ? new TreeNode[0] : Collections.emptyList());
            }
            else {
                table.setSelection(null);
            }
        }
        else {
            String[] selectedRowKeys = selectionValue.split(",");
            if (multiple) {
                List<TreeNode> selectedNodes = new ArrayList<>();

                for (int i = 0; i < selectedRowKeys.length; i++) {
                    table.setRowKey(root, selectedRowKeys[i]);
                    TreeNode rowNode = table.getRowNode();
                    if (rowNode != null) {
                        selectedNodes.add(rowNode);
                    }
                }

                table.setSelection(isArray ? selectedNodes.toArray(new TreeNode[selectedNodes.size()]) : selectedNodes);
            }
            else {
                table.setRowKey(root, selectedRowKeys[0]);
                table.setSelection(table.getRowNode());
            }

            table.setRowKey(root, null); //cleanup
        }

        if (table.isCheckboxSelectionMode() && isSelectionRequest(context, clientId)) {
            String selectedNodeRowKey = params.get(clientId + "_instantSelection");
            table.setRowKey(root, selectedNodeRowKey);
            TreeNode selectedNode = table.getRowNode();
            List<String> descendantRowKeys = new ArrayList<>();
            table.populateRowKeys(selectedNode, descendantRowKeys);

            StringBuilder builder = SharedStringBuilder.get(context, SB_DECODE_SELECTION);
            for (int i = 0; i < descendantRowKeys.size(); i++) {
                if (i > 0) {
                    builder.append(",");
                }
                builder.append(descendantRowKeys.get(i));
            }

            if (ComponentUtils.isRequestSource(table, context)) {
                PrimeFaces.current().ajax().addCallbackParam("descendantRowKeys", builder.toString());
            }
        }
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return table.isSelectionEnabled();
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return false;
    }

    private boolean isSelectionRequest(FacesContext context, String clientId) {
        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_instantSelection");
    }
}
