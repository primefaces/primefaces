/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.datagrid;

import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.api.Widget;
import org.primefaces.component.datalist.DataListBase;

import jakarta.faces.component.behavior.ClientBehaviorHolder;

public abstract class DataGridBase extends UIPageableData
        implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, FlexAware, MultiViewStateAware<DataGridState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataGridRenderer";

    public enum PropertyKeys {

        widgetVar,
        columns,
        style,
        styleClass,
        rowStyle,
        rowStyleClass,
        rowTitle,
        flex
    }

    public DataGridBase() {
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

    public int getColumns() {
        return (Integer) getStateHelper().eval(PropertyKeys.columns, 3);
    }

    public void setColumns(int columns) {
        getStateHelper().put(PropertyKeys.columns, columns);
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

    @Override
    public boolean isMultiViewState() {
        return (Boolean) getStateHelper().eval(DataListBase.PropertyKeys.multiViewState, false);
    }

    public void setMultiViewState(boolean multiViewState) {
        getStateHelper().put(DataListBase.PropertyKeys.multiViewState, multiViewState);
    }

    public String getRowStyle() {
        return (String) getStateHelper().eval(PropertyKeys.rowStyle, null);
    }

    public void setRowStyle(String rowStyle) {
        getStateHelper().put(PropertyKeys.rowStyle, rowStyle);
    }

    public String getRowStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public String getRowTitle() {
        return (String) getStateHelper().eval(PropertyKeys.rowTitle, null);
    }

    public void setRowTitle(String rowTitle) {
        getStateHelper().put(PropertyKeys.rowTitle, rowTitle);
    }

    @Override
    public Boolean getFlex() {
        return (Boolean) getStateHelper().eval(PropertyKeys.flex, null);
    }

    public void setFlex(Boolean flex) {
        getStateHelper().put(PropertyKeys.flex, flex);
    }
}