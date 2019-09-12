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
package org.primefaces.component.overlaypanel;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;

public abstract class OverlayPanelBase extends UIPanel implements Widget {

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
        collision,
        dynamic,
        dismissable,
        showCloseIcon,
        modal,
        blockScroll,
        showDelay;

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

    public OverlayPanelBase() {
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

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public String getShowEvent() {
        return (String) getStateHelper().eval(PropertyKeys.showEvent, null);
    }

    public void setShowEvent(String showEvent) {
        getStateHelper().put(PropertyKeys.showEvent, showEvent);
    }

    public String getHideEvent() {
        return (String) getStateHelper().eval(PropertyKeys.hideEvent, null);
    }

    public void setHideEvent(String hideEvent) {
        getStateHelper().put(PropertyKeys.hideEvent, hideEvent);
    }

    public String getShowEffect() {
        return (String) getStateHelper().eval(PropertyKeys.showEffect, null);
    }

    public void setShowEffect(String showEffect) {
        getStateHelper().put(PropertyKeys.showEffect, showEffect);
    }

    public String getHideEffect() {
        return (String) getStateHelper().eval(PropertyKeys.hideEffect, null);
    }

    public void setHideEffect(String hideEffect) {
        getStateHelper().put(PropertyKeys.hideEffect, hideEffect);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public String getOnShow() {
        return (String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(String onShow) {
        getStateHelper().put(PropertyKeys.onShow, onShow);
    }

    public String getOnHide() {
        return (String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(String onHide) {
        getStateHelper().put(PropertyKeys.onHide, onHide);
    }

    public String getMy() {
        return (String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(String my) {
        getStateHelper().put(PropertyKeys.my, my);
    }

    public String getAt() {
        return (String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(String at) {
        getStateHelper().put(PropertyKeys.at, at);
    }

    public String getCollision() {
        return (String) getStateHelper().eval(PropertyKeys.collision, null);
    }

    public void setCollision(String collision) {
        getStateHelper().put(PropertyKeys.collision, collision);
    }

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    public boolean isDismissable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dismissable, true);
    }

    public void setDismissable(boolean dismissable) {
        getStateHelper().put(PropertyKeys.dismissable, dismissable);
    }

    public boolean isShowCloseIcon() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCloseIcon, false);
    }

    public void setShowCloseIcon(boolean showCloseIcon) {
        getStateHelper().put(PropertyKeys.showCloseIcon, showCloseIcon);
    }

    public boolean isModal() {
        return (Boolean) getStateHelper().eval(PropertyKeys.modal, false);
    }

    public void setModal(boolean modal) {
        getStateHelper().put(PropertyKeys.modal, modal);
    }

    public boolean isBlockScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.blockScroll, false);
    }

    public void setBlockScroll(boolean blockScroll) {
        getStateHelper().put(PropertyKeys.blockScroll, blockScroll);
    }

    public int getShowDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.showDelay, 0);
    }

    public void setShowDelay(int showDelay) {
        getStateHelper().put(PropertyKeys.showDelay, showDelay);
    }
}