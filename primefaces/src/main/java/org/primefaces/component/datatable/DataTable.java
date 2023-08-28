/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.CollectionDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.IterableDataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.PrimeFaces;
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
import org.primefaces.event.*;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.*;
import org.primefaces.util.*;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class DataTable extends DataTableBase {

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
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header ui-corner-bottom";
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
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter ui-widget ui-state-default ui-corner-left";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
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

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("page", PageEvent.class)
            .put("sort", SortEvent.class)
            .put("filter", FilterEvent.class)
            .put("rowSelect", SelectEvent.class)
            .put("rowUnselect", UnselectEvent.class)
            .put("rowEdit", RowEditEvent.class)
            .put("rowEditInit", RowEditEvent.class)
            .put("rowEditCancel", RowEditEvent.class)
            .put("colResize", ColumnResizeEvent.class)
            .put("toggleSelect", ToggleSelectEvent.class)
            .put("colReorder", null)
            .put("contextMenu", SelectEvent.class)
            .put("rowSelectRadio", SelectEvent.class)
            .put("rowSelectCheckbox", SelectEvent.class)
            .put("rowUnselectCheckbox", UnselectEvent.class)
            .put("rowDblselect", SelectEvent.class)
            .put("rowToggle", ToggleEvent.class)
            .put("cellEditInit", CellEditEvent.class)
            .put("cellEdit", CellEditEvent.class)
            .put("rowReorder", ReorderEvent.class)
            .put("tap", SelectEvent.class)
            .put("taphold", SelectEvent.class)
            .put("cellEditCancel", CellEditEvent.class)
            .put("virtualScroll", PageEvent.class)
            .put("liveScroll", PageEvent.class)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    private boolean reset = false;
    private List<UIColumn> columns;
    private Map<String, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

    protected enum InternalPropertyKeys {
        defaultFilter,
        filterByAsMap,
        defaultSort,
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

    public boolean isRowSelectionEnabled() {
        return getSelectionMode() != null;
    }

    public boolean isColumnSelectionEnabled() {
        return getColumnSelectionMode() != null;
    }

    public String getColumnSelectionMode() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && (child instanceof Column)) {
                String selectionMode = ((Column) child).getSelectionMode();
                if (selectionMode != null) {
                    return selectionMode;
                }
            }
        }

        return null;
    }

    public boolean isSelectionEnabled() {
        return isRowSelectionEnabled() || isColumnSelectionEnabled();
    }

    public boolean isSingleSelectionMode() {
        String selectionMode = getSelectionMode();

        if (LangUtils.isNotBlank(selectionMode)) {
            return "single".equalsIgnoreCase(selectionMode);
        }
        else {
            String columnSelectionMode = getColumnSelectionMode();
            return "single".equalsIgnoreCase(columnSelectionMode);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        super.processEvent(event);

        // restore "value" from "filteredValue" - we must work on filtered data when filtering is active
        // in future we might remember filtered rowKeys and skip them while rendering instead of doing it this way
        if (event instanceof PostRestoreStateEvent
                && this == event.getComponent()
                && isFilteringEnabled()
                && !isLazy()) {

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
                if (isFilteringCurrentlyActive()) {
                    filterAndSort();
                }
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
            AjaxBehaviorEvent event = deferredEvents.get("filter");
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
            if (columnFilterValueVE == null) {
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

            if ("rowSelect".equals(eventName) || "rowSelectRadio".equals(eventName) || "contextMenu".equals(eventName)
                    || "rowSelectCheckbox".equals(eventName) || "rowDblselect".equals(eventName)) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if ("rowUnselect".equals(eventName) || "rowUnselectCheckbox".equals(eventName)) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if ("page".equals(eventName) || "virtualScroll".equals(eventName) || "liveScroll".equals(eventName)) {
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (first / rows) : 0;

                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
            }
            else if ("sort".equals(eventName)) {
                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), getSortByAsMap());
            }
            else if ("filter".equals(eventName)) {
                deferredEvents.put("filter", (AjaxBehaviorEvent) event);
                return;
            }
            else if ("rowEdit".equals(eventName) || "rowEditCancel".equals(eventName) || "rowEditInit".equals(eventName)) {
                loadLazyDataIfRequired();

                int rowIndex = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
                setRowIndex(rowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), getRowData());
            }
            else if ("colResize".equals(eventName)) {
                String columnId = params.get(clientId + "_columnId");
                int width = Double.valueOf(params.get(clientId + "_width")).intValue();
                int height = Double.valueOf(params.get(clientId + "_height")).intValue();

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if ("toggleSelect".equals(eventName)) {
                boolean checked = Boolean.parseBoolean(params.get(clientId + "_checked"));

                wrapperEvent = new ToggleSelectEvent(this, behaviorEvent.getBehavior(), checked);
            }
            else if ("colReorder".equals(eventName)) {
                wrapperEvent = behaviorEvent;
            }
            else if ("rowToggle".equals(eventName)) {
                loadLazyDataIfRequired();

                boolean expansion = params.containsKey(clientId + "_rowExpansion");
                Visibility visibility = expansion ? Visibility.VISIBLE : Visibility.HIDDEN;
                String rowIndex = expansion ? params.get(clientId + "_expandedRowIndex") : params.get(clientId + "_collapsedRowIndex");
                setRowIndex(Integer.parseInt(rowIndex));

                wrapperEvent = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility, getRowData());
            }
            else if ("cellEdit".equals(eventName) || "cellEditCancel".equals(eventName) || "cellEditInit".equals(eventName)) {
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

                wrapperEvent = new CellEditEvent(this, behaviorEvent.getBehavior(), rowIndex, column, rowKey);
            }
            else if ("rowReorder".equals(eventName)) {
                int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
                int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));

                wrapperEvent = new ReorderEvent(this, behaviorEvent.getBehavior(), fromIndex, toIndex);
            }
            else if ("tap".equals(eventName) || "taphold".equals(eventName)) {
                String rowkey = params.get(clientId + "_rowkey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getRowData(rowkey));
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

        setFirst(offset);

        if (calculateFirst()) {
            offset = getFirst();
            LOGGER.warning(() -> "DataTable#loadLazyScrollData: offset has been recalculated due to overflow (first >= rowCount)");
            if (clientCacheRequest) {
                LOGGER.warning(() -> "DataTable#loadLazyScrollData: fetching next page has been canceled due to overflow (first >= rowCount)");
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
        setDefaultSort(false);
        setDefaultFilter(false);
        setSortByAsMap(null);
        setFilterByAsMap(null);
        setSelectedRowKeys(null);
        setScrollOffset(0);
    }

    public RowExpansion getRowExpansion() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof RowExpansion) {
                return (RowExpansion) child;
            }
        }

        return null;
    }

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isBodyUpdate(FacesContext context) {
        String clientId = getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_updateBody");
    }

    public SubTable getSubTable() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof SubTable) {
                return (SubTable) child;
            }
        }

        return null;
    }

    public String getRowKey(Object object) {
        DataModel model = getDataModel();
        if (model instanceof SelectableDataModel) {
            return ((SelectableDataModel) model).getRowKey(object);
        }
        else {
            boolean hasRowKeyVe = getValueExpression(PropertyKeys.rowKey.name()) != null;
            if (!hasRowKeyVe) {
                throw new UnsupportedOperationException("DataTable#rowKey must be defined for component " + getClientId(getFacesContext()));
            }

            return ComponentUtils.executeInRequestScope(getFacesContext(), getVar(), object, () -> {
                return getRowKey();
            });
        }
    }

    public Object getRowData(String rowKey) {
        DataModel model = getDataModel();
        if (model instanceof SelectableDataModel) {
            return ((SelectableDataModel) model).getRowData(rowKey);
        }
        else {
            Collection data = (Collection) getDataModel().getWrappedData();
            for (Object o : data) {
                if (Objects.equals(rowKey, getRowKey(o))) {
                    return o;
                }
            }

            return null;
        }
    }

    public Set<String> getExpandedRowKeys() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.expandedRowKeys, Collections::emptySet);
    }

    public void setExpandedRowKeys(Set<String> expandedRowKeys) {
        getStateHelper().put(InternalPropertyKeys.expandedRowKeys, expandedRowKeys);
    }

    public Set<String> getSelectedRowKeys() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.selectedRowKeys, Collections::emptySet);
    }

    public void setSelectedRowKeys(Set<String> selectedRowKeys) {
        getStateHelper().put(InternalPropertyKeys.selectedRowKeys, selectedRowKeys);
    }

    public String getSelectedRowKeysAsString() {
        return String.join(",", getSelectedRowKeys());
    }

    public boolean isSelectAll() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.selectAll, () -> false);
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
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && child instanceof HeaderRow) {
                return (HeaderRow) child;
            }
        }

        return null;
    }

    @Override
    public List<UIColumn> getColumns() {
        if (this.columns != null) {
            return this.columns;
        }

        List<UIColumn> columns = collectColumns();

        // lets cache it only when RENDER_RESPONSE is reached, the columns might change before reaching that phase
        // see https://github.com/primefaces/primefaces/issues/2110
        if (getFacesContext().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            this.columns = columns;
        }

        return columns;
    }

    @Override
    public void setColumns(List<UIColumn> columns) {
        this.columns = columns;
    }

    public String getScrollState() {
        Map<String, String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = getClientId() + "_scrollState";
        String value = params.get(name);

        return value == null ? (isRTL() ? "-1,0" : "0,0") : value;
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
        String columnSelectionMode = getColumnSelectionMode();
        if (LangUtils.isNotBlank(columnSelectionMode)) {
            return "single".equals(columnSelectionMode) ? "radio" : "checkbox";
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
                                            process(context, rowChild, phaseId);        //e.g ui:repeat
                                        }
                                    }
                                }
                                else {
                                    process(context, columnGroupChild, phaseId);        //e.g ui:repeat
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows, Set<UIComponent> rejectedChildren) {
        if (getFacesContext().isPostback() && !ComponentUtils.isSkipIteration(context, context.getFacesContext())) {
            loadLazyDataIfRequired();
        }
        return super.visitRows(context, callback, visitRows, rejectedChildren);
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
                if (child.isRendered()) {
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

    @Override
    public boolean isDefaultSort() {
        return getSortByAsMap() != null && Boolean.TRUE.equals(getStateHelper().get(InternalPropertyKeys.defaultSort));
    }

    @Override
    public void setDefaultSort(boolean defaultSort) {
        getStateHelper().put(InternalPropertyKeys.defaultSort, defaultSort);
    }

    @Override
    public boolean isDefaultFilter() {
        return Boolean.TRUE.equals(getStateHelper().get(InternalPropertyKeys.defaultFilter));
    }

    @Override
    public void setDefaultFilter(boolean defaultFilter) {
        getStateHelper().put(InternalPropertyKeys.defaultFilter, defaultFilter);
    }

    public Locale resolveDataLocale() {
        FacesContext context = getFacesContext();
        return LocaleUtils.resolveLocale(context, getDataLocale(), getClientId(context));
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

    public List<?> getFilteredValue() {
        ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.name());
        if (ve != null) {
            return (List<?>) ve.getValue(getFacesContext().getELContext());
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

        resetDynamicColumns();

        // reset component for MyFaces view pooling
        if (deferredEvents != null) {
            deferredEvents.clear();
        }
        reset = false;
        columns = null;

        return super.saveState(context);
    }

    @Override
    protected void preDecode(FacesContext context) {
        resetDynamicColumns();
        super.preDecode(context);
    }

    @Override
    protected void preValidate(FacesContext context) {
        resetDynamicColumns();
        super.preValidate(context);
    }

    @Override
    protected void preUpdate(FacesContext context) {
        resetDynamicColumns();
        super.preUpdate(context);
    }

    @Override
    protected void preEncode(FacesContext context) {
        resetDynamicColumns();
        super.preEncode(context);
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
        // and then transfered into view state
        // this means that restoring MVS is NOT required on a postback actually
        if (getFacesContext().isPostback()) {
            return;
        }
        DataTableFeatures.selectionFeature().decodeSelection(getFacesContext(), this, rowKeys);
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
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.filterByAsMap, () -> initFilterBy(getFacesContext()));
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
         * But PrimeFaces comes with it´s own UIData which extends/modifies UIData provided by JSF-impl.
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

    public LazyDataModel<Object> getLazyDataModel() {
        if (isLazy()) {
            DataModel<Object> value = getDataModel();
            if (value instanceof LazyDataModel) {
                return (LazyDataModel<Object>) value;
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
}
