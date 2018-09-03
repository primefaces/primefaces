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
package org.primefaces.component.megamenu;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class MegaMenuBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MegaMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        style,
        styleClass,
        autoDisplay,
        activeIndex,
        orientation
    }

    public MegaMenuBase() {
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

    public boolean isAutoDisplay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoDisplay, true);
    }

    public void setAutoDisplay(boolean autoDisplay) {
        getStateHelper().put(PropertyKeys.autoDisplay, autoDisplay);
    }

    public int getActiveIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.activeIndex, Integer.MIN_VALUE);
    }

    public void setActiveIndex(int activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, activeIndex);
    }

    public String getOrientation() {
        return (String) getStateHelper().eval(PropertyKeys.orientation, "horizontal");
    }

    public void setOrientation(String orientation) {
        getStateHelper().put(PropertyKeys.orientation, orientation);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}