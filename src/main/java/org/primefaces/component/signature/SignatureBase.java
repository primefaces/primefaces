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

import org.primefaces.util.ComponentUtils;


abstract class SignatureBase extends UIInput implements org.primefaces.component.api.Widget {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getBackgroundColor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.backgroundColor, null);
    }

    public void setBackgroundColor(java.lang.String _backgroundColor) {
        getStateHelper().put(PropertyKeys.backgroundColor, _backgroundColor);
    }

    public java.lang.String getColor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.color, null);
    }

    public void setColor(java.lang.String _color) {
        getStateHelper().put(PropertyKeys.color, _color);
    }

    public int getThickness() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.thickness, 2);
    }

    public void setThickness(int _thickness) {
        getStateHelper().put(PropertyKeys.thickness, _thickness);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public boolean isReadonly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.readonly, false);
    }

    public void setReadonly(boolean _readonly) {
        getStateHelper().put(PropertyKeys.readonly, _readonly);
    }

    public boolean isGuideline() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.guideline, false);
    }

    public void setGuideline(boolean _guideline) {
        getStateHelper().put(PropertyKeys.guideline, _guideline);
    }

    public java.lang.String getGuidelineColor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.guidelineColor, null);
    }

    public void setGuidelineColor(java.lang.String _guidelineColor) {
        getStateHelper().put(PropertyKeys.guidelineColor, _guidelineColor);
    }

    public int getGuidelineOffset() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.guidelineOffset, 25);
    }

    public void setGuidelineOffset(int _guidelineOffset) {
        getStateHelper().put(PropertyKeys.guidelineOffset, _guidelineOffset);
    }

    public int getGuidelineIndent() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.guidelineIndent, 10);
    }

    public void setGuidelineIndent(int _guidelineIndent) {
        getStateHelper().put(PropertyKeys.guidelineIndent, _guidelineIndent);
    }

    public java.lang.String getOnchange() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(java.lang.String _onchange) {
        getStateHelper().put(PropertyKeys.onchange, _onchange);
    }

    public java.lang.String getBase64Value() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.base64Value, null);
    }

    public void setBase64Value(java.lang.String _base64Value) {
        getStateHelper().put(PropertyKeys.base64Value, _base64Value);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}