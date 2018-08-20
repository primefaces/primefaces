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

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "left");
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public boolean isFullScreen() {
        return (Boolean) getStateHelper().eval(PropertyKeys.fullScreen, false);
    }

    public void setFullScreen(boolean fullScreen) {
        getStateHelper().put(PropertyKeys.fullScreen, fullScreen);
    }

    public boolean isBlockScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.blockScroll, false);
    }

    public void setBlockScroll(boolean blockScroll) {
        getStateHelper().put(PropertyKeys.blockScroll, blockScroll);
    }

    public int getBaseZIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.baseZIndex, 0);
    }

    public void setBaseZIndex(int baseZIndex) {
        getStateHelper().put(PropertyKeys.baseZIndex, baseZIndex);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}