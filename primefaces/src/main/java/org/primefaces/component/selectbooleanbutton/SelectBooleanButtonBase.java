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
package org.primefaces.component.selectbooleanbutton;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.html.HtmlSelectBooleanCheckbox;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "blur", event = AjaxBehaviorEvent.class, description = "Fires when the element loses focus."),
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the element's value changes."),
    @FacesBehaviorEvent(name = "click", event = AjaxBehaviorEvent.class, description = "Fires when the element is clicked."),
    @FacesBehaviorEvent(name = "valueChange", event = AjaxBehaviorEvent.class, description = "Fires when the element's value is changed.", defaultEvent = true),
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
    @FacesBehaviorEvent(name = "select", event = AjaxBehaviorEvent.class, description = "Fires when some text is selected in the element.")
})
public abstract class SelectBooleanButtonBase extends HtmlSelectBooleanCheckbox implements Widget, StyleAware, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectBooleanButtonRenderer";

    public SelectBooleanButtonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Name of the client side widget.")
    public abstract String getWidgetVar();

    @Property(description = "Custom label for on state.")
    public abstract String getOnLabel();

    @Property(description = "Custom label for off state.")
    public abstract String getOffLabel();

    @Property(description = "The icon representing the \"On\" position.")
    public abstract String getOnIcon();

    @Property(description = "The icon representing the \"Off\" position.")
    public abstract String getOffIcon();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

}