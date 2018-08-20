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

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TooltipBase extends UIOutput implements Widget {

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

        private String toString;

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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getShowEvent() {
        return (String) getStateHelper().eval(PropertyKeys.showEvent, null);
    }

    public void setShowEvent(String showEvent) {
        getStateHelper().put(PropertyKeys.showEvent, showEvent);
    }

    public String getShowEffect() {
        return (String) getStateHelper().eval(PropertyKeys.showEffect, "fade");
    }

    public void setShowEffect(String showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, showEffect);
    }

    public int getShowDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.showDelay, 150);
    }

    public void setShowDelay(int showDelay) {
        getStateHelper().put(PropertyKeys.showDelay, showDelay);
    }

    public String getHideEvent() {
        return (String) getStateHelper().eval(PropertyKeys.hideEvent, null);
    }

    public void setHideEvent(String hideEvent) {
        getStateHelper().put(PropertyKeys.hideEvent, hideEvent);
    }

    public String getHideEffect() {
        return (String) getStateHelper().eval(PropertyKeys.hideEffect, "fade");
    }

    public void setHideEffect(String hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, hideEffect);
    }

    public int getHideDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.hideDelay, 0);
    }

    public void setHideDelay(int hideDelay) {
        getStateHelper().put(PropertyKeys.hideDelay, hideDelay);
    }

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
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

    public String getGlobalSelector() {
        return (String) getStateHelper().eval(PropertyKeys.globalSelector, null);
    }

    public void setGlobalSelector(String globalSelector) {
        getStateHelper().put(PropertyKeys.globalSelector, globalSelector);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    public boolean isTrackMouse() {
        return (Boolean) getStateHelper().eval(PropertyKeys.trackMouse, false);
    }

    public void setTrackMouse(boolean trackMouse) {
        getStateHelper().put(PropertyKeys.trackMouse, trackMouse);
    }

    public String getBeforeShow() {
        return (String) getStateHelper().eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        getStateHelper().put(PropertyKeys.beforeShow, beforeShow);
    }

    public String getOnHide() {
        return (String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(String onHide) {
        getStateHelper().put(PropertyKeys.onHide, onHide);
    }

    public String getOnShow() {
        return (String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(String onShow) {
        getStateHelper().put(PropertyKeys.onShow, onShow);
    }

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "right");
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public boolean isDelegate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.delegate, false);
    }

    public void setDelegate(boolean delegate) {
        getStateHelper().put(PropertyKeys.delegate, delegate);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}