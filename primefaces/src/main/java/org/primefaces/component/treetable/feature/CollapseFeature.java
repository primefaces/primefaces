/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Map;

import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.model.TreeNode;

public class CollapseFeature implements TreeTableFeature {

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable tt) throws IOException {
        TreeNode root = tt.getValue();
        String clientId = tt.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String nodeKey = params.get(clientId + "_collapse");
        tt.setRowKey(root, nodeKey);
        TreeNode node = tt.getRowNode();
        node.setExpanded(false);
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_collapse");
    }
}
