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
