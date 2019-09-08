/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.datatable;

import javax.el.MethodExpression;
import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.*;

public abstract class DataTableBase extends UIData implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable {

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
        rowEditMode,
        stickyTopAt,
        globalFilterFunction
    }

    public DataTableBase() {
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

    public boolean isScrollable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
    }

    public void setScrollable(boolean scrollable) {
        getStateHelper().put(PropertyKeys.scrollable, scrollable);
    }

    public String getScrollHeight() {
        return (String) getStateHelper().eval(PropertyKeys.scrollHeight, null);
    }

    public void setScrollHeight(String scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public String getScrollWidth() {
        return (String) getStateHelper().eval(PropertyKeys.scrollWidth, null);
    }

    public void setScrollWidth(String scrollWidth) {
        getStateHelper().put(PropertyKeys.scrollWidth, scrollWidth);
    }

    public String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, null);
    }

    public void setSelectionMode(String selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, selectionMode);
    }

    public Object getSelection() {
        return getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(Object selection) {
        getStateHelper().put(PropertyKeys.selection, selection);
    }

    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
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

    public boolean isLiveScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.liveScroll, false);
    }

    public void setLiveScroll(boolean liveScroll) {
        getStateHelper().put(PropertyKeys.liveScroll, liveScroll);
    }

    public String getRowStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public String getOnExpandStart() {
        return (String) getStateHelper().eval(PropertyKeys.onExpandStart, null);
    }

    public void setOnExpandStart(String onExpandStart) {
        getStateHelper().put(PropertyKeys.onExpandStart, onExpandStart);
    }

    public boolean isResizableColumns() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, resizableColumns);
    }

    public Object getSortBy() {
        return getStateHelper().eval(PropertyKeys.sortBy, null);
    }

    public void setSortBy(Object sortBy) {
        getStateHelper().put(PropertyKeys.sortBy, sortBy);
    }

    public String getSortOrder() {
        return (String) getStateHelper().eval(PropertyKeys.sortOrder, "ascending");
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
        return (Integer) getStateHelper().eval(PropertyKeys.scrollRows, 0);
    }

    public void setScrollRows(int scrollRows) {
        getStateHelper().put(PropertyKeys.scrollRows, scrollRows);
    }

    public Object getRowKey() {
        return getStateHelper().eval(PropertyKeys.rowKey, null);
    }

    public void setRowKey(Object rowKey) {
        getStateHelper().put(PropertyKeys.rowKey, rowKey);
    }

    public String getFilterEvent() {
        return (String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(String filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, filterEvent);
    }

    public int getFilterDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.filterDelay, Integer.MAX_VALUE);
    }

    public void setFilterDelay(int filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, filterDelay);
    }

    public String getTableStyle() {
        return (String) getStateHelper().eval(PropertyKeys.tableStyle, null);
    }

    public void setTableStyle(String tableStyle) {
        getStateHelper().put(PropertyKeys.tableStyle, tableStyle);
    }

    public String getTableStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.tableStyleClass, null);
    }

    public void setTableStyleClass(String tableStyleClass) {
        getStateHelper().put(PropertyKeys.tableStyleClass, tableStyleClass);
    }

    public boolean isDraggableColumns() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggableColumns, false);
    }

    public void setDraggableColumns(boolean draggableColumns) {
        getStateHelper().put(PropertyKeys.draggableColumns, draggableColumns);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
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
        return (String) getStateHelper().eval(PropertyKeys.sortMode, "single");
    }

    public void setSortMode(String sortMode) {
        getStateHelper().put(PropertyKeys.sortMode, sortMode);
    }

    public String getEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.editMode, "row");
    }

    public void setEditMode(String editMode) {
        getStateHelper().put(PropertyKeys.editMode, editMode);
    }

    public boolean isEditingRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editingRow, false);
    }

    public void setEditingRow(boolean editingRow) {
        getStateHelper().put(PropertyKeys.editingRow, editingRow);
    }

    public String getCellSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.cellSeparator, null);
    }

    public void setCellSeparator(String cellSeparator) {
        getStateHelper().put(PropertyKeys.cellSeparator, cellSeparator);
    }

    public String getSummary() {
        return (String) getStateHelper().eval(PropertyKeys.summary, null);
    }

    public void setSummary(String summary) {
        getStateHelper().put(PropertyKeys.summary, summary);
    }

    public int getFrozenRows() {
        return (Integer) getStateHelper().eval(PropertyKeys.frozenRows, 0);
    }

    public void setFrozenRows(int frozenRows) {
        getStateHelper().put(PropertyKeys.frozenRows, frozenRows);
    }

    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public boolean isLiveResize() {
        return (Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, liveResize);
    }

    public boolean isStickyHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, stickyHeader);
    }

    public boolean isExpandedRow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expandedRow, false);
    }

    public void setExpandedRow(boolean expandedRow) {
        getStateHelper().put(PropertyKeys.expandedRow, expandedRow);
    }

    public boolean isDisabledSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabledSelection, false);
    }

    public void setDisabledSelection(boolean disabledSelection) {
        getStateHelper().put(PropertyKeys.disabledSelection, disabledSelection);
    }

    public String getRowSelectMode() {
        return (String) getStateHelper().eval(PropertyKeys.rowSelectMode, "new");
    }

    public void setRowSelectMode(String rowSelectMode) {
        getStateHelper().put(PropertyKeys.rowSelectMode, rowSelectMode);
    }

    public String getRowExpandMode() {
        return (String) getStateHelper().eval(PropertyKeys.rowExpandMode, "multiple");
    }

    public void setRowExpandMode(String rowExpandMode) {
        getStateHelper().put(PropertyKeys.rowExpandMode, rowExpandMode);
    }

    public Object getDataLocale() {
        return getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(Object dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, dataLocale);
    }

    public boolean isNativeElements() {
        return (Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, nativeElements);
    }

    public int getFrozenColumns() {
        return (Integer) getStateHelper().eval(PropertyKeys.frozenColumns, 0);
    }

    public void setFrozenColumns(int frozenColumns) {
        getStateHelper().put(PropertyKeys.frozenColumns, frozenColumns);
    }

    public boolean isDraggableRows() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggableRows, false);
    }

    public void setDraggableRows(boolean draggableRows) {
        getStateHelper().put(PropertyKeys.draggableRows, draggableRows);
    }

    public boolean isCaseSensitiveSort() {
        return (Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, caseSensitiveSort);
    }

    public boolean isSkipChildren() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipChildren, false);
    }

    public void setSkipChildren(boolean skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, skipChildren);
    }

    public boolean isDisabledTextSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
    }

    public void setDisabledTextSelection(boolean disabledTextSelection) {
        getStateHelper().put(PropertyKeys.disabledTextSelection, disabledTextSelection);
    }

    public String getSortField() {
        return (String) getStateHelper().eval(PropertyKeys.sortField, null);
    }

    public void setSortField(String sortField) {
        getStateHelper().put(PropertyKeys.sortField, sortField);
    }

    public String getInitMode() {
        return (String) getStateHelper().eval(PropertyKeys.initMode, "load");
    }

    public void setInitMode(String initMode) {
        getStateHelper().put(PropertyKeys.initMode, initMode);
    }

    public int getNullSortOrder() {
        return (Integer) getStateHelper().eval(PropertyKeys.nullSortOrder, 1);
    }

    public void setNullSortOrder(int nullSortOrder) {
        getStateHelper().put(PropertyKeys.nullSortOrder, nullSortOrder);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public boolean isReflow() {
        return (Boolean) getStateHelper().eval(PropertyKeys.reflow, false);
    }

    public void setReflow(boolean reflow) {
        getStateHelper().put(PropertyKeys.reflow, reflow);
    }

    public int getLiveScrollBuffer() {
        return (Integer) getStateHelper().eval(PropertyKeys.liveScrollBuffer, 0);
    }

    public void setLiveScrollBuffer(int liveScrollBuffer) {
        getStateHelper().put(PropertyKeys.liveScrollBuffer, liveScrollBuffer);
    }

    public boolean isRowHover() {
        return (Boolean) getStateHelper().eval(PropertyKeys.rowHover, false);
    }

    public void setRowHover(boolean rowHover) {
        getStateHelper().put(PropertyKeys.rowHover, rowHover);
    }

    public String getResizeMode() {
        return (String) getStateHelper().eval(PropertyKeys.resizeMode, "fit");
    }

    public void setResizeMode(String resizeMode) {
        getStateHelper().put(PropertyKeys.resizeMode, resizeMode);
    }

    public String getAriaRowLabel() {
        return (String) getStateHelper().eval(PropertyKeys.ariaRowLabel, null);
    }

    public void setAriaRowLabel(String ariaRowLabel) {
        getStateHelper().put(PropertyKeys.ariaRowLabel, ariaRowLabel);
    }

    public boolean isSaveOnCellBlur() {
        return (Boolean) getStateHelper().eval(PropertyKeys.saveOnCellBlur, true);
    }

    public void setSaveOnCellBlur(boolean saveOnCellBlur) {
        getStateHelper().put(PropertyKeys.saveOnCellBlur, saveOnCellBlur);
    }

    public boolean isClientCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.clientCache, false);
    }

    public void setClientCache(boolean clientCache) {
        getStateHelper().put(PropertyKeys.clientCache, clientCache);
    }

    public boolean isMultiViewState() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiViewState, false);
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
        return (String) getStateHelper().eval(PropertyKeys.globalFilter, null);
    }

    public void setGlobalFilter(String globalFilter) {
        getStateHelper().put(PropertyKeys.globalFilter, globalFilter);
    }

    public String getCellEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(String cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, cellEditMode);
    }

    public boolean isExpandableRowGroups() {
        return (Boolean) getStateHelper().eval(PropertyKeys.expandableRowGroups, false);
    }

    public void setExpandableRowGroups(boolean expandableRowGroups) {
        getStateHelper().put(PropertyKeys.expandableRowGroups, expandableRowGroups);
    }

    public boolean isVirtualScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.virtualScroll, false);
    }

    public void setVirtualScroll(boolean virtualScroll) {
        getStateHelper().put(PropertyKeys.virtualScroll, virtualScroll);
    }

    public String getRowDragSelector() {
        return (String) getStateHelper().eval(PropertyKeys.rowDragSelector, null);
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
        return (String) getStateHelper().eval(PropertyKeys.onRowClick, null);
    }

    public void setOnRowClick(String onRowClick) {
        getStateHelper().put(PropertyKeys.onRowClick, onRowClick);
    }

    public String getEditInitEvent() {
        return (String) getStateHelper().eval(PropertyKeys.editInitEvent, "click");
    }

    public void setEditInitEvent(String editInitEvent) {
        getStateHelper().put(PropertyKeys.editInitEvent, editInitEvent);
    }

    public String getRowSelector() {
        return (String) getStateHelper().eval(PropertyKeys.rowSelector, null);
    }

    public void setRowSelector(String rowSelector) {
        getStateHelper().put(PropertyKeys.rowSelector, rowSelector);
    }

    public boolean isDisableContextMenuIfEmpty() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disableContextMenuIfEmpty, false);
    }

    public void setDisableContextMenuIfEmpty(boolean disableContextMenuIfEmpty) {
        getStateHelper().put(PropertyKeys.disableContextMenuIfEmpty, disableContextMenuIfEmpty);
    }

    public boolean isEscapeText() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escapeText, true);
    }

    public void setEscapeText(boolean escapeText) {
        getStateHelper().put(PropertyKeys.escapeText, escapeText);
    }

    public String getRowEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.rowEditMode, "eager");
    }

    public void setRowEditMode(String rowEditMode) {
        getStateHelper().put(PropertyKeys.rowEditMode, rowEditMode);
    }

    public String getStickyTopAt() {
        return (String) getStateHelper().eval(PropertyKeys.stickyTopAt, null);
    }

    public void setStickyTopAt(String stickyTopAt) {
        getStateHelper().put(PropertyKeys.stickyTopAt, stickyTopAt);
    }

    public MethodExpression getGlobalFilterFunction() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.globalFilterFunction, null);
    }

    public void setGlobalFilterFunction(MethodExpression globalFilterFunction) {
        getStateHelper().put(PropertyKeys.globalFilterFunction, globalFilterFunction);
    }
}