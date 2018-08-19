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
package org.primefaces.component.sidebar;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SidebarBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SidebarRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        visible,
        position,
        fullScreen,
        blockScroll,
        baseZIndex,
        appendTo,
        onShow,
        onHide
    }

    public SidebarBase() {
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

    public boolean isVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean _visible) {
        getStateHelper().put(PropertyKeys.visible, _visible);
    }

    public java.lang.String getPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.position, "left");
    }

    public void setPosition(java.lang.String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public boolean isFullScreen() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.fullScreen, false);
    }

    public void setFullScreen(boolean _fullScreen) {
        getStateHelper().put(PropertyKeys.fullScreen, _fullScreen);
    }

    public boolean isBlockScroll() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.blockScroll, false);
    }

    public void setBlockScroll(boolean _blockScroll) {
        getStateHelper().put(PropertyKeys.blockScroll, _blockScroll);
    }

    public int getBaseZIndex() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.baseZIndex, 0);
    }

    public void setBaseZIndex(int _baseZIndex) {
        getStateHelper().put(PropertyKeys.baseZIndex, _baseZIndex);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}