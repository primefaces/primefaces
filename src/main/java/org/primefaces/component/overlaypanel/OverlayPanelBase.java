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
package org.primefaces.component.overlaypanel;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class OverlayPanelBase extends UIPanel implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OverlayPanelRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        forValue("for"),
        showEvent,
        hideEvent,
        showEffect,
        hideEffect,
        appendTo,
        onShow,
        onHide,
        my,
        at,
        dynamic,
        dismissable,
        showCloseIcon,
        modal,
        showDelay;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public OverlayPanelBase() {
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

    public java.lang.String getFor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(java.lang.String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public java.lang.String getShowEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showEvent, null);
    }

    public void setShowEvent(java.lang.String _showEvent) {
        getStateHelper().put(PropertyKeys.showEvent, _showEvent);
    }

    public java.lang.String getHideEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.hideEvent, null);
    }

    public void setHideEvent(java.lang.String _hideEvent) {
        getStateHelper().put(PropertyKeys.hideEvent, _hideEvent);
    }

    public java.lang.String getShowEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showEffect, null);
    }

    public void setShowEffect(java.lang.String _showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, _showEffect);
    }

    public java.lang.String getHideEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.hideEffect, null);
    }

    public void setHideEffect(java.lang.String _hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, _hideEffect);
    }

    public java.lang.String getAppendTo() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(java.lang.String _appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, _appendTo);
    }

    public java.lang.String getOnShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(java.lang.String _onShow) {
        getStateHelper().put(PropertyKeys.onShow, _onShow);
    }

    public java.lang.String getOnHide() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(java.lang.String _onHide) {
        getStateHelper().put(PropertyKeys.onHide, _onHide);
    }

    public java.lang.String getMy() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(java.lang.String _my) {
        getStateHelper().put(PropertyKeys.my, _my);
    }

    public java.lang.String getAt() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(java.lang.String _at) {
        getStateHelper().put(PropertyKeys.at, _at);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    public boolean isDismissable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dismissable, true);
    }

    public void setDismissable(boolean _dismissable) {
        getStateHelper().put(PropertyKeys.dismissable, _dismissable);
    }

    public boolean isShowCloseIcon() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showCloseIcon, false);
    }

    public void setShowCloseIcon(boolean _showCloseIcon) {
        getStateHelper().put(PropertyKeys.showCloseIcon, _showCloseIcon);
    }

    public boolean isModal() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.modal, false);
    }

    public void setModal(boolean _modal) {
        getStateHelper().put(PropertyKeys.modal, _modal);
    }

    public int getShowDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.showDelay, 0);
    }

    public void setShowDelay(int _showDelay) {
        getStateHelper().put(PropertyKeys.showDelay, _showDelay);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}