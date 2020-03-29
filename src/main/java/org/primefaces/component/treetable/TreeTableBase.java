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
package org.primefaces.component.treetable;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UITree;
import org.primefaces.component.api.Widget;

public abstract class TreeTableBase extends UITree implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TreeTableRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        scrollable,
        scrollHeight,
        scrollWidth,
        tableStyle,
        tableStyleClass,
        emptyMessage,
        resizableColumns,
        rowStyleClass,
        liveResize,
        sortBy,
        sortOrder,
        sortFunction,
        nativeElements,
        dataLocale,
        caseSensitiveSort,
        expandMode,
        stickyHeader,
        editable,
        editMode,
        editingRow,
        cellSeparator,
        disabledTextSelection,
        paginator,
        paginatorTemplate,
        rowsPerPageTemplate,
        rowsPerPageLabel,
        currentPageReportTemplate,
        pageLinks,
        paginatorPosition,
        paginatorAlwaysVisible,
        rows,
        first,
        filteredNode,
        filterEvent,
        filterDelay,
        cellEditMode
    }

    public TreeTableBase() {
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

    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
    }

    public boolean isResizableColumns() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, resizableColumns);
    }

    public String getRowStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public boolean isLiveResize() {
        return (Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, liveResize);
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

    public boolean isNativeElements() {
        return (Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, nativeElements);
    }

    public Object getDataLocale() {
        return getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(Object dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, dataLocale);
    }

    public boolean isCaseSensitiveSort() {
        return (Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, caseSensitiveSort);
    }

    public String getExpandMode() {
        return (String) getStateHelper().eval(PropertyKeys.expandMode, "children");
    }

    public void setExpandMode(String expandMode) {
        getStateHelper().put(PropertyKeys.expandMode, expandMode);
    }

    public boolean isStickyHeader() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, stickyHeader);
    }

    public boolean isEditable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
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

    public boolean isDisabledTextSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
    }

    public void setDisabledTextSelection(boolean disabledTextSelection) {
        getStateHelper().put(PropertyKeys.disabledTextSelection, disabledTextSelection);
    }

    public boolean isPaginator() {
        return (Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
    }

    public void setPaginator(boolean paginator) {
        getStateHelper().put(PropertyKeys.paginator, paginator);
    }

    @Override
    public String getPaginatorTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.paginatorTemplate,
                "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
    }

    public void setPaginatorTemplate(String paginatorTemplate) {
        getStateHelper().put(PropertyKeys.paginatorTemplate, paginatorTemplate);
    }

    @Override
    public String getRowsPerPageTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.rowsPerPageTemplate, null);
    }

    public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
        getStateHelper().put(PropertyKeys.rowsPerPageTemplate, rowsPerPageTemplate);
    }

    @Override
    public String getRowsPerPageLabel() {
        return (String) getStateHelper().eval(PropertyKeys.rowsPerPageLabel, null);
    }

    public void setRowsPerPageLabel(String rowsPerPageLabel) {
        getStateHelper().put(PropertyKeys.rowsPerPageLabel, rowsPerPageLabel);
    }

    @Override
    public String getCurrentPageReportTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, "({currentPage} of {totalPages})");
    }

    public void setCurrentPageReportTemplate(String currentPageReportTemplate) {
        getStateHelper().put(PropertyKeys.currentPageReportTemplate, currentPageReportTemplate);
    }

    @Override
    public int getPageLinks() {
        return (Integer) getStateHelper().eval(PropertyKeys.pageLinks, 10);
    }

    public void setPageLinks(int pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, pageLinks);
    }

    @Override
    public String getPaginatorPosition() {
        return (String) getStateHelper().eval(PropertyKeys.paginatorPosition, "both");
    }

    public void setPaginatorPosition(String paginatorPosition) {
        getStateHelper().put(PropertyKeys.paginatorPosition, paginatorPosition);
    }

    @Override
    public boolean isPaginatorAlwaysVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.paginatorAlwaysVisible, true);
    }

    public void setPaginatorAlwaysVisible(boolean paginatorAlwaysVisible) {
        getStateHelper().put(PropertyKeys.paginatorAlwaysVisible, paginatorAlwaysVisible);
    }

    @Override
    public int getRows() {
        return (Integer) getStateHelper().eval(PropertyKeys.rows, 0);
    }

    public void setRows(int rows) {
        getStateHelper().put(PropertyKeys.rows, rows);
    }

    @Override
    public int getFirst() {
        return (Integer) getStateHelper().eval(PropertyKeys.first, 0);
    }

    public void setFirst(int first) {
        getStateHelper().put(PropertyKeys.first, first);
    }

    public Object getFilteredNode() {
        return getStateHelper().eval(PropertyKeys.filteredNode, null);
    }

    public void setFilteredNode(Object filteredNode) {
        getStateHelper().put(PropertyKeys.filteredNode, filteredNode);
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

    public String getCellEditMode() {
        return (String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(String cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, cellEditMode);
    }
}