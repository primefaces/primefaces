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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class TreeTableFilterFeatureTest {

    @Test
    public void testCloneTreeNodeWithCustomNodeConstructor() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode root = new DefaultTreeNode();
        DefaultTreeNode cloneRoot = new DefaultTreeNode();
        CustomNode<String> customNode = new CustomNode<>("Type", "Name", root);
        TreeTable treeTable = new TreeTable();

        TreeNode cloneCustomNode = filterFeature.cloneTreeNode(customNode, cloneRoot);

        Assertions.assertEquals(1, cloneRoot.getChildren().size());
        Assertions.assertEquals(cloneCustomNode, cloneRoot.getChildren().get(0));
    }

    @Test
    public void testCloneTreeNodeWithBuiltInNodeConstructor() {
        FilterFeature filterFeature = new FilterFeature();

        DefaultTreeNode root = new DefaultTreeNode();
        DefaultTreeNode cloneRoot = new DefaultTreeNode();
        DefaultTreeNode<String> regularNode = new DefaultTreeNode<>("Type", "Name", root);
        TreeTable treeTable = new TreeTable();

        TreeNode cloneCustomNode = filterFeature.cloneTreeNode(regularNode, cloneRoot);

        Assertions.assertEquals(1, cloneRoot.getChildren().size());
        Assertions.assertEquals(cloneCustomNode, cloneRoot.getChildren().get(0));
    }
}
