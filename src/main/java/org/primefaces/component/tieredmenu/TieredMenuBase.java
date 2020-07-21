/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.tieredmenu;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.OverlayMenu;

public abstract class TieredMenuBase extends AbstractMenu implements Widget, OverlayMenu {

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

    @Override
    public String getTriggerEvent() {
        return (String) getStateHelper().eval(PropertyKeys.triggerEvent, "click");
    }

    public void setTriggerEvent(String triggerEvent) {
        getStateHelper().put(PropertyKeys.triggerEvent, triggerEvent);
    }

    public String getToggleEvent() {
        return (String) getStateHelper().eval(PropertyKeys.toggleEvent, null);
    }

    public void setToggleEvent(String toggleEvent) {
        getStateHelper().put(PropertyKeys.toggleEvent, toggleEvent);
    }
}