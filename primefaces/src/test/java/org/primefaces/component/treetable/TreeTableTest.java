/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.treetable;

import org.primefaces.component.api.UITree;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.PrimeRendererWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTableTest {

    /**
     * The row key the tree table stands on ends up in the client id of every descendant and puts the node in the
     * request map under the tree table's var, and the same holds for the column a p:columns stands on. The expand,
     * collapse, cell edit, row edit and page ajax requests never reach encodeTbody, which is the only place which
     * used to reset the row key, so the reset has to happen after encodeEnd, for every request which encoded the
     * tree table.
     */
    @Test
    void cleanupIterationStateLeavesTheTreeTableStandingOnNoRowAndNoColumn() {
        FacesContext context = new FacesContextMock();

        TreeNode<String> root = new DefaultTreeNode<>();
        new DefaultTreeNode<>("one", root);
        new DefaultTreeNode<>("two", root);
        root.setRowKey(UITree.ROOT_ROW_KEY);

        Columns columns = new Columns();
        columns.setId("cols");
        columns.setVar("col");
        columns.setValue(Arrays.asList("id", "name"));

        TreeTable treeTable = new TreeTable();
        treeTable.setId("treeTable");
        treeTable.setVar("node");
        treeTable.setValue(root);
        treeTable.getChildren().add(columns);
        treeTable.buildRowKeys(root);

        treeTable.setRowKey(root, "1");
        columns.setRowIndex(1);

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        assertTrue(requestMap.containsKey("node"), "the tree table must stand on a node for this test to mean anything");
        assertTrue(requestMap.containsKey("col"), "the columns must stand on a column for this test to mean anything");

        treeTable.cleanupIterationState(context);

        assertNull(treeTable.getRowKey());
        assertEquals(-1, columns.getRowIndex());
        assertEquals(treeTable.getClientId(context), treeTable.getContainerClientId(context));
        assertFalse(requestMap.containsKey("node"));
        assertFalse(requestMap.containsKey("col"));
    }

    /**
     * The expand, collapse, cell edit, row edit and page features never reach encodeTbody, which is the only place
     * which used to reset the row key. They all dispatch from TreeTableRenderer#encodeEnd, so encoding the expand
     * feature covers the shape of all five. There is no page level reproducer: these render one fragment and nothing
     * renders after them.
     */
    @Test
    void encodeEndOfTheExpandFeatureLeavesTheTreeTableStandingOnNoRow() throws Exception {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        TreeNode<String> root = new DefaultTreeNode<>();
        TreeNode<String> one = new DefaultTreeNode<>("one", root);
        new DefaultTreeNode<>("one-one", one);
        root.setRowKey(UITree.ROOT_ROW_KEY);

        Column column = new Column();
        column.setId("col");

        TreeTable treeTable = new TreeTable();
        treeTable.setId("treeTable");
        treeTable.setVar("node");
        treeTable.setValue(root);
        treeTable.getChildren().add(column);
        treeTable.buildRowKeys(root);
        // initFilterBy resolves the global filter through the search expression handler, which the mocks do not have
        treeTable.setFilterByAsMap(new HashMap<>());

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        params.put(treeTable.getClientId(context) + "_encodeFeature", "true");
        params.put(treeTable.getClientId(context) + "_expand", "0");

        new PrimeRendererWrapper(new TreeTableRenderer()).encodeEnd(context, treeTable);

        assertNull(treeTable.getRowKey());
        assertEquals(treeTable.getClientId(context), treeTable.getContainerClientId(context));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("node"));
    }
}
