/**
 * Copyright 2009-2018 PrimeTek.
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
import java.util.List;

public class DefaultTreeNode implements TreeNode, Serializable {

    public static final String DEFAULT_TYPE = "default";

    private static final long serialVersionUID = 1L;


    private String type;

    private Object data;

    private List<TreeNode> children;

    private TreeNode parent;

    private boolean expanded;

    private boolean selected;

    private boolean selectable = true;

    private String rowKey;

    public DefaultTreeNode() {
        this.type = DEFAULT_TYPE;
        this.children = new TreeNodeChildren(this);
    }

    public DefaultTreeNode(Object data) {
        this.type = DEFAULT_TYPE;
        this.children = new TreeNodeChildren(this);
        this.data = data;
    }

    public DefaultTreeNode(Object data, TreeNode parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        this.children = new TreeNodeChildren(this);
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public DefaultTreeNode(String type, Object data, TreeNode parent) {
        this.type = type;
        this.data = data;
        this.children = new TreeNodeChildren(this);
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        if (children instanceof TreeNodeChildren) {
            this.children = children;
        }
        else {
            TreeNodeChildren nodeChildren = new TreeNodeChildren(this);
            nodeChildren.addAll(children);
            this.children = nodeChildren;
        }
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    @Override
    public void clearParent() {
        this.parent = null;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean value) {
        this.selected = value;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public boolean isLeaf() {
        if (children == null) {
            return true;
        }

        return children.isEmpty();
    }

    @Override
    public String getRowKey() {
        return rowKey;
    }

    @Override
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        DefaultTreeNode other = (DefaultTreeNode) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        }
        else if (!data.equals(other.data)) {
            return false;
        }

        if (rowKey == null) {
            if (other.rowKey != null) {
                return false;
            }
        }
        else if (!rowKey.equals(other.rowKey)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        if (data != null) {
            return data.toString();
        }
        else {
            return super.toString();
        }
    }

    @Override
    public boolean isPartialSelected() {
        return false;
    }

    @Override
    public void setPartialSelected(boolean value) {
        //nothing
    }
}
