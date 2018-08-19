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
package org.primefaces.component.editor;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class EditorBase extends UIInput implements Widget {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getControls() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.controls, null);
    }

    public void setControls(java.lang.String _controls) {
        getStateHelper().put(PropertyKeys.controls, _controls);
    }

    public int getHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.height, java.lang.Integer.MIN_VALUE);
    }

    public void setHeight(int _height) {
        getStateHelper().put(PropertyKeys.height, _height);
    }

    public int getWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.width, java.lang.Integer.MIN_VALUE);
    }

    public void setWidth(int _width) {
        getStateHelper().put(PropertyKeys.width, _width);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
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

    public java.lang.String getOnchange() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(java.lang.String _onchange) {
        getStateHelper().put(PropertyKeys.onchange, _onchange);
    }

    public int getMaxlength() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxlength, java.lang.Integer.MAX_VALUE);
    }

    public void setMaxlength(int _maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, _maxlength);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}