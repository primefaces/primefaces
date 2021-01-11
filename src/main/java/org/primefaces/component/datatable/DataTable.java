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
package org.primefaces.component.datatable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.feature.*;
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
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
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
    public static final String SORTABLE_PRIORITY_CLASS = "ui-sortable-column-badge";
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
    public static final String ARIA_FILTER_BY = "primefaces.datatable.aria.FILTER_BY";
    public static final String ARIA_HEADER_CHECKBOX_ALL = "primefaces.datatable.aria.HEADER_CHECKBOX_ALL";
    public static final String SORT_LABEL = "primefaces.datatable.SORT_LABEL";
    public static final String SORT_ASC = "primefaces.datatable.SORT_ASC";
    public static final String SORT_DESC = "primefaces.datatable.SORT_DESC";
    public static final String ROW_GROUP_TOGGLER = "primefaces.rowgrouptoggler.aria.ROW_GROUP_TOGGLER";

    static final Map<DataTableFeatureKey, DataTableFeature> FEATURES = MapBuilder.<DataTableFeatureKey, DataTableFeature>builder()
            .put(DataTableFeatureKey.DRAGGABLE_COLUMNS, new DraggableColumnsFeature())
            .put(DataTableFeatureKey.FILTER, new FilterFeature())
            .put(DataTableFeatureKey.PAGE, new PageFeature())
            .put(DataTableFeatureKey.SORT, new SortFeature())
            .put(DataTableFeatureKey.RESIZABLE_COLUMNS, new ResizableColumnsFeature())
            .put(DataTableFeatureKey.SELECT, new SelectionFeature())
            .put(DataTableFeatureKey.ROW_EDIT, new RowEditFeature())
            .put(DataTableFeatureKey.CELL_EDIT, new CellEditFeature())
            .put(DataTableFeatureKey.ROW_EXPAND, new RowExpandFeature())
            .put(DataTableFeatureKey.SCROLL, new ScrollFeature())
            .put(DataTableFeatureKey.DRAGGABLE_ROWS, new DraggableRowsFeature())
            .put(DataTableFeatureKey.ADD_ROW, new AddRowFeature())
            .build();

    private static final String SB_GET_SELECTED_ROW_KEYS_AS_STRING = DataTable.class.getName() + "#getSelectedRowKeysAsString";
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
    private List<Object> selectedRowKeys = new ArrayList<>();
    private boolean isRowKeyRestored = false;
    private List<UIColumn> columns;
    private Set<Integer> expandedRowsSet;
    private Map<String, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

    public DataTableFeature getFeature(DataTableFeatureKey key) {
        return FEATURES.get(key);
    }

    public boolean shouldEncodeFeature(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_encodeFeature");
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
        String columnSelectionMode = getColumnSelectionMode();

        if (selectionMode != null) {
            return "single".equalsIgnoreCase(selectionMode);
        }
        else if (columnSelectionMode != null) {
            return "single".equalsIgnoreCase(columnSelectionMode);
        }
        else {
            return false;
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        super.processValidators(context);

        //filters need to be decoded during PROCESS_VALIDATIONS phase,
        //so that local values of each filters are properly converted and validated
        DataTableFeature feature = FEATURES.get(DataTableFeatureKey.FILTER);
        if (feature.shouldDecode(context, this)) {
            feature.decode(context, this);
            AjaxBehaviorEvent ajaxEvt = deferredEvents.get("filter");
            if (ajaxEvt != null) {
                FilterEvent evt = new FilterEvent(this, ajaxEvt.getBehavior(), getFilterByAsMap());
                evt.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                super.queueEvent(evt);
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);

        ValueExpression selectionVE = getValueExpression(PropertyKeys.selection.toString());

        if (selectionVE != null) {
            selectionVE.setValue(context.getELContext(), getLocalSelection());
            setSelection(null);
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

    public boolean hasFooterColumn() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && (child instanceof UIColumn)) {
                UIColumn column = (UIColumn) child;

                if (column.getFooterText() != null || ComponentUtils.shouldRenderFacet(column.getFacet("footer"))) {
                    return true;
                }
            }
        }

        return false;
    }

    public void loadLazyDataIfRequired() {
        if (isLazy() && ((LazyDataModel) getValue()).getWrappedData() == null) {
            loadLazyData();
        }
    }

    public void loadLazyData() {
        DataModel model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel lazyModel = (LazyDataModel) model;

            calculateFirst();

            FacesContext context = getFacesContext();
            int first = getFirst();
            int rows = getRows();

            if (isClientCacheRequest(context)) {
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                first = Integer.parseInt(params.get(getClientId(context) + "_first")) + getRows();
            }

            List<?> data = lazyModel.load(first, rows, getActiveSortMeta(), getActiveFilterMeta());
            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator/livescroller for callback
            if (ComponentUtils.isRequestSource(this, context) && (isPaginator() || isLiveScroll() || isVirtualScroll())) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
            }
        }
    }

    public void loadLazyScrollData(int offset, int rows) {
        DataModel model = getDataModel();

        if (model instanceof LazyDataModel) {
            LazyDataModel lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(offset, rows, getActiveSortMeta(), getActiveFilterMeta());

            lazyModel.setPageSize(rows);
            lazyModel.setWrappedData(data);

            //Update paginator/livescroller  for callback
            if (ComponentUtils.isRequestSource(this, getFacesContext()) && (isPaginator() || isLiveScroll() || isVirtualScroll())) {
                PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
            }
        }
    }

    public void clearLazyCache() {
        if (getDataModel() instanceof LazyDataModel) {
            LazyDataModel model = (LazyDataModel) getDataModel();
            model.setWrappedData(null);
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

    public Object getLocalSelection() {
        return getStateHelper().get(PropertyKeys.selection);
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

    public Object getRowKeyFromModel(Object object) {
        DataModel model = getDataModel();
        if (!(model instanceof SelectableDataModel)) {
            throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled.");
        }

        return ((SelectableDataModel) getDataModel()).getRowKey(object);
    }

    public Object getRowData(String rowKey) {

        boolean hasRowKeyVe = getValueExpression(PropertyKeys.rowKey.toString()) != null;
        DataModel model = getDataModel();

        // use rowKey if available and if != lazy
        // lazy must implement #getRowData
        if (hasRowKeyVe && !(model instanceof LazyDataModel)) {
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
            String var = getVar();
            Collection data = (Collection) getDataModel().getWrappedData();

            if (data != null) {
                for (Iterator it = data.iterator(); it.hasNext(); ) {
                    Object object = it.next();
                    requestMap.put(var, object);

                    if (String.valueOf(getRowKey()).equals(rowKey)) {
                        return object;
                    }
                }
            }

            return null;
        }
        else {
            if (!(model instanceof SelectableDataModel)) {
                throw new FacesException("DataModel must implement "
                        + SelectableDataModel.class.getName()
                        + " when selection is enabled or you need to define rowKey attribute");
            }

            return ((SelectableDataModel) model).getRowData(rowKey);
        }
    }

    public void findSelectedRowKeys() {
        Object selection = getSelection();
        boolean hasRowKeyVe = getValueExpression(PropertyKeys.rowKey.toString()) != null;
        String var = getVar();
        Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();

        if (isMultiViewState() && selection == null && isRowKeyRestored && getSelectedRowKeys() != null) {
            selectedRowKeys = getSelectedRowKeys();
            isRowKeyRestored = false;
        }
        else {
            selectedRowKeys = new ArrayList<>();
        }

        if (isSelectionEnabled() && selection != null) {
            if (isSingleSelectionMode()) {
                addToSelectedRowKeys(selection, requestMap, var, hasRowKeyVe);
            }
            else {
                if (selection.getClass().isArray()) {
                    for (int i = 0; i < Array.getLength(selection); i++) {
                        addToSelectedRowKeys(Array.get(selection, i), requestMap, var, hasRowKeyVe);
                    }
                }
                else {
                    List<?> list = (List<?>) selection;

                    for (Iterator<? extends Object> it = list.iterator(); it.hasNext(); ) {
                        addToSelectedRowKeys(it.next(), requestMap, var, hasRowKeyVe);
                    }
                }

            }

            requestMap.remove(var);
        }
    }

    protected void addToSelectedRowKeys(Object object, Map<String, Object> requestMap, String var, boolean hasRowKey) {
        requestMap.put(var, object);

        Object rowKey = hasRowKey ? getRowKey() : getRowKeyFromModel(object);

        if (rowKey != null) {
            selectedRowKeys.add(rowKey);
        }
    }

    public List<Object> getSelectedRowKeys() {
        return selectedRowKeys;
    }

    public String getSelectedRowKeysAsString() {
        StringBuilder builder = SharedStringBuilder.get(SB_GET_SELECTED_ROW_KEYS_AS_STRING);

        for (int i = 0; i < getSelectedRowKeys().size(); i++) {
            if (i > 0) {
                builder.append(",");
            }

            builder.append(getSelectedRowKeys().get(i));
        }

        return builder.toString();
    }

    public SummaryRow getSummaryRow() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent kid = getChildren().get(i);
            if (kid.isRendered() && kid instanceof SummaryRow) {
                return (SummaryRow) kid;
            }
        }

        return null;
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
        String tableSelectionMode = getSelectionMode();
        String columnSelectionMode = getColumnSelectionMode();
        String selectionMode = null;

        if (tableSelectionMode != null) {
            selectionMode = tableSelectionMode;
        }
        else if (columnSelectionMode != null) {
            selectionMode = "single".equals(columnSelectionMode) ? "radio" : "checkbox";
        }

        return selectionMode;
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
    protected void processChildren(FacesContext context, PhaseId phaseId) {
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
                        if (getExpandedRowsSet().contains(rowIndex)) {
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

    public Set<Integer> getExpandedRowsSet() {
        if (expandedRowsSet == null) {
            expandedRowsSet = new HashSet<>();

            FacesContext context = getFacesContext();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String expandedRows = params.get(getClientId(context) + "_rowExpansionState");

            if (!LangUtils.isValueBlank(expandedRows)) {
                String[] tmp = expandedRows.split(",");
                for (int i = 0; i < tmp.length; ++i) {
                    expandedRowsSet.add(Integer.parseInt(tmp[i]));
                }
            }
        }

        return expandedRowsSet;
    }

    public List<UIColumn> findOrderedColumns(String columnOrder) {
        FacesContext context = getFacesContext();
        List<UIColumn> orderedColumns = null;

        if (columnOrder != null) {
            orderedColumns = new ArrayList<>();

            String[] order = columnOrder.split(",");
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));

            for (String columnId : order) {

                for (UIComponent child : getChildren()) {
                    if (child instanceof Column && child.getClientId(context).equals(columnId)) {
                        orderedColumns.add((UIColumn) child);
                        break;
                    }
                    else if (child instanceof Columns) {
                        String columnsClientId = child.getClientId(context);

                        if (columnId.startsWith(columnsClientId)) {
                            String[] ids = columnId.split(separator);
                            int index = Integer.parseInt(ids[ids.length - 1]);

                            orderedColumns.add(new DynamicColumn(index, (Columns) child, context));
                            break;
                        }

                    }
                }

            }
        }

        return orderedColumns;
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

    public void updateFilteredValue(FacesContext context, List<?> value) {
        ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.toString());

        if (ve != null) {
            ve.setValue(context.getELContext(), value);
        }
        else {
            setFilteredValue(value);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        resetDynamicColumns();

        // reset component for MyFaces view pooling
        if (deferredEvents != null) {
            deferredEvents.clear();
        }
        reset = false;
        selectedRowKeys = new ArrayList<>();
        isRowKeyRestored = false;
        columns = null;
        expandedRowsSet = null;

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

            if (isSelectionEnabled()) {
                selectedRowKeys = ts.getSelectedRowKeys();
                isRowKeyRestored = true;
            }

            setColumnMeta(ts.getColumnMeta());
        }
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
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.filterByAsMap, Collections::emptyMap);
    }

    @Override
    public void setFilterByAsMap(Map<String, FilterMeta> sortBy) {
        getStateHelper().put(InternalPropertyKeys.filterByAsMap, sortBy);
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
}
