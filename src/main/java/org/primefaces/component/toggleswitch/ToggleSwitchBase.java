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
package org.primefaces.component.toggleswitch;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ToggleSwitchBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ToggleSwitchRenderer";

    public enum PropertyKeys {

        widgetVar,
        label,
        disabled,
        onchange,
        style,
        styleClass,
        tabindex,
        onfocus,
        onblur
    }

    public ToggleSwitchBase() {
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

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getOnchange() {
        return (String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(String onchange) {
        getStateHelper().put(PropertyKeys.onchange, onchange);
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

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, null);
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public String getOnfocus() {
        return (String) getStateHelper().eval(PropertyKeys.onfocus, null);
    }

    public void setOnfocus(String onfocus) {
        getStateHelper().put(PropertyKeys.onfocus, onfocus);
    }

    public String getOnblur() {
        return (String) getStateHelper().eval(PropertyKeys.onblur, null);
    }

    public void setOnblur(String onblur) {
        getStateHelper().put(PropertyKeys.onblur, onblur);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}