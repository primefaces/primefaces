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
package org.primefaces.component.dock;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class DockBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DockRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        position,
        itemWidth,
        maxWidth,
        proximity,
        halign
    }

    public DockBase() {
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

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "bottom");
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public int getItemWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.itemWidth, 40);
    }

    public void setItemWidth(int itemWidth) {
        getStateHelper().put(PropertyKeys.itemWidth, itemWidth);
    }

    public int getMaxWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxWidth, 50);
    }

    public void setMaxWidth(int maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, maxWidth);
    }

    public int getProximity() {
        return (Integer) getStateHelper().eval(PropertyKeys.proximity, 90);
    }

    public void setProximity(int proximity) {
        getStateHelper().put(PropertyKeys.proximity, proximity);
    }

    public String getHalign() {
        return (String) getStateHelper().eval(PropertyKeys.halign, "center");
    }

    public void setHalign(String halign) {
        getStateHelper().put(PropertyKeys.halign, halign);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}