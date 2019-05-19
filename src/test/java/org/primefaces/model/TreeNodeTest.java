/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import static org.junit.Assert.*;

import org.junit.Test;

public class TreeNodeTest {

	@Test
	public void shouldAddChildNodes() {
		TreeNode node = new DefaultTreeNode("Parent", null);

		new DefaultTreeNode("Child1", node);
		new DefaultTreeNode("Child2", node);

		assertEquals(2, node.getChildCount());
	}

	@Test
	public void shouldHaveParent() {
		TreeNode root = new DefaultTreeNode("Parent", null);

		TreeNode child1 = new DefaultTreeNode("Child1", root);
		TreeNode child11 = new DefaultTreeNode("Child11", child1);

		assertEquals(root, child1.getParent());
		assertEquals(child1, child11.getParent());
	}

        @Test
        public void shouldHaveUnequalChildNodes() {
            TreeNode root = new DefaultTreeNode(null);

            TreeNode child0 = new DefaultTreeNode(null, root);
            TreeNode child1 = new DefaultTreeNode(null, root);
            TreeNode child2 = new DefaultTreeNode(null, root);

            assertEquals(child0, root.getChildren().get(0));
            assertEquals(child1, root.getChildren().get(1));
            assertEquals(child2, root.getChildren().get(2));

            assertEquals(0, root.getChildren().indexOf(
                    root.getChildren().get(0)));
            assertEquals(1, root.getChildren().indexOf(
                    root.getChildren().get(1)));
            assertEquals(2, root.getChildren().indexOf(
                    root.getChildren().get(2)));

            assertEquals(0, root.getChildren().indexOf(child0));
            assertEquals(1, root.getChildren().indexOf(child1));
            assertEquals(2, root.getChildren().indexOf(child2));
        }
}
