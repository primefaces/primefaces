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
package org.primefaces.component.editor;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;

public abstract class EditorBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.EditorRenderer";

    public enum PropertyKeys {

        widgetVar,
        controls,
        height,
        width,
        disabled,
        style,
        styleClass,
        onchange,
        maxlength
    }

    public EditorBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getControls() {
        return (String) getStateHelper().eval(PropertyKeys.controls, null);
    }

    public void setControls(String controls) {
        getStateHelper().put(PropertyKeys.controls, controls);
    }

    public int getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, Integer.MIN_VALUE);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public int getWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.width, Integer.MIN_VALUE);
    }

    public void setWidth(int width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public String getOnchange() {
        return (String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(String onchange) {
        getStateHelper().put(PropertyKeys.onchange, onchange);
    }

    public int getMaxlength() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxlength, Integer.MAX_VALUE);
    }

    public void setMaxlength(int maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, maxlength);
    }
}