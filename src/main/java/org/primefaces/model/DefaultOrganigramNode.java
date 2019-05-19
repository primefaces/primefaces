/**
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation for the {@link OrganigramNode}.
 */
public class DefaultOrganigramNode implements OrganigramNode, Serializable {

    public static final String DEFAULT_TYPE = "default";

    private static final long serialVersionUID = 1L;

    private String type;
    private Object data;
    private List<OrganigramNode> children;
    private OrganigramNode parent;
    private String rowKey;

    private boolean expanded = true;

    private boolean selectable;
    private boolean draggable;
    private boolean droppable;
    private boolean collapsible = true;

    public DefaultOrganigramNode() {
        this.type = DEFAULT_TYPE;
        this.children = new ArrayList<OrganigramNode>();
    }

    public DefaultOrganigramNode(Object data) {
        this();
        this.data = data;
    }

    public DefaultOrganigramNode(Object data, OrganigramNode parent) {
        this(data);
        if (parent != null) {
            this.parent = parent;
            parent.getChildren().add(this);
        }
    }

    public DefaultOrganigramNode(String type, Object data, OrganigramNode parent) {
        this(data, parent);

        this.type = type;
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

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public List<OrganigramNode> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<OrganigramNode> children) {
        this.children = children;
    }

    @Override
    public OrganigramNode getParent() {
        return parent;
    }

    @Override
    public void setParent(OrganigramNode parent) {
        if (parent != null) {
            parent.getChildren().add(this);
        }
        this.parent = parent;
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
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean isDroppable() {
        return droppable;
    }

    @Override
    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
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
    public void clearParent() {
        this.parent = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
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

        DefaultOrganigramNode other = (DefaultOrganigramNode) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        }
        else if (!data.equals(other.data)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isCollapsible() {
        return collapsible;
    }

    @Override
    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
    }
}
