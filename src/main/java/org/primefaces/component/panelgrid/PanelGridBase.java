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
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.columns, 0);
    }

    public void setColumns(int _columns) {
        getStateHelper().put(PropertyKeys.columns, _columns);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getColumnClasses() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.columnClasses, null);
    }

    public void setColumnClasses(java.lang.String _columnClasses) {
        getStateHelper().put(PropertyKeys.columnClasses, _columnClasses);
    }

    public java.lang.String getLayout() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.layout, "tabular");
    }

    public void setLayout(java.lang.String _layout) {
        getStateHelper().put(PropertyKeys.layout, _layout);
    }

    public java.lang.String getRole() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.role, "grid");
    }

    public void setRole(java.lang.String _role) {
        getStateHelper().put(PropertyKeys.role, _role);
    }

}