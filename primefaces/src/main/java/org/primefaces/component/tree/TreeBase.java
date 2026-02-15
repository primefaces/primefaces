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
package org.primefaces.component.tree;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UITreeImpl;
import org.primefaces.component.api.Widget;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TreeDragDropEvent;

import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "select", event = NodeSelectEvent.class, description = "Fires when a node is selected."),
    @FacesBehaviorEvent(name = "unselect", event = NodeUnselectEvent.class, description = "Fires when a node is unselected."),
    @FacesBehaviorEvent(name = "expand", event = NodeExpandEvent.class, description = "Fires when a node is expanded."),
    @FacesBehaviorEvent(name = "collapse", event = NodeCollapseEvent.class, description = "Fires when a node is collapsed."),
    @FacesBehaviorEvent(name = "dragdrop", event = TreeDragDropEvent.class, description = "Fires when a node is dragged and dropped."),
    @FacesBehaviorEvent(name = "contextMenu", event = NodeSelectEvent.class, description = "Fires when context menu is invoked on a node."),
    @FacesBehaviorEvent(name = "filter", event = AjaxBehaviorEvent.class, description = "Fires when data is filtered.")
})
public abstract class TreeBase extends UITreeImpl implements Widget, RTLAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TreeRenderer";

    public TreeBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Object getLocalSelectedNodes() {
        return getStateHelper().get(UITreeImpl.PropertyKeys.selection);
    }

    @Property(defaultValue = "false", description = "Specifies the ajax/client toggleMode.")
    public abstract boolean isDynamic();

    @Property(defaultValue = "true", description = "Specifies caching on dynamically loaded nodes. When set to true expanded nodes will be kept in memory.")
    public abstract boolean isCache();

    @Property(description = "Javascript event to process when a tree node is clicked.")
    public abstract String getOnNodeClick();

    @Property(defaultValue = "true", description = "Highlights nodes on hover when selection is enabled, set to false to disable highlighting.")
    public abstract boolean isHighlight();

    @Property(description = "Unique key of the data presented by tree nodes.")
    public abstract Object getDatakey();

    @Property(defaultValue = "false", description = "When enabled, Displays slide effect during toggling of a node.")
    public abstract boolean isAnimate();

    @Property(defaultValue = "vertical", description = "Defines the orientation of the tree, valid values are \"vertical\" and \"horizontal\".")
    public abstract String getOrientation();

    @Property(defaultValue = "false", description = "Controls dragging of tree nodes.")
    public abstract boolean isDraggable();

    @Property(defaultValue = "false", description = "Controls dropping of tree nodes.")
    public abstract boolean isDroppable();

    @Property(description = "Scope key to group a set of tree components for transferring nodes using drag and drop.")
    public abstract String getDragdropScope();

    @Property(defaultValue = "self",
            description = "Defines parent-child relationship when a node is dragged, valid values are \"self\", \"parent\" and \"ancestor\".")
    public abstract String getDragMode();

    @Property(defaultValue = "none", description = "Defines parent-child restrictions when a node is dropped valid values are \"none\" and \"sibling\".")
    public abstract String getDropRestrict();

    @Property(defaultValue = "move",
            description = "When enabled and dropMode='move', the copy of the selected nodes can be dropped from a tree to another tree using Shift key.")
    public abstract String getDropMode();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract int getTabindex();

    @Property(description = "Property to be used for filtering.")
    public abstract Object getFilterBy();

    @Property(defaultValue = "startsWith", description = "Match mode for filtering.")
    public abstract String getFilterMatchMode();

    @Property(defaultValue = "false", description = "Disables the tree.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "When enabled, the selected multiple nodes can be dragged from a tree to another tree.")
    public abstract boolean isMultipleDrag();

    @Property(defaultValue = "false",
            description = "When enabled and dropMode='move', the copy of the selected nodes can be dropped from a tree to another tree using Shift key.")
    public abstract boolean isDropCopyNode();

    @Property(description = "Method returning whether the dragged node(s) can be dropped on the dropped node.")
    public abstract jakarta.el.MethodExpression getOnDrop();

    @Property(defaultValue = "lenient", description = "Mode for filtering valid values are \"lenient\" and \"strict\".")
    public abstract String getFilterMode();

    @Property(description = "Custom implementation to filter TreeNodes against a constraint.")
    public abstract jakarta.el.MethodExpression getFilterFunction();

    @Property(implicitDefaultValue = "keyup", description = "Client side event to invoke filtering."
            + " If \"enter\" it will only filter after ENTER key is pressed.")
    public abstract String getFilterEvent();

    @Property(defaultValue = "Integer.MAX_VALUE", implicitDefaultValue = "300",
            description = "Delay to wait in milliseconds before sending each filter query.")
    public abstract int getFilterDelay();

    @Property(description = "Placeholder for the filter input element.")
    public abstract java.lang.String getFilterPlaceholder();
}