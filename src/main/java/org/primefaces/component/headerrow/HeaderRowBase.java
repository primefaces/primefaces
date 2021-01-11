/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.headerrow;

import javax.faces.component.UIComponentBase;


public abstract class HeaderRowBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.HeaderRowRenderer";

    public enum PropertyKeys {
        field,
        groupBy,
        sortFunction,
        sortOrder,
        expandable,
        expanded,
        style,
        styleClass,
        rowspan,
        colspan
    }

    public HeaderRowBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getField() {
        return (String) getStateHelper().eval(PropertyKeys.field, null);
    }

    public void setField(String field) {
        getStateHelper().put(PropertyKeys.field, field);
    }

    public String getGroupBy() {
        return (String) getStateHelper().eval(PropertyKeys.groupBy, null);
    }

    public void setGroupBy(String groupBy) {
        getStateHelper().put(PropertyKeys.groupBy, groupBy);
    }

    public String getSortOrder() {
        return (String) getStateHelper().eval(PropertyKeys.sortOrder, "asc");
    }

    public void setSortOrder(String sortOrder) {
        getStateHelper().put(PropertyKeys.sortOrder, sortOrder);
    }

    public boolean isExpandable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expandable, false);
    }

    public void setExpandable(boolean expandable) {
        getStateHelper().put(PropertyKeys.expandable, expandable);
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

    public Integer getRowspan() {
        return (Integer) getStateHelper().eval(PropertyKeys.rowspan, null);
    }

    public void setRowspan(Integer rowspan) {
        getStateHelper().put(PropertyKeys.rowspan, rowspan);
    }

    public Integer getColspan() {
        return (Integer) getStateHelper().eval(PropertyKeys.colspan, null);
    }

    public void setColspan(Integer colspan) {
        getStateHelper().put(PropertyKeys.colspan, colspan);
    }

    public javax.el.MethodExpression getSortFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(javax.el.MethodExpression sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, sortFunction);
    }

    public boolean isExpanded() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expanded, true);
    }

    public void setExpanded(boolean expanded) {
        getStateHelper().put(PropertyKeys.expanded, expanded);
    }

}