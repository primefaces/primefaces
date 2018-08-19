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
package org.primefaces.component.treetable;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UITree;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TreeTableBase extends UITree implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, Pageable {

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
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
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

    public String getEmptyMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
    }

    public boolean isResizableColumns() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
    }

    public void setResizableColumns(boolean resizableColumns) {
        getStateHelper().put(PropertyKeys.resizableColumns, resizableColumns);
    }

    public String getRowStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
    }

    public void setRowStyleClass(String rowStyleClass) {
        getStateHelper().put(PropertyKeys.rowStyleClass, rowStyleClass);
    }

    public boolean isLiveResize() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
    }

    public void setLiveResize(boolean liveResize) {
        getStateHelper().put(PropertyKeys.liveResize, liveResize);
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

    public boolean isNativeElements() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
    }

    public void setNativeElements(boolean nativeElements) {
        getStateHelper().put(PropertyKeys.nativeElements, nativeElements);
    }

    public java.lang.Object getDataLocale() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.dataLocale, null);
    }

    public void setDataLocale(java.lang.Object dataLocale) {
        getStateHelper().put(PropertyKeys.dataLocale, dataLocale);
    }

    public boolean isCaseSensitiveSort() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
    }

    public void setCaseSensitiveSort(boolean caseSensitiveSort) {
        getStateHelper().put(PropertyKeys.caseSensitiveSort, caseSensitiveSort);
    }

    public String getExpandMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.expandMode, "children");
    }

    public void setExpandMode(String expandMode) {
        getStateHelper().put(PropertyKeys.expandMode, expandMode);
    }

    public boolean isStickyHeader() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
    }

    public void setStickyHeader(boolean stickyHeader) {
        getStateHelper().put(PropertyKeys.stickyHeader, stickyHeader);
    }

    public boolean isEditable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
    }

    public void setEditable(boolean editable) {
        getStateHelper().put(PropertyKeys.editable, editable);
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

    public boolean isDisabledTextSelection() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
    }

    public void setDisabledTextSelection(boolean disabledTextSelection) {
        getStateHelper().put(PropertyKeys.disabledTextSelection, disabledTextSelection);
    }

    public boolean isPaginator() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
    }

    public void setPaginator(boolean paginator) {
        getStateHelper().put(PropertyKeys.paginator, paginator);
    }

    @Override
    public String getPaginatorTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorTemplate, "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
    }

    public void setPaginatorTemplate(String paginatorTemplate) {
        getStateHelper().put(PropertyKeys.paginatorTemplate, paginatorTemplate);
    }

    @Override
    public String getRowsPerPageTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageTemplate, null);
    }

    public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
        getStateHelper().put(PropertyKeys.rowsPerPageTemplate, rowsPerPageTemplate);
    }

    @Override
    public String getRowsPerPageLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageLabel, null);
    }

    public void setRowsPerPageLabel(String rowsPerPageLabel) {
        getStateHelper().put(PropertyKeys.rowsPerPageLabel, rowsPerPageLabel);
    }

    @Override
    public String getCurrentPageReportTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, "({currentPage} of {totalPages})");
    }

    public void setCurrentPageReportTemplate(String currentPageReportTemplate) {
        getStateHelper().put(PropertyKeys.currentPageReportTemplate, currentPageReportTemplate);
    }

    @Override
    public int getPageLinks() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.pageLinks, 10);
    }

    public void setPageLinks(int pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, pageLinks);
    }

    @Override
    public String getPaginatorPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorPosition, "both");
    }

    public void setPaginatorPosition(String paginatorPosition) {
        getStateHelper().put(PropertyKeys.paginatorPosition, paginatorPosition);
    }

    @Override
    public boolean isPaginatorAlwaysVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginatorAlwaysVisible, true);
    }

    public void setPaginatorAlwaysVisible(boolean paginatorAlwaysVisible) {
        getStateHelper().put(PropertyKeys.paginatorAlwaysVisible, paginatorAlwaysVisible);
    }

    @Override
    public int getRows() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.rows, 0);
    }

    public void setRows(int rows) {
        getStateHelper().put(PropertyKeys.rows, rows);
    }

    @Override
    public int getFirst() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.first, 0);
    }

    public void setFirst(int first) {
        getStateHelper().put(PropertyKeys.first, first);
    }

    public java.lang.Object getFilteredNode() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.filteredNode, null);
    }

    public void setFilteredNode(java.lang.Object filteredNode) {
        getStateHelper().put(PropertyKeys.filteredNode, filteredNode);
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

    public String getCellEditMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
    }

    public void setCellEditMode(String cellEditMode) {
        getStateHelper().put(PropertyKeys.cellEditMode, cellEditMode);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}