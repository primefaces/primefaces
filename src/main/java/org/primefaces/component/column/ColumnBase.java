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
package org.primefaces.component.column;

import javax.faces.component.UIColumn;

import org.primefaces.model.menu.MenuColumn;


abstract class ColumnBase extends UIColumn implements org.primefaces.component.api.UIColumn, MenuColumn {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ColumnRenderer";

    public enum PropertyKeys {

        sortBy,
        style,
        styleClass,
        sortFunction,
        filterBy,
        filterStyle,
        filterStyleClass,
        filterOptions,
        filterMatchMode,
        filterPosition,
        rowspan,
        colspan,
        headerText,
        footerText,
        selectionMode,
        filterMaxLength,
        resizable,
        exportable,
        filterValue,
        width,
        toggleable,
        filterFunction,
        field,
        priority,
        sortable,
        filterable,
        visible,
        selectRow,
        ariaHeaderText,
        exportFunction,
        groupRow,
        exportHeaderValue,
        exportFooterValue
    }

    public ColumnBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public java.lang.Object getSortBy() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    public void setSortBy(java.lang.Object _sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, _sortBy);
    }

    @Override
    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    @Override
    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    @Override
    public javax.el.MethodExpression getSortFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(javax.el.MethodExpression _sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, _sortFunction);
    }

    @Override
    public java.lang.Object getFilterBy() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(java.lang.Object _filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, _filterBy);
    }

    @Override
    public java.lang.String getFilterStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterStyle, null);
    }

    public void setFilterStyle(java.lang.String _filterStyle) {
        getStateHelper().put(PropertyKeys.filterStyle, _filterStyle);
    }

    @Override
    public java.lang.String getFilterStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterStyleClass, null);
    }

    public void setFilterStyleClass(java.lang.String _filterStyleClass) {
        getStateHelper().put(PropertyKeys.filterStyleClass, _filterStyleClass);
    }

    @Override
    public java.lang.Object getFilterOptions() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.filterOptions, null);
    }

    public void setFilterOptions(java.lang.Object _filterOptions) {
        getStateHelper().put(PropertyKeys.filterOptions, _filterOptions);
    }

    @Override
    public java.lang.String getFilterMatchMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, "startsWith");
    }

    public void setFilterMatchMode(java.lang.String _filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
    }

    @Override
    public java.lang.String getFilterPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterPosition, "bottom");
    }

    public void setFilterPosition(java.lang.String _filterPosition) {
        getStateHelper().put(PropertyKeys.filterPosition, _filterPosition);
    }

    @Override
    public int getRowspan() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.rowspan, 1);
    }

    public void setRowspan(int _rowspan) {
        getStateHelper().put(PropertyKeys.rowspan, _rowspan);
    }

    @Override
    public int getColspan() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.colspan, 1);
    }

    public void setColspan(int _colspan) {
        getStateHelper().put(PropertyKeys.colspan, _colspan);
    }

    @Override
    public java.lang.String getHeaderText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.headerText, null);
    }

    public void setHeaderText(java.lang.String _headerText) {
        getStateHelper().put(PropertyKeys.headerText, _headerText);
    }

    @Override
    public java.lang.String getFooterText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.footerText, null);
    }

    public void setFooterText(java.lang.String _footerText) {
        getStateHelper().put(PropertyKeys.footerText, _footerText);
    }

    @Override
    public java.lang.String getSelectionMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, null);
    }

    public void setSelectionMode(java.lang.String _selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
    }

    @Override
    public int getFilterMaxLength() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.filterMaxLength, java.lang.Integer.MAX_VALUE);
    }

    public void setFilterMaxLength(int _filterMaxLength) {
        getStateHelper().put(PropertyKeys.filterMaxLength, _filterMaxLength);
    }

    @Override
    public boolean isResizable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
    }

    public void setResizable(boolean _resizable) {
        getStateHelper().put(PropertyKeys.resizable, _resizable);
    }

    @Override
    public boolean isExportable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.exportable, true);
    }

    public void setExportable(boolean _exportable) {
        getStateHelper().put(PropertyKeys.exportable, _exportable);
    }

    @Override
    public java.lang.Object getFilterValue() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.filterValue, null);
    }

    public void setFilterValue(java.lang.Object _filterValue) {
        getStateHelper().put(PropertyKeys.filterValue, _filterValue);
    }

    @Override
    public java.lang.String getWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(java.lang.String _width) {
        getStateHelper().put(PropertyKeys.width, _width);
    }

    @Override
    public boolean isToggleable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, true);
    }

    public void setToggleable(boolean _toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, _toggleable);
    }

    @Override
    public javax.el.MethodExpression getFilterFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(javax.el.MethodExpression _filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
    }

    @Override
    public java.lang.String getField() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.field, null);
    }

    public void setField(java.lang.String _field) {
        getStateHelper().put(PropertyKeys.field, _field);
    }

    @Override
    public int getPriority() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.priority, 0);
    }

    public void setPriority(int _priority) {
        getStateHelper().put(PropertyKeys.priority, _priority);
    }

    @Override
    public boolean isSortable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.sortable, true);
    }

    public void setSortable(boolean _sortable) {
        getStateHelper().put(PropertyKeys.sortable, _sortable);
    }

    @Override
    public boolean isFilterable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.filterable, true);
    }

    public void setFilterable(boolean _filterable) {
        getStateHelper().put(PropertyKeys.filterable, _filterable);
    }

    @Override
    public boolean isVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean _visible) {
        getStateHelper().put(PropertyKeys.visible, _visible);
    }

    @Override
    public boolean isSelectRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.selectRow, true);
    }

    public void setSelectRow(boolean _selectRow) {
        getStateHelper().put(PropertyKeys.selectRow, _selectRow);
    }

    @Override
    public java.lang.String getAriaHeaderText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.ariaHeaderText, null);
    }

    public void setAriaHeaderText(java.lang.String _ariaHeaderText) {
        getStateHelper().put(PropertyKeys.ariaHeaderText, _ariaHeaderText);
    }

    @Override
    public javax.el.MethodExpression getExportFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.exportFunction, null);
    }

    public void setExportFunction(javax.el.MethodExpression _exportFunction) {
        getStateHelper().put(PropertyKeys.exportFunction, _exportFunction);
    }

    @Override
    public boolean isGroupRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.groupRow, false);
    }

    public void setGroupRow(boolean _groupRow) {
        getStateHelper().put(PropertyKeys.groupRow, _groupRow);
    }

    @Override
    public java.lang.String getExportHeaderValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.exportHeaderValue, null);
    }

    public void setExportHeaderValue(java.lang.String _exportHeaderValue) {
        getStateHelper().put(PropertyKeys.exportHeaderValue, _exportHeaderValue);
    }

    @Override
    public java.lang.String getExportFooterValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.exportFooterValue, null);
    }

    public void setExportFooterValue(java.lang.String _exportFooterValue) {
        getStateHelper().put(PropertyKeys.exportFooterValue, _exportFooterValue);
    }

}