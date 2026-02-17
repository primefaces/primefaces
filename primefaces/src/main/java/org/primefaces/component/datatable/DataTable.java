/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.PrimeFaces;
import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.feature.DataTableFeatures;
import org.primefaces.component.datatable.feature.FilterFeature;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.component.row.Row;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.Visibility;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.ELUtils;
import org.primefaces.util.LangUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PostRestoreStateEvent;
import jakarta.faces.model.ArrayDataModel;
import jakarta.faces.model.CollectionDataModel;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.IterableDataModel;
import jakarta.faces.model.ListDataModel;

@FacesComponent(value = DataTable.COMPONENT_TYPE, namespace = DataTable.COMPONENT_FAMILY)
@FacesComponentInfo(description = "DataTable is an enhanced version of the standard Datatable that provides built-in solutions to many commons use cases"
        + " like paging, sorting, selection, lazy loading, filtering and more.")
@FacesComponentHandler(DataTableHandler.class)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class DataTable extends DataTableBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataTable";

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String TABLE_WRAPPER_CLASS = "ui-datatable-tablewrapper";
    public static final String REFLOW_CLASS = "ui-datatable-reflow";
    public static final String RTL_CLASS = "ui-datatable-rtl";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String DYNAMIC_COLUMN_HEADER_CLASS = "ui-dynamic-column";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data ui-widget-content";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String SELECTABLE_ROW_CLASS = "ui-datatable-selectable";
    public static final String EMPTY_MESSAGE_ROW_CLASS = "ui-widget-content ui-datatable-empty-message";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String SORTABLE_PRIORITY_CLASS = "ui-sortable-column-badge ui-helper-hidden";
    public static final String STATIC_COLUMN_CLASS = "ui-static-column";
    public static final String UNSELECTABLE_COLUMN_CLASS = "ui-column-unselectable";
    public static final String HIDDEN_COLUMN_CLASS = "ui-helper-hidden";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_TITLE_CLASS = "ui-column-title";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter ui-widget ui-state-default";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default";
    public static final String COLUMN_CUSTOM_FILTER_CLASS = "ui-column-customfilter";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String DRAGGABLE_COLUMN_CLASS = "ui-draggable-column";
    public static final String EXPANDED_ROW_CLASS = "ui-expanded-row";
    public static final String EXPANDED_ROW_CONTENT_CLASS = "ui-expanded-row-content";
    public static final String ROW_TOGGLER_CLASS = "ui-row-toggler";
    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String CELL_EDITOR_CLASS = "ui-cell-editor";
    public static final String CELL_EDITOR_INPUT_CLASS = "ui-cell-editor-input";
    public static final String CELL_EDITOR_OUTPUT_CLASS = "ui-cell-editor-output";
    public static final String CELL_EDITOR_DISABLED_CLASS = "ui-cell-editor-disabled";
    public static final String ROW_EDITOR_COLUMN_CLASS = "ui-row-editor-column";
    public static final String ROW_EDITOR_CLASS = "ui-row-editor ui-helper-clearfix";
    public static final String SELECTION_COLUMN_CLASS = "ui-selection-column";
    public static final String GROUPED_COLUMN_CLASS = "ui-grouped-column";
    public static final String EVEN_ROW_CLASS = "ui-datatable-even";
    public static final String ODD_ROW_CLASS = "ui-datatable-odd";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-datatable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-datatable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-datatable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-datatable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-datatable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-datatable-scrollable-footer-box";
    public static final String VIRTUALSCROLL_WRAPPER_CLASS = "ui-datatable-virtualscroll-wrapper";
    public static final String VIRTUALSCROLL_TABLE_CLASS = "ui-datatable-virtualscroll-table";
    public static final String COLUMN_RESIZER_CLASS = "ui-column-resizer";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-datatable-resizable";
    public static final String SUBTABLE_HEADER = "ui-datatable-subtable-header";
    public static final String SUBTABLE_FOOTER = "ui-datatable-subtable-footer";
    public static final String SUMMARY_ROW_CLASS = "ui-datatable-summaryrow ui-widget-header";
    public static final String HEADER_ROW_CLASS = "ui-rowgroup-header ui-datatable-headerrow ui-widget-header";
    public static final String ROW_GROUP_TOGGLER_CLASS = "ui-rowgroup-toggler";
    public static final String ROW_GROUP_TOGGLER_OPEN_ICON_CLASS = "ui-rowgroup-toggler-icon ui-icon ui-icon-circle-triangle-s";
    public static final String ROW_GROUP_TOGGLER_CLOSED_ICON_CLASS = "ui-rowgroup-toggler-icon ui-icon ui-icon-circle-triangle-e";
    public static final String EDITING_ROW_CLASS = "ui-row-editing";
    public static final String STICKY_HEADER_CLASS = "ui-datatable-sticky";
    public static final String SORT_LABEL = "primefaces.datatable.SORT_LABEL";
    public static final String SORT_ASC = "primefaces.datatable.SORT_ASC";
    public static final String SORT_DESC = "primefaces.datatable.SORT_DESC";
    public static final String STRIPED_ROWS_CLASS = "ui-datatable-striped";
    public static final String GRIDLINES_CLASS = "ui-datatable-gridlines";
    public static final String SMALL_SIZE_CLASS = "ui-datatable-sm";
    public static final String LARGE_SIZE_CLASS = "ui-datatable-lg";

    private static final Logger LOGGER = Logger.getLogger(DataTable.class.getName());

    private boolean reset = false;
    private List<UIColumn> columns;
    private final Map<PrimeClientBehaviorEventKeys, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

    protected enum InternalPropertyKeys {
        filterByAsMap,
        sortByAsMap,
        visibleColumnsAsMap,
        resizableColumnsAsMap,
        selectedRowKeys,
        selectAll,
        expandedRowKeys,
        columnMeta,
        width;
    }

    public boolean shouldEncodeFeature(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_encodeFeature");
    }

    public boolean isFullUpdateRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_fullUpdate");
    }

    public boolean isRowEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_rowEditAction");
    }

    public boolean isRowEditInitRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_rowEditInit");
    }

    public boolean isCellEditCancelRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_cellEditCancel");
    }

    public boolean isCellEditInitRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_cellEditInit");
    }

    public boolean isClientCacheRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_clientCache");
    }

    public boolean isPageStateRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pageState");
    }

    public boolean isScrollingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_scrolling");
    }

    public boolean isRowEditCancelRequest(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(getClientId(context) + "_rowEditAction");
        return "cancel".equals(value);
    }

    public boolean hasSelectionColumn() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && (child instanceof Column)) {
                boolean selectionBox = ((Column) child).isSelectionBox();
                if (selectionBox) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSelectionEnabled() {
        return getSelectionMode() != null;
    }

    public boolean isSingleSelectionMode() {
        String selectionMode = getSelectionMode();
        return "single".equalsIgnoreCase(selectionMode);
    }

    @Override
    protected void preEncode(FacesContext context) {
        super.preEncode(context);

        if (isSelectAll() && ((List<?>) getSelection()).isEmpty()) {
            setSelectAll(false);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        super.processEvent(event);

        // restored filter-state if it was filtered in the previous request
        if (event instanceof PostRestoreStateEvent
                && this == event.getComponent()
                && isFilteringEnabled()
                && isFilteringCurrentlyActive()
                && !isLazy()) {

            // restore "value" from "filteredValue" - we must work on filtered data
            // in future we might remember filtered rowKeys and skip them while rendering instead of doing it this way
            ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.name());
            if (ve != null) {
                List<?> filteredValue = getFilteredValue();
                if (filteredValue != null) {
                    setValue(convertIntoObjectValueType(getFacesContext(), this, filteredValue));
                }
            }
            else {
                // trigger filter as previous requests were filtered
                // in older PF versions, we stored the filtered data in the viewstate but this blows up memory
                // and caused bugs with editing and serialization like #7999
                filterAndSort();
            }
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        super.processValidators(context);

        //filters need to be decoded during PROCESS_VALIDATIONS phase,
        //so that local values of each filters are properly converted and validated
        FilterFeature feature = DataTableFeatures.filterFeature();
        if (feature.shouldDecode(context, this)) {
            feature.decode(context, this);
            AjaxBehaviorEvent event = deferredEvents.get(ClientBehaviorEventKeys.filter);
            if (event != null) {
                FilterEvent wrappedEvent = new FilterEvent(this, event.getBehavior(), getFilterByAsMap());
                wrappedEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                super.queueEvent(wrappedEvent);
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);

        // GitHub #8992: Must set mutate the filter value
        Map<String, FilterMeta> filterBy = getFilterByAsMap();
        ELContext elContext = context.getELContext();
        for (FilterMeta filter : filterBy.values()) {
            UIColumn column = findColumn(filter.getColumnKey());
            if (column == null) {
                continue;
            }
            ValueExpression columnFilterValueVE = column.getValueExpression(Column.PropertyKeys.filterValue.toString());
            if (columnFilterValueVE == null || columnFilterValueVE.isReadOnly(elContext)) {
                continue;
            }
            if (column.isDynamic()) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyStatelessModel();
                columnFilterValueVE.setValue(elContext, filter.getFilterValue());
                dynamicColumn.cleanStatelessModel();
            }
            else {
                columnFilterValueVE.setValue(elContext, filter.getFilterValue());
            }
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowSelect, ClientBehaviorEventKeys.rowSelectRadio,
                    ClientBehaviorEventKeys.contextMenu, ClientBehaviorEventKeys.rowSelectCheckbox, ClientBehaviorEventKeys.rowDblselect)) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowUnselect, ClientBehaviorEventKeys.rowUnselectCheckbox)) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent<>(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.page, ClientBehaviorEventKeys.virtualScroll, ClientBehaviorEventKeys.liveScroll)) {
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (first / rows) : 0;
                String rowsPerPageParam = params.get(clientId + "_rows");
                Integer rowsPerPage = null;
                if (LangUtils.isNotBlank(rowsPerPageParam)) {
                    if ("*".equals(rowsPerPageParam)) { // GitHub #14187 ShowAll option
                        rowsPerPage = rows;
                    }
                    else {
                        rowsPerPage = Integer.parseInt(rowsPerPageParam);
                    }
                }

                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page, rowsPerPage);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.sort)) {
                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), getSortByAsMap());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.filter)) {
                deferredEvents.put(ClientBehaviorEventKeys.filter, (AjaxBehaviorEvent) event);
                return;
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowEdit, ClientBehaviorEventKeys.rowEditInit, ClientBehaviorEventKeys.rowEditCancel)) {
                loadLazyDataIfRequired();

                int rowIndex = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
                setRowIndex(rowIndex);
                wrapperEvent = new RowEditEvent<>(this, behaviorEvent.getBehavior(), getRowData());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.colResize)) {
                String columnId = params.get(clientId + "_columnId");
                int width = Double.valueOf(params.get(clientId + "_width")).intValue();
                int height = Double.valueOf(params.get(clientId + "_height")).intValue();

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.toggleSelect)) {
                boolean checked = Boolean.parseBoolean(params.get(clientId + "_checked"));

                wrapperEvent = new ToggleSelectEvent(this, behaviorEvent.getBehavior(), checked);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.colReorder)) {
                wrapperEvent = behaviorEvent;
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowToggle)) {
                loadLazyDataIfRequired();

                boolean expansion = params.containsKey(clientId + "_rowExpansion");
                Visibility visibility = expansion ? Visibility.VISIBLE : Visibility.HIDDEN;
                String rowIndex = expansion ? params.get(clientId + "_expandedRowIndex") : params.get(clientId + "_collapsedRowIndex");
                setRowIndex(Integer.parseInt(rowIndex));

                wrapperEvent = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility, getRowData());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.cellEdit, ClientBehaviorEventKeys.cellEditInit,
                    ClientBehaviorEventKeys.cellEditCancel)) {
                String[] cellInfo = params.get(clientId + "_cellInfo").split(",");
                int rowIndex = Integer.parseInt(cellInfo[0]);
                int cellIndex = Integer.parseInt(cellInfo[1]);
                String rowKey = null;
                if (cellInfo.length == 3) {
                    rowKey = cellInfo[2];
                }
                int i = -1;
                UIColumn column = null;

                for (UIColumn col : getColumns()) {
                    if (col.isRendered()) {
                        i++;

                        if (i == cellIndex) {
                            column = col;
                            break;
                        }
                    }
                }

                wrapperEvent = new CellEditEvent<>(this, behaviorEvent.getBehavior(), rowIndex, column, rowKey);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowReorder)) {
                int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
                int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));

                wrapperEvent = new ReorderEvent(this, behaviorEvent.getBehavior(), fromIndex, toIndex);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.tap, ClientBehaviorEventKeys.taphold)) {
                String rowkey = params.get(clientId + "_rowkey");
                wrapperEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), getRowData(rowkey));
            }

            if (wrapperEvent == null) {
                throw new FacesException("Component " + getClass().getName() + " does not support event " + eventName + "!");
            }

            wrapperEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    public void loadLazyDataIfRequired() {
        if (getDataModel().getWrappedData() == null) {
            loadLazyDataIfEnabled();
        }
    }

    public boolean loadLazyDataIfEnabled() {
        LazyDataModel<?> lazyModel = getLazyDataModel();
        if (lazyModel != null) {
            int first = getFirst();
            int rows = 0;

            if (isLiveScroll()) {
                rows = getScrollRows();
            }
            else if (isVirtualScroll()) {
                rows = getRows();
                int scrollRows = getScrollRows();
                int virtualScrollRows = (scrollRows * 2);
                rows = (rows == 0) ? virtualScrollRows : Math.min(virtualScrollRows, rows);
            }
            else {
                rows = getRows();
            }
            loadLazyScrollData(first, rows);
        }

        return lazyModel != null;
    }

    public void loadLazyScrollData(int offset, int rows) {
        LazyDataModel<?> model = getLazyDataModel();
        if (model == null) {
            throw new FacesException("Unexpected call, datatable " + getClientId(getFacesContext()) + " is not lazy.");
        }

        Map<String, FilterMeta> filterBy = getActiveFilterMeta();
        model.setRowCount(model.count(filterBy));

        FacesContext context = getFacesContext();
        boolean clientCacheRequest = isClientCacheRequest(context);
        if (clientCacheRequest) {
            offset += rows;
        }

        if (isVirtualScroll() || isLiveScroll()) {
            setFirst(0);
        }
        else {
            setFirst(offset);
        }

        if (calculateFirst()) {
            offset = getFirst();
            LOGGER.fine(() -> "DataTable#loadLazyScrollData: offset has been recalculated due to overflow (first >= rowCount)");
            if (clientCacheRequest) {
                LOGGER.fine(() -> "DataTable#loadLazyScrollData: fetching next page has been canceled due to overflow (first >= rowCount)");
                return;
            }
        }

        List<?> data = model.load(offset, rows, getActiveSortMeta(), getActiveFilterMeta());
        model.setPageSize(rows);
        // set empty list if model returns null; this avoids multiple calls while visiting the component+rows
        model.setWrappedData(data != null ? data : Collections.emptyList());

        //Update paginator/livescroller for callback
        if (ComponentUtils.isRequestSource(this, getFacesContext()) && (isPaginator() || isLiveScroll() || isVirtualScroll())) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", model.getRowCount());
        }
    }

    public int getScrollOffset() {
        return (java.lang.Integer) getStateHelper().eval("scrollOffset", 0);
    }

    public void setScrollOffset(int scrollOffset) {
        getStateHelper().put("scrollOffset", scrollOffset);
    }

    public boolean isReset() {
        return reset;
    }

    public void resetValue() {
        setValue(null);
        setFilteredValue(null);
    }

    public void reset() {
        resetValue();
        setFirst(0);
        resetRows();
        reset = true;
        setSortByAsMap(null);
        setFilterByAsMap(null);
        setSelectedRowKeys(null);
        setScrollOffset(0);
    }

    public RowExpansion getRowExpansion() {
        return ComponentTraversalUtils.firstChild(RowExpansion.class, this);
    }

    public SubTable getSubTable() {
        return ComponentTraversalUtils.firstChildRendered(SubTable.class, this);
    }

    public <T> String getRowKey(T object) {
        DataModel<T> model = getDataModel();
        if (model instanceof SelectableDataModel) {
            return ((SelectableDataModel<T>) model).getRowKey(object);
        }
        else {
            boolean hasRowKeyVe = getValueExpression(PropertyKeys.rowKey.name()) != null;
            if (!hasRowKeyVe) {
                throw new UnsupportedOperationException("DataTable#rowKey must be defined for component " + getClientId(getFacesContext()));
            }

            return ComponentUtils.executeInRequestScope(getFacesContext(), getVar(), object, this::getRowKey);
        }
    }

    public <T> T getRowData(String rowKey) {
        DataModel<T> model = getDataModel();
        if (model instanceof SelectableDataModel) {
            return ((SelectableDataModel<T>) model).getRowData(rowKey);
        }
        else {
            Collection<T> data = (Collection<T>) getDataModel().getWrappedData();
            for (T o : data) {
                if (Objects.equals(rowKey, getRowKey(o))) {
                    return o;
                }
            }

            return null;
        }
    }

    public Set<String> getExpandedRowKeys() {
        return (Set<String>) getStateHelper().eval(InternalPropertyKeys.expandedRowKeys, Collections::emptySet);
    }

    public void setExpandedRowKeys(Set<String> expandedRowKeys) {
        getStateHelper().put(InternalPropertyKeys.expandedRowKeys, expandedRowKeys);
    }

    public Set<String> getSelectedRowKeys() {
        return (Set<String>) getStateHelper().eval(InternalPropertyKeys.selectedRowKeys, Collections::emptySet);
    }

    public void setSelectedRowKeys(Set<String> selectedRowKeys) {
        getStateHelper().put(InternalPropertyKeys.selectedRowKeys, selectedRowKeys);
    }

    /**
     * Checks if the DataTable's data is already loaded and available.
     * For non-lazy tables, this always returns true.
     * For lazy tables, this checks if the data model has been initialized and contains wrapped data.
     *
     * @return true if the table is non-lazy or if the lazy data model is loaded, false otherwise
     */
    public boolean isLazyDataLoaded() {
        if (!isLazy()) {
            return true;
        }
        DataModel<?> model = getDataModel();
        return model != null && model.getWrappedData() != null;
    }

    public String getSelectedRowKeysAsString() {
        return getSelectedRowKeys()
                .stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(","));
    }

    public boolean isSelectAll() {
        return (boolean) getStateHelper().eval(InternalPropertyKeys.selectAll, () -> false);
    }

    public void setSelectAll(boolean selectAll) {
        getStateHelper().put(InternalPropertyKeys.selectAll, selectAll);
    }

    public List<SummaryRow> getSummaryRows() {
        List<SummaryRow> sumRows = new ArrayList<>(3);
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent kid = getChildren().get(i);
            if (kid.isRendered() && kid instanceof SummaryRow) {
                sumRows.add((SummaryRow) kid);
            }
        }

        return sumRows;
    }

    @Override
    public HeaderRow getHeaderRow() {
        return ComponentTraversalUtils.firstChildRendered(HeaderRow.class, this);
    }

    @Override
    public List<UIColumn> getColumns() {
        if (this.columns != null) {
            return this.columns;
        }

        List<UIColumn> columnsTmp = collectColumns();
        if (isCacheableColumns(columnsTmp)) {
            this.columns = columnsTmp;
        }

        return columnsTmp;
    }

    @Override
    public void setColumns(List<UIColumn> columns) {
        this.columns = columns;
    }

    public String getScrollState() {
        Map<String, String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = getClientId() + "_scrollState";
        return Objects.requireNonNullElseGet(params.get(name), () -> isRTL() ? "-1,0" : "0,0");
    }

    @Override
    protected boolean shouldSkipChildren(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String paramValue = params.get(Constants.RequestParams.SKIP_CHILDREN_PARAM);
        if (paramValue != null && !Boolean.parseBoolean(paramValue)) {
            return false;
        }
        else {
            return (isSkipChildren() || params.containsKey(getClientId(context) + "_skipChildren"));
        }
    }

    public boolean isMultiSort() {
        return "multiple".equals(getSortMode());
    }

    public String resolveSelectionMode() {
        if (hasSelectionColumn()) {
            return isSingleSelectionMode() ? "radio" : "checkbox";
        }
        else {
            return getSelectionMode();
        }
    }

    @Override
    protected boolean requiresColumns() {
        return true;
    }

    @Override
    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                if (child.isRendered()) {
                    if (child instanceof UIColumn) {
                        if (child instanceof Column) {
                            for (UIComponent facet : child.getFacets().values()) {
                                process(context, facet, phaseId);
                            }
                        }
                        else if (child instanceof Columns) {
                            Columns uicolumns = (Columns) child;
                            int f = uicolumns.getFirst();
                            int r = uicolumns.getRows();
                            int l = (r == 0) ? uicolumns.getRowCount() : (f + r);

                            for (int i = f; i < l; i++) {
                                uicolumns.setRowIndex(i);

                                if (!uicolumns.isRowAvailable()) {
                                    break;
                                }

                                for (UIComponent facet : child.getFacets().values()) {
                                    process(context, facet, phaseId);
                                }
                            }

                            uicolumns.setRowIndex(-1);
                        }
                    }
                    else if (child instanceof ColumnGroup) {
                        if (child.getChildCount() > 0) {
                            for (UIComponent columnGroupChild : child.getChildren()) {
                                if (columnGroupChild instanceof Row && columnGroupChild.getChildCount() > 0) {
                                    for (UIComponent rowChild : columnGroupChild.getChildren()) {
                                        if (rowChild instanceof Column && rowChild.getFacetCount() > 0) {
                                            for (UIComponent facet : rowChild.getFacets().values()) {
                                                process(context, facet, phaseId);
                                            }
                                        }
                                        else {
                                            process(context, rowChild, phaseId);        //e.g. ui:repeat
                                        }
                                    }
                                }
                                else {
                                    process(context, columnGroupChild, phaseId);        //e.g. ui:repeat
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (getFacesContext().isPostback() && !ComponentUtils.isSkipIteration(context, context.getFacesContext())) {
            loadLazyDataIfRequired();
        }
        return super.visitRows(context, callback, visitRows);
    }

    @Override
    protected void processChildren(FacesContext context, PhaseId phaseId) {
        if (getFacesContext().isPostback()) {
            loadLazyDataIfRequired();
        }

        int first = getFirst();
        int rows = getRows();
        int rowCount = getRowCount();
        int last = 0;

        if (rows == 0) {
            if (isLiveScroll()) {
                last = getScrollRows() + getScrollOffset();
            }
            else if (isVirtualScroll()) {
                last = first + (getScrollRows() * 2);
            }
            else {
                last = rowCount;
            }
        }
        else {
            last = first + rows;
        }

        List<UIComponent> iterableChildren = null;

        for (int rowIndex = first; rowIndex < last; rowIndex++) {
            setRowIndex(rowIndex);

            if (!isRowAvailable()) {
                break;
            }

            if (iterableChildren == null) {
                iterableChildren = getIterableChildren();
            }

            for (int i = 0; i < iterableChildren.size(); i++) {
                UIComponent child = iterableChildren.get(i);
                if (child instanceof Columns) {
                    Columns columns = (Columns) child;
                    for (int j = 0; j < columns.getRowCount(); j++) {
                        columns.setRowIndex(j);

                        if (!columns.isRowAvailable()) {
                            break;
                        }

                        if (columns.isRendered()) {
                            for (int k = 0; k < columns.getChildCount(); k++) {
                                UIComponent grandkid = columns.getChildren().get(k);
                                process(context, grandkid, phaseId);
                            }
                        }
                    }
                    columns.setRowIndex(-1);
                }
                else if (child.isRendered()) {
                    if (child instanceof Column) {
                        for (int j = 0; j < child.getChildCount(); j++) {
                            UIComponent grandkid = child.getChildren().get(j);
                            process(context, grandkid, phaseId);
                        }
                    }
                    else if (child instanceof RowExpansion) {
                        Object rowData = getRowData();
                        String rowKey = getRowKey(rowData);
                        if (getExpandedRowKeys().contains(rowKey) || isExpandedRow()) {
                            process(context, child, phaseId);
                        }
                    }
                    else {
                        process(context, child, phaseId);
                    }
                }
            }
        }
    }

    public Locale resolveDataLocale() {
        return resolveDataLocale(getFacesContext());
    }

    @Override
    protected List<UIComponent> getIterableChildren() {
        List<UIComponent> iterableChildren = new ArrayList<>(getChildCount());

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (!(child instanceof ColumnGroup)) {
                iterableChildren.add(child);
            }
        }

        return iterableChildren;
    }

    @Override
    public List<?> getFilteredValue() {
        ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.name());
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setFilteredValue(List<?> filteredValue) {
        ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.name());
        if (ve != null) {
            ve.setValue(getFacesContext().getELContext(), filteredValue);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset value when filtering is enabled
        // filtering stores the filtered values the value property, so it needs to be reset; see #7336
        if (isFilteringEnabled()) {
            setValue(null);
        }

        // reset component for MyFaces view pooling
        deferredEvents.clear();
        reset = false;
        columns = null;

        return super.saveState(context);
    }

    @Override
    public void restoreMultiViewState() {
        DataTableState ts = getMultiViewState(false);
        if (ts != null) {
            if (isPaginator()) {
                setFirst(ts.getFirst());
                int rows = (ts.getRows() == 0) ? getRows() : ts.getRows();
                setRows(rows);
            }

            if (ts.getSortBy() != null) {
                updateSortByWithMVS(ts.getSortBy());
            }

            if (ts.getFilterBy() != null) {
                updateFilterByWithMVS(getFacesContext(), ts.getFilterBy());
            }

            if (isSelectionEnabled() && ts.getSelectedRowKeys() != null) {
                updateSelectionWithMVS(ts.getSelectedRowKeys());
            }

            if (ts.getExpandedRowKeys() != null) {
                updateExpansionWithMVS(ts.getExpandedRowKeys());
            }

            setColumnMeta(ts.getColumnMeta());
        }
    }

    public void updateSelectionWithMVS(Set<String> rowKeys) {
        // we have 3 states:
        // 1) multi-view state
        // 2) view state
        // 3) request state
        // in general multi-view state should only be restored on the initial request to a view
        // and then transferred into view state
        // this means that restoring MVS is NOT required on a postback actually
        if (getFacesContext().isPostback()) {
            return;
        }
        // Store rowKeys for later resolution after model loading
        // This is critical for lazy tables where data is not yet loaded
        setSelectedRowKeys(rowKeys);

        // For non-lazy tables or lazy tables with data already loaded, apply selection immediately
        // For lazy tables without data, selection will be applied in decodeSelectionRowKeys after model loading
        if (isLazyDataLoaded()) {
            DataTableFeatures.selectionFeature().decodeSelection(getFacesContext(), this, rowKeys);
        }
    }

    public void updateExpansionWithMVS(Set<String> rowKeys) {
        setExpandedRowKeys(rowKeys);
    }

    @Override
    public DataTableState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, DataTableState::new);
    }

    @Override
    public void resetMultiViewState() {
        reset();
    }

    public String getGroupedColumnIndexes() {
        return IntStream.range(0, getColumns().size())
                .filter(i -> getColumns().get(i).isGroupRow())
                .mapToObj(Objects::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public Map<String, SortMeta> getSortByAsMap() {
        return ComponentUtils.computeIfAbsent(getStateHelper(), InternalPropertyKeys.sortByAsMap, () -> initSortBy(getFacesContext()));
    }

    @Override
    public void setSortByAsMap(Map<String, SortMeta> sortBy) {
        getStateHelper().put(InternalPropertyKeys.sortByAsMap, sortBy);
    }

    @Override
    public Map<String, FilterMeta> getFilterByAsMap() {
        return (Map<String, FilterMeta>) getStateHelper().eval(InternalPropertyKeys.filterByAsMap, () -> initFilterBy(getFacesContext()));
    }

    @Override
    public void setFilterByAsMap(Map<String, FilterMeta> filterBy) {
        getStateHelper().put(InternalPropertyKeys.filterByAsMap, filterBy);
    }

    @Override
    public int getFrozenColumnsCount() {
        return getFrozenColumns();
    }

    @Override
    public boolean isFilterByAsMapDefined() {
        return getStateHelper().get(InternalPropertyKeys.filterByAsMap) != null;
    }

    @Override
    public Map<String, ColumnMeta> getColumnMeta() {
        Map<String, ColumnMeta> value =
                (Map<String, ColumnMeta>) getStateHelper().get(InternalPropertyKeys.columnMeta);
        if (value == null) {
            value = new HashMap<>();
            setColumnMeta(value);
        }
        return value;
    }

    @Override
    public void setColumnMeta(Map<String, ColumnMeta> columnMeta) {
        getStateHelper().put(InternalPropertyKeys.columnMeta, columnMeta);
    }

    @Override
    public String getWidth() {
        return (String) getStateHelper().eval(InternalPropertyKeys.width, null);
    }

    @Override
    public void setWidth(String width) {
        getStateHelper().put(InternalPropertyKeys.width, width);
    }

    /**
     * Recalculates filteredValue after adding, updating or removing rows to/from a filtered DataTable.
     * NOTE: this is only supported for non-lazy DataTables, eg bound to a java.util.List.
     */
    @Override
    public void filterAndSort() {
        if (isLazy()) {
            return;
        }

        /*
         * setDataModel is defined by UIData. So different implementations for Mojarra and MyFaces.
         * But PrimeFaces comes with it´s own UIData which extends/modifies UIData provided by Jakarta Faces-impl.
         * But PrimeFaces UIData does not know all impl-specifics, so ....
         */
        setDataModel(null); // for MyFaces 2.3 - compatibility

        DataTableFeatures.filterFeature().filter(FacesContext.getCurrentInstance(), this);
        DataTableFeatures.sortFeature().sort(FacesContext.getCurrentInstance(), this);
    }

    public void selectRow(String rowKey) {
        getSelectedRowKeys().add(rowKey);
        if (isMultiViewState()) {
            DataTableState mvs = getMultiViewState(true);
            if (mvs.getSelectedRowKeys() == null) {
                mvs.setSelectedRowKeys(new HashSet<>());
            }
            mvs.getSelectedRowKeys().add(rowKey);
        }
    }

    public void unselectRow(String rowKey) {
        if (!getSelectedRowKeys().remove(rowKey)) {
            LOGGER.log(Level.INFO, "No existing row with key {0}", rowKey);
        }
        if (isMultiViewState()) {
            DataTableState mvs = getMultiViewState(false);
            if (mvs != null && mvs.getSelectedRowKeys() != null) {
                mvs.getSelectedRowKeys().remove(rowKey);
            }
        }
    }

    public void expandRow(String rowKey) {
        getExpandedRowKeys().add(rowKey);
        if (isMultiViewState()) {
            DataTableState mvs = getMultiViewState(true);
            if (mvs.getExpandedRowKeys() == null) {
                mvs.setExpandedRowKeys(new HashSet<>());
            }
            mvs.getExpandedRowKeys().add(rowKey);
        }
    }

    public void collapseRow(String rowKey) {
        if (!getExpandedRowKeys().remove(rowKey)) {
            LOGGER.log(Level.INFO, "No existing row with key {0}", rowKey);
        }
        if (isMultiViewState()) {
            DataTableState mvs = getMultiViewState(false);
            if (mvs != null && mvs.getExpandedRowKeys() != null) {
                mvs.getExpandedRowKeys().remove(rowKey);
            }
        }
    }

    public <T> LazyDataModel<T> getLazyDataModel() {
        if (isLazy()) {
            DataModel<T> value = getDataModel();
            if (value instanceof LazyDataModel) {
                return (LazyDataModel<T>) value;
            }
        }
        return null;
    }

    public static Object convertIntoObjectValueType(FacesContext context, DataTable table, List<?> value) {
        Class<?> expectedType = ELUtils.getType(context, table.getValueExpression("value"));
        if (expectedType != null && DataModel.class.isAssignableFrom(expectedType)) {
            try {
                if (ListDataModel.class.isAssignableFrom(expectedType)) {
                    return expectedType.getConstructor(List.class).newInstance(value);
                }
                else if (CollectionDataModel.class.isAssignableFrom(expectedType)) {
                    return expectedType.getConstructor(Collection.class).newInstance(value);
                }
                else if (IterableDataModel.class.isAssignableFrom(expectedType)) {
                    return expectedType.getConstructor(Iterable.class).newInstance(value);
                }
                else if (ArrayDataModel.class.isAssignableFrom(expectedType)) {
                    return expectedType.getConstructor(Object[].class).newInstance(value.toArray());
                }
            }
            catch (ReflectiveOperationException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }

        return value;
    }

    protected boolean isCacheableColumns(List<UIColumn> columns) {
        // lets cache it only when RENDER_RESPONSE is reached, the columns might change before reaching that phase
        // see https://github.com/primefaces/primefaces/issues/2110
        // do not cache if nested in iterator component and contains dynamic columns since number of columns may vary per iteration
        // see https://github.com/primefaces/primefaces/issues/2154
        return getFacesContext().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE
                && (!isNestedWithinIterator() || columns.stream().noneMatch(DynamicColumn.class::isInstance));
    }

    @Override
    public String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, () -> {
            // if not set by xhtml, we need to check the type of the value binding
            Class<?> type = ELUtils.getType(getFacesContext(),
                    getValueExpression(PropertyKeys.selection.toString()),
                    this::getSelection);
            if (type != null) {
                String selectionMode = "single";
                if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                    selectionMode = "multiple";
                }

                // remember in ViewState, to not do the same check again
                setSelectionMode(selectionMode);

                return selectionMode;
            }
            else {
                return null;
            }
        });
    }
}
