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

public class TreeModelTest {

	@Test
	public void rowCountReturnsTheNumberOfChildrenOfCurrentNode() {
		TreeNode root = new TreeNode("Parent");
		
		TreeNode child1 = new TreeNode("Child1");
		TreeNode child2 = new TreeNode("Child2");
		
		TreeNode child11 = new TreeNode("Child11");
		TreeNode child12 = new TreeNode("Child12");
		TreeNode child21 = new TreeNode("Child21");
		
		child1.addChild(child11);
		child1.addChild(child12);
		child2.addChild(child21);
		
		root.addChild(child1);
		root.addChild(child2);
		
		TreeModel model = new TreeModel(root);
		
		assertEquals(2, model.getRowCount());
		
		model.setRowIndex(0);
		
		assertEquals(2, model.getRowCount());
	}
}
