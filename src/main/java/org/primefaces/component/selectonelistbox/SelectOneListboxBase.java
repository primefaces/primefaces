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
package org.primefaces.component.selectonelistbox;

import javax.faces.component.html.HtmlSelectOneListbox;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SelectOneListboxBase extends HtmlSelectOneListbox implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectOneListboxRenderer";

    public enum PropertyKeys {

        widgetVar,
        var,
        filter,
        filterMatchMode,
        filterFunction,
        caseSensitive,
        scrollHeight
    }

    public SelectOneListboxBase() {
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

    public java.lang.String getVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
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

    public int getScrollHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
    }

    public void setScrollHeight(int _scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}