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
package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.model.TreeNode;

public class TreeDragDropEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private TreeNode dragNode;

    private TreeNode[] dragNodes;

    private TreeNode dropNode;

    private int dropIndex;

    private boolean droppedNodeCopy;

    public TreeDragDropEvent(UIComponent component, Behavior behavior, TreeNode dragNode, TreeNode dropNode, int dropIndex, boolean droppedNodeCopy) {
        super(component, behavior);
        this.dragNode = dragNode;
        this.dropNode = dropNode;
        this.dropIndex = dropIndex;
        this.droppedNodeCopy = droppedNodeCopy;
    }

    public TreeDragDropEvent(UIComponent component, Behavior behavior, TreeNode[] dragNodes, TreeNode dropNode, int dropIndex, boolean droppedNodeCopy) {
        super(component, behavior);
        this.dragNodes = dragNodes;
        this.dropNode = dropNode;
        this.dropIndex = dropIndex;
        this.droppedNodeCopy = droppedNodeCopy;
    }

    public TreeNode getDragNode() {
        return dragNode;
    }

    public TreeNode getDropNode() {
        return dropNode;
    }

    public TreeNode[] getDragNodes() {
        return dragNodes;
    }

    public int getDropIndex() {
        return dropIndex;
    }

    public boolean isDroppedNodeCopy() {
        return droppedNodeCopy;
    }
}
