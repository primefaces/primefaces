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

public class TreeExplorerImplTest {

	@Test
	public void findsTreeNodeWithAGivenPath() {
		TreeNode root = new TreeNode("Parent");
		
		TreeNode child_0 = new TreeNode("Child_0");
		
		TreeNode child_00 = new TreeNode("Child_00");
		TreeNode child_01 = new TreeNode("Child_01");
		TreeNode child_02 = new TreeNode("Child_02");
		TreeNode child_03 = new TreeNode("Child_03");
		TreeNode child_04 = new TreeNode("Child_04");
		TreeNode child_05 = new TreeNode("Child_05");
		TreeNode child_06 = new TreeNode("Child_06");
		TreeNode child_07 = new TreeNode("Child_07");
		TreeNode child_08 = new TreeNode("Child_08");
		TreeNode child_09 = new TreeNode("Child_09");
		TreeNode child_10 = new TreeNode("Child_10");
		TreeNode child_11 = new TreeNode("Child_11");
		TreeNode child_110 = new TreeNode("Child_110");
		child_11.addChild(child_110);
		
		child_0.addChild(child_00);
		child_0.addChild(child_01);
		child_0.addChild(child_02);
		child_0.addChild(child_03);
		child_0.addChild(child_04);
		child_0.addChild(child_05);
		child_0.addChild(child_06);
		child_0.addChild(child_07);
		child_0.addChild(child_08);
		child_0.addChild(child_09);
		child_0.addChild(child_10);
		child_0.addChild(child_11);
		
		root.addChild(child_0);
		
		//assertEquals(child_0, new TreeExplorerImpl().findTreeNode("0", new TreeModel(root)));
		//assertEquals(child_11, new TreeExplorerImpl().findTreeNode("0.11", new TreeModel(root)));
		assertEquals(child_110, new TreeExplorerImpl().findTreeNode("0.11.0", new TreeModel(root)));
	}
}