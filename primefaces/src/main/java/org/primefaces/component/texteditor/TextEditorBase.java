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
package org.primefaces.component.texteditor;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import java.util.List;

import jakarta.faces.component.UIInput;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "blur", event = AjaxBehaviorEvent.class, description = "Fires when the element loses focus."),
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the element's value changes."),
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
})
public abstract class TextEditorBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TextEditorRenderer";

    public TextEditorBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Height of the editor.")
    public abstract String getHeight();

    @Property(description = "Whether to instantiate the editor to read-only mode.", defaultValue = "false")
    public abstract boolean isReadonly();

    @Property(description = "Disables the editor.", defaultValue = "false")
    public abstract boolean isDisabled();

    @Property(description = "Placeholder text to show when editor is empty.")
    public abstract String getPlaceholder();

    @Property(description = "Whether the toolbar of the editor is visible.", defaultValue = "true")
    public abstract boolean isToolbarVisible();

    @Property(description = "Whether to allow blocks to be included.", defaultValue = "true")
    public abstract boolean isAllowBlocks();

    @Property(description = "Whether to allow formatting to be included.", defaultValue = "true")
    public abstract boolean isAllowFormatting();

    @Property(description = "Whether to allow links to be included.", defaultValue = "true")
    public abstract boolean isAllowLinks();

    @Property(description = "Whether to allow styles to be included.", defaultValue = "true")
    public abstract boolean isAllowStyles();

    @Property(description = "Whether to allow images to be included.", defaultValue = "true")
    public abstract boolean isAllowImages();

    @Property(description = "Define a list of formats to allow in the editor. By default all formats are allowed.")
    public abstract List getFormats();

    @Property(description = "Secure the component with the HTML Sanitizer library on the classpath.", defaultValue = "true")
    public abstract boolean isSecure();
}