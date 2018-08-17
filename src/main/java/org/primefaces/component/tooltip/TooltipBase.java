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
package org.primefaces.component.tooltip;

import javax.faces.component.UIOutput;

import org.primefaces.util.ComponentUtils;


abstract class TooltipBase extends UIOutput implements org.primefaces.component.api.Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TooltipRenderer";

    public enum PropertyKeys {

        widgetVar,
        showEvent,
        showEffect,
        showDelay,
        hideEvent,
        hideEffect,
        hideDelay,
        forValue("for"),
        style,
        styleClass,
        globalSelector,
        escape,
        trackMouse,
        beforeShow,
        onHide,
        onShow,
        position,
        delegate;

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

    public TooltipBase() {
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

    public java.lang.String getShowEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showEvent, null);
    }

    public void setShowEvent(java.lang.String _showEvent) {
        getStateHelper().put(PropertyKeys.showEvent, _showEvent);
    }

    public java.lang.String getShowEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showEffect, "fade");
    }

    public void setShowEffect(java.lang.String _showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, _showEffect);
    }

    public int getShowDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.showDelay, 150);
    }

    public void setShowDelay(int _showDelay) {
        getStateHelper().put(PropertyKeys.showDelay, _showDelay);
    }

    public java.lang.String getHideEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.hideEvent, null);
    }

    public void setHideEvent(java.lang.String _hideEvent) {
        getStateHelper().put(PropertyKeys.hideEvent, _hideEvent);
    }

    public java.lang.String getHideEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.hideEffect, "fade");
    }

    public void setHideEffect(java.lang.String _hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, _hideEffect);
    }

    public int getHideDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.hideDelay, 0);
    }

    public void setHideDelay(int _hideDelay) {
        getStateHelper().put(PropertyKeys.hideDelay, _hideDelay);
    }

    public java.lang.String getFor() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(java.lang.String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
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

    public java.lang.String getGlobalSelector() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.globalSelector, null);
    }

    public void setGlobalSelector(java.lang.String _globalSelector) {
        getStateHelper().put(PropertyKeys.globalSelector, _globalSelector);
    }

    public boolean isEscape() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean _escape) {
        getStateHelper().put(PropertyKeys.escape, _escape);
    }

    public boolean isTrackMouse() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.trackMouse, false);
    }

    public void setTrackMouse(boolean _trackMouse) {
        getStateHelper().put(PropertyKeys.trackMouse, _trackMouse);
    }

    public java.lang.String getBeforeShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(java.lang.String _beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, _beforeShow);
    }

    public java.lang.String getOnHide() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(java.lang.String _onHide) {
        getStateHelper().put(PropertyKeys.onHide, _onHide);
    }

    public java.lang.String getOnShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(java.lang.String _onShow) {
        getStateHelper().put(PropertyKeys.onShow, _onShow);
    }

    public java.lang.String getPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.position, "right");
    }

    public void setPosition(java.lang.String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public boolean isDelegate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.delegate, false);
    }

    public void setDelegate(boolean _delegate) {
        getStateHelper().put(PropertyKeys.delegate, _delegate);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}