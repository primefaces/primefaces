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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.util.SerializableFunction;

public class LazyTreeNode<T> extends DefaultTreeNode<T> {

    private SerializableFunction<T, List<T>> loadFunction;
    private SerializableFunction<T, Boolean> leafFunction;
    private boolean loaded;

    // serialization
    public LazyTreeNode() {
    }

    public LazyTreeNode(T data,
            SerializableFunction<T, List<T>> loadFunction,
            SerializableFunction<T, Boolean> leafFunction) {
        super(data);
        this.loadFunction = loadFunction;
        this.leafFunction = leafFunction;
    }

    @Override
    public List<TreeNode<T>> getChildren() {
        if (isLeaf()) {
            return Collections.emptyList();
        }

        lazyLoad();

        return super.getChildren();
    }

    @Override
    public int getChildCount() {
        if (isLeaf()) {
            return 0;
        }

        lazyLoad();

        return super.getChildCount();
    }

    @Override
    public boolean isLeaf() {
        return leafFunction.apply(getData());
    }

    public boolean isLoaded() {
        return loaded;
    }

    protected void lazyLoad() {
        if (!loaded) {
            loaded = true;

            List<T> childData = loadFunction.apply(getData());
            List<LazyTreeNode<T>> childNodes = childData
                    .stream()
                    .map(f -> new LazyTreeNode<>(f, loadFunction, leafFunction))
                    .collect(Collectors.toList());
            super.getChildren().addAll(childNodes);
        }
    }

    @Override
    protected List<TreeNode<T>> initChildren() {
        return new LazyTreeNodeChildren(this);
    }

    public static class LazyTreeNodeChildren<T> extends TreeNodeChildren<T> {

        // serialization
        public LazyTreeNodeChildren() {
        }

        public LazyTreeNodeChildren(LazyTreeNode parent) {
            super(parent);
        }

        @Override
        protected void updateRowKeys(TreeNode node) {
            if (((LazyTreeNode) node).loaded) {
                super.updateRowKeys(node);
            }
        }

        @Override
        protected void updateRowKeys(int index, TreeNode node) {
            if (((LazyTreeNode) node).loaded) {
                super.updateRowKeys(index, node);
            }
        }

        @Override
        protected void updateRowKeys(TreeNode node, TreeNode childNode, int i) {
            if (((LazyTreeNode) node).loaded) {
                super.updateRowKeys(node, childNode, i);
            }
        }
    }
}
