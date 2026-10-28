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
package org.primefaces.component.api;

import org.primefaces.component.tree.Tree;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UITreeTest {

    private Tree tree;
    private CheckboxTreeNode<String> root;
    private CheckboxTreeNode<String> selectable;
    private CheckboxTreeNode<String> unselectable;

    @BeforeEach
    void setup() {
        new FacesContextMock(new CollectingResponseWriter());

        root = new CheckboxTreeNode<>("root", null);
        selectable = new CheckboxTreeNode<>("selectable", root);
        unselectable = new CheckboxTreeNode<>("unselectable", root);
        unselectable.setSelectable(false);

        tree = new Tree();
        tree.setVar("node");
        tree.setValue(root);
        tree.setSelectionMode("checkbox");
    }

    @Test
    void decodeSelectedNodesRejectsNewUnselectableNode() {
        List<TreeNode<?>> selected = tree.decodeSelectedNodes(root, new String[] {"0", "1"}, null);

        assertEquals(List.of(selectable), selected);
    }

    @Test
    void decodeSelectedNodesKeepsPreviouslySelectedUnselectableNode() {
        List<TreeNode<?>> selected = tree.decodeSelectedNodes(root, new String[] {"0"}, List.of(unselectable));

        assertEquals(List.of(selectable, unselectable), selected);
    }

    @Test
    void decodeSelectedNodesDropsUnselectableNodeRemovedFromTheTree() {
        root.getChildren().remove(unselectable);

        List<TreeNode<?>> selected = tree.decodeSelectedNodes(root, new String[0], List.of(unselectable));

        assertEquals(List.of(), selected);
    }

    @Test
    void decodeSelectedNodesDropsUnselectableNodeReplacedAtItsRowKey() {
        root.getChildren().remove(unselectable);
        CheckboxTreeNode<String> other = new CheckboxTreeNode<>("other", root);
        other.setSelectable(false);

        List<TreeNode<?>> selected = tree.decodeSelectedNodes(root, new String[0], List.of(unselectable));

        assertEquals(List.of(), selected);
    }

    @Test
    void decodeSelectedNodesDropsUnselectableNodeOnlyInCheckboxMode() {
        tree.setSelectionMode("multiple");

        List<TreeNode<?>> selected = tree.decodeSelectedNodes(root, new String[] {"0"}, new TreeNode<?>[] {unselectable});

        assertEquals(List.of(selectable), selected);
    }
}
