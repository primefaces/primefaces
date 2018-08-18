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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public boolean isScrollable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
    }

    public void setScrollable(boolean _scrollable) {
        getStateHelper().put(PropertyKeys.scrollable, _scrollable);
    }

    public java.lang.String getScrollHeight() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.scrollHeight, null);
    }

    public void setScrollHeight(java.lang.String _scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
    }

    public java.lang.String getScrollWidth() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.scrollWidth, null);
    }

    public void setScrollWidth(java.lang.String _scrollWidth) {
        getStateHelper().put(PropertyKeys.scrollWidth, _scrollWidth);
    }

    public java.lang.String getSelectionMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, null);
    }

    public void setSelectionMode(java.lang.String _selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
    }

    public java.lang.Object getSelection() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(java.lang.Object _selection) {
        getStateHelper().put(PropertyKeys.selection, _selection);
    }

    public java.lang.String getEmptyMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
    }

    public void setEmptyMessage(java.lang.String _emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, _emptyMessage);
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

    public boolean isLiveScroll() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveScroll, false);
    }

    public void setLiveScroll(boolean _liveScroll) {
        getStateHelper().put(PropertyKeys.liveScroll, _liveScroll);
    }

    public java.lang.String getRowStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(java.lang.String _rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, _rowStyleClass);
    }

    public java.lang.String getOnExpandStart() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onExpandStart, null);
    }

    public void setOnExpandStart(java.lang.String _onExpandStart) {
        getStateHelper().put(PropertyKeys.onExpandStart, _onExpandStart);
    }

    public boolean isResizableColumns() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean _resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, _resizableColumns);
    }

    public java.lang.Object getSortBy() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    public void setSortBy(java.lang.Object _sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, _sortBy);
    }

    public java.lang.String getSortOrder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortOrder, "ascending");
    }

    public void setSortOrder(java.lang.String _sortOrder) {
        getStateHelper().put(PropertyKeys.sortOrder, _sortOrder);
    }

    public javax.el.MethodExpression getSortFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(javax.el.MethodExpression _sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, _sortFunction);
    }

    public int getScrollRows() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollRows, 0);
    }

    public void setScrollRows(int _scrollRows) {
        getStateHelper().put(PropertyKeys.scrollRows, _scrollRows);
    }

    public java.lang.Object getRowKey() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.rowKey, null);
    }

    public void setRowKey(java.lang.Object _rowKey) {
        getStateHelper().put(PropertyKeys.rowKey, _rowKey);
    }

    public java.lang.String getFilterEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(java.lang.String _filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, _filterEvent);
    }

    public int getFilterDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.filterDelay, java.lang.Integer.MAX_VALUE);
    }

    public void setFilterDelay(int _filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, _filterDelay);
    }

    public java.lang.String getTableStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyle, null);
    }

    public void setTableStyle(java.lang.String _tableStyle) {
        getStateHelper().put(PropertyKeys.tableStyle, _tableStyle);
    }

    public java.lang.String getTableStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyleClass, null);
    }

    public void setTableStyleClass(java.lang.String _tableStyleClass) {
        getStateHelper().put(PropertyKeys.tableStyleClass, _tableStyleClass);
    }

    public boolean isDraggableColumns() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggableColumns, false);
    }

    public void setDraggableColumns(boolean _draggableColumns) {
        getStateHelper().put(PropertyKeys.draggableColumns, _draggableColumns);
    }

    public boolean isEditable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean _editable) {
        getStateHelper().put(PropertyKeys.editable, _editable);
    }

    public java.util.List getFilteredValue() {
        return (java.util.List) getStateHelper().eval(PropertyKeys.filteredValue, null);
    }

    public void setFilteredValue(java.util.List _filteredValue) {
        getStateHelper().put(PropertyKeys.filteredValue, _filteredValue);
    }

    public java.lang.String getSortMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortMode, "single");
    }

    public void setSortMode(java.lang.String _sortMode) {
        getStateHelper().put(PropertyKeys.sortMode, _sortMode);
    }

    public java.lang.String getEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.editMode, "row");
    }

    public void setEditMode(java.lang.String _editMode) {
        getStateHelper().put(PropertyKeys.editMode, _editMode);
    }

    public boolean isEditingRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editingRow, false);
    }

    public void setEditingRow(boolean _editingRow) {
        getStateHelper().put(PropertyKeys.editingRow, _editingRow);
    }

    public java.lang.String getCellSeparator() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cellSeparator, null);
    }

    public void setCellSeparator(java.lang.String _cellSeparator) {
        getStateHelper().put(PropertyKeys.cellSeparator, _cellSeparator);
    }

    public java.lang.String getSummary() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.summary, null);
    }

    public void setSummary(java.lang.String _summary) {
        getStateHelper().put(PropertyKeys.summary, _summary);
    }

    public int getFrozenRows() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frozenRows, 0);
    }

    public void setFrozenRows(int _frozenRows) {
        getStateHelper().put(PropertyKeys.frozenRows, _frozenRows);
    }

    public java.lang.String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(java.lang.String _dir) {
        getStateHelper().put(PropertyKeys.dir, _dir);
    }

    public boolean isLiveResize() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean _liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, _liveResize);
    }

    public boolean isStickyHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean _stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, _stickyHeader);
    }

    public boolean isExpandedRow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.expandedRow, false);
    }

    public void setExpandedRow(boolean _expandedRow) {
        getStateHelper().put(PropertyKeys.expandedRow, _expandedRow);
    }

    public boolean isDisabledSelection() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledSelection, false);
    }

    public void setDisabledSelection(boolean _disabledSelection) {
        getStateHelper().put(PropertyKeys.disabledSelection, _disabledSelection);
    }

    public java.lang.String getRowSelectMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowSelectMode, "new");
    }

    public void setRowSelectMode(java.lang.String _rowSelectMode) {
        getStateHelper().put(PropertyKeys.rowSelectMode, _rowSelectMode);
    }

    public java.lang.String getRowExpandMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowExpandMode, "multiple");
    }

    public void setRowExpandMode(java.lang.String _rowExpandMode) {
        getStateHelper().put(PropertyKeys.rowExpandMode, _rowExpandMode);
    }

    public java.lang.Object getDataLocale() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(java.lang.Object _dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, _dataLocale);
    }

    public boolean isNativeElements() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean _nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, _nativeElements);
    }

    public int getFrozenColumns() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.frozenColumns, 0);
    }

    public void setFrozenColumns(int _frozenColumns) {
        getStateHelper().put(PropertyKeys.frozenColumns, _frozenColumns);
    }

    public boolean isDraggableRows() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggableRows, false);
    }

    public void setDraggableRows(boolean _draggableRows) {
        getStateHelper().put(PropertyKeys.draggableRows, _draggableRows);
    }

    public boolean isCaseSensitiveSort() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean _caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, _caseSensitiveSort);
    }

    public boolean isSkipChildren() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.skipChildren, false);
    }

    public void setSkipChildren(boolean _skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, _skipChildren);
    }

    public boolean isDisabledTextSelection() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
    }

    public void setDisabledTextSelection(boolean _disabledTextSelection) {
        getStateHelper().put(PropertyKeys.disabledTextSelection, _disabledTextSelection);
    }

    public java.lang.String getSortField() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.sortField, null);
    }

    public void setSortField(java.lang.String _sortField) {
        getStateHelper().put(PropertyKeys.sortField, _sortField);
    }

    public java.lang.String getInitMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.initMode, "load");
    }

    public void setInitMode(java.lang.String _initMode) {
        getStateHelper().put(PropertyKeys.initMode, _initMode);
    }

    public int getNullSortOrder() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.nullSortOrder, 1);
    }

    public void setNullSortOrder(int _nullSortOrder) {
        getStateHelper().put(PropertyKeys.nullSortOrder, _nullSortOrder);
    }

    public java.lang.String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(java.lang.String _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public boolean isReflow() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.reflow, false);
    }

    public void setReflow(boolean _reflow) {
        getStateHelper().put(PropertyKeys.reflow, _reflow);
    }

    public int getLiveScrollBuffer() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.liveScrollBuffer, 0);
    }

    public void setLiveScrollBuffer(int _liveScrollBuffer) {
        getStateHelper().put(PropertyKeys.liveScrollBuffer, _liveScrollBuffer);
    }

    public boolean isRowHover() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.rowHover, false);
    }

    public void setRowHover(boolean _rowHover) {
        getStateHelper().put(PropertyKeys.rowHover, _rowHover);
    }

    public java.lang.String getResizeMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.resizeMode, "fit");
    }

    public void setResizeMode(java.lang.String _resizeMode) {
        getStateHelper().put(PropertyKeys.resizeMode, _resizeMode);
    }

    public java.lang.String getAriaRowLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.ariaRowLabel, null);
    }

    public void setAriaRowLabel(java.lang.String _ariaRowLabel) {
        getStateHelper().put(PropertyKeys.ariaRowLabel, _ariaRowLabel);
    }

    public boolean isSaveOnCellBlur() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.saveOnCellBlur, true);
    }

    public void setSaveOnCellBlur(boolean _saveOnCellBlur) {
        getStateHelper().put(PropertyKeys.saveOnCellBlur, _saveOnCellBlur);
    }

    public boolean isClientCache() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.clientCache, false);
    }

    public void setClientCache(boolean _clientCache) {
        getStateHelper().put(PropertyKeys.clientCache, _clientCache);
    }

    public boolean isMultiViewState() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiViewState, false);
    }

    public void setMultiViewState(boolean _multiViewState) {
        getStateHelper().put(PropertyKeys.multiViewState, _multiViewState);
    }

    public java.util.List getFilterBy() {
        return (java.util.List) getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(java.util.List _filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, _filterBy);
    }

    public java.lang.String getGlobalFilter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.globalFilter, null);
    }

    public void setGlobalFilter(java.lang.String _globalFilter) {
        getStateHelper().put(PropertyKeys.globalFilter, _globalFilter);
    }

    public java.lang.String getCellEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(java.lang.String _cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, _cellEditMode);
    }

    public boolean isExpandableRowGroups() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.expandableRowGroups, false);
    }

    public void setExpandableRowGroups(boolean _expandableRowGroups) {
        getStateHelper().put(PropertyKeys.expandableRowGroups, _expandableRowGroups);
    }

    public boolean isVirtualScroll() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.virtualScroll, false);
    }

    public void setVirtualScroll(boolean _virtualScroll) {
        getStateHelper().put(PropertyKeys.virtualScroll, _virtualScroll);
    }

    public java.lang.String getRowDragSelector() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowDragSelector, null);
    }

    public void setRowDragSelector(java.lang.String _rowDragSelector) {
        getStateHelper().put(PropertyKeys.rowDragSelector, _rowDragSelector);
    }

    public javax.el.MethodExpression getDraggableRowsFunction() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.draggableRowsFunction, null);
    }

    public void setDraggableRowsFunction(javax.el.MethodExpression _draggableRowsFunction) {
        getStateHelper().put(PropertyKeys.draggableRowsFunction, _draggableRowsFunction);
    }

    public java.lang.String getOnRowClick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onRowClick, null);
    }

    public void setOnRowClick(java.lang.String _onRowClick) {
        getStateHelper().put(PropertyKeys.onRowClick, _onRowClick);
    }

    public java.lang.String getEditInitEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.editInitEvent, "click");
    }

    public void setEditInitEvent(java.lang.String _editInitEvent) {
        getStateHelper().put(PropertyKeys.editInitEvent, _editInitEvent);
    }

    public java.lang.String getRowSelector() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowSelector, null);
    }

    public void setRowSelector(java.lang.String _rowSelector) {
        getStateHelper().put(PropertyKeys.rowSelector, _rowSelector);
    }

    public boolean isDisableContextMenuIfEmpty() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disableContextMenuIfEmpty, false);
    }

    public void setDisableContextMenuIfEmpty(boolean _disableContextMenuIfEmpty) {
        getStateHelper().put(PropertyKeys.disableContextMenuIfEmpty, _disableContextMenuIfEmpty);
    }

    public boolean isEscapeText() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.escapeText, true);
    }

    public void setEscapeText(boolean _escapeText) {
        getStateHelper().put(PropertyKeys.escapeText, _escapeText);
    }

    public java.lang.String getRowEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowEditMode, "eager");
    }

    public void setRowEditMode(java.lang.String _rowEditMode) {
        getStateHelper().put(PropertyKeys.rowEditMode, _rowEditMode);
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