/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.data.tree;

import java.util.Collections;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.primefaces.model.TreeNodeChildren;

public class LazyLoadingTreeNode extends DefaultTreeNode<FileInfo> {

    private Function<String, List<FileInfo>> loadFunction;
    private boolean lazyLoaded;

    public LazyLoadingTreeNode(FileInfo data, Function<String, List<FileInfo>> loadFunction) {
        super(data);
        this.loadFunction = loadFunction;
    }

    @Override
    public List<TreeNode<FileInfo>> getChildren() {
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
        return !getData().isDirectory();
    }

    private void lazyLoad() {
        if (!lazyLoaded) {
            lazyLoaded = true;

            String parentId = ((FileInfo) getData()).getPath();

            List<LazyLoadingTreeNode> childNodes = loadFunction.apply(parentId).stream()
                    .map(f -> new LazyLoadingTreeNode(f, loadFunction)).collect(Collectors.toList());
            super.getChildren().addAll(childNodes);
        }
    }

    @Override
    protected List<TreeNode<FileInfo>> initChildren() {
        return new LazyLoadingTreeNodeChildren(this);
    }

    public static class LazyLoadingTreeNodeChildren extends TreeNodeChildren<FileInfo> {

        public LazyLoadingTreeNodeChildren(LazyLoadingTreeNode parent) {
            super(parent);
        }

        @Override
        protected void updateRowKeys(TreeNode<?> node) {
            if (((LazyLoadingTreeNode) node).lazyLoaded) {
                super.updateRowKeys(node);
            }
        }

        @Override
        protected void updateRowKeys(int index, TreeNode<?> node) {
            if (((LazyLoadingTreeNode) node).lazyLoaded) {
                super.updateRowKeys(index, node);
            }
        }

        @Override
        protected void updateRowKeys(TreeNode<?> node, TreeNode<?> childNode, int i) {
            if (((LazyLoadingTreeNode) node).lazyLoaded) {
                super.updateRowKeys(node, childNode, i);
            }
        }
    }
}
