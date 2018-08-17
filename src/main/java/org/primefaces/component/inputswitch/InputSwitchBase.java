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
package org.primefaces.component.inputswitch;

import javax.faces.component.UIInput;

import org.primefaces.util.ComponentUtils;


abstract class InputSwitchBase extends UIInput implements org.primefaces.component.api.Widget, javax.faces.component.behavior.ClientBehaviorHolder, org.primefaces.component.api.PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputSwitchRenderer";

    public enum PropertyKeys {

        widgetVar,
        onLabel,
        offLabel,
        label,
        disabled,
        onchange,
        style,
        styleClass,
        tabindex,
        showLabels,
        onfocus,
        onblur
    }

    public InputSwitchBase() {
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

    public java.lang.String getOnLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onLabel, "on");
    }

    public void setOnLabel(java.lang.String _onLabel) {
        getStateHelper().put(PropertyKeys.onLabel, _onLabel);
    }

    public java.lang.String getOffLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.offLabel, "off");
    }

    public void setOffLabel(java.lang.String _offLabel) {
        getStateHelper().put(PropertyKeys.offLabel, _offLabel);
    }

    public java.lang.String getLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(java.lang.String _label) {
        getStateHelper().put(PropertyKeys.label, _label);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public java.lang.String getOnchange() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(java.lang.String _onchange) {
        getStateHelper().put(PropertyKeys.onchange, _onchange);
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

    public java.lang.String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, null);
    }

    public void setTabindex(java.lang.String _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public boolean isShowLabels() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showLabels, true);
    }

    public void setShowLabels(boolean _showLabels) {
        getStateHelper().put(PropertyKeys.showLabels, _showLabels);
    }

    public java.lang.String getOnfocus() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onfocus, null);
    }

    public void setOnfocus(java.lang.String _onfocus) {
        getStateHelper().put(PropertyKeys.onfocus, _onfocus);
    }

    public java.lang.String getOnblur() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onblur, null);
    }

    public void setOnblur(java.lang.String _onblur) {
        getStateHelper().put(PropertyKeys.onblur, _onblur);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}