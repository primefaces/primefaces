/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

public class TreeNodeChildrenTest {

    @Test
    public void swapRetainsParent() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Parent", null);
        TreeNode<String> child1 = new DefaultTreeNode<>("Child1", root);
        TreeNode<String> child2 = new DefaultTreeNode<>("Child2", root);

        List<TreeNode<String>> values = new TreeNodeChildren<>(root);

        values.add(child1);
        values.add(child2);

        // Pre-assert
        assertEquals(child1, values.get(0));
        assertEquals(root, values.get(0).getParent());
        assertEquals(child2, values.get(1));
        assertEquals(root, values.get(1).getParent());

        // Act
        Collections.swap(values, 0, 1);

        // Assert
        assertEquals(child2, values.get(0));
        assertEquals(root, values.get(0).getParent());
        assertEquals(child1, values.get(1));
        assertEquals(root, values.get(1).getParent());
    }

    @Test
    public void moveNodeSameListDown() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);
        TreeNode<String> node5 = new DefaultTreeNode<>("Node 5", root);

        // Pre-assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().add(2, root.getChildren().get(0));

        // Assert
        assertEquals(node2, root.getChildren().get(0));
        assertEquals(root, node2.getParent());
        assertEquals(node1, root.getChildren().get(1));
        assertEquals(root, node1.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());
    }

    @Test
    public void moveNodeSameListUp() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);
        TreeNode<String> node5 = new DefaultTreeNode<>("Node 5", root);

        // Pre-assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().add(2, root.getChildren().get(3));

        // Assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node4, root.getChildren().get(2));
        assertEquals(root, node4.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());
    }

    @Test
    public void moveNodeToChild() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node31 = new DefaultTreeNode<>("Node 3.1", node3);
        TreeNode<String> node32 = new DefaultTreeNode<>("Node 3.2", node3);
        TreeNode<String> node33 = new DefaultTreeNode<>("Node 3.3", node3);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);
        TreeNode<String> node5 = new DefaultTreeNode<>("Node 5", root);

        // Pre-assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node31, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node3, node31.getParent());
        assertEquals(node32, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node3, node32.getParent());
        assertEquals(node33, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node3, node33.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().get(2).getChildren().add(1, root.getChildren().get(3));

        // Assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node31, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node3, node31.getParent());
        assertEquals(node4, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node3, node4.getParent());
        assertEquals(node32, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node3, node32.getParent());
        assertEquals(node33, root.getChildren().get(2).getChildren().get(3));
        assertEquals(node3, node33.getParent());
        assertEquals(node5, root.getChildren().get(3));
        assertEquals(root, node5.getParent());
    }

    @Test
    public void moveNodeToParent() {
        // Arrange
        TreeNode<String> root = new DefaultTreeNode<>("Node R", null);
        TreeNode<String> node1 = new DefaultTreeNode<>("Node 1", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Node 2", root);
        TreeNode<String> node3 = new DefaultTreeNode<>("Node 3", root);
        TreeNode<String> node31 = new DefaultTreeNode<>("Node 3.1", node3);
        TreeNode<String> node32 = new DefaultTreeNode<>("Node 3.2", node3);
        TreeNode<String> node33 = new DefaultTreeNode<>("Node 3.3", node3);
        TreeNode<String> node4 = new DefaultTreeNode<>("Node 4", root);
        TreeNode<String> node5 = new DefaultTreeNode<>("Node 5", root);

        // Pre-assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node3, root.getChildren().get(2));
        assertEquals(root, node3.getParent());
        assertEquals(node31, root.getChildren().get(2).getChildren().get(0));
        assertEquals(node3, node31.getParent());
        assertEquals(node32, root.getChildren().get(2).getChildren().get(1));
        assertEquals(node3, node32.getParent());
        assertEquals(node33, root.getChildren().get(2).getChildren().get(2));
        assertEquals(node3, node33.getParent());
        assertEquals(node4, root.getChildren().get(3));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(4));
        assertEquals(root, node5.getParent());

        // Act (simulate Drag&Drop)
        root.getChildren().add(2, root.getChildren().get(2).getChildren().get(1));

        // Assert
        assertEquals(node1, root.getChildren().get(0));
        assertEquals(root, node1.getParent());
        assertEquals(node2, root.getChildren().get(1));
        assertEquals(root, node2.getParent());
        assertEquals(node32, root.getChildren().get(2));
        assertEquals(root, node32.getParent());
        assertEquals(node3, root.getChildren().get(3));
        assertEquals(root, node3.getParent());
        assertEquals(node31, root.getChildren().get(3).getChildren().get(0));
        assertEquals(node3, node31.getParent());
        assertEquals(node33, root.getChildren().get(3).getChildren().get(1));
        assertEquals(node3, node33.getParent());
        assertEquals(node4, root.getChildren().get(4));
        assertEquals(root, node4.getParent());
        assertEquals(node5, root.getChildren().get(5));
        assertEquals(root, node5.getParent());
    }
}
