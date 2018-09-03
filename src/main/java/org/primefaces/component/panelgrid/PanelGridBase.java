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
package org.primefaces.component.panelgrid;

import javax.faces.component.UIPanel;


abstract class PanelGridBase extends UIPanel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PanelGridRenderer";

    public enum PropertyKeys {

        columns,
        style,
        styleClass,
        columnClasses,
        layout,
        role
    }

    public PanelGridBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public int getColumns() {
        return (Integer) getStateHelper().eval(PropertyKeys.columns, 0);
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

    public String getColumnClasses() {
        return (String) getStateHelper().eval(PropertyKeys.columnClasses, null);
    }

    public void setColumnClasses(String columnClasses) {
        getStateHelper().put(PropertyKeys.columnClasses, columnClasses);
    }

    public String getLayout() {
        return (String) getStateHelper().eval(PropertyKeys.layout, "tabular");
    }

    public void setLayout(String layout) {
        getStateHelper().put(PropertyKeys.layout, layout);
    }

    public String getRole() {
        return (String) getStateHelper().eval(PropertyKeys.role, "grid");
    }

    public void setRole(String role) {
        getStateHelper().put(PropertyKeys.role, role);
    }

}