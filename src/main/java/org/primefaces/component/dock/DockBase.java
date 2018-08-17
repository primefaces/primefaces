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

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class DockBase extends AbstractMenu implements org.primefaces.component.api.Widget {

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

    public java.lang.String getPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.position, "bottom");
    }

    public void setPosition(java.lang.String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public int getItemWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.itemWidth, 40);
    }

    public void setItemWidth(int _itemWidth) {
        getStateHelper().put(PropertyKeys.itemWidth, _itemWidth);
    }

    public int getMaxWidth() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxWidth, 50);
    }

    public void setMaxWidth(int _maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, _maxWidth);
    }

    public int getProximity() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.proximity, 90);
    }

    public void setProximity(int _proximity) {
        getStateHelper().put(PropertyKeys.proximity, _proximity);
    }

    public java.lang.String getHalign() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.halign, "center");
    }

    public void setHalign(java.lang.String _halign) {
        getStateHelper().put(PropertyKeys.halign, _halign);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}