/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
