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

import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTableRendererTest {

    private static TreeTable treeTable() {
        TreeNode<String> root = new DefaultTreeNode<>();
        new DefaultTreeNode<>("one", root);
        new DefaultTreeNode<>("two", root);

        TreeTable treeTable = new TreeTable();
        treeTable.setId("treetable");
        treeTable.setVar("node");
        treeTable.setValue(root);
        return treeTable;
    }

    /**
     * stripedRows is opt-in, the same way it is on DataTable, so the class only shows up when asked for.
     */
    @Test
    void encodeMarkupWritesStripedRowsClass() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        TreeTable treeTable = treeTable();
        treeTable.setStripedRows(true);

        new TreeTableRenderer().encodeMarkup(context, treeTable);

        assertTrue(writer.toString().contains(TreeTable.STRIPED_ROWS_CLASS), writer.toString());
    }

    @Test
    void encodeMarkupWithoutStripedRows() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        new TreeTableRenderer().encodeMarkup(context, treeTable());

        assertFalse(writer.toString().contains(TreeTable.STRIPED_ROWS_CLASS), writer.toString());
    }
}
