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
package org.primefaces.component.organigram;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "select", event = OrganigramNodeSelectEvent.class, description = "Fires when a node is selected.", defaultEvent = true),
    @FacesBehaviorEvent(name = "expand", event = OrganigramNodeExpandEvent.class, description = "Fires when a node is expanded."),
    @FacesBehaviorEvent(name = "collapse", event = OrganigramNodeCollapseEvent.class, description = "Fires when a node is collapsed."),
    @FacesBehaviorEvent(name = "dragdrop", event = OrganigramNodeDragDropEvent.class, description = "Fires when a node is dragged and dropped."),
    @FacesBehaviorEvent(name = "contextmenu", event = OrganigramNodeSelectEvent.class, description = "Fires when context menu is triggered on a node.")
})
public abstract class OrganigramBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OrganigramRenderer";

    public OrganigramBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    @Property(description = "The root node of the organigram.")
    public abstract org.primefaces.model.OrganigramNode getValue();

    @Property(description = "Name of the iterator variable used to reference each node in the organigram.")
    public abstract String getVar();

    @Property(description = "Currently selected node.")
    public abstract org.primefaces.model.OrganigramNode getSelection();

    @Property(defaultValue = "10", description = "Height of the connector for leaf nodes in pixels.")
    public abstract int getLeafNodeConnectorHeight();

    @Property(defaultValue = "false", description = "Enables zoom functionality.")
    public abstract boolean isZoom();

    @Property(defaultValue = "false", description = "Automatically scrolls to the selected node.")
    public abstract boolean isAutoScrollToSelection();
}