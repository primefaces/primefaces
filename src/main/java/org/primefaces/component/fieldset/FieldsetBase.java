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
package org.primefaces.component.fieldset;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class FieldsetBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.FieldsetRenderer";

    public enum PropertyKeys {

        widgetVar,
        legend,
        style,
        styleClass,
        toggleable,
        toggleSpeed,
        collapsed,
        tabindex,
        escape,
        title
    }

    public FieldsetBase() {
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

    public String getLegend() {
        return (String) getStateHelper().eval(PropertyKeys.legend, null);
    }

    public void setLegend(String legend) {
        getStateHelper().put(PropertyKeys.legend, legend);
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

    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    public int getToggleSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
    }

    public void setToggleSpeed(int toggleSpeed) {
        getStateHelper().put(PropertyKeys.toggleSpeed, toggleSpeed);
    }

    public boolean isCollapsed() {
        return (Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, collapsed);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}