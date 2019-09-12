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
package org.primefaces.component.selectcheckboxmenu;

import javax.faces.component.html.HtmlSelectManyCheckbox;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class SelectCheckboxMenuBase extends HtmlSelectManyCheckbox implements Widget, PrimeClientBehaviorHolder {

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
        filterPlaceholder,
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

    public java.lang.String getFilterPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.filterPlaceholder, null);
    }

    public void setFilterPlaceholder(String filterPlaceholder) {
        getStateHelper().put(PropertyKeys.filterPlaceholder, filterPlaceholder);
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
        return (String) getStateHelper().eval(PropertyKeys.labelSeparator, ", ");
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
}