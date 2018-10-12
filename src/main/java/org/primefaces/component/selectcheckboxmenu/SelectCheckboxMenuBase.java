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

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SelectCheckboxMenuBase extends HtmlSelectManyCheckbox implements Widget, PrimeClientBehaviorHolder {

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
        dynamic,
        labelSeparator,
        emptyLabel
    }

    public SelectCheckboxMenuBase() {
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

    public int getScrollHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.scrollHeight, Integer.MAX_VALUE);
    }

    public void setScrollHeight(int scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
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

    public boolean isFilter() {
        return (Boolean) getStateHelper().eval(PropertyKeys.filter, false);
    }

    public void setFilter(boolean filter) {
        getStateHelper().put(PropertyKeys.filter, filter);
    }

    public String getFilterMatchMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(String filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, filterMatchMode);
    }

    public String getFilterFunction() {
        return (String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(String filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, filterFunction);
    }

    public boolean isCaseSensitive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.caseSensitive, false);
    }

    public void setCaseSensitive(boolean caseSensitive) {
        getStateHelper().put(PropertyKeys.caseSensitive, caseSensitive);
    }

    public String getPanelStyle() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(String panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, panelStyle);
    }

    public String getPanelStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(String panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, panelStyleClass);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, "@(body)");
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    @Override
    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, null);
    }

    @Override
    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    @Override
    public String getTitle() {
        return (String) getStateHelper().eval(PropertyKeys.title, null);
    }

    @Override
    public void setTitle(String title) {
        getStateHelper().put(PropertyKeys.title, title);
    }

    public boolean isShowHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
    }

    public void setShowHeader(boolean showHeader) {
        getStateHelper().put(PropertyKeys.showHeader, showHeader);
    }

    public boolean isUpdateLabel() {
        return (Boolean) getStateHelper().eval(PropertyKeys.updateLabel, false);
    }

    public void setUpdateLabel(boolean updateLabel) {
        getStateHelper().put(PropertyKeys.updateLabel, updateLabel);
    }

    public boolean isMultiple() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean multiple) {
        getStateHelper().put(PropertyKeys.multiple, multiple);
    }

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    public String getLabelSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.labelSeparator, ",");
    }

    public void setLabelSeparator(String labelSeparator) {
        getStateHelper().put(PropertyKeys.labelSeparator, labelSeparator);
    }

    public String getEmptyLabel() {
        return (String) getStateHelper().eval(PropertyKeys.emptyLabel, null);
    }

    public void setEmptyLabel(String emptyLabel) {
        getStateHelper().put(PropertyKeys.emptyLabel, emptyLabel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}