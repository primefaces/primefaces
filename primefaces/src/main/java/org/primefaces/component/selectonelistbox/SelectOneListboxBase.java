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
package org.primefaces.component.selectonelistbox;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import jakarta.faces.component.html.HtmlSelectOneListbox;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "blur", event = AjaxBehaviorEvent.class, description = "Fires when the element loses focus."),
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the element's value changes."),
    @FacesBehaviorEvent(name = "valueChange", event = AjaxBehaviorEvent.class, description = "Fires when the element's value is changed.",
            defaultEvent = true),
    @FacesBehaviorEvent(name = "click", event = AjaxBehaviorEvent.class, description = "Fires when the element is clicked."),
    @FacesBehaviorEvent(name = "dblclick", event = AjaxBehaviorEvent.class, description = "Fires when the element is double-clicked."),
    @FacesBehaviorEvent(name = "focus", event = AjaxBehaviorEvent.class, description = "Fires when the element gains focus."),
    @FacesBehaviorEvent(name = "keydown", event = AjaxBehaviorEvent.class, description = "Fires when a key is pressed down on the element."),
    @FacesBehaviorEvent(name = "keypress", event = AjaxBehaviorEvent.class, description = "Fires when a key is pressed and released on the element."),
    @FacesBehaviorEvent(name = "keyup", event = AjaxBehaviorEvent.class, description = "Fires when a key is released on the element."),
    @FacesBehaviorEvent(name = "mousedown", event = AjaxBehaviorEvent.class, description = "Fires when a mouse button is pressed down on the element."),
    @FacesBehaviorEvent(name = "mousemove", event = AjaxBehaviorEvent.class, description = "Fires when the mouse is moved over the element."),
    @FacesBehaviorEvent(name = "mouseout", event = AjaxBehaviorEvent.class, description = "Fires when the mouse leaves the element."),
    @FacesBehaviorEvent(name = "mouseover", event = AjaxBehaviorEvent.class, description = "Fires when the mouse enters the element."),
    @FacesBehaviorEvent(name = "mouseup", event = AjaxBehaviorEvent.class, description = "Fires when a mouse button is released over the element."),
    @FacesBehaviorEvent(name = "select", event = AjaxBehaviorEvent.class, description = "Fires when some text is selected in the element."),
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "itemUnselect", event = UnselectEvent.class, description = "Fires when an item is unselected."),
    @FacesBehaviorEvent(name = "clear", event = AjaxBehaviorEvent.class, description = "Fires when the selection is cleared.")
})
public abstract class SelectOneListboxBase extends HtmlSelectOneListbox implements Widget, InputHolder, StyleAware, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectOneListboxRenderer";

    public SelectOneListboxBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Name of iterator to be used in custom content display.")
    public abstract String getVar();

    @Property(description = "Displays an input filter for the list.",
            defaultValue = "false")
    public abstract boolean isFilter();

    @Property(description = "Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.")
    public abstract String getFilterMatchMode();

    @Property(description = "Client side function to use in custom filterMatchMode.")
    public abstract String getFilterFunction();

    @Property(description = "Defines if filtering would be case sensitive.",
            defaultValue = "false")
    public abstract boolean isCaseSensitive();

    @Property(description = "Defines if filtering would be done using normalized values (accents will be removed from characters).",
            defaultValue = "false")
    public abstract boolean isFilterNormalize();

    @Property(description = "Defines the height of the scrollable area.",
            defaultValue = "Integer.MAX_VALUE")
    public abstract int getScrollHeight();

}