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
package org.primefaces.component.selectcheckboxmenu;

import javax.faces.component.html.HtmlSelectManyCheckbox;

import org.primefaces.util.ComponentUtils;


abstract class SelectCheckboxMenuBase extends HtmlSelectManyCheckbox implements org.primefaces.component.api.Widget, org.primefaces.component.api.PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectCheckboxMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        scrollHeight,
        onShow,
        onHide,
        filter,
        filterMatchMode,
        filterFunction,
        caseSensitive,
        panelStyle,
        panelStyleClass,
        appendTo,
        tabindex,
        title,
        showHeader,
        updateLabel,
        multiple,
        dynamic;
    }

    public SelectCheckboxMenuBase() {
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

    public int getScrollHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
    }

    public void setScrollHeight(int _scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
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

    public boolean isFilter() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.filter, false);
    }

    public void setFilter(boolean _filter) {
        getStateHelper().put(PropertyKeys.filter, _filter);
    }

    public java.lang.String getFilterMatchMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(java.lang.String _filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
    }

    public java.lang.String getFilterFunction() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(java.lang.String _filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
    }

    public boolean isCaseSensitive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitive, false);
    }

    public void setCaseSensitive(boolean _caseSensitive) {
        getStateHelper().put(PropertyKeys.caseSensitive, _caseSensitive);
    }

    public java.lang.String getPanelStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(java.lang.String _panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, _panelStyle);
    }

    public java.lang.String getPanelStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(java.lang.String _panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, _panelStyleClass);
    }

    public java.lang.String getAppendTo() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(java.lang.String _appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, _appendTo);
    }

    public java.lang.String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, null);
    }

    public void setTabindex(java.lang.String _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public java.lang.String getTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.title, null);
    }

    public void setTitle(java.lang.String _title) {
        getStateHelper().put(PropertyKeys.title, _title);
    }

    public boolean isShowHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
    }

    public void setShowHeader(boolean _showHeader) {
        getStateHelper().put(PropertyKeys.showHeader, _showHeader);
    }

    public boolean isUpdateLabel() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.updateLabel, false);
    }

    public void setUpdateLabel(boolean _updateLabel) {
        getStateHelper().put(PropertyKeys.updateLabel, _updateLabel);
    }

    public boolean isMultiple() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean _multiple) {
        getStateHelper().put(PropertyKeys.multiple, _multiple);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}