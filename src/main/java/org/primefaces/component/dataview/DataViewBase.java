/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.dataview;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.*;

public abstract class DataViewBase extends UIData
        implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable, MultiViewStateAware<DataViewState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataViewRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        layout,
        gridIcon,
        listIcon,
        multiViewState
    }

    public DataViewBase() {
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

    public String getLayout() {
        return (String) getStateHelper().eval(PropertyKeys.layout, "list");
    }

    public void setLayout(String layout) {
        getStateHelper().put(PropertyKeys.layout, layout);
    }

    public String getGridIcon() {
        return (String) getStateHelper().eval(PropertyKeys.gridIcon, null);
    }

    public void setGridIcon(String gridIcon) {
        getStateHelper().put(PropertyKeys.gridIcon, gridIcon);
    }

    public String getListIcon() {
        return (String) getStateHelper().eval(PropertyKeys.listIcon, null);
    }

    public void setListIcon(String listIcon) {
        getStateHelper().put(PropertyKeys.listIcon, listIcon);
    }

    @Override
    public boolean isMultiViewState() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiViewState, false);
    }

    public void setMultiViewState(boolean multiViewState) {
        getStateHelper().put(PropertyKeys.multiViewState, multiViewState);
    }
}