/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

public class TreeNodeChildren<T> extends TreeNodeList<T> {

    private static final long serialVersionUID = 1L;

    private TreeNode<T> parent;

    public TreeNodeChildren(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    private void eraseParent(TreeNode<T> node) {
        TreeNode parentNode = node.getParent();
        if (parentNode != null) {
            parentNode.getChildren().remove(node);
            node.setParent(null);
        }
    }

    @Override
    public boolean add(TreeNode<T> node) {
        if (node == null) {
            throw new NullPointerException();
        }

        eraseParent(node);
        boolean result = super.add(node);
        node.setParent(parent);
        updateRowKeys(parent.getChildCount() - 1, parent);
        return result;
    }

    @Override
    public void add(int index, TreeNode<T> node) {
        if (node == null) {
            throw new NullPointerException();
        }
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }

        eraseParent(node);
        super.add(index, node);
        node.setParent(parent);
        updateRowKeys(index, parent);
    }

    @Override
    public boolean addAll(Collection<? extends TreeNode<T>> collection) {
        Iterator<TreeNode<T>> elements = (new ArrayList<TreeNode<T>>(collection)).iterator();
        int size = this.size();

        boolean changed = false;
        while (elements.hasNext()) {
            TreeNode node = elements.next();
            if (node == null) {
                throw new NullPointerException();
            }

            eraseParent(node);
            super.add(node);
            node.setParent(parent);
            changed = true;
        }

        if (changed) {
            updateRowKeys(size, parent);
        }

        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends TreeNode<T>> collection) {
        Iterator<TreeNode<T>> elements = (new ArrayList<TreeNode<T>>(collection)).iterator();

        boolean changed = false;
        while (elements.hasNext()) {
            TreeNode node = elements.next();
            if (node == null) {
                throw new NullPointerException();
            }

            eraseParent(node);
            super.add(index++, node);
            node.setParent(parent);
            changed = true;
        }

        if (changed) {
            updateRowKeys(index, parent);
        }

        return changed;
    }

    @Override
    public TreeNode set(int index, TreeNode node) {
        if (node == null) {
            throw new NullPointerException();
        }
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        if (!parent.equals(node.getParent())) {
            eraseParent(node);
        }

        TreeNode previous = get(index);
        super.set(index, node);
        node.setParent(parent);
        updateRowKeys(parent, node, index);
        return previous;
    }

    @Override
    public TreeNode remove(int index) {
        TreeNode node = get(index);
        node.setParent(null);
        super.remove(index);
        updateRowKeys(index, parent);
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

        int index = super.indexOf(node);
        if (super.remove(node)) {
            updateRowKeys(index, parent);
            return true;
        }
        else {
            return false;
        }
    }

    protected void updateRowKeys(TreeNode<?> node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);

                updateRowKeys(node, childNode, i);
            }
        }
    }

    protected void updateRowKeys(int index, TreeNode<?> node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = index; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                updateRowKeys(node, childNode, i);
            }
        }
    }

    protected void updateRowKeys(TreeNode<?> node, TreeNode<?> childNode, int i) {
        String childRowKey = node.getParent() == null ? String.valueOf(i) : node.getRowKey() + "_" + i;
        childNode.setRowKey(childRowKey);
        this.updateRowKeys(childNode);
    }

}
