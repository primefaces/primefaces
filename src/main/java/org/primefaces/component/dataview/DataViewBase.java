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
package org.primefaces.component.dataview;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class DataViewBase extends UIData implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataViewRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        layout,
        gridIcon,
        listIcon
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
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}