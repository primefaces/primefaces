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
package org.primefaces.component.selectmanymenu;

import javax.faces.component.html.HtmlSelectManyMenu;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SelectManyMenuBase extends HtmlSelectManyMenu implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectManyMenuRenderer";

    public enum PropertyKeys {

        widgetVar,
        var,
        showCheckbox,
        filter,
        filterMatchMode,
        filterFunction,
        caseSensitive,
        scrollHeight;
    }

    public SelectManyMenuBase() {
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

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public boolean isShowCheckbox() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCheckbox, false);
    }

    public void setShowCheckbox(boolean showCheckbox) {
        getStateHelper().put(PropertyKeys.showCheckbox, showCheckbox);
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

    public int getScrollHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.scrollHeight, Integer.MAX_VALUE);
    }

    public void setScrollHeight(int scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}