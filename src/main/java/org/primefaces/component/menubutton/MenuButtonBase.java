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
package org.primefaces.component.menubutton;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

public abstract class MenuButtonBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MenuButtonRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        value,
        style,
        styleClass,
        disabled,
        icon,
        iconPos,
        appendTo,
        menuStyleClass,
        title,
        ariaLabel,
        collision,
        maxHeight
    }

    public MenuButtonBase() {
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

    public String getValue() {
        return (String) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(String value) {
        getStateHelper().put(PropertyKeys.value, value);
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

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getIcon() {
        return (String) getStateHelper().eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        getStateHelper().put(PropertyKeys.icon, icon);
    }

    public String getIconPos() {
        return (String) getStateHelper().eval(PropertyKeys.iconPos, "left");
    }

    public void setIconPos(String iconPos) {
        getStateHelper().put(PropertyKeys.iconPos, iconPos);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public String getMenuStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.menuStyleClass, null);
    }

    public void setMenuStyleClass(String menuStyleClass) {
        getStateHelper().put(PropertyKeys.menuStyleClass, menuStyleClass);
    }

    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    public String getAriaLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaLabel, null);
    }

    public void setAriaLabel(String ariaLabel) {
        getStateHelper().put(PropertyKeys.ariaLabel, ariaLabel);
    }

    public String getCollision() {
        return (String) getStateHelper().eval(PropertyKeys.collision, "flip");
    }

    public void setCollision(String collision) {
        getStateHelper().put(PropertyKeys.collision, collision);
    }

    public String getMaxHeight() {
        return (String) getStateHelper().eval(PropertyKeys.maxHeight, null);
    }

    public void setMaxHeight(String maxHeight) {
        getStateHelper().put(PropertyKeys.maxHeight, maxHeight);
    }
}