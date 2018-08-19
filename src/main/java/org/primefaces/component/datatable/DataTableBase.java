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
package org.primefaces.component.datatable;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.*;
import org.primefaces.util.ComponentUtils;


abstract class DataTableBase extends UIData implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataTableRenderer";

    public enum PropertyKeys {

        widgetVar,
        scrollable,
        scrollHeight,
        scrollWidth,
        selectionMode,
        selection,
        emptyMessage,
        style,
        styleClass,
        liveScroll,
        rowStyleClass,
        onExpandStart,
        resizableColumns,
        sortBy,
        sortOrder,
        sortFunction,
        scrollRows,
        rowKey,
        filterEvent,
        filterDelay,
        tableStyle,
        tableStyleClass,
        draggableColumns,
        editable,
        filteredValue,
        sortMode,
        editMode,
        editingRow,
        cellSeparator,
        summary,
        frozenRows,
        dir,
        liveResize,
        stickyHeader,
        expandedRow,
        disabledSelection,
        rowSelectMode,
        rowExpandMode,
        dataLocale,
        nativeElements,
        frozenColumns,
        draggableRows,
        caseSensitiveSort,
        skipChildren,
        disabledTextSelection,
        sortField,
        initMode,
        nullSortOrder,
        tabindex,
        reflow,
        liveScrollBuffer,
        rowHover,
        resizeMode,
        ariaRowLabel,
        saveOnCellBlur,
        clientCache,
        multiViewState,
        filterBy,
        globalFilter,
        cellEditMode,
        expandableRowGroups,
        virtualScroll,
        rowDragSelector,
        draggableRowsFunction,
        onRowClick,
        editInitEvent,
        rowSelector,
        disableContextMenuIfEmpty,
        escapeText,
        rowEditMode
    }

    public DataTableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isScrollable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
    }

    public void setScrollable(boolean scrollable) {
        getStateHelper().put(PropertyKeys.scrollable, scrollable);
    }

    public String getScrollHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.scrollHeight, null);
    }

    public void setScrollHeight(String scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public String getScrollWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.scrollWidth, null);
    }

    public void setScrollWidth(String scrollWidth) {
        getStateHelper().put(PropertyKeys.scrollWidth, scrollWidth);
    }

    public String getSelectionMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, null);
    }

    public void setSelectionMode(String selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, selectionMode);
    }

    public java.lang.Object getSelection() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(java.lang.Object selection) {
        getStateHelper().put(PropertyKeys.selection, selection);
    }

    public String getEmptyMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
    }

    public String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isLiveScroll() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveScroll, false);
    }

    public void setLiveScroll(boolean liveScroll) {
        getStateHelper().put(PropertyKeys.liveScroll, liveScroll);
    }

    public String getRowStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public String getOnExpandStart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onExpandStart, null);
    }

    public void setOnExpandStart(String onExpandStart) {
        getStateHelper().put(PropertyKeys.onExpandStart, onExpandStart);
    }

    public boolean isResizableColumns() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, resizableColumns);
    }

    public java.lang.Object getSortBy() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    public void setSortBy(java.lang.Object sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, sortBy);
    }

    public String getSortOrder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortOrder, "ascending");
    }

    public void setSortOrder(String sortOrder) {
        getStateHelper().put(PropertyKeys.sortOrder, sortOrder);
    }

    public javax.el.MethodExpression getSortFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(javax.el.MethodExpression sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, sortFunction);
    }

    public int getScrollRows() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollRows, 0);
    }

    public void setScrollRows(int scrollRows) {
        getStateHelper().put(PropertyKeys.scrollRows, scrollRows);
    }

    public java.lang.Object getRowKey() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.rowKey, null);
    }

    public void setRowKey(java.lang.Object rowKey) {
        getStateHelper().put(PropertyKeys.rowKey, rowKey);
    }

    public String getFilterEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(String filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, filterEvent);
    }

    public int getFilterDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.filterDelay, java.lang.Integer.MAX_VALUE);
    }

    public void setFilterDelay(int filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, filterDelay);
    }

    public String getTableStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyle, null);
    }

    public void setTableStyle(String tableStyle) {
        getStateHelper().put(PropertyKeys.tableStyle, tableStyle);
    }

    public String getTableStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyleClass, null);
    }

    public void setTableStyleClass(String tableStyleClass) {
        getStateHelper().put(PropertyKeys.tableStyleClass, tableStyleClass);
    }

    public boolean isDraggableColumns() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggableColumns, false);
    }

    public void setDraggableColumns(boolean draggableColumns) {
        getStateHelper().put(PropertyKeys.draggableColumns, draggableColumns);
    }

    public boolean isEditable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
    }

    public java.util.List getFilteredValue() {
        return (java.util.List) getStateHelper().eval(PropertyKeys.filteredValue, null);
    }

    public void setFilteredValue(java.util.List filteredValue) {
        getStateHelper().put(PropertyKeys.filteredValue, filteredValue);
    }

    public String getSortMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortMode, "single");
    }

    public void setSortMode(String sortMode) {
        getStateHelper().put(PropertyKeys.sortMode, sortMode);
    }

    public String getEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.editMode, "row");
    }

    public void setEditMode(String editMode) {
        getStateHelper().put(PropertyKeys.editMode, editMode);
    }

    public boolean isEditingRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editingRow, false);
    }

    public void setEditingRow(boolean editingRow) {
        getStateHelper().put(PropertyKeys.editingRow, editingRow);
    }

    public String getCellSeparator() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cellSeparator, null);
    }

    public void setCellSeparator(String cellSeparator) {
        getStateHelper().put(PropertyKeys.cellSeparator, cellSeparator);
    }

    public String getSummary() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.summary, null);
    }

    public void setSummary(String summary) {
        getStateHelper().put(PropertyKeys.summary, summary);
    }

    public int getFrozenRows() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frozenRows, 0);
    }

    public void setFrozenRows(int frozenRows) {
        getStateHelper().put(PropertyKeys.frozenRows, frozenRows);
    }

    public String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public boolean isLiveResize() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, liveResize);
    }

    public boolean isStickyHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, stickyHeader);
    }

    public boolean isExpandedRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.expandedRow, false);
    }

    public void setExpandedRow(boolean expandedRow) {
        getStateHelper().put(PropertyKeys.expandedRow, expandedRow);
    }

    public boolean isDisabledSelection() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledSelection, false);
    }

    public void setDisabledSelection(boolean disabledSelection) {
        getStateHelper().put(PropertyKeys.disabledSelection, disabledSelection);
    }

    public String getRowSelectMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowSelectMode, "new");
    }

    public void setRowSelectMode(String rowSelectMode) {
        getStateHelper().put(PropertyKeys.rowSelectMode, rowSelectMode);
    }

    public String getRowExpandMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowExpandMode, "multiple");
    }

    public void setRowExpandMode(String rowExpandMode) {
        getStateHelper().put(PropertyKeys.rowExpandMode, rowExpandMode);
    }

    public java.lang.Object getDataLocale() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(java.lang.Object dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, dataLocale);
    }

    public boolean isNativeElements() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, nativeElements);
    }

    public int getFrozenColumns() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frozenColumns, 0);
    }

    public void setFrozenColumns(int frozenColumns) {
        getStateHelper().put(PropertyKeys.frozenColumns, frozenColumns);
    }

    public boolean isDraggableRows() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggableRows, false);
    }

    public void setDraggableRows(boolean draggableRows) {
        getStateHelper().put(PropertyKeys.draggableRows, draggableRows);
    }

    public boolean isCaseSensitiveSort() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, caseSensitiveSort);
    }

    public boolean isSkipChildren() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.skipChildren, false);
    }

    public void setSkipChildren(boolean skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isDisabledTextSelection() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
    }

    public void setDisabledTextSelection(boolean disabledTextSelection) {
        getStateHelper().put(PropertyKeys.disabledTextSelection, disabledTextSelection);
    }

    public String getSortField() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortField, null);
    }

    public void setSortField(String sortField) {
        getStateHelper().put(PropertyKeys.sortField, sortField);
    }

    public String getInitMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.initMode, "load");
    }

    public void setInitMode(String initMode) {
        getStateHelper().put(PropertyKeys.initMode, initMode);
    }

    public int getNullSortOrder() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.nullSortOrder, 1);
    }

    public void setNullSortOrder(int nullSortOrder) {
        getStateHelper().put(PropertyKeys.nullSortOrder, nullSortOrder);
    }

    public String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public boolean isReflow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.reflow, false);
    }

    public void setReflow(boolean reflow) {
        getStateHelper().put(PropertyKeys.reflow, reflow);
    }

    public int getLiveScrollBuffer() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.liveScrollBuffer, 0);
    }

    public void setLiveScrollBuffer(int liveScrollBuffer) {
        getStateHelper().put(PropertyKeys.liveScrollBuffer, liveScrollBuffer);
    }

    public boolean isRowHover() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.rowHover, false);
    }

    public void setRowHover(boolean rowHover) {
        getStateHelper().put(PropertyKeys.rowHover, rowHover);
    }

    public String getResizeMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.resizeMode, "fit");
    }

    public void setResizeMode(String resizeMode) {
        getStateHelper().put(PropertyKeys.resizeMode, resizeMode);
    }

    public String getAriaRowLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.ariaRowLabel, null);
    }

    public void setAriaRowLabel(String ariaRowLabel) {
        getStateHelper().put(PropertyKeys.ariaRowLabel, ariaRowLabel);
    }

    public boolean isSaveOnCellBlur() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.saveOnCellBlur, true);
    }

    public void setSaveOnCellBlur(boolean saveOnCellBlur) {
        getStateHelper().put(PropertyKeys.saveOnCellBlur, saveOnCellBlur);
    }

    public boolean isClientCache() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.clientCache, false);
    }

    public void setClientCache(boolean clientCache) {
        getStateHelper().put(PropertyKeys.clientCache, clientCache);
    }

    public boolean isMultiViewState() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiViewState, false);
    }

    public void setMultiViewState(boolean multiViewState) {
        getStateHelper().put(PropertyKeys.multiViewState, multiViewState);
    }

    public java.util.List getFilterBy() {
        return (java.util.List) getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(java.util.List filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, filterBy);
    }

    public String getGlobalFilter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.globalFilter, null);
    }

    public void setGlobalFilter(String globalFilter) {
        getStateHelper().put(PropertyKeys.globalFilter, globalFilter);
    }

    public String getCellEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(String cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, cellEditMode);
    }

    public boolean isExpandableRowGroups() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.expandableRowGroups, false);
    }

    public void setExpandableRowGroups(boolean expandableRowGroups) {
        getStateHelper().put(PropertyKeys.expandableRowGroups, expandableRowGroups);
    }

    public boolean isVirtualScroll() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.virtualScroll, false);
    }

    public void setVirtualScroll(boolean virtualScroll) {
        getStateHelper().put(PropertyKeys.virtualScroll, virtualScroll);
    }

    public String getRowDragSelector() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowDragSelector, null);
    }

    public void setRowDragSelector(String rowDragSelector) {
        getStateHelper().put(PropertyKeys.rowDragSelector, rowDragSelector);
    }

    public javax.el.MethodExpression getDraggableRowsFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.draggableRowsFunction, null);
    }

    public void setDraggableRowsFunction(javax.el.MethodExpression draggableRowsFunction) {
        getStateHelper().put(PropertyKeys.draggableRowsFunction, draggableRowsFunction);
    }

    public String getOnRowClick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onRowClick, null);
    }

    public void setOnRowClick(String onRowClick) {
        getStateHelper().put(PropertyKeys.onRowClick, onRowClick);
    }

    public String getEditInitEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.editInitEvent, "click");
    }

    public void setEditInitEvent(String editInitEvent) {
        getStateHelper().put(PropertyKeys.editInitEvent, editInitEvent);
    }

    public String getRowSelector() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowSelector, null);
    }

    public void setRowSelector(String rowSelector) {
        getStateHelper().put(PropertyKeys.rowSelector, rowSelector);
    }

    public boolean isDisableContextMenuIfEmpty() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableContextMenuIfEmpty, false);
    }

    public void setDisableContextMenuIfEmpty(boolean disableContextMenuIfEmpty) {
        getStateHelper().put(PropertyKeys.disableContextMenuIfEmpty, disableContextMenuIfEmpty);
    }

    public boolean isEscapeText() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escapeText, true);
    }

    public void setEscapeText(boolean escapeText) {
        getStateHelper().put(PropertyKeys.escapeText, escapeText);
    }

    public String getRowEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowEditMode, "eager");
    }

    public void setRowEditMode(String rowEditMode) {
        getStateHelper().put(PropertyKeys.rowEditMode, rowEditMode);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }

    @Override
    public boolean isRTL() {
        return "rtl".equalsIgnoreCase(getDir());
    }
}