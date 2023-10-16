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
package org.primefaces.selenium.component;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.model.tree.TreeNode;

/**
 * Component wrapper for the PrimeFaces {@code p:tree}.
 */
public abstract class Tree extends AbstractComponent {

    public static final String CONTAINER_CLASS = "ui-tree";
    public static final String ROOT_NODES_CLASS = "ui-tree-container";
    public static final String TREE_NODE_CLASS = "ui-treenode";
    public static final String PARENT_NODE_CLASS = "ui-treenode-parent";
    public static final String CHILDREN_NODES_CLASS = "ui-treenode-children";
    public static final String NODE_CONTENT_CLASS = "ui-treenode-content";
    public static final String LEAF_NODE_CLASS = "ui-treenode-leaf";

    public static final String CHILD_SELECTOR = ".ui-tree-container>.ui-treenode";

    public List<TreeNode> getChildren() {
        return findElements(By.cssSelector(CHILD_SELECTOR)).stream()
                .map(e -> new TreeNode(e, CHILD_SELECTOR, this))
                .collect(Collectors.toList());
    }

}
