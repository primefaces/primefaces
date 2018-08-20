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
package org.primefaces.component.menu;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class MenuBase extends AbstractMenu implements Widget, OverlayMenu {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        trigger,
        my,
        at,
        overlay,
        style,
        styleClass,
        triggerEvent,
        toggleable
    }

    public MenuBase() {
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

    @Override
    public String getTrigger() {
        return (String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(String trigger) {
        getStateHelper().put(PropertyKeys.trigger, trigger);
    }

    @Override
    public String getMy() {
        return (String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(String my) {
        getStateHelper().put(PropertyKeys.my, my);
    }

    @Override
    public String getAt() {
        return (String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(String at) {
        getStateHelper().put(PropertyKeys.at, at);
    }

    public boolean isOverlay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.overlay, false);
    }

    public void setOverlay(boolean overlay) {
        getStateHelper().put(PropertyKeys.overlay, overlay);
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

    @Override
    public String getTriggerEvent() {
        return (String) getStateHelper().eval(PropertyKeys.triggerEvent, "click");
    }

    public void setTriggerEvent(String triggerEvent) {
        getStateHelper().put(PropertyKeys.triggerEvent, triggerEvent);
    }

    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}