/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

public class CheckboxTreeNode<T> extends DefaultTreeNode<T> implements TreeNode<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean partialSelected;

    public CheckboxTreeNode() {
        super();
    }

    public CheckboxTreeNode(T data) {
        super(data);
    }

    public CheckboxTreeNode(T data, TreeNode parent) {
        super(data, parent);
    }

    public CheckboxTreeNode(String type, T data, TreeNode parent) {
        super(type, data, parent);
    }

    @Override
    protected TreeNodeChildren<T> initChildren() {
        return new CheckboxTreeNodeChildren<>(this);
    }

    public void setSelected(boolean selected, boolean propagateDown, boolean propagateUp) {
        this.selected = selected;
        this.partialSelected = false;

        if (propagateDown && propagateUp) {
            setSelected(selected);
        }
        else if (!propagateDown && propagateUp) {
            if (getParent() != null) {
                ((CheckboxTreeNode) getParent()).propagateSelectionUp();
            }
        }
        else if (propagateDown) {
            if (!isLeaf()) {
                for (TreeNode child : children) {
                    ((CheckboxTreeNode) child).propagateSelectionDown(selected);
                }
            }
        }
    }

    public void setSelected(boolean selected, boolean propagate) {
        if (propagate) {
            setSelected(selected);
        }
        else {
            this.selected = selected;
        }
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.partialSelected = false;

        if (!isLeaf()) {
            for (TreeNode child : children) {
                ((CheckboxTreeNode) child).propagateSelectionDown(selected);
            }
        }

        if (getParent() != null) {
            ((CheckboxTreeNode) getParent()).propagateSelectionUp();
        }
    }

    protected void propagateSelectionDown(boolean selected) {
        if (!this.isSelectable()) {
            return;
        }
        this.selected = selected;
        this.partialSelected = false;

        for (TreeNode child : children) {
            ((CheckboxTreeNode) child).propagateSelectionDown(selected);
        }
    }

    protected void propagateSelectionUp() {
        if (!isSelectable()) {
            return;
        }
        boolean allChildrenSelected = true;
        this.partialSelected = false;

        for (int i = 0; i < getChildCount(); i++) {
            TreeNode childNode = getChildren().get(i);

            boolean childSelected = childNode.isSelected();
            boolean childPartialSelected = childNode.isPartialSelected();
            allChildrenSelected = allChildrenSelected && childSelected;
            this.partialSelected = partialSelected || childSelected || childPartialSelected;
        }

        this.selected = allChildrenSelected;

        if (allChildrenSelected) {
            setPartialSelected(false);
        }

        if (getParent() != null) {
            ((CheckboxTreeNode) getParent()).propagateSelectionUp();
        }
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

        CheckboxTreeNode other = (CheckboxTreeNode) obj;
        if (data == null) {
            if (other.getData() != null) {
                return false;
            }
        }
        else if (!data.equals(other.getData())) {
            return false;
        }

        if (rowKey == null) {
            return other.rowKey == null;
        }

        return rowKey.equals(other.rowKey);
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
        return partialSelected;
    }

    @Override
    public void setPartialSelected(boolean partialSelected) {
        this.partialSelected = partialSelected;
    }
}
