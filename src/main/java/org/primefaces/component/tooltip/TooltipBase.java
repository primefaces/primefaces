/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.tooltip;

import javax.faces.component.UIOutput;

import org.primefaces.component.api.Widget;

public abstract class TooltipBase extends UIOutput implements Widget {

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
}