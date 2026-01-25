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
package org.primefaces.component.dnd;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class DraggableBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DraggableRenderer";

    public DraggableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "false", description = "When enabled, a proxy element is used for dragging instead of the element itself.")
    public abstract boolean isProxy();

    @Property(defaultValue = "false", description = "When enabled, the element can only be dragged, not dropped.")
    public abstract boolean isDragOnly();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "If set to true, the element will be returned to its start position when dragging stops.")
    public abstract boolean isRevert();

    @Property(defaultValue = "false", description = "When enabled, the draggable will snap to the edges of the nearest elements.")
    public abstract boolean isSnap();

    @Property(description = "Id of the component to make draggable.")
    public abstract String getFor();

    @Property(description = "Constrains dragging to either the horizontal (x) or vertical (y) axis. Possible values: 'x', 'y'.")
    public abstract String getAxis();

    @Property(description = "Constrains dragging to within the bounds of the specified element or region. " +
        "Can be a selector, element, string ('parent', 'document', 'window'), or array [x1, y1, x2, y2].")
    public abstract String getContainment();

    @Property(implicitDefaultValue = "original", description = "Allows for a helper element to be used for dragging display. " +
        "Possible values: 'original', 'clone', or a function that returns a DOM element.")
    public abstract String getHelper();

    @Property(implicitDefaultValue = "both", description = "Defines how snap elements are detected. Possible values: 'inner', 'outer', 'both'.")
    public abstract String getSnapMode();

    @Property(defaultValue = "20", description = "Distance in pixels from the snap element edges at which snapping should occur.")
    public abstract int getSnapTolerance();

    @Property(defaultValue = "-1", description = "Controls the z-index of the helper element during dragging.")
    public abstract int getZindex();

    @Property(description = "Restricts dragging start from specified elements. Dragging can only start if the mousedown occurs on the specified element(s).")
    public abstract String getHandle();

    @Property(defaultValue = "1.0", description = "Opacity for the helper while being dragged.")
    public abstract double getOpacity();

    @Property(description = "Controls the z-index of the group of elements for which the stack option is set.")
    public abstract String getStack();

    @Property(description = "Snaps the dragging helper to a grid, specified as [x,y] in pixels.")
    public abstract String getGrid();

    @Property(implicitDefaultValue = "default", description = "Used to group sets of draggable and droppable components.")
    public abstract String getScope();

    @Property(defaultValue = "crosshair", description = "CSS cursor to display when dragging.")
    public abstract String getCursor();

    @Property(description = "Id of the dashboard component to connect with.")
    public abstract String getDashboard();

    @Property(implicitDefaultValue = "parent",
        description = "Which element the draggable helper should be appended to while dragging. Can be a selector, element, jQuery object, or 'parent'.")
    public abstract String getAppendTo();

    @Property(description = "Client side callback to execute when dragging starts.")
    public abstract String getOnStart();

    @Property(description = "Client side callback to execute when dragging stops.")
    public abstract String getOnStop();

    @Property(description = "Client side callback to execute during dragging, immediately before the current move happens." +
        " Function receives (event, ui) where ui.helper, ui.position, and ui.offset are available. " +
        "The values in ui.position may be changed to modify where the element will be positioned.")
    public abstract String getOnDrag();

    @Property(implicitDefaultValue = "input,textarea,button,select,option", description = "Prevents dragging from starting on specified elements.")
    public abstract String getCancel();
}