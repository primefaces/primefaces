/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.api.UITree;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTableFilterFeatureTest {

    @Test
    void cloneTreeNodeWithCustomNodeConstructor() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode root = new DefaultTreeNode();
        DefaultTreeNode cloneRoot = new DefaultTreeNode();
        CustomNode<String> customNode = new CustomNode<>("Type", "Name", root);
        TreeTable treeTable = new TreeTable();

        TreeNode cloneCustomNode = filterFeature.cloneTreeNode(customNode, cloneRoot);

        assertEquals(1, cloneRoot.getChildren().size());
        assertEquals(cloneCustomNode, cloneRoot.getChildren().get(0));
    }

    @Test
    void cloneTreeNodeWithBuiltInNodeConstructor() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode root = new DefaultTreeNode();
        DefaultTreeNode cloneRoot = new DefaultTreeNode();
        DefaultTreeNode<String> regularNode = new DefaultTreeNode<>("Type", "Name", root);
        TreeTable treeTable = new TreeTable();

        TreeNode cloneCustomNode = filterFeature.cloneTreeNode(regularNode, cloneRoot);

        assertEquals(1, cloneRoot.getChildren().size());
        assertEquals(cloneCustomNode, cloneRoot.getChildren().get(0));
    }

    @Test
    void createFilteredValueFromRowKeysKeepsChildrenOnDefaultPrune() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> matchingNode = new DefaultTreeNode<>("Type", "Match", root);
        DefaultTreeNode<String> childOfMatch = new DefaultTreeNode<>("Type", "Child", matchingNode);
        new DefaultTreeNode<>("Type", "Other", root);
        TreeTable treeTable = new TreeTable();

        root.setRowKey(UITree.ROOT_ROW_KEY);
        treeTable.buildRowKeys(root);

        TreeNode<?> filteredRoot = filterFeature.cloneTreeNode(root, root.getParent());
        filterFeature.createFilteredValueFromRowKeys(treeTable, root, filteredRoot,
                List.of(matchingNode.getRowKey()), false);

        assertEquals(1, filteredRoot.getChildCount());
        TreeNode<?> filteredMatch = filteredRoot.getChildren().get(0);
        assertEquals(matchingNode.getRowKey(), filteredMatch.getRowKey());
        assertEquals(1, filteredMatch.getChildCount());
        assertEquals(childOfMatch.getRowKey(), filteredMatch.getChildren().get(0).getRowKey());
    }

    @Test
    void createFilteredValueFromRowKeysPrunesChildrenWhenRequested() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> matchingNode = new DefaultTreeNode<>("Type", "Match", root);
        new DefaultTreeNode<>("Type", "Child", matchingNode);
        TreeTable treeTable = new TreeTable();

        root.setRowKey(UITree.ROOT_ROW_KEY);
        treeTable.buildRowKeys(root);

        TreeNode<?> filteredRoot = filterFeature.cloneTreeNode(root, root.getParent());
        filterFeature.createFilteredValueFromRowKeys(treeTable, root, filteredRoot,
                List.of(matchingNode.getRowKey()), true);

        assertEquals(1, filteredRoot.getChildCount());
        TreeNode<?> filteredMatch = filteredRoot.getChildren().get(0);
        assertEquals(matchingNode.getRowKey(), filteredMatch.getRowKey());
        assertEquals(0, filteredMatch.getChildCount());
        assertTrue(filteredMatch.getChildren().isEmpty());
    }
}
