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
package org.primefaces.component.treetable;

import java.util.*;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.event.*;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.*;
import org.primefaces.model.filter.*;
import org.primefaces.util.*;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class TreeTable extends TreeTableBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TreeTable";

    public static final String CONTAINER_CLASS = "ui-treetable ui-widget";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-treetable ui-treetable-resizable ui-widget";
    public static final String HEADER_CLASS = "ui-treetable-header ui-widget-header ui-corner-top";
    public static final String DATA_CLASS = "ui-treetable-data ui-widget-content";
    public static final String FOOTER_CLASS = "ui-treetable-footer ui-widget-header ui-corner-bottom";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String SORTABLE_COLUMN_HEADER_CLASS = "ui-state-default ui-sortable-column";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String SELECTED_ROW_CLASS = "ui-widget-content ui-state-highlight ui-selected";
    public static final String COLUMN_CONTENT_WRAPPER = "ui-tt-c";
    public static final String EXPAND_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-e ui-c";
    public static final String COLLAPSE_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-s ui-c";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-treetable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-treetable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-treetable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-treetable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-treetable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-treetable-scrollable-footer-box";
    public static final String SELECTABLE_NODE_CLASS = "ui-treetable-selectable-node";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String INDENT_CLASS = "ui-treetable-indent";
    public static final String EMPTY_MESSAGE_ROW_CLASS = "ui-widget-content ui-treetable-empty-message";
    public static final String PARTIAL_SELECTED_CLASS = "ui-treetable-partialselected";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String REFLOW_CLASS = "ui-treetable-reflow";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String COLUMN_CUSTOM_FILTER_CLASS = "ui-column-customfilter";
    public static final String HIDDEN_COLUMN_CLASS = "ui-helper-hidden";
    public static final String STATIC_COLUMN_CLASS = "ui-static-column";

    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String EDITING_ROW_CLASS = "ui-row-editing";

    static final Map<MatchMode, FilterConstraint> FILTER_CONSTRAINTS = MapBuilder.<MatchMode, FilterConstraint>builder()
            .put(MatchMode.STARTS_WITH, new StartsWithFilterConstraint())
            .put(MatchMode.ENDS_WITH, new EndsWithFilterConstraint())
            .put(MatchMode.CONTAINS, new ContainsFilterConstraint())
            .put(MatchMode.EXACT, new ExactFilterConstraint())
            .put(MatchMode.LESS_THAN, new LessThanFilterConstraint())
            .put(MatchMode.LESS_THAN_EQUALS, new LessThanEqualsFilterConstraint())
            .put(MatchMode.GREATER_THAN, new GreaterThanFilterConstraint())
            .put(MatchMode.GREATER_THAN_EQUALS, new GreaterThanEqualsFilterConstraint())
            .put(MatchMode.EQUALS, new EqualsFilterConstraint())
            .put(MatchMode.IN, new InFilterConstraint())
            .put(MatchMode.GLOBAL, new GlobalFilterConstraint())
            .build();

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("contextMenu", NodeSelectEvent.class)
            .put("select", NodeSelectEvent.class)
            .put("unselect", NodeUnselectEvent.class)
            .put("expand", NodeExpandEvent.class)
            .put("collapse", NodeCollapseEvent.class)
            .put("colResize", ColumnResizeEvent.class)
            .put("sort", SortEvent.class)
            .put("filter", FilterEvent.class)
            .put("rowEdit", RowEditEvent.class)
            .put("rowEditInit", RowEditEvent.class)
            .put("rowEditCancel", RowEditEvent.class)
            .put("cellEdit", CellEditEvent.class)
            .put("cellEditInit", CellEditEvent.class)
            .put("cellEditCancel", CellEditEvent.class)
            .put("page", PageEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    private List<UIColumn> columns;
    private Columns dynamicColumns;
    private List<String> filteredRowKeys = new ArrayList<>();
    private Map<String, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isExpandRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_expand");
    }

    public boolean isCollapseRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_collapse");
    }

    public boolean isSelectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_instantSelection");
    }

    public boolean isSortRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_sorting");
    }

    public boolean isPaginationRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_pagination");
    }

    public boolean isRowEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_rowEditAction");
    }

    public boolean isCellEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_cellInfo");
    }

    public boolean isCellEditCancelRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_cellEditCancel");
    }

    public boolean isCellEditInitRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_cellEditInit");
    }

    public boolean isFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_filtering");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context) && (event instanceof AjaxBehaviorEvent)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;
            TreeNode root = getValue();

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("expand")) {
                String nodeKey = params.get(clientId + "_expand");
                setRowKey(root, nodeKey);
                TreeNode node = getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (eventName.equals("collapse")) {
                String nodeKey = params.get(clientId + "_collapse");
                setRowKey(root, nodeKey);
                TreeNode node = getRowNode();
                node.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (eventName.equals("select") || eventName.equals("contextMenu")) {
                String nodeKey = params.get(clientId + "_instantSelection");
                setRowKey(root, nodeKey);
                TreeNode node = getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (eventName.equals("unselect")) {
                String nodeKey = params.get(clientId + "_instantUnselection");
                setRowKey(root, nodeKey);
                TreeNode node = getRowNode();

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if (eventName.equals("sort")) {
                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), getSortByAsMap());
            }
            else if (eventName.equals("filter")) {
                deferredEvents.put("filter", (AjaxBehaviorEvent) event);
                return;
            }
            else if (eventName.equals("rowEdit") || eventName.equals("rowEditCancel") || eventName.equals("rowEditInit")) {
                String nodeKey = params.get(clientId + "_rowEditIndex");
                setRowKey(root, nodeKey);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (eventName.equals("cellEdit") || eventName.equals("cellEditCancel") || eventName.equals("cellEditInit")) {
                String[] cellInfo = params.get(clientId + "_cellInfo").split(",");
                String rowKey = cellInfo[0];
                int cellIndex = Integer.parseInt(cellInfo[1]);
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

                wrapperEvent = new CellEditEvent(this, behaviorEvent.getBehavior(), column, rowKey);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (eventName.equals("page")) {
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (first / rows) : 0;

                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (isToggleRequest(context)) {
            decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        super.processValidators(context);

        if (isFilterRequest(context)) {
            Map<String, FilterMeta> filterBy = initFilterBy(context);
            updateFilterByValuesWithFilterRequest(context, filterBy);
            setFilterByAsMap(filterBy);

            AjaxBehaviorEvent event = deferredEvents.get("filter");
            if (event != null) {
                FilterEvent wrappedEvent = new FilterEvent(this, event.getBehavior(), getFilterByAsMap());
                wrappedEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                super.queueEvent(wrappedEvent);
            }
        }
    }

    public boolean hasFooterColumn() {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof Column && child.isRendered()) {
                Column column = (Column) child;

                if (column.getFacet("footer") != null || column.getFooterText() != null) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isToggleRequest(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_expand") != null || params.get(clientId + "_collapse") != null;
    }

    public boolean isResizeRequest(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_colResize") != null;
    }

    public String getScrollState() {
        Map<String, String> params = getFacesContext().getExternalContext().getRequestParameterMap();
        String name = getClientId() + "_scrollState";
        String value = params.get(name);

        return value == null ? "0,0" : value;
    }

    public boolean isCheckboxSelection() {
        String selectionMode = getSelectionMode();

        return selectionMode != null && selectionMode.equals("checkbox");
    }

    public Locale resolveDataLocale() {
        FacesContext context = getFacesContext();
        return LocaleUtils.resolveLocale(context, getDataLocale(), getClientId(context));
    }

    @Override
    public List<UIColumn> getColumns() {
        if (this.columns != null) {
            return this.columns;
        }

        List<UIColumn> columns = initColumns();

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

    public Columns getDynamicColumns() {
        return dynamicColumns;
    }

    public void setDynamicColumns(Columns value) {
        dynamicColumns = value;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (dynamicColumns != null) {
            dynamicColumns.setRowIndex(-1);
        }

        // reset component for MyFaces view pooling
        columns = null;
        dynamicColumns = null;
        filteredRowKeys = new ArrayList<>();

        return super.saveState(context);
    }

    @Override
    protected void validateSelection(FacesContext context) {
        String selectionMode = getSelectionMode();

        if (selectionMode != null && isRequired()) {
            Object selection = getLocalSelectedNodes();
            boolean isValueBlank = (selectionMode.equalsIgnoreCase("single")) ? (selection == null) : (((TreeNode[]) selection).length == 0);

            if (isValueBlank) {
                super.updateSelection(context);
            }
        }

        super.validateSelection(context);
    }

    @Override
    public int getRowCount() {
        TreeNode root = getValue();
        if (root == null) {
            return (-1);
        }
        else {
            List<TreeNode> children = root.getChildren();
            return children == null ? -1 : children.size();
        }
    }

    @Override
    public int getPage() {
        if (getRowCount() > 0) {
            int rows = getRowsToRender();

            if (rows > 0) {
                int first = getFirst();

                return first / rows;
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    @Override
    public int getRowsToRender() {
        int rows = getRows();

        return rows == 0 ? getRowCount() : rows;
    }

    @Override
    public int getPageCount() {
        return (int) Math.ceil(getRowCount() * 1d / getRowsToRender());
    }

    @Override
    public UIComponent getHeader() {
        return getFacet("header");

    }

    @Override
    public UIComponent getFooter() {
        return getFacet("footer");
    }

    public void calculateFirst() {
        int rows = getRows();

        if (rows > 0) {
            int first = getFirst();
            int rowCount = getRowCount();

            if (rowCount > 0 && first >= rowCount) {
                int numberOfPages = (int) Math.ceil(rowCount * 1d / rows);

                setFirst(Math.max((numberOfPages - 1) * rows, 0));
            }
        }
    }

    public void updatePaginationData(FacesContext context) {
        String componentClientId = getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        ELContext elContext = context.getELContext();

        String firstParam = params.get(componentClientId + "_first");
        String rowsParam = params.get(componentClientId + "_rows");

        setFirst(Integer.valueOf(firstParam));
        setRows(Integer.valueOf(rowsParam));

        ValueExpression firstVe = getValueExpression("first");
        ValueExpression rowsVe = getValueExpression("rows");

        if (firstVe != null && !firstVe.isReadOnly(elContext)) {
            firstVe.setValue(context.getELContext(), getFirst());
        }
        if (rowsVe != null && !rowsVe.isReadOnly(elContext)) {
            rowsVe.setValue(context.getELContext(), getRows());
        }
    }

    public boolean isFilteringEnabled() {
        return !getFilterByAsMap().isEmpty();
    }

    public void updateFilteredValue(FacesContext context, TreeNode node) {
        ValueExpression ve = getValueExpression(PropertyKeys.filteredValue.name());

        if (ve != null) {
            ve.setValue(context.getELContext(), node);
        }
        else {
            setFilteredValue(node);
        }
    }

    public List<String> getFilteredRowKeys() {
        return filteredRowKeys;
    }

    public void setFilteredRowKeys(List<String> filteredRowKeys) {
        this.filteredRowKeys = filteredRowKeys;
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
        Columns dynamicCols = this.getDynamicColumns();
        if (dynamicCols != null && isNestedWithinIterator()) {
            dynamicCols.setRowIndex(-1);
            this.setColumns(null);
        }
    }

    @Override
    protected boolean requiresColumns() {
        return true;
    }

    @Override
    public void restoreMultiViewState() {
        TreeTableState ts = getMultiViewState(false);
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

            // TODO selection

            setVisibleColumnsAsMap(ts.getVisibleColumns());
            setResizableColumnsAsMap(ts.getResizableColumns());
        }
    }

    @Override
    public TreeTableState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, TreeTableState::new);
    }

    @Override
    public void resetMultiViewState() {
        reset();
    }

    public void reset() {
        setValue(null);
        setFilteredValue(null);

        setFirst(0);
        setDefaultSort(false);
        setDefaultFilter(false);
        setSortByAsMap(null);
        setFilterByAsMap(null);
    }

    @Override
    public Map<String, SortMeta> getSortByAsMap() {
        return ComponentUtils.computeIfAbsent(getStateHelper(), InternalPropertyKeys.sortByAsMap.name(), () -> initSortBy(getFacesContext()));
    }

    @Override
    public void setSortByAsMap(Map<String, SortMeta> sortBy) {
        getStateHelper().put(InternalPropertyKeys.sortByAsMap.name(), sortBy);
    }

    @Override
    public Map<String, FilterMeta> getFilterByAsMap() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.filterByAsMap.name(), Collections::emptyMap);
    }

    @Override
    public void setFilterByAsMap(Map<String, FilterMeta> sortBy) {
        getStateHelper().put(InternalPropertyKeys.filterByAsMap.name(), sortBy);
    }

    @Override
    public boolean isDefaultSort() {
        return getSortByAsMap() != null && Boolean.TRUE.equals(getStateHelper().get(InternalPropertyKeys.defaultSort.name()));
    }

    @Override
    public void setDefaultSort(boolean defaultSort) {
        getStateHelper().put(InternalPropertyKeys.defaultSort.name(), defaultSort);
    }

    @Override
    public boolean isDefaultFilter() {
        return Boolean.TRUE.equals(getStateHelper().get(InternalPropertyKeys.defaultFilter.name()));
    }

    @Override
    public void setDefaultFilter(boolean defaultFilter) {
        getStateHelper().put(InternalPropertyKeys.defaultFilter.name(), defaultFilter);
    }

    @Override
    public boolean isFilterByAsMapDefined() {
        return getStateHelper().get(InternalPropertyKeys.filterByAsMap.name()) != null;
    }

    public boolean isMultiSort() {
        String sortMode = getSortMode();
        return sortMode != null && sortMode.equals("multiple");
    }

    @Override
    public Map<String, Boolean> getVisibleColumnsAsMap() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.visibleColumnsAsMap.name(), Collections::emptyMap);
    }

    @Override
    public void setVisibleColumnsAsMap(Map<String, Boolean> visibleColumnsAsMap) {
        getStateHelper().put(InternalPropertyKeys.visibleColumnsAsMap.name(), visibleColumnsAsMap);
    }

    @Override
    public Map<String, String> getResizableColumnsAsMap() {
        return ComponentUtils.eval(getStateHelper(), InternalPropertyKeys.resizableColumnsAsMap.name(), Collections::emptyMap);
    }

    @Override
    public void setResizableColumnsAsMap(Map<String, String> resizableColumnsAsMap) {
        getStateHelper().put(InternalPropertyKeys.resizableColumnsAsMap.name(), resizableColumnsAsMap);
    }
}