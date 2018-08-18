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
package org.primefaces.component.panel;

import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class PanelBase extends UIPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getHeader() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(java.lang.String _header) {
        getStateHelper().put(PropertyKeys.header, _header);
    }

    public java.lang.String getFooter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(java.lang.String _footer) {
        getStateHelper().put(PropertyKeys.footer, _footer);
    }

    public boolean isToggleable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean _toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, _toggleable);
    }

    public int getToggleSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
    }

    public void setToggleSpeed(int _toggleSpeed) {
        getStateHelper().put(PropertyKeys.toggleSpeed, _toggleSpeed);
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

    public boolean isCollapsed() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean _collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, _collapsed);
    }

    public boolean isClosable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean _closable) {
        getStateHelper().put(PropertyKeys.closable, _closable);
    }

    public int getCloseSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.closeSpeed, 500);
    }

    public void setCloseSpeed(int _closeSpeed) {
        getStateHelper().put(PropertyKeys.closeSpeed, _closeSpeed);
    }

    public boolean isVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean _visible) {
        getStateHelper().put(PropertyKeys.visible, _visible);
    }

    public java.lang.String getCloseTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.closeTitle, null);
    }

    public void setCloseTitle(java.lang.String _closeTitle) {
        getStateHelper().put(PropertyKeys.closeTitle, _closeTitle);
    }

    public java.lang.String getToggleTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.toggleTitle, null);
    }

    public void setToggleTitle(java.lang.String _toggleTitle) {
        getStateHelper().put(PropertyKeys.toggleTitle, _toggleTitle);
    }

    public java.lang.String getMenuTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.menuTitle, null);
    }

    public void setMenuTitle(java.lang.String _menuTitle) {
        getStateHelper().put(PropertyKeys.menuTitle, _menuTitle);
    }

    public java.lang.String getToggleOrientation() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.toggleOrientation, "vertical");
    }

    public void setToggleOrientation(java.lang.String _toggleOrientation) {
        getStateHelper().put(PropertyKeys.toggleOrientation, _toggleOrientation);
    }

    public boolean isToggleableHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleableHeader, false);
    }

    public void setToggleableHeader(boolean _toggleableHeader) {
        getStateHelper().put(PropertyKeys.toggleableHeader, _toggleableHeader);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}