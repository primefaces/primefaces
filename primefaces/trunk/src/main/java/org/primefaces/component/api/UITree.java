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
package org.primefaces.component.api;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import org.primefaces.model.TreeNode;

public abstract class UITree extends UIComponentBase {
    
    public final static String SEPARATOR = "_";
    
    private String rowKey;
   
    private TreeNode rowNode;
            
    protected enum PropertyKeys {
		var
		,value;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

        @Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}
    
    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;

        if(rowKey == null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(getVar());
        } 
        else {
            TreeNode root = getValue();
            this.rowNode = findTreeNode(root, rowKey);
            
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(getVar(), this.rowNode.getData());
        }
    }
    
    public TreeNode getRowNode() {
        return rowNode;
    }
    
    public java.lang.String getVar() {
		return (String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(java.lang.String _var) {
		getStateHelper().put(PropertyKeys.var, _var);
	}
    
    public TreeNode getValue() {
		return (TreeNode) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(TreeNode _value) {
		getStateHelper().put(PropertyKeys.value, _value);
	}
    
    protected TreeNode findTreeNode(TreeNode searchRoot, String rowKey) {
		String[] paths = rowKey.split(SEPARATOR);
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String relativeRowKey = rowKey.substring(2);
				
			return findTreeNode(searchRoot, relativeRowKey);
		}
	}
}
