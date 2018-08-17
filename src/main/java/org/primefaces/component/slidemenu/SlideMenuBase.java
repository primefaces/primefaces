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
package org.primefaces.component.slidemenu;

import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.util.ComponentUtils;


abstract class SlideMenuBase extends AbstractMenu implements org.primefaces.component.api.Widget, org.primefaces.component.menu.OverlayMenu {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SlideMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        style,
        styleClass,
        backLabel,
        trigger,
        my,
        at,
        overlay,
        triggerEvent
    }

    public SlideMenuBase() {
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

    public java.lang.String getBackLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.backLabel, "Back");
    }

    public void setBackLabel(java.lang.String _backLabel) {
        getStateHelper().put(PropertyKeys.backLabel, _backLabel);
    }

    @Override
    public java.lang.String getTrigger() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(java.lang.String _trigger) {
        getStateHelper().put(PropertyKeys.trigger, _trigger);
    }

    @Override
    public java.lang.String getMy() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(java.lang.String _my) {
        getStateHelper().put(PropertyKeys.my, _my);
    }

    @Override
    public java.lang.String getAt() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(java.lang.String _at) {
        getStateHelper().put(PropertyKeys.at, _at);
    }

    public boolean isOverlay() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.overlay, false);
    }

    public void setOverlay(boolean _overlay) {
        getStateHelper().put(PropertyKeys.overlay, _overlay);
    }

    @Override
    public java.lang.String getTriggerEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.triggerEvent, "click");
    }

    public void setTriggerEvent(java.lang.String _triggerEvent) {
        getStateHelper().put(PropertyKeys.triggerEvent, _triggerEvent);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}