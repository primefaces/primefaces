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
package org.primefaces.component.api;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Property;

import jakarta.faces.convert.Converter;
import jakarta.faces.event.AjaxBehaviorEvent;

/**
 * Interface for components that are aware of typical input behaviors and attributes.
 * <p>
 * Implementing this interface allows a component to declare and document standard input-related properties, such as access key,
 * maximum length, converter, accessibility features, keyboard input modes, and event scripts.
 * <p>
 * The presence of this interface indicates that the implementing component behaves similarly to HTML input elements,
 * providing customizable interaction and validation mechanisms.
 */
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "blur", event = AjaxBehaviorEvent.class, description = "Fires when the element loses focus."),
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the element's value changes."),
    @FacesBehaviorEvent(name = "click", event = AjaxBehaviorEvent.class, description = "Fires when the element is clicked."),
    @FacesBehaviorEvent(name = "contextmenu", event = AjaxBehaviorEvent.class, description = "Fires when the context menu is triggered on the element."),
    @FacesBehaviorEvent(name = "copy", event = AjaxBehaviorEvent.class, description = "Fires when a copy operation occurs on the element."),
    @FacesBehaviorEvent(name = "cut", event = AjaxBehaviorEvent.class, description = "Fires when a cut operation occurs on the element."),
    @FacesBehaviorEvent(name = "dblclick", event = AjaxBehaviorEvent.class, description = "Fires when the element is double-clicked."),
    @FacesBehaviorEvent(name = "drag", event = AjaxBehaviorEvent.class, description = "Fires when an element or text selection is being dragged."),
    @FacesBehaviorEvent(name = "dragend", event = AjaxBehaviorEvent.class, description = "Fires when a drag operation is being ended."),
    @FacesBehaviorEvent(name = "dragenter", event = AjaxBehaviorEvent.class, description = "Fires when a dragged element enters a valid drop target."),
    @FacesBehaviorEvent(name = "dragleave", event = AjaxBehaviorEvent.class, description = "Fires when a dragged element leaves a valid drop target."),
    @FacesBehaviorEvent(name = "dragover", event = AjaxBehaviorEvent.class, description = "Fires when a dragged element is over a valid drop target."),
    @FacesBehaviorEvent(name = "dragstart", event = AjaxBehaviorEvent.class, description = "Fires when the user starts dragging an element."),
    @FacesBehaviorEvent(name = "drop", event = AjaxBehaviorEvent.class, description = "Fires when an element is dropped on a valid drop target."),
    @FacesBehaviorEvent(name = "focus", event = AjaxBehaviorEvent.class, description = "Fires when the element gains focus."),
    @FacesBehaviorEvent(name = "input", event = AjaxBehaviorEvent.class, description = "Fires when the value of an input element is changed."),
    @FacesBehaviorEvent(name = "invalid", event = AjaxBehaviorEvent.class, description = "Fires when the element is invalid."),
    @FacesBehaviorEvent(name = "keydown", event = AjaxBehaviorEvent.class, description = "Fires when a key is pressed down on the element."),
    @FacesBehaviorEvent(name = "keypress", event = AjaxBehaviorEvent.class, description = "Fires when a key is pressed and released on the element."),
    @FacesBehaviorEvent(name = "keyup", event = AjaxBehaviorEvent.class, description = "Fires when a key is released on the element."),
    @FacesBehaviorEvent(name = "mousedown", event = AjaxBehaviorEvent.class, description = "Fires when a mouse button is pressed down on the element."),
    @FacesBehaviorEvent(name = "mousemove", event = AjaxBehaviorEvent.class, description = "Fires when the mouse is moved over the element."),
    @FacesBehaviorEvent(name = "mouseout", event = AjaxBehaviorEvent.class, description = "Fires when the mouse leaves the element."),
    @FacesBehaviorEvent(name = "mouseover", event = AjaxBehaviorEvent.class, description = "Fires when the mouse enters the element."),
    @FacesBehaviorEvent(name = "mouseup", event = AjaxBehaviorEvent.class, description = "Fires when a mouse button is released over the element."),
    @FacesBehaviorEvent(name = "paste", event = AjaxBehaviorEvent.class, description = "Fires when a paste operation occurs on the element."),
    @FacesBehaviorEvent(name = "reset", event = AjaxBehaviorEvent.class, description = "Fires when the form is reset."),
    @FacesBehaviorEvent(name = "scroll", event = AjaxBehaviorEvent.class, description = "Fires when the element is scrolled."),
    @FacesBehaviorEvent(name = "search", event = AjaxBehaviorEvent.class, description = "Fires when a search is initiated on the element."),
    @FacesBehaviorEvent(name = "select", event = AjaxBehaviorEvent.class, description = "Fires when some text is selected in the element."),
    @FacesBehaviorEvent(name = "valueChange", event = AjaxBehaviorEvent.class, description = "Fires when the element's value is changed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "wheel", event = AjaxBehaviorEvent.class, description = "Fires when the wheel event is triggered on the element.")
})
public interface InputAware extends StyleAware, RTLAware {

    @Property(description = "Specifies a shortcut key to activate or focus the element.", callSuper = true)
    String getAccesskey();

    @Property(description = "Identifies the element(s) that describe the object, enhancing accessibility.")
    String getAriaDescribedBy();

    @Property(description = "Specifies a converter instance to be used with the component.", callSuper = true)
    @SuppressWarnings("rawtypes")
    Converter getConverter();

    @Property(description = "Custom message to display when conversion fails.", callSuper = true)
    String getConverterMessage();

    @Property(description = "Indicates whether the input is disabled.", defaultValue = "false", callSuper = true)
    boolean isDisabled();

    @Property(description = "Should component process its value immediately in the Apply Request Values phase.", defaultValue = "false", callSuper = true)
    boolean isImmediate();

    @Property(description = "Indicates whether the local value of the component has been set.", defaultValue = "false", callSuper = true)
    boolean isLocalValueSet();

    @Property(description = "Specifies the type of data expected in the input, aiding virtual keyboards.")
    String getInputmode();

    @Property(description = "Defines a label for the input element.", callSuper = true)
    String getLabel();

    @Property(description = "Specifies the language of the element's content.", callSuper = true)
    String getLang();

    @Property(description = "Defines the maximum number of characters allowed in the input.", defaultValue = "Integer.MIN_VALUE")
    int getMaxlength();

    @Property(description = "Script to execute when the element is double-clicked.")
    String getOndblclick();

    @Property(description = "Script to execute when an element is dragged.")
    String getOndrag();

    @Property(description = "Script to execute at the end of a drag operation.")
    String getOndragend();

    @Property(description = "Script to execute when a dragged element enters a valid drop target.")
    String getOndragenter();

    @Property(description = "Script to execute when a dragged element leaves a valid drop target.")
    String getOndragleave();

    @Property(description = "Script to execute when a dragged element is over a valid drop target.")
    String getOndragover();

    @Property(description = "Script to execute at the start of a drag operation.")
    String getOndragstart();

    @Property(description = "Script to execute when a dragged element is dropped.")
    String getOndrop();

    @Property(description = "Script to execute when the element loses focus.")
    String getOnblur();

    @Property(description = "Script to execute when the element's value changes.")
    String getOnchange();

    @Property(description = "Script to execute when the element is clicked.")
    String getOnclick();

    @Property(description = "Script to execute when the context menu is triggered.")
    String getOncontextmenu();

    @Property(description = "Script to execute when content is copied from the element.")
    String getOncopy();

    @Property(description = "Script to execute when content is cut from the element.")
    String getOncut();

    @Property(description = "Script to execute when the element gains focus.")
    String getOnfocus();

    @Property(description = "Script to execute when the element receives user input.")
    String getOninput();

    @Property(description = "Script to execute when the element's value is invalid.")
    String getOninvalid();

    @Property(description = "Script to execute when a key is pressed down.")
    String getOnkeydown();

    @Property(description = "Script to execute when a key is pressed and released.")
    String getOnkeypress();

    @Property(description = "Script to execute when a key is released.")
    String getOnkeyup();

    @Property(description = "Script to execute when a mouse button is pressed down.")
    String getOnmousedown();

    @Property(description = "Script to execute when the mouse is moved.")
    String getOnmousemove();

    @Property(description = "Script to execute when the mouse leaves the element.")
    String getOnmouseout();

    @Property(description = "Script to execute when the mouse enters the element.")
    String getOnmouseover();

    @Property(description = "Script to execute when a mouse button is released.")
    String getOnmouseup();

    @Property(description = "Script to execute when content is pasted into the element.")
    String getOnpaste();

    @Property(description = "Script to execute when the form is reset.")
    String getOnreset();

    @Property(description = "Script to execute when the element is scrolled.")
    String getOnscroll();

    @Property(description = "Script to execute when a search is performed.")
    String getOnsearch();

    @Property(description = "Script to execute when text is selected.")
    String getOnselect();

    @Property(description = "Script to execute when the mouse wheel is used.")
    String getOnwheel();

    @Property(description = "Specifies a short hint describing the expected value of the input.")
    String getPlaceholder();

    @Property(description = "Indicates whether the input is read-only.", defaultValue = "false", callSuper = true)
    boolean isReadonly();

    @Property(description = "Custom message to display when the required validation fails.", callSuper = true)
    String getRequiredMessage();

    @Property(description = "Specifies that the input must be filled out before submitting the form.", defaultValue = "false", callSuper = true)
    boolean isRequired();

    @Property(description = "Defines the role of the element for accessibility purposes.", callSuper = true)
    String getRole();

    @Property(description = "Sets the tab order of the element.", callSuper = true)
    String getTabindex();

    @Property(description = "Provides additional information about the element, typically displayed as a tooltip.", callSuper = true)
    String getTitle();

    @Property(description = "Indicates whether the input's value is valid.", defaultValue = "true", callSuper = true)
    boolean isValid();

    @Property(description = "Custom message to display when validation fails.", callSuper = true)
    String getValidatorMessage();

    @Property(description = "Holds the current value of the input.", callSuper = true)
    Object getValue();
}
