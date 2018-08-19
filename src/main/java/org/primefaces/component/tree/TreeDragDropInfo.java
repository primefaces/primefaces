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
package org.primefaces.component.tree;

import javax.faces.component.UIComponent;

import org.primefaces.model.TreeNode;

public class TreeDragDropInfo {

    private final UIComponent component;
    private final TreeNode dropNode;
    private final int dropIndex;
    private final boolean droppedNodeCopy;
    private TreeNode dragNode;
    private TreeNode[] dragNodes;

    public TreeDragDropInfo(UIComponent component, TreeNode dragNode, TreeNode dropNode, int dropIndex, boolean droppedNodeCopy) {
        this.component = component;
        this.dragNode = dragNode;
        this.dropNode = dropNode;
        this.dropIndex = dropIndex;
        this.droppedNodeCopy = droppedNodeCopy;
    }

    public TreeDragDropInfo(UIComponent component, TreeNode[] dragNodes, TreeNode dropNode, int dropIndex, boolean droppedNodeCopy) {
        this.component = component;
        this.dragNodes = dragNodes;
        this.dropNode = dropNode;
        this.dropIndex = dropIndex;
        this.droppedNodeCopy = droppedNodeCopy;
    }

    public UIComponent getComponent() {
        return component;
    }

    public TreeNode getDragNode() {
        return dragNode;
    }

    public TreeNode[] getDragNodes() {
        return dragNodes;
    }

    public TreeNode getDropNode() {
        return dropNode;
    }

    public int getDropIndex() {
        return dropIndex;
    }

    public boolean isDroppedNodeCopy() {
        return droppedNodeCopy;
    }
}
