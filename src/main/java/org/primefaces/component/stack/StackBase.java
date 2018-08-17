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
package org.primefaces.component.stack;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class StackBase extends AbstractMenu implements org.primefaces.component.api.Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.StackRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        icon,
        openSpeed,
        closeSpeed,
        expanded
    }

    public StackBase() {
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

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel _model) {
        getStateHelper().put(PropertyKeys.model, _model);
    }

    public java.lang.String getIcon() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(java.lang.String _icon) {
        getStateHelper().put(PropertyKeys.icon, _icon);
    }

    public int getOpenSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.openSpeed, 300);
    }

    public void setOpenSpeed(int _openSpeed) {
        getStateHelper().put(PropertyKeys.openSpeed, _openSpeed);
    }

    public int getCloseSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.closeSpeed, 300);
    }

    public void setCloseSpeed(int _closeSpeed) {
        getStateHelper().put(PropertyKeys.closeSpeed, _closeSpeed);
    }

    public boolean isExpanded() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.expanded, false);
    }

    public void setExpanded(boolean _expanded) {
        getStateHelper().put(PropertyKeys.expanded, _expanded);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}