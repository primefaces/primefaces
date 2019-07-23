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
	
	@Test
	public void shouldHaveConsistenKeys() {
		TreeNode root = new DefaultTreeNode(null);

		TreeNode child0 = new DefaultTreeNode(null, root);
		TreeNode child1 = new DefaultTreeNode(null, root);
		String childRowKey0 = child0.getRowKey();
		String childRowKey1 = child1.getRowKey();
		//check that the row key is the same after inserting the first node
		assertEquals(child0.getRowKey(), root.getChildren().get(0).getRowKey());
		TreeNode child2 = new DefaultTreeNode(null, root);
		String childRowKey2 = child2.getRowKey();
		//check that the row key is the same after inserting the second node
		assertEquals(child0.getRowKey(), root.getChildren().get(0).getRowKey());
		assertEquals(child1.getRowKey(), root.getChildren().get(1).getRowKey());
		assertEquals(childRowKey0,child0.getRowKey());
		assertEquals(childRowKey1,child1.getRowKey());
		assertEquals(childRowKey2,child2.getRowKey());

		//add and remove node (at the end)
		TreeNode child3 = new DefaultTreeNode(null, root);
		root.getChildren().remove(3);
		//check that the row key is the same after adding and removing a node at the end
		assertEquals(child0.getRowKey(), root.getChildren().get(0).getRowKey());
		assertEquals(child1.getRowKey(), root.getChildren().get(1).getRowKey());
		assertEquals(child2.getRowKey(), root.getChildren().get(2).getRowKey());
		assertEquals(childRowKey0,child0.getRowKey());
		assertEquals(childRowKey1,child1.getRowKey());
		assertEquals(childRowKey2,child2.getRowKey());

		//add and remove node (at the begin)
		child3 = new DefaultTreeNode(null);
		root.getChildren().add(0,child3);
		root.getChildren().remove(0);
		//check that the row key is the same after adding and remove a node at the begin
		assertEquals(child0.getRowKey(), root.getChildren().get(0).getRowKey());
		assertEquals(child1.getRowKey(), root.getChildren().get(1).getRowKey());
		assertEquals(child2.getRowKey(), root.getChildren().get(2).getRowKey());
		assertEquals(childRowKey0,child0.getRowKey());
		assertEquals(childRowKey1,child1.getRowKey());
		assertEquals(childRowKey2,child2.getRowKey());

		//check the keys are not equal
		assertNotEquals(child0.getRowKey(), child1.getRowKey());
		assertNotEquals(child1.getRowKey(), child2.getRowKey());
		assertNotEquals(child0.getRowKey(), child2.getRowKey());

		//add sub children / remove them / add again
		TreeNode subChild0 = new DefaultTreeNode(null, child0);
		TreeNode subChild1 = new DefaultTreeNode(null, child0);
		assertNotEquals(subChild0.getRowKey(), subChild1.getRowKey());
		String subChildRowKey0 = subChild0.getRowKey();
		String subChildRowKey1 = subChild1.getRowKey();
		child0.getChildren().remove(subChild0);
		child0.getChildren().remove(subChild1);
		//add same order
		child0.getChildren().add(0,subChild1);
		child0.getChildren().add(0,subChild0);
		assertEquals(subChildRowKey0, subChild0.getRowKey());
		assertEquals(subChildRowKey1, subChild1.getRowKey());
		child0.getChildren().remove(1);
		child0.getChildren().remove(0);
		//add inverse order
		child0.getChildren().add(subChild1);
		child0.getChildren().add(subChild0);
		assertEquals(subChildRowKey0, subChild1.getRowKey());
		assertEquals(subChildRowKey1, subChild0.getRowKey());
		child0.getChildren().remove(subChild0);
		child0.getChildren().remove(subChild1);

		child0.getChildren().add(subChild0);
		child0.getChildren().add(subChild1);
		assertEquals(subChildRowKey0, subChild0.getRowKey());
		assertEquals(subChildRowKey1, subChild1.getRowKey());

		assertEquals(childRowKey0,child0.getRowKey());
		assertEquals(childRowKey1,child1.getRowKey());
		assertEquals(childRowKey2,child2.getRowKey());

		TreeNode child4 = new DefaultTreeNode(null);
		root.getChildren().add(0,child4);
		assertEquals(childRowKey0,child4.getRowKey());
		root.getChildren().remove(0);

		assertEquals(subChildRowKey0, subChild0.getRowKey());
		assertEquals(subChildRowKey1, subChild1.getRowKey());
		assertEquals(childRowKey0,child0.getRowKey());
		assertEquals(childRowKey1,child1.getRowKey());
		assertEquals(childRowKey2,child2.getRowKey());
        }
}
