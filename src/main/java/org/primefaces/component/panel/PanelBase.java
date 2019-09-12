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
package org.primefaces.component.panel;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class PanelBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PanelRenderer";

    public enum PropertyKeys {

        widgetVar,
        header,
        footer,
        toggleable,
        toggleSpeed,
        style,
        styleClass,
        collapsed,
        closable,
        closeSpeed,
        visible,
        closeTitle,
        toggleTitle,
        menuTitle,
        toggleOrientation,
        toggleableHeader
    }

    public PanelBase() {
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

    public String getHeader() {
        return (String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        getStateHelper().put(PropertyKeys.header, header);
    }

    public String getFooter() {
        return (String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(String footer) {
        getStateHelper().put(PropertyKeys.footer, footer);
    }

    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    public int getToggleSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
    }

    public void setToggleSpeed(int toggleSpeed) {
        getStateHelper().put(PropertyKeys.toggleSpeed, toggleSpeed);
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

    public boolean isCollapsed() {
        return (Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, collapsed);
    }

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
    }

    public int getCloseSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.closeSpeed, 500);
    }

    public void setCloseSpeed(int closeSpeed) {
        getStateHelper().put(PropertyKeys.closeSpeed, closeSpeed);
    }

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public String getCloseTitle() {
        return (String) getStateHelper().eval(PropertyKeys.closeTitle, null);
    }

    public void setCloseTitle(String closeTitle) {
        getStateHelper().put(PropertyKeys.closeTitle, closeTitle);
    }

    public String getToggleTitle() {
        return (String) getStateHelper().eval(PropertyKeys.toggleTitle, null);
    }

    public void setToggleTitle(String toggleTitle) {
        getStateHelper().put(PropertyKeys.toggleTitle, toggleTitle);
    }

    public String getMenuTitle() {
        return (String) getStateHelper().eval(PropertyKeys.menuTitle, null);
    }

    public void setMenuTitle(String menuTitle) {
        getStateHelper().put(PropertyKeys.menuTitle, menuTitle);
    }

    public String getToggleOrientation() {
        return (String) getStateHelper().eval(PropertyKeys.toggleOrientation, "vertical");
    }

    public void setToggleOrientation(String toggleOrientation) {
        getStateHelper().put(PropertyKeys.toggleOrientation, toggleOrientation);
    }

    public boolean isToggleableHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleableHeader, false);
    }

    public void setToggleableHeader(boolean toggleableHeader) {
        getStateHelper().put(PropertyKeys.toggleableHeader, toggleableHeader);
    }
}