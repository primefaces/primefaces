/*
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

import org.primefaces.component.api.UITree;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import org.primefaces.model.TreeNode;
import javax.faces.model.DataModel;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.component.column.Column;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LocaleUtils;
import javax.faces.event.BehaviorEvent;
import org.primefaces.component.api.UIData;
import org.primefaces.model.filter.ContainsFilterConstraint;
import org.primefaces.model.filter.EndsWithFilterConstraint;
import org.primefaces.model.filter.EqualsFilterConstraint;
import org.primefaces.model.filter.ExactFilterConstraint;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.GlobalFilterConstraint;
import org.primefaces.model.filter.GreaterThanEqualsFilterConstraint;
import org.primefaces.model.filter.GreaterThanFilterConstraint;
import org.primefaces.model.filter.InFilterConstraint;
import org.primefaces.model.filter.LessThanEqualsFilterConstraint;
import org.primefaces.model.filter.LessThanFilterConstraint;
import org.primefaces.model.filter.StartsWithFilterConstraint;


public abstract class TreeTableBase extends UITree implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder,org.primefaces.component.api.Pageable {


	public static final String COMPONENT_TYPE = "org.primefaces.component.TreeTable";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.TreeTableRenderer";

	public enum PropertyKeys {

		widgetVar
		,style
		,styleClass
		,scrollable
		,scrollHeight
		,scrollWidth
		,tableStyle
		,tableStyleClass
		,emptyMessage
		,resizableColumns
		,rowStyleClass
		,liveResize
		,sortBy
		,sortOrder
		,sortFunction
		,nativeElements
		,dataLocale
		,caseSensitiveSort
		,expandMode
		,stickyHeader
		,editable
		,editMode
		,editingRow
		,cellSeparator
		,disabledTextSelection
		,paginator
		,paginatorTemplate
		,rowsPerPageTemplate
		,rowsPerPageLabel
		,currentPageReportTemplate
		,pageLinks
		,paginatorPosition
		,paginatorAlwaysVisible
		,rows
		,first
		,filteredNode
		,filterEvent
		,filterDelay
		,cellEditMode;
	}

	public TreeTableBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
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

	public java.lang.String getEmptyMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
	}
	public void setEmptyMessage(java.lang.String _emptyMessage) {
		getStateHelper().put(PropertyKeys.emptyMessage, _emptyMessage);
	}

	public boolean isResizableColumns() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
	}
	public void setResizableColumns(boolean _resizableColumns) {
		getStateHelper().put(PropertyKeys.resizableColumns, _resizableColumns);
	}

	public java.lang.String getRowStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
	}
	public void setRowStyleClass(java.lang.String _rowStyleClass) {
		getStateHelper().put(PropertyKeys.rowStyleClass, _rowStyleClass);
	}

	public boolean isLiveResize() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveResize, false);
	}
	public void setLiveResize(boolean _liveResize) {
		getStateHelper().put(PropertyKeys.liveResize, _liveResize);
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

	public boolean isNativeElements() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.nativeElements, false);
	}
	public void setNativeElements(boolean _nativeElements) {
		getStateHelper().put(PropertyKeys.nativeElements, _nativeElements);
	}

	public java.lang.Object getDataLocale() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.dataLocale, null);
	}
	public void setDataLocale(java.lang.Object _dataLocale) {
		getStateHelper().put(PropertyKeys.dataLocale, _dataLocale);
	}

	public boolean isCaseSensitiveSort() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.caseSensitiveSort, false);
	}
	public void setCaseSensitiveSort(boolean _caseSensitiveSort) {
		getStateHelper().put(PropertyKeys.caseSensitiveSort, _caseSensitiveSort);
	}

	public java.lang.String getExpandMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.expandMode, "children");
	}
	public void setExpandMode(java.lang.String _expandMode) {
		getStateHelper().put(PropertyKeys.expandMode, _expandMode);
	}

	public boolean isStickyHeader() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stickyHeader, false);
	}
	public void setStickyHeader(boolean _stickyHeader) {
		getStateHelper().put(PropertyKeys.stickyHeader, _stickyHeader);
	}

	public boolean isEditable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.editable, false);
	}
	public void setEditable(boolean _editable) {
		getStateHelper().put(PropertyKeys.editable, _editable);
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

	public boolean isDisabledTextSelection() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabledTextSelection, true);
	}
	public void setDisabledTextSelection(boolean _disabledTextSelection) {
		getStateHelper().put(PropertyKeys.disabledTextSelection, _disabledTextSelection);
	}

	public boolean isPaginator() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginator, false);
	}
	public void setPaginator(boolean _paginator) {
		getStateHelper().put(PropertyKeys.paginator, _paginator);
	}

	public java.lang.String getPaginatorTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorTemplate, "{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}");
	}
	public void setPaginatorTemplate(java.lang.String _paginatorTemplate) {
		getStateHelper().put(PropertyKeys.paginatorTemplate, _paginatorTemplate);
	}

	public java.lang.String getRowsPerPageTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageTemplate, null);
	}
	public void setRowsPerPageTemplate(java.lang.String _rowsPerPageTemplate) {
		getStateHelper().put(PropertyKeys.rowsPerPageTemplate, _rowsPerPageTemplate);
	}

	public java.lang.String getRowsPerPageLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowsPerPageLabel, null);
	}
	public void setRowsPerPageLabel(java.lang.String _rowsPerPageLabel) {
		getStateHelper().put(PropertyKeys.rowsPerPageLabel, _rowsPerPageLabel);
	}

	public java.lang.String getCurrentPageReportTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.currentPageReportTemplate, "({currentPage} of {totalPages})");
	}
	public void setCurrentPageReportTemplate(java.lang.String _currentPageReportTemplate) {
		getStateHelper().put(PropertyKeys.currentPageReportTemplate, _currentPageReportTemplate);
	}

	public int getPageLinks() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.pageLinks, 10);
	}
	public void setPageLinks(int _pageLinks) {
		getStateHelper().put(PropertyKeys.pageLinks, _pageLinks);
	}

	public java.lang.String getPaginatorPosition() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.paginatorPosition, "both");
	}
	public void setPaginatorPosition(java.lang.String _paginatorPosition) {
		getStateHelper().put(PropertyKeys.paginatorPosition, _paginatorPosition);
	}

	public boolean isPaginatorAlwaysVisible() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.paginatorAlwaysVisible, true);
	}
	public void setPaginatorAlwaysVisible(boolean _paginatorAlwaysVisible) {
		getStateHelper().put(PropertyKeys.paginatorAlwaysVisible, _paginatorAlwaysVisible);
	}

	public int getRows() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.rows, 0);
	}
	public void setRows(int _rows) {
		getStateHelper().put(PropertyKeys.rows, _rows);
	}

	public int getFirst() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.first, 0);
	}
	public void setFirst(int _first) {
		getStateHelper().put(PropertyKeys.first, _first);
	}

	public java.lang.Object getFilteredNode() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.filteredNode, null);
	}
	public void setFilteredNode(java.lang.Object _filteredNode) {
		getStateHelper().put(PropertyKeys.filteredNode, _filteredNode);
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

	public java.lang.String getCellEditMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.cellEditMode, "eager");
	}
	public void setCellEditMode(java.lang.String _cellEditMode) {
		getStateHelper().put(PropertyKeys.cellEditMode, _cellEditMode);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}