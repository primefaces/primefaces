/*
 * Copyright 2009 Prime Technology.
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
		TreeNode node = new TreeNode("Parent");
		
		node.addChild(new TreeNode("Child1"));
		node.addChild(new TreeNode("Child2"));
		
		assertEquals(2, node.getChildCount());
	}
	
	@Test
	public void shouldHaveParent() {		
		TreeNode root = new TreeNode("Parent");
		
		TreeNode child1 = new TreeNode("Child1");
		TreeNode child11 = new TreeNode("Child11");
		child1.addChild(child11);
		
		root.addChild(child1);
		
		assertEquals(root, child1.getParent());
		assertEquals(child1, child11.getParent());
	}
}
