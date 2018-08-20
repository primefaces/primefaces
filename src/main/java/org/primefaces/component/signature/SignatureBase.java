/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.signature;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SignatureBase extends UIInput implements Widget {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}