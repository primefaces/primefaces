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
package org.primefaces.component.tieredmenu;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.OverlayMenu;
import org.primefaces.util.ComponentUtils;


abstract class TieredMenuBase extends AbstractMenu implements Widget, OverlayMenu {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TieredMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        style,
        styleClass,
        autoDisplay,
        trigger,
        my,
        at,
        overlay,
        triggerEvent,
        toggleEvent;
    }

    public TieredMenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isAutoDisplay() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoDisplay, true);
    }

    public void setAutoDisplay(boolean autoDisplay) {
        getStateHelper().put(PropertyKeys.autoDisplay, autoDisplay);
    }

    public String getTrigger() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(String trigger) {
        getStateHelper().put(PropertyKeys.trigger, trigger);
    }

    public String getMy() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(String my) {
        getStateHelper().put(PropertyKeys.my, my);
    }

    public String getAt() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(String at) {
        getStateHelper().put(PropertyKeys.at, at);
    }

    public boolean isOverlay() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.overlay, false);
    }

    public void setOverlay(boolean overlay) {
        getStateHelper().put(PropertyKeys.overlay, overlay);
    }

    public String getTriggerEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.triggerEvent, "click");
    }

    public void setTriggerEvent(String triggerEvent) {
        getStateHelper().put(PropertyKeys.triggerEvent, triggerEvent);
    }

    public String getToggleEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.toggleEvent, null);
    }

    public void setToggleEvent(String toggleEvent) {
        getStateHelper().put(PropertyKeys.toggleEvent, toggleEvent);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}