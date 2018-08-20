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
package org.primefaces.component.selectmanycheckbox;

import javax.faces.component.html.HtmlSelectManyCheckbox;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SelectManyCheckboxBase extends HtmlSelectManyCheckbox implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectManyCheckboxRenderer";

    public enum PropertyKeys {

        widgetVar,
        columns
    }

    public SelectManyCheckboxBase() {
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
        return (Integer) getStateHelper().eval(PropertyKeys.columns, 0);
    }

    public void setColumns(int columns) {
        getStateHelper().put(PropertyKeys.columns, columns);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}