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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CheckboxTreeNodeChildren extends TreeNodeList {

    private static final long serialVersionUID = 1L;

    private TreeNode parent;

    public CheckboxTreeNodeChildren(TreeNode parent) {
        this.parent = parent;
    }

    private void eraseParent(TreeNode node) {
        TreeNode parentNode = node.getParent();
        if (parentNode != null) {
            parentNode.getChildren().remove(node);
            node.setParent(null);
        }
    }

    @Override
    public boolean add(TreeNode node) {
        if (node == null) {
            throw new NullPointerException();
        }
        else {
            eraseParent(node);
            boolean result = super.add(node);
            node.setParent(parent);
            updateRowKeys(parent);
            updateSelectionState(parent);
            return result;
        }
    }

    @Override
    public void add(int index, TreeNode node) {
        if (node == null) {
            throw new NullPointerException();
        }
        else if ((index < 0) || (index > size())) {
            throw new IndexOutOfBoundsException();
        }
        else {
            eraseParent(node);
            super.add(index, node);
            node.setParent(parent);
            updateRowKeys(parent);
            updateSelectionState(parent);
        }
    }

    @Override
    public boolean addAll(Collection<? extends TreeNode> collection) {
        Iterator<TreeNode> elements = (new ArrayList<TreeNode>(collection)).iterator();
        boolean changed = false;
        while (elements.hasNext()) {
            TreeNode node = elements.next();
            if (node == null) {
                throw new NullPointerException();
            }
            else {
                eraseParent(node);
                super.add(node);
                node.setParent(parent);
                changed = true;
            }
        }

        if (changed) {
            updateRowKeys(parent);
            updateSelectionState(parent);
        }

        return (changed);
    }

    @Override
    public boolean addAll(int index, Collection<? extends TreeNode> collection) {
        Iterator<TreeNode> elements = (new ArrayList<TreeNode>(collection)).iterator();
        boolean changed = false;
        while (elements.hasNext()) {
            TreeNode node = elements.next();
            if (node == null) {
                throw new NullPointerException();
            }
            else {
                eraseParent(node);
                super.add(index++, node);
                node.setParent(parent);
                changed = true;
            }
        }

        if (changed) {
            updateRowKeys(parent);
            updateSelectionState(parent);
        }

        return (changed);
    }

    @Override
    public TreeNode set(int index, TreeNode node) {
        if (node == null) {
            throw new NullPointerException();
        }
        else if ((index < 0) || (index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        else {
            if (!parent.equals(node.getParent())) {
                eraseParent(node);
            }

            TreeNode previous = get(index);
            super.set(index, node);
            previous.setParent(null);
            node.setParent(parent);
            updateRowKeys(parent);
            updateSelectionState(parent);
            return previous;
        }
    }

    /**
     * Optimized set implementation to be used in sorting
     *
     * @param index index of the element to replace
     * @param node node to be stored at the specified position
     * @return the node previously at the specified position
     */
    @Override
    public TreeNode setSibling(int index, TreeNode node) {
        if (node == null) {
            throw new NullPointerException();
        }
        else if ((index < 0) || (index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        else {
            if (!parent.equals(node.getParent())) {
                eraseParent(node);
            }

            TreeNode previous = get(index);
            super.set(index, node);
            node.setParent(parent);
            updateRowKeys(parent);
            updateSelectionState(parent);
            return previous;
        }
    }

    @Override
    public TreeNode remove(int index) {
        TreeNode node = get(index);
        node.setParent(null);
        super.remove(index);
        updateRowKeys(parent);
        updateSelectionState(parent);
        return node;
    }

    @Override
    public boolean remove(Object object) {
        TreeNode node = (TreeNode) object;
        if (node == null) {
            throw new NullPointerException();
        }

        if (super.indexOf(node) != -1) {
            node.clearParent();
        }

        if (super.remove(node)) {
            updateRowKeys(parent);
            updateSelectionState(parent);
            return true;
        }
        else {
            return false;
        }
    }

    private void updateRowKeys(TreeNode node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                updateRowKeys(childNode);
            }
        }
    }

    private void updateSelectionState(TreeNode node) {
        boolean allChildrenSelected = true;
        boolean partialSelected = false;

        for (int i = 0; i < node.getChildren().size(); i++) {
            TreeNode childNode = node.getChildren().get(i);

            boolean childSelected = childNode.isSelected();
            boolean childPartialSelected = childNode.isPartialSelected();
            allChildrenSelected = allChildrenSelected && childSelected;
            partialSelected = partialSelected || childSelected || childPartialSelected;
        }

        ((CheckboxTreeNode) node).setSelected(allChildrenSelected, false);

        if (allChildrenSelected) {
            node.setPartialSelected(false);
        }
        else {
            node.setPartialSelected(partialSelected);
        }

        TreeNode parentNode = node.getParent();
        if (parentNode != null) {
            updateSelectionState(parentNode);
        }
    }
}
