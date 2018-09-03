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

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class StackBase extends AbstractMenu implements Widget {

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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    public int getOpenSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.openSpeed, 300);
    }

    public void setOpenSpeed(int openSpeed) {
        getStateHelper().put(PropertyKeys.openSpeed, openSpeed);
    }

    public int getCloseSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.closeSpeed, 300);
    }

    public void setCloseSpeed(int closeSpeed) {
        getStateHelper().put(PropertyKeys.closeSpeed, closeSpeed);
    }

    public boolean isExpanded() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expanded, false);
    }

    public void setExpanded(boolean expanded) {
        getStateHelper().put(PropertyKeys.expanded, expanded);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}