/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.model.*;
import org.primefaces.util.*;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import javax.faces.model.DataModel;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    /**
     * Backward compatibility for column properties (e.g sortBy, filterBy)
     * using old syntax #{car[column.property]}) instead of #{column.property}
     */
    private static final Pattern OLD_SYNTAX_COLUMN_PROPERTY_REGEX = Pattern.compile("^#\\{\\w+\\[(.+)]}$");
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    private int columnsCountWithSpan = -1;
    private boolean reset = false;
    private List<Object> selectedRowKeys = new ArrayList<>();
    private boolean isRowKeyRestored = false;
    private int columnsCount = -1;
    private List<UIColumn> columns;
    private Columns dynamicColumns;
    private String togglableColumnsAsString;
    private Map<String, Boolean> togglableColsMap;
    private String resizableColumnsAsString;
    private Map<String, String> resizableColsMap;
    private Set<Integer> expandedRowsSet;

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

        return value != null && value.equals("cancel");
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
            return selectionMode.equalsIgnoreCase("single");
        }
        else if (columnSelectionMode != null) {
            return columnSelectionMode.equalsIgnoreCase("single");
        }
        else {
            return false;
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

            if (eventName.equals("rowSelect") || eventName.equals("rowSelectRadio") || eventName.equals("contextMenu")
                    || eventName.equals("rowSelectCheckbox") || eventName.equals("rowDblselect")) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if (eventName.equals("rowUnselect") || eventName.equals("rowUnselectCheckbox")) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), getRowData(rowKey));
            }
            else if (eventName.equals("page") || eventName.equals("virtualScroll") || eventName.equals("liveScroll")) {
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (first / rows) : 0;

                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
            }
            else if (eventName.equals("sort")) {
                SortMeta meta;
                int sortColumnIndex = 0;

                if (isMultiSort()) {
                    String[] sortKeys = params.get(clientId + "_sortKey").split(",");
                    meta = getSortByAsMap().get(sortKeys[sortKeys.length - 1]);
                    sortColumnIndex = sortKeys.length - 1;
                }
                else {
                    meta = getSortByAsMap().get(params.get(clientId + "_sortKey"));
                }

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), (UIColumn) meta.getComponent(), meta.getOrder(), sortColumnIndex);
            }
            else if (eventName.equals("filter")) {
                wrapperEvent = new FilterEvent(this, behaviorEvent.getBehavior(), getFilteredValue());
            }
            else if (eventName.equals("rowEdit") || eventName.equals("rowEditCancel") || eventName.equals("rowEditInit")) {
                int rowIndex = Integer.parseInt(params.get(clientId + "_rowEditIndex"));
                setRowIndex(rowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), getRowData());
            }
            else if (eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Double.valueOf(params.get(clientId + "_width")).intValue();
                int height = Double.valueOf(params.get(clientId + "_height")).intValue();

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if (eventName.equals("toggleSelect")) {
                boolean checked = Boolean.parseBoolean(params.get(clientId + "_checked"));

                wrapperEvent = new ToggleSelectEvent(this, behaviorEvent.getBehavior(), checked);
            }
            else if (eventName.equals("colReorder")) {
                wrapperEvent = behaviorEvent;
            }
            else if (eventName.equals("rowToggle")) {
                boolean expansion = params.containsKey(clientId + "_rowExpansion");
                Visibility visibility = expansion ? Visibility.VISIBLE : Visibility.HIDDEN;
                String rowIndex = expansion ? params.get(clientId + "_expandedRowIndex") : params.get(clientId + "_collapsedRowIndex");
                setRowIndex(Integer.parseInt(rowIndex));

                wrapperEvent = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility, getRowData());
            }
            else if (eventName.equals("cellEdit") || eventName.equals("cellEditCancel") || eventName.equals("cellEditInit")) {
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
            else if (eventName.equals("rowReorder")) {
                int fromIndex = Integer.parseInt(params.get(clientId + "_fromIndex"));
                int toIndex = Integer.parseInt(params.get(clientId + "_toIndex"));

                wrapperEvent = new ReorderEvent(this, behaviorEvent.getBehavior(), fromIndex, toIndex);
            }
            else if (eventName.equals("tap") || eventName.equals("taphold")) {
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

    public UIColumn findColumn(String columnKey) {
        if ("globalFilter".equals(columnKey)) {
            return null;
        }

        List<UIColumn> columns = getColumns();

        //body columns
        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = columns.get(i);
            if (Objects.equals(column.getColumnKey(), columnKey)) {
                return column;
            }
        }

        //header columns
        if (getFrozenColumns() > 0) {
            UIColumn column = findColumnInGroup(columnKey, getColumnGroup("frozenHeader"));
            if (column == null) {
                column = findColumnInGroup(columnKey, getColumnGroup("scrollableHeader"));
            }

            if (column != null) {
                return column;
            }
        }
        else {
            return findColumnInGroup(columnKey, getColumnGroup("header"));
        }

        throw new FacesException("Cannot find column with key: " + columnKey);
    }

    public UIColumn findColumnInGroup(String columnKey, ColumnGroup group) {
        if (group == null) {
            return null;
        }

        for (UIComponent row : group.getChildren()) {
            for (UIComponent rowChild : row.getChildren()) {
                if (rowChild instanceof Column) {
                    Column column = (Column) rowChild;
                    if (Objects.equals(column.getColumnKey(), columnKey)) {
                        return column;
                    }
                }
                else if (rowChild instanceof Columns) {
                    Columns uiColumns = (Columns) rowChild;
                    List<DynamicColumn> dynaColumns = uiColumns.getDynamicColumns();
                    for (UIColumn column : dynaColumns) {
                        if (Objects.equals(column.getColumnKey(), columnKey)) {
                            return column;
                        }
                    }
                }
            }
        }

        return null;
    }

    public ColumnGroup getColumnGroup(String target) {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if (type != null && type.equals(target)) {
                    return colGroup;
                }
            }
        }

        return null;
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

    public void loadLazyData() {
        int first = 0;
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
            calculateFirst();
            first = getFirst();
            rows = getRows();
        }

        FacesContext context = getFacesContext();

        if (isClientCacheRequest(context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            first = Integer.parseInt(params.get(getClientId(context) + "_first")) + getRows();
        }

        loadLazyScrollData(first, rows);
    }

    public void loadLazyScrollData(int offset, int rows) {
        DataModel model = getDataModel();
        if (!(model instanceof LazyDataModel)) {
            throw new FacesException("Unexpected call, datatable " + getClientId(getFacesContext()) + " is not lazy.");
        }

        LazyDataModel lazyModel = (LazyDataModel) model;

        Map<String, SortMeta> sorters = getSortByAsMap().values().stream()
                .filter(SortMeta::isActive)
                .collect(Collectors.toMap(SortMeta::getField, Function.identity()));
        Map<String, FilterMeta> filters = getFilterByAsMap().values().stream()
                .filter(FilterMeta::isActive)
                .collect(Collectors.toMap(FilterMeta::getField, Function.identity()));
        List<?> data = lazyModel.load(offset, rows, sorters, filters);

        lazyModel.setPageSize(rows);
        lazyModel.setWrappedData(data);

        //Update paginator/livescroller  for callback
        if (ComponentUtils.isRequestSource(this, getFacesContext()) && (isPaginator() || isLiveScroll() || isVirtualScroll())) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", lazyModel.getRowCount());
        }
    }

    public static String resolveStaticField(ValueExpression expression) {
        if (expression != null) {
            String expressionString = expression.getExpressionString();
            if (expressionString.startsWith("#{")) {
                expressionString = expressionString.substring(2, expressionString.indexOf('}')); //Remove #{}
                return expressionString.substring(expressionString.indexOf('.') + 1); //Remove var
            }
        }

        return null;
    }

    /**
     * Get bean's property value from a value expression.
     * Support old syntax (e.g #{car[column.property]}) instead of #{column.property}
     * @param exprVE
     * @return
     */
    public static String resolveDynamicField(FacesContext context, ValueExpression exprVE) {
        if (exprVE == null) {
            return null;
        }

        ELContext elContext = context.getELContext();
        String exprStr = exprVE.getExpressionString();

        Matcher matcher = OLD_SYNTAX_COLUMN_PROPERTY_REGEX.matcher(exprStr );
        if (matcher.find()) {
            exprStr = matcher.group(1);
            exprVE = context.getApplication().getExpressionFactory()
                    .createValueExpression(elContext, "#{" + exprStr  + "}", String.class);
        }

        return (String) exprVE.getValue(elContext);
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
        setFilterBy(null);
    }

    public void reset() {
        resetValue();
        setFirst(0);
        resetRows();
        reset = true;
        setDefaultSort(false);
        setSortByAsMap(null);
        setFilterByAsMap(null);
        setScrollOffset(0);
    }

    public boolean isFilteringEnabled() {
        return !getFilterByAsMap().isEmpty();
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

    public HeaderRow getHeaderRow() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent kid = getChildren().get(i);
            if (kid.isRendered() && kid instanceof HeaderRow) {
                return (HeaderRow) kid;
            }
        }

        return null;
    }

    public int getColumnsCount() {
        if (columnsCount == -1) {
            columnsCount = 0;

            for (int i = 0; i < getChildCount(); i++) {
                UIComponent kid = getChildren().get(i);
                if (kid.isRendered()) {
                    if (kid instanceof Columns) {
                        int dynamicColumnsCount = ((Columns) kid).getDynamicColumns().size();
                        if (dynamicColumnsCount > 0) {
                            columnsCount += dynamicColumnsCount;
                        }
                    }
                    else if (kid instanceof Column) {
                        if (((UIColumn) kid).isVisible()) {
                            columnsCount++;
                        }
                    }
                    else if (kid instanceof SubTable) {
                        SubTable subTable = (SubTable) kid;
                        for (UIComponent subTableKid : subTable.getChildren()) {
                            if (subTableKid.isRendered() && subTableKid instanceof Column) {
                                columnsCount++;
                            }
                        }
                    }
                }
            }
        }

        return columnsCount;
    }

    public int getColumnsCountWithSpan() {
        if (columnsCountWithSpan == -1) {
            columnsCountWithSpan = 0;

            for (int i = 0; i < getChildCount(); i++) {
                UIComponent kid = getChildren().get(i);
                if (kid.isRendered()) {
                    if (kid instanceof Columns) {
                        int dynamicColumnsCount = ((Columns) kid).getDynamicColumns().size();
                        if (dynamicColumnsCount > 0) {
                            columnsCountWithSpan += dynamicColumnsCount;
                        }
                    }
                    else if (kid instanceof Column) {
                        Column col = (Column) kid;
                        if (col.isVisible()) {
                            columnsCountWithSpan += col.getColspan();
                        }
                    }
                    else if (kid instanceof SubTable) {
                        SubTable subTable = (SubTable) kid;
                        for (UIComponent subTableKid : subTable.getChildren()) {
                            if (subTableKid.isRendered() && subTableKid instanceof Column) {
                                columnsCountWithSpan += ((Column) subTableKid).getColspan();
                            }
                        }
                    }
                }
            }
        }

        return columnsCountWithSpan;
    }

    public List<UIColumn> getColumns() {
        if (this.columns != null) {
            return this.columns;
        }

        List<UIColumn> columns = new ArrayList<>();
        FacesContext context = getFacesContext();
        char separator = UINamingContainer.getSeparatorChar(context);

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof Column) {
                columns.add((Column) child);
            }
            else if (child instanceof Columns) {
                Columns uiColumns = (Columns) child;
                String uiColumnsClientId = uiColumns.getClientId(context);

                for (int j = 0; j < uiColumns.getRowCount(); j++) {
                    DynamicColumn dynaColumn = new DynamicColumn(j, uiColumns);
                    dynaColumn.setColumnKey(uiColumnsClientId + separator + j);
                    columns.add(dynaColumn);
                }
            }
        }

        // lets cache it only when RENDER_RESPONSE is reached, the columns might change before reaching that phase
        // see https://github.com/primefaces/primefaces/issues/2110
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            this.columns = columns;
        }

        return columns;
    }

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
        String sortMode = getSortMode();

        return (sortMode != null && sortMode.equals("multiple"));
    }

    public String resolveSelectionMode() {
        String tableSelectionMode = getSelectionMode();
        String columnSelectionMode = getColumnSelectionMode();
        String selectionMode = null;

        if (tableSelectionMode != null) {
            selectionMode = tableSelectionMode;
        }
        else if (columnSelectionMode != null) {
            selectionMode = columnSelectionMode.equals("single") ? "radio" : "checkbox";
        }

        return selectionMode;
    }

    @Override
    protected boolean requiresColumns() {
        return true;
    }

    public Columns getDynamicColumns() {
        return dynamicColumns;
    }

    public void setDynamicColumns(Columns value) {
        dynamicColumns = value;
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

    public boolean isDefaultSort() {
        return getSortByAsMap() != null && Boolean.TRUE.equals(getStateHelper().get("defaultSort"));
    }

    public void setDefaultSort(boolean defaultSort) {
        getStateHelper().put("defaultSort", defaultSort);
    }

    public boolean isDefaultFilter() {
        return Boolean.TRUE.equals(getStateHelper().get("defaultFilter"));
    }

    public void setDefaultFilter(boolean defaultFilter) {
        getStateHelper().put("defaultFilter", defaultFilter);
    }

    public void setTogglableColumnsAsString(String togglableColumnsAsString) {
        this.togglableColumnsAsString = togglableColumnsAsString;
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

    public Map getTogglableColumnsMap() {
        if (togglableColsMap == null) {
            togglableColsMap = new HashMap<>();
            boolean isValueBlank = LangUtils.isValueBlank(togglableColumnsAsString);

            if (isValueBlank) {
                FacesContext context = getFacesContext();
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                setTogglableColumnsAsString(params.get(getClientId(context) + "_columnTogglerState"));
            }

            if (!isValueBlank) {
                String[] colsArr = togglableColumnsAsString.split(",");
                for (int i = 0; i < colsArr.length; i++) {
                    String temp = colsArr[i];
                    int sepIndex = temp.lastIndexOf('_');
                    togglableColsMap.put(temp.substring(0, sepIndex), Boolean.parseBoolean(temp.substring(sepIndex + 1, temp.length())));
                }
            }
        }

        return togglableColsMap;
    }

    public void setTogglableColumnsMap(Map<String, Boolean> togglableColsMap) {
        this.togglableColsMap = togglableColsMap;
    }

    public String getResizableColumnsAsString() {
        return resizableColumnsAsString;
    }

    public void setResizableColumnsAsString(String resizableColumnsAsString) {
        this.resizableColumnsAsString = resizableColumnsAsString;
    }

    public Map getResizableColumnsMap() {
        if (resizableColsMap == null) {
            resizableColsMap = new HashMap<>();
            boolean isValueBlank = LangUtils.isValueBlank(resizableColumnsAsString);

            if (isValueBlank) {
                FacesContext context = getFacesContext();
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                setResizableColumnsAsString(params.get(getClientId(context) + "_resizableColumnState"));
            }

            if (!isValueBlank) {
                String[] colsArr = resizableColumnsAsString.split(",");
                for (int i = 0; i < colsArr.length; i++) {
                    String temp = colsArr[i];
                    int sepIndex = temp.lastIndexOf('_');
                    resizableColsMap.put(temp.substring(0, sepIndex), temp.substring(sepIndex + 1, temp.length()));
                }
            }
        }

        return resizableColsMap;
    }

    public void setResizableColumnsMap(Map<String, String> resizableColsMap) {
        this.resizableColsMap = resizableColsMap;
    }

    public List findOrderedColumns(String columnOrder) {
        FacesContext context = getFacesContext();
        List orderedColumns = null;

        if (columnOrder != null) {
            orderedColumns = new ArrayList();

            String[] order = columnOrder.split(",");
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));

            for (String columnId : order) {

                for (UIComponent child : getChildren()) {
                    if (child instanceof Column && child.getClientId(context).equals(columnId)) {
                        orderedColumns.add(child);
                        break;
                    }
                    else if (child instanceof Columns) {
                        String columnsClientId = child.getClientId(context);

                        if (columnId.startsWith(columnsClientId)) {
                            String[] ids = columnId.split(separator);
                            int index = Integer.parseInt(ids[ids.length - 1]);

                            orderedColumns.add(new DynamicColumn(index, (Columns) child, (columnsClientId + separator + index)));
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

    private boolean isFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_filtering");
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
        // reset component for MyFaces view pooling
        columnsCountWithSpan = -1;
        reset = false;
        selectedRowKeys = new ArrayList<>();
        isRowKeyRestored = false;
        columnsCount = -1;
        columns = null;
        dynamicColumns = null;
        togglableColumnsAsString = null;
        togglableColsMap = null;
        resizableColumnsAsString = null;
        resizableColsMap = null;
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

    private void resetDynamicColumns() {
        Columns dynamicCols = getDynamicColumns();
        if (dynamicCols != null && isNestedWithinIterator()) {
            dynamicCols.setRowIndex(-1);
            setColumns(null);
        }
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
                updateSortByWithTableState(ts.getSortBy());
            }

            if (ts.getFilterBy() != null) {
                updateFilterByWithTableState(ts.getFilterBy());
            }

            if (isSelectionEnabled()) {
                selectedRowKeys = ts.getRowKeys();
                isRowKeyRestored = true;
            }

            setColumns(findOrderedColumns(ts.getOrderedColumnsAsString()));
            setTogglableColumnsAsString(ts.getTogglableColumnsAsString());
            setResizableColumnsAsString(ts.getResizableColumnsAsString());
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

    public String getSortMetaAsString() {
        return getSortByAsMap().keySet().stream()
                .collect(Collectors.joining("','", "['", "']"));
    }

    public static ValueExpression createValueExprFromVarField(FacesContext context, String var, String field) {
        if (LangUtils.isValueBlank(var) || LangUtils.isValueBlank(field)) {
            throw new FacesException("Table 'var' and Column 'field' attributes must be non null.");
        }

        return context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{" + var + "." + field + "}", Object.class);
    }

    protected Map<String, SortMeta> initSortBy() {
        Map<String, SortMeta> sortMeta = new HashMap<>();
        AtomicBoolean sorted = new AtomicBoolean();

        HeaderRow headerRow = getHeaderRow();
        if (headerRow != null) {
            SortMeta s = SortMeta.of(getFacesContext(), getVar(), headerRow);
            sortMeta.put(s.getColumnKey(), s);
            sorted.set(true);
        }

        char separator = UINamingContainer.getSeparatorChar(getFacesContext());
        visitColumns(getFacesContext(), separator, this, c -> {
            SortMeta s = SortMeta.of(getFacesContext(), getVar(), c);
            if (s != null) {
                sorted.set(sorted.get() || s.isActive());
                sortMeta.put(s.getColumnKey(), s);
            }
        });

        // merge internal sortBy with user sortBy
        Object userSortBy = getSortBy();
        if (userSortBy != null) {
            updateSortByWithUserSortBy(sortMeta, userSortBy, sorted);
        }

        setDefaultSort(sorted.get());

        return sortMeta;
    }

    protected void updateSortByWithTableState(Map<String, SortMeta> tsSortBy) {
        boolean defaultSort = isDefaultSort();
        for (Map.Entry<String, SortMeta> entry : tsSortBy.entrySet()) {
            SortMeta intlSortBy = getSortByAsMap().get(entry.getKey());
            if (intlSortBy != null) {
                SortMeta tsSortMeta = entry.getValue();
                intlSortBy.setPriority(tsSortMeta.getPriority());
                intlSortBy.setOrder(tsSortMeta.getOrder());
                defaultSort |= intlSortBy.isActive();
            }
        }

        setDefaultSort(defaultSort);
    }

    protected void updateSortByWithUserSortBy(Map<String, SortMeta> intlSortBy, Object usrSortBy, AtomicBoolean sorted) {
        Collection<SortMeta> sortBy;
        if (usrSortBy instanceof SortMeta) {
            sortBy = Collections.singletonList((SortMeta) usrSortBy);
        }
        else if (!(usrSortBy instanceof Collection)) {
            throw new FacesException("DataTable#sortBy expects a single or a collection of SortMeta");
        }
        else {
            sortBy = (Collection<SortMeta>) usrSortBy;
        }

        for (SortMeta userSM : sortBy) {
            SortMeta intlSM = intlSortBy.values().stream()
                    .filter(o -> o.getField().equals(userSM.getField()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userSM.getField() + "' has been found"));

            ValueExpression sortByVE = userSM.getSortBy();
            if (sortByVE == null) {
                sortByVE = createValueExprFromVarField(getFacesContext(), getVar(), userSM.getField());
            }

            intlSM.setPriority(userSM.getPriority());
            intlSM.setOrder(userSM.getOrder());
            intlSM.setSortBy(sortByVE);
            intlSM.setFunction(userSM.getFunction());
            sorted.set(sorted.get() || userSM.isActive());
        }
    }

    public SortMeta getHighestPriorityActiveSortMeta() {
        return getSortByAsMap().values().stream()
                .filter(SortMeta::isActive)
                .min(Comparator.comparingInt(SortMeta::getPriority))
                .orElse(null);
    }

    public boolean isSortingCurrentlyActive() {
        return getSortByAsMap().values().stream().anyMatch(SortMeta::isActive);
    }

    public boolean isColumnSortable(UIColumn column) {
        Map<String, SortMeta> sortBy = getSortByAsMap();
        if (sortBy.containsKey(column.getColumnKey())) {
            return true;
        }

        SortMeta s = SortMeta.of(getFacesContext(), getVar(), column);
        if (s == null) {
            return false;
        }

        // unlikely to happen, in case columns change between two ajax requests
        sortBy.put(s.getColumnKey(), s);
        setSortByAsMap(sortBy);
        return true;
    }

    protected void visitColumns(FacesContext context, char separator, UIComponent root, Consumer<UIColumn> visitor) {
        for (int i = 0; i < root.getChildCount(); i++) {
            UIComponent child = root.getChildren().get(i);
            if (child.isRendered()) {
                if (child instanceof Columns) {
                    Columns columns = (Columns) child;
                    String uiColumnsClientId = columns.getClientId(context);

                    for (int j = 0; j < columns.getRowCount(); j++) {
                        DynamicColumn dynaColumn = new DynamicColumn(j, columns);
                        dynaColumn.setColumnKey(uiColumnsClientId + separator + j);
                        visitor.accept(dynaColumn);
                    }
                }
                else if (child instanceof UIColumn) {
                    UIColumn column = (UIColumn) child;
                    visitor.accept(column);
                }
                else if (child instanceof ColumnGroup) {
                    visitColumns(context, separator, child, visitor);
                }
            }
        }
    }

    public Map<String, FilterMeta> initFilterBy() {
        boolean invalidate = getStateHelper().get("_filterBy") == null;
        Map<String, FilterMeta> filterBy = invalidate ? new HashMap<>() : getFilterByAsMap();
        AtomicBoolean filtered = invalidate ? new AtomicBoolean() : new AtomicBoolean(isDefaultFilter());

        // build columns filterBy
        char separator = UINamingContainer.getSeparatorChar(getFacesContext());

        visitColumns(getFacesContext(), separator, this, c -> {
            FilterMeta f = filterBy.get(c.getColumnKey());
            if (f != null && !invalidate) {
                f.setColumn(c);
            }
            else {
                f = FilterMeta.of(getFacesContext(), getVar(), c);
                if (f != null) {
                    filterBy.put(f.getColumnKey(), f);
                    filtered.set(filtered.get() || f.isActive());
                }
            }
        });

        // merge internal filterBy with user filterBy
        Object userfilterBy = getFilterBy();
        if (userfilterBy != null) {
            updateFilterByWithUserFilterBy(filterBy, userfilterBy, filtered);
        }

        // build global filterBy
        updateFilterByWithGlobalFilter(filterBy, filtered);

        // finally set if default filtering is enabled
        setDefaultFilter(filtered.get());

        setFilterByAsMap(filterBy);

        return filterBy;
    }

    protected void updateFilterByWithTableState(Map<String, FilterMeta> tsFilterBy) {
        boolean defaultFilter = isDefaultFilter();
        for (Map.Entry<String, FilterMeta> entry : tsFilterBy.entrySet()) {
            FilterMeta intlFilterBy = getFilterByAsMap().get(entry.getKey());
            if (intlFilterBy != null) {
                FilterMeta tsFilterMeta = entry.getValue();
                intlFilterBy.setFilterValue(tsFilterMeta.getFilterValue());
                defaultFilter |= intlFilterBy.isActive();
            }
        }

        setDefaultFilter(defaultFilter);
    }

    protected void updateFilterByWithUserFilterBy(Map<String, FilterMeta> intlFilterBy, Object usrFilterBy, AtomicBoolean filtered) {
        Collection<FilterMeta> filterByTmp;
        if (usrFilterBy instanceof FilterMeta) {
            filterByTmp = Collections.singletonList((FilterMeta) usrFilterBy);
        }
        else if (!(usrFilterBy instanceof Collection)) {
            throw new FacesException("DataTable#filterBy expects a single or a collection of FilterMeta");
        }
        else {
            filterByTmp = (Collection<FilterMeta>) usrFilterBy;
        }

        for (FilterMeta userFM : filterByTmp) {
            FilterMeta intlFM = intlFilterBy.values().stream()
                    .filter(o -> o.getField().equals(userFM.getField()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userFM.getField() + "' has been found"));

            ValueExpression filterByVE = userFM.getFilterBy();
            if (filterByVE == null) {
                filterByVE = createValueExprFromVarField(getFacesContext(), getVar(), userFM.getField());
            }

            intlFM.setFilterValue(userFM.getFilterValue());
            intlFM.setFilterBy(filterByVE);
            intlFM.setConstraint(userFM.getConstraint());
            intlFM.setMatchMode(userFM.getMatchMode());
            filtered.set(filtered.get() || userFM.isActive());
        }
    }

    protected void updateFilterByWithGlobalFilter(Map<String, FilterMeta> filterBy, AtomicBoolean filtered) {
        String globalFilter = getGlobalFilter();
        Set<SearchExpressionHint> hint = LangUtils.isValueBlank(globalFilter)
                ? EnumSet.of(SearchExpressionHint.IGNORE_NO_RESULT)
                : Collections.emptySet();
        UIComponent globalFilterComponent = SearchExpressionFacade
                .resolveComponent(getFacesContext(), this, DataTable.PropertyKeys.globalFilter.toString(), hint);
        if (globalFilterComponent != null) {
            if (globalFilterComponent instanceof ValueHolder) {
                ((ValueHolder) globalFilterComponent).setValue(globalFilter);
            }
            FilterMeta globalFilterBy = FilterMeta.of(filterBy.values(), globalFilter, getGlobalFilterFunction());
            filterBy.put(globalFilterBy.getColumnKey(), globalFilterBy);
            filtered.set(filtered.get() || globalFilterBy.isActive());
        }
    }

    public boolean isColumnFilterable(UIColumn column) {
        Map<String, FilterMeta> filterBy = getFilterByAsMap();
        return filterBy.containsKey(column.getColumnKey());
    }

    public Map<String, SortMeta> getSortByAsMap() {
        return ComponentUtils.computeIfAbsent(getStateHelper(), "_sortBy", this::initSortBy);
    }

    public void setSortByAsMap(Map<String, SortMeta> sortBy) {
        getStateHelper().put("_sortBy", sortBy);
    }

    public Map<String, FilterMeta> getFilterByAsMap() {
        return ComponentUtils.eval(getStateHelper(), "_filterBy", Collections::emptyMap);
    }

    public void setFilterByAsMap(Map<String, FilterMeta> sortBy) {
        getStateHelper().put("_filterBy", sortBy);
    }
}
