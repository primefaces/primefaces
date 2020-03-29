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
package org.primefaces.component.signature;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;

public abstract class SignatureBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SignatureRenderer";

    public enum PropertyKeys {

        widgetVar,
        backgroundColor,
        color,
        thickness,
        style,
        styleClass,
        readonly,
        guideline,
        guidelineColor,
        guidelineOffset,
        guidelineIndent,
        onchange,
        base64Value
    }

    public SignatureBase() {
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

    public String getBackgroundColor() {
        return (String) getStateHelper().eval(PropertyKeys.backgroundColor, null);
    }

    public void setBackgroundColor(String backgroundColor) {
        getStateHelper().put(PropertyKeys.backgroundColor, backgroundColor);
    }

    public String getColor() {
        return (String) getStateHelper().eval(PropertyKeys.color, null);
    }

    public void setColor(String color) {
        getStateHelper().put(PropertyKeys.color, color);
    }

    public int getThickness() {
        return (Integer) getStateHelper().eval(PropertyKeys.thickness, 2);
    }

    public void setThickness(int thickness) {
        getStateHelper().put(PropertyKeys.thickness, thickness);
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

    public boolean isReadonly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.readonly, false);
    }

    public void setReadonly(boolean readonly) {
        getStateHelper().put(PropertyKeys.readonly, readonly);
    }

    public boolean isGuideline() {
        return (Boolean) getStateHelper().eval(PropertyKeys.guideline, false);
    }

    public void setGuideline(boolean guideline) {
        getStateHelper().put(PropertyKeys.guideline, guideline);
    }

    public String getGuidelineColor() {
        return (String) getStateHelper().eval(PropertyKeys.guidelineColor, null);
    }

    public void setGuidelineColor(String guidelineColor) {
        getStateHelper().put(PropertyKeys.guidelineColor, guidelineColor);
    }

    public int getGuidelineOffset() {
        return (Integer) getStateHelper().eval(PropertyKeys.guidelineOffset, 25);
    }

    public void setGuidelineOffset(int guidelineOffset) {
        getStateHelper().put(PropertyKeys.guidelineOffset, guidelineOffset);
    }

    public int getGuidelineIndent() {
        return (Integer) getStateHelper().eval(PropertyKeys.guidelineIndent, 10);
    }

    public void setGuidelineIndent(int guidelineIndent) {
        getStateHelper().put(PropertyKeys.guidelineIndent, guidelineIndent);
    }

    public String getOnchange() {
        return (String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(String onchange) {
        getStateHelper().put(PropertyKeys.onchange, onchange);
    }

    public String getBase64Value() {
        return (String) getStateHelper().eval(PropertyKeys.base64Value, null);
    }

    public void setBase64Value(String base64Value) {
        getStateHelper().put(PropertyKeys.base64Value, base64Value);
    }
}