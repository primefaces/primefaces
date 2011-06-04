/*
 * Copyright 2009-2011 Prime Technology.
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

import java.io.Serializable;

public class TreeExplorerImpl implements TreeExplorer, Serializable {

	public TreeNode findTreeNode(String path, TreeModel model) {
		String[] paths = path.split("_");
		
		if(paths.length == 0)
			return null;
		
		int currentIndex = Integer.parseInt(paths[0]);
		model.setRowIndex(currentIndex);
		TreeNode currentNode  = (TreeNode) model.getWrappedData();

		if(paths.length == 1) {
			return currentNode;
		} 
		else {
			String childPath = buildSubpath(paths);	//subpath
				
			return findTreeNode(childPath, new TreeModel(currentNode));
		}
	}
	
	private String buildSubpath(String[] path) {
		StringBuilder pathBuilder = new StringBuilder();
		
		for(int i=1; i < path.length; i++) {
			pathBuilder.append(path[i]);
			
			if(i != (path.length-1))
				pathBuilder.append("_");
		}
		
		return pathBuilder.toString();
	}
}