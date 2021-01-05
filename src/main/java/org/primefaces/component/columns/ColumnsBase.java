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
package org.primefaces.component.columns;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UIData;
import org.primefaces.model.SortMeta;


public abstract class ColumnsBase extends UIData implements UIColumn {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

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
        filterValue,
        rowspan,
        colspan,
        headerText,
        footerText,
        filterMaxLength,
        resizable,
        exportable,
        width,
        toggleable,
        draggable,
        filterFunction,
        field,
        responsivePriority,
        sortable,
        filterable,
        visible,
        selectRow,
        ariaHeaderText,
        exportFunction,
        groupRow,
        exportValue,
        exportHeaderValue,
        exportFooterValue,
        sortOrder,
        sortPriority,
        nullSortOrder,
        caseSensitiveSort,
        displayPriority
    }

    public ColumnsBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Object getSortBy() {
        return getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    public void setSortBy(Object sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, sortBy);
    }

    @Override
    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    @Override
    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public javax.el.MethodExpression getSortFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(javax.el.MethodExpression sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, sortFunction);
    }

    @Override
    public Object getFilterBy() {
        return getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(Object filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, filterBy);
    }

    @Override
    public String getFilterStyle() {
        return (String) getStateHelper().eval(PropertyKeys.filterStyle, null);
    }

    public void setFilterStyle(String filterStyle) {
        getStateHelper().put(PropertyKeys.filterStyle, filterStyle);
    }

    @Override
    public String getFilterStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.filterStyleClass, null);
    }

    public void setFilterStyleClass(String filterStyleClass) {
        getStateHelper().put(PropertyKeys.filterStyleClass, filterStyleClass);
    }

    @Override
    public Object getFilterOptions() {
        return getStateHelper().eval(PropertyKeys.filterOptions, null);
    }

    public void setFilterOptions(Object filterOptions) {
        getStateHelper().put(PropertyKeys.filterOptions, filterOptions);
    }

    @Override
    public String getFilterMatchMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMatchMode, "startsWith");
    }

    public void setFilterMatchMode(String filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, filterMatchMode);
    }

    @Override
    public String getFilterPosition() {
        return (String) getStateHelper().eval(PropertyKeys.filterPosition, "bottom");
    }

    public void setFilterPosition(String filterPosition) {
        getStateHelper().put(PropertyKeys.filterPosition, filterPosition);
    }

    @Override
    public Object getFilterValue() {
        return getStateHelper().eval(PropertyKeys.filterValue, null);
    }

    public void setFilterValue(Object filterValue) {
        getStateHelper().put(PropertyKeys.filterValue, filterValue);
    }

    @Override
    public int getRowspan() {
        return (Integer) getStateHelper().eval(PropertyKeys.rowspan, 1);
    }

    public void setRowspan(int rowspan) {
        getStateHelper().put(PropertyKeys.rowspan, rowspan);
    }

    @Override
    public int getColspan() {
        return (Integer) getStateHelper().eval(PropertyKeys.colspan, 1);
    }

    public void setColspan(int colspan) {
        getStateHelper().put(PropertyKeys.colspan, colspan);
    }

    @Override
    public String getHeaderText() {
        return (String) getStateHelper().eval(PropertyKeys.headerText, null);
    }

    public void setHeaderText(String headerText) {
        getStateHelper().put(PropertyKeys.headerText, headerText);
    }

    @Override
    public String getFooterText() {
        return (String) getStateHelper().eval(PropertyKeys.footerText, null);
    }

    public void setFooterText(String footerText) {
        getStateHelper().put(PropertyKeys.footerText, footerText);
    }

    @Override
    public int getFilterMaxLength() {
        return (Integer) getStateHelper().eval(PropertyKeys.filterMaxLength, Integer.MAX_VALUE);
    }

    public void setFilterMaxLength(int filterMaxLength) {
        getStateHelper().put(PropertyKeys.filterMaxLength, filterMaxLength);
    }

    @Override
    public boolean isResizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
    }

    public void setResizable(boolean resizable) {
        getStateHelper().put(PropertyKeys.resizable, resizable);
    }

    @Override
    public boolean isExportable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.exportable, true);
    }

    public void setExportable(boolean exportable) {
        getStateHelper().put(PropertyKeys.exportable, exportable);
    }

    @Override
    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    @Override
    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, true);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    @Override
    public boolean isDraggable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
    }

    public void setDraggable(boolean draggable) {
        getStateHelper().put(PropertyKeys.draggable, draggable);
    }

    @Override
    public javax.el.MethodExpression getFilterFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(javax.el.MethodExpression filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, filterFunction);
    }

    @Override
    public String getField() {
        return (String) getStateHelper().eval(PropertyKeys.field, null);
    }

    public void setField(String field) {
        getStateHelper().put(PropertyKeys.field, field);
    }

    @Override
    public int getResponsivePriority() {
        return (Integer) getStateHelper().eval(PropertyKeys.responsivePriority, 0);
    }

    public void setResponsivePriority(int responsivePriority) {
        getStateHelper().put(PropertyKeys.responsivePriority, responsivePriority);
    }

    @Override
    public boolean isSortable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.sortable, true);
    }

    public void setSortable(boolean sortable) {
        getStateHelper().put(PropertyKeys.sortable, sortable);
    }

    @Override
    public boolean isFilterable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.filterable, true);
    }

    public void setFilterable(boolean filterable) {
        getStateHelper().put(PropertyKeys.filterable, filterable);
    }

    @Override
    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    @Override
    public boolean isSelectRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.selectRow, true);
    }

    public void setSelectRow(boolean selectRow) {
        getStateHelper().put(PropertyKeys.selectRow, selectRow);
    }

    @Override
    public String getAriaHeaderText() {
        return (String) getStateHelper().eval(PropertyKeys.ariaHeaderText, null);
    }

    public void setAriaHeaderText(String ariaHeaderText) {
        getStateHelper().put(PropertyKeys.ariaHeaderText, ariaHeaderText);
    }

    @Override
    public javax.el.MethodExpression getExportFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.exportFunction, null);
    }

    public void setExportFunction(javax.el.MethodExpression exportFunction) {
        getStateHelper().put(PropertyKeys.exportFunction, exportFunction);
    }

    @Override
    public boolean isGroupRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.groupRow, false);
    }

    public void setGroupRow(boolean groupRow) {
        getStateHelper().put(PropertyKeys.groupRow, groupRow);
    }

    @Override
    public String getExportValue() {
        return (String) getStateHelper().eval(PropertyKeys.exportValue, null);
    }

    public void setExportValue(String exportValue) {
        getStateHelper().put(PropertyKeys.exportValue, exportValue);
    }

    @Override
    public String getExportHeaderValue() {
        return (String) getStateHelper().eval(PropertyKeys.exportHeaderValue, null);
    }

    public void setExportHeaderValue(String exportHeaderValue) {
        getStateHelper().put(PropertyKeys.exportHeaderValue, exportHeaderValue);
    }

    @Override
    public String getExportFooterValue() {
        return (String) getStateHelper().eval(PropertyKeys.exportFooterValue, null);
    }

    public void setExportFooterValue(String exportFooterValue) {
        getStateHelper().put(PropertyKeys.exportFooterValue, exportFooterValue);
    }

    @Override
    public String getSortOrder() {
        return (String) getStateHelper().eval(PropertyKeys.sortOrder, null);
    }

    public void setSortOrder(String order) {
        getStateHelper().put(PropertyKeys.sortOrder, order);
    }

    @Override
    public int getSortPriority() {
        return (Integer) getStateHelper().eval(PropertyKeys.sortPriority, SortMeta.MIN_PRIORITY);
    }

    public void setSortPriority(int sortPriority) {
        getStateHelper().put(PropertyKeys.sortPriority, sortPriority);
    }

    @Override
    public int getNullSortOrder() {
        return (Integer) getStateHelper().eval(PropertyKeys.nullSortOrder, 1);
    }

    public void setNullSortOrder(int nullSortOrder) {
        getStateHelper().put(PropertyKeys.nullSortOrder, nullSortOrder);
    }

    @Override
    public boolean isCaseSensitiveSort() {
        return (Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, caseSensitiveSort);
    }

    @Override
    public int getDisplayPriority() {
        return (Integer) getStateHelper().eval(PropertyKeys.displayPriority, 0);
    }

    public void setDisplayPriority(int displayPriority) {
        getStateHelper().put(PropertyKeys.displayPriority, displayPriority);
    }
}