/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

public class TreeTableModel extends DataModel implements Serializable {

	private String SEPARATOR = "_";
	
	private Object wrappedData = null;
	private int rowIndex = -1;
    private String rowKey = null;
	private TreeNode root;
	private List<TreeNode> list;
	
	public TreeTableModel() {}
	
	public TreeTableModel(TreeNode root) {
		this.wrappedData = root;
		this.root = root;
		this.list = new ArrayList<TreeNode>();
		index(this.root);
	}
	
	private void index(TreeNode node) {
		if(node.getParent() != null) {
			list.add(node);
		}
		
        for(TreeNode child : node.getChildren()) {
            index(child);
        }
	}
	
	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getRowData() {
		if (list == null)
			return null;
		else if (!isRowAvailable())
			throw new IllegalArgumentException();
		else
			return ((TreeNode) list.get(this.rowIndex)).getData();
	}
	
	public TreeNode getRowNode() {
		if (list == null)
			return null;
		else if (!isRowAvailable())
			throw new IllegalArgumentException();
		else
			return list.get(this.rowIndex);
	}

	@Override
	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public Object getWrappedData() {
		return wrappedData;
	}

	@Override
	public boolean isRowAvailable() {
		return (rowIndex >= 0 && rowIndex < list.size());
	}

	@Override
	public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
	}

	@Override
	public void setWrappedData(Object wrappedData) {
		this.wrappedData = wrappedData;
	}
		
	public int getNodeIndex(TreeNode node) {
		return list.indexOf(node);
	}
	
	public TreeNode findTreeNode(TreeNode searchRoot, String path) {
		String[] paths = path.split(SEPARATOR);
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String childPath = path.substring(2);
				
			return findTreeNode(searchRoot, childPath);
		}
	}
}