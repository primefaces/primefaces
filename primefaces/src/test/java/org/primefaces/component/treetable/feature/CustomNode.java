package org.primefaces.component.treetable.feature;

import org.primefaces.model.TreeNode;

public class CustomNode<T> extends org.primefaces.model.DefaultTreeNode<T> {

    public CustomNode(String type, T data, TreeNode parent) {
        super(type, data, parent);
    }

    public CustomNode(CustomNode<T> other) {
        super(other.getType(), other.getData(), null);
    }
}
