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
package org.primefaces.component.progressbar;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ProgressBarBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ProgressBarRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        disabled,
        ajax,
        interval,
        style,
        styleClass,
        labelTemplate,
        displayOnly,
        global,
        mode;
    }

    public ProgressBarBase() {
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

    public int getValue() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.value, 0);
    }

    public void setValue(int _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public boolean isAjax() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.ajax, false);
    }

    public void setAjax(boolean _ajax) {
        getStateHelper().put(PropertyKeys.ajax, _ajax);
    }

    public int getInterval() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.interval, 3000);
    }

    public void setInterval(int _interval) {
        getStateHelper().put(PropertyKeys.interval, _interval);
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

    public java.lang.String getLabelTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.labelTemplate, null);
    }

    public void setLabelTemplate(java.lang.String _labelTemplate) {
        getStateHelper().put(PropertyKeys.labelTemplate, _labelTemplate);
    }

    public boolean isDisplayOnly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.displayOnly, false);
    }

    public void setDisplayOnly(boolean _displayOnly) {
        getStateHelper().put(PropertyKeys.displayOnly, _displayOnly);
    }

    public boolean isGlobal() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.global, true);
    }

    public void setGlobal(boolean _global) {
        getStateHelper().put(PropertyKeys.global, _global);
    }

    public java.lang.String getMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.mode, "determinate");
    }

    public void setMode(java.lang.String _mode) {
        getStateHelper().put(PropertyKeys.mode, _mode);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}