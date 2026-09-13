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
package org.primefaces.component.tree;

import org.primefaces.component.api.UITree;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.PrimeRendererWrapper;

import java.util.List;
import java.util.Map;

import jakarta.el.ValueExpression;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeRendererTest {

    /**
     * The row the tree stands on ends up in the client id of everything below it, and it puts the node it holds in
     * the request map under the tree's var. So a render which iterated the tree leaves it standing on no row. The
     * state of a child is saved under the client id it reports at the end of the render, and whatever renders after
     * the tree resolves the var of its own scope.
     */
    @Test
    void encodeEndLeavesTheTreeStandingOnNoRow() throws Exception {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());

        TreeNode<String> root = new DefaultTreeNode<>();
        new DefaultTreeNode<>("one", root);
        new DefaultTreeNode<>("two", root);

        UITreeNode treeNode = new UITreeNode();
        treeNode.setType("default");

        Tree tree = new Tree();
        tree.setId("tree");
        tree.setVar("node");
        tree.setValue(root);
        tree.getChildren().add(treeNode);

        new PrimeRendererWrapper(new TreeRenderer()).encodeEnd(context, tree);

        assertNull(tree.getRowKey());
        assertEquals(tree.getClientId(context), tree.getContainerClientId(context));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("node"));
    }

    /**
     * The instant selection branch of decodeSelection stands the tree on the node which was checked, to collect the
     * row keys below it, and does not reset it, unlike the branch above it. So the decode leaves the tree standing on
     * no node either.
     */
    @Test
    void decodeLeavesTheTreeStandingOnNoRow() {
        FacesContext context = new FacesContextMock();

        TreeNode<String> root = new DefaultTreeNode<>();
        TreeNode<String> one = new DefaultTreeNode<>("one", root);
        new DefaultTreeNode<>("one-one", one);
        new DefaultTreeNode<>("two", root);

        ValueExpression selectionVE = mock(ValueExpression.class);
        when(selectionVE.getType(context.getELContext())).thenReturn((Class) List.class);

        Tree tree = new Tree();
        tree.setId("tree");
        tree.setVar("node");
        tree.setValue(root);
        tree.setSelectionMode("checkbox");
        tree.setDynamic(true);
        tree.setPropagateSelectionDown(true);
        tree.setValueExpression(UITree.PropertyKeys.selection.toString(), selectionVE);
        tree.buildRowKeys(root);

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        params.put(tree.getClientId(context) + "_instantSelection", "0");

        new PrimeRendererWrapper(new TreeRenderer()).decode(context, tree);

        assertNull(tree.getRowKey());
        assertEquals(tree.getClientId(context), tree.getContainerClientId(context));
        assertFalse(context.getExternalContext().getRequestMap().containsKey("node"));
    }
}
