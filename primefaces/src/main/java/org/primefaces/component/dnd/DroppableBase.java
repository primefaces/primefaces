/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.DragDropEvent;

import jakarta.faces.component.UIComponentBase;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "drop", event = DragDropEvent.class, description = "Fires when an element is dropped.", defaultEvent = true)
})
public abstract class DroppableBase extends UIComponentBase implements Widget, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DroppableRenderer";

    public DroppableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Id of the component to make droppable.")
    public abstract String getFor();

    @Property(defaultValue = "false", description = "Disables the droppable if set to true.")
    public abstract boolean isDisabled();

    @Property(description = "Style class to apply when an acceptable draggable is being hovered over the droppable.")
    public abstract String getHoverStyleClass();

    @Property(description = "Style class to apply when an acceptable draggable is being dragged.")
    public abstract String getActiveStyleClass();

    @Property(description = "Client side callback to execute when an accepted draggable is dropped on the droppable. " +
        "Function receives (event, ui) where ui.draggable, ui.helper, ui.position, and ui.offset are available.")
    public abstract String getOnDrop();

    @Property(implicitDefaultValue = "*", description = "Controls which draggable elements are accepted by the droppable. " +
        "Can be a selector string or a function that returns true if the draggable should be accepted.")
    public abstract String getAccept();

    @Property(implicitDefaultValue = "default", description = "Used to group sets of draggable and droppable items, " +
        "in addition to the accept option. A draggable with the same scope value as a droppable will be accepted.")
    public abstract String getScope();

    @Property(implicitDefaultValue = "intersect",
        description = "Specifies which mode to use for testing whether a draggable is hovering over a droppable. " +
            "Possible values: 'fit', 'intersect', 'pointer', 'touch'.")
    public abstract String getTolerance();

    @Property(description = "Id of the datasource component to bind drag data.")
    public abstract String getDatasource();

    @Property(defaultValue = "false", description = "By default, when an element is dropped on nested droppables, " +
        "each droppable will receive the element. Setting this to true prevents parent droppables from receiving the element.")
    public abstract boolean isGreedy();
}