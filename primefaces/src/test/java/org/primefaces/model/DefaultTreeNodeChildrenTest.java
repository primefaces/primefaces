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
package org.primefaces.model;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultTreeNodeChildrenTest {

    @Test
    void swapRetainsParent() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Parent", null);
        TreeNode<String> child0 = new DefaultTreeNode<>("Child 0", root);
        TreeNode<String> child1 = new DefaultTreeNode<>("Child 1", root);

        List<TreeNode<String>> values = new DefaultTreeNodeChildren<>(root);

        values.add(child0);
        values.add(child1);

        // Pre-assert
        assertEquals(child0, values.get(0));
        assertEquals(root, values.get(0).getParent());
        assertEquals(child1, values.get(1));
        assertEquals(root, values.get(1).getParent());

        // Act
        Collections.swap(values, 0, 1);

        // Assert
        assertEquals(child1, values.get(0));
        assertEquals(root, values.get(0).getParent());
        assertEquals(child0, values.get(1));
        assertEquals(root, values.get(1).getParent());
    }

    @Test
    void moveNodeSameListDown() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node0 = new DefaultTreeNode<>("Node 0", root);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);

        // Pre-assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());

        // Act (simulate Drag&Drop)
        // new index is coming from the client side, the index is calculated without the down dragged node
        // so to move 'Node 0' between 'Node 1' and 'Node 2' index=1 has to be used
        root.getChildren().add(1, root.getChildren().get(0));

        // Assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node0, root.getChildren().get(1));
        assertEquals(root, node0.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());
    }

    @Test
    void moveNodeSameListUp() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node0 = new DefaultTreeNode<>("Node 0", root);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);

        // Pre-assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().add(2, root.getChildren().get(3));

        // Assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node2, root.getChildren().get(3));
        assertEquals(root, node2.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());
    }

    @Test
    void moveNodeToChild() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node0 = new DefaultTreeNode<>("Node 0", root);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node20 = new DefaultTreeNode<>("Node 2.0", node2);
        TreeNode<String> node21 = new DefaultTreeNode<>("Node 2.1", node2);
        TreeNode<String> node22 = new DefaultTreeNode<>("Node 2.2", node2);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);

        // Pre-assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node20, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node2, node20.getParent());
        assertEquals(node21, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node2, node21.getParent());
        assertEquals(node22, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node2, node22.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().get(2).getChildren().add(1, root.getChildren().get(3));

        // Assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node20, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node2, node20.getParent());
        assertEquals(node3, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node2, node3.getParent());
        assertEquals(node21, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node2, node21.getParent());
        assertEquals(node22, root.getChildren().get(2).getChildren().get(3));
        assertEquals(node2, node22.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
    }

    @Test
    void moveNodeToParent() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node0 = new DefaultTreeNode<>("Node 0", root);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node20 = new DefaultTreeNode<>("Node 2.0", node2);
        TreeNode<String> node21 = new DefaultTreeNode<>("Node 2.1", node2);
        TreeNode<String> node22 = new DefaultTreeNode<>("Node 2.2", node2);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);

        // Pre-assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(2));
        assertEquals(root, node2.getParent());
        assertEquals(node20, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node2, node20.getParent());
        assertEquals(node21, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node2, node21.getParent());
        assertEquals(node22, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node2, node22.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().add(2, root.getChildren().get(2).getChildren().get(1));

        // Assert
        assertEquals(node0, root.getChildren().get(0));
        assertEquals(root, node0.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node21, root.getChildren().get(2));
        assertEquals(root, node21.getParent());
        assertEquals(node2, root.getChildren().get(3));
        assertEquals(root, node2.getParent());
        assertEquals(node20, root.getChildren().get(3).getChildren().get(0));
        assertEquals(node2, node20.getParent());
        assertEquals(node22, root.getChildren().get(3).getChildren().get(1));
        assertEquals(node2, node22.getParent());
        assertEquals(node3, root.getChildren().get(4));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(5));
        assertEquals(root, node4.getParent());
    }
}
