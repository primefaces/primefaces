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
package org.primefaces.component.accordionpanel;

import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class AccordionPanelBase extends UITabPanel implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AccordionPanelRenderer";

    public enum PropertyKeys {

        widgetVar,
        activeIndex,
        style,
        styleClass,
        onTabChange,
        onTabShow,
        onTabClose,
        cache,
        multiple,
        dir,
        tabindex,
        tabController;
    }

    public AccordionPanelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getActiveIndex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.activeIndex, "0");
    }

    public void setActiveIndex(java.lang.String _activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, _activeIndex);
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

    public java.lang.String getOnTabChange() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabChange, null);
    }

    public void setOnTabChange(java.lang.String _onTabChange) {
        getStateHelper().put(PropertyKeys.onTabChange, _onTabChange);
    }

    public java.lang.String getOnTabShow() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabShow, null);
    }

    public void setOnTabShow(java.lang.String _onTabShow) {
        getStateHelper().put(PropertyKeys.onTabShow, _onTabShow);
    }

    public java.lang.String getOnTabClose() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabClose, null);
    }

    public void setOnTabClose(java.lang.String _onTabClose) {
        getStateHelper().put(PropertyKeys.onTabClose, _onTabClose);
    }

    public boolean isCache() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean _cache) {
        getStateHelper().put(PropertyKeys.cache, _cache);
    }

    public boolean isMultiple() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean _multiple) {
        getStateHelper().put(PropertyKeys.multiple, _multiple);
    }

    public java.lang.String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(java.lang.String _dir) {
        getStateHelper().put(PropertyKeys.dir, _dir);
    }

    public java.lang.String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(java.lang.String _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public javax.el.MethodExpression getTabController() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.tabController, null);
    }

    public void setTabController(javax.el.MethodExpression _tabController) {
        getStateHelper().put(PropertyKeys.tabController, _tabController);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }

    public boolean isRTL() {
        return "rtl".equalsIgnoreCase(getDir());
    }
}