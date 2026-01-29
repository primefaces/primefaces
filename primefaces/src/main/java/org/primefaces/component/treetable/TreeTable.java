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
package org.primefaces.component.treetable;

import org.primefaces.PrimeFaces;
import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.treetable.feature.FilterFeature;
import org.primefaces.component.treetable.feature.TreeTableFeatures;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeChildren;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PostRestoreStateEvent;

@FacesComponent(value = TreeTable.COMPONENT_TYPE, namespace = TreeTable.COMPONENT_FAMILY)
@FacesComponentDescription("TreeTable is is used for displaying hierarchical data in tabular format.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class TreeTable extends TreeTableBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TreeTable";

    public static final String CONTAINER_CLASS = "ui-treetable ui-widget";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-treetable ui-treetable-resizable ui-widget";
    public static final String HEADER_CLASS = "ui-treetable-header ui-widget-header";
    public static final String DATA_CLASS = "ui-treetable-data ui-widget-content";
    public static final String FOOTER_CLASS = "ui-treetable-footer ui-widget-header";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String SORTABLE_COLUMN_HEADER_CLASS = "ui-state-default ui-sortable-column";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String SELECTED_ROW_CLASS = "ui-widget-content ui-state-highlight ui-selected";
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
    public static final String SORTABLE_PRIORITY_CLASS = "ui-sortable-column-badge ui-helper-hidden";
    public static final String REFLOW_CLASS = "ui-treetable-reflow";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default";
    public static final String COLUMN_CUSTOM_FILTER_CLASS = "ui-column-customfilter";
    public static final String HIDDEN_COLUMN_CLASS = "ui-helper-hidden";
    public static final String STATIC_COLUMN_CLASS = "ui-static-column";
    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String EDITING_ROW_CLASS = "ui-row-editing";
    public static final String GRIDLINES_CLASS = "ui-treetable-gridlines";
    public static final String SMALL_SIZE_CLASS = "ui-treetable-sm";
    public static final String LARGE_SIZE_CLASS = "ui-treetable-lg";

    private List<UIColumn> columns;
    private List<String> filteredRowKeys = new ArrayList<>();
    private Map<String, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            FacesEvent wrapperEvent = null;
            TreeNode<?> root = getValue();

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.expand)) {
                String nodeKey = params.get(clientId + "_expand");
                setRowKey(root, nodeKey);
                TreeNode<?> node = getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.collapse)) {
                String nodeKey = params.get(clientId + "_collapse");
                setRowKey(root, nodeKey);
                TreeNode<?> node = getRowNode();
                node.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.select, ClientBehaviorEventKeys.dblselect)) {
                String nodeKey = params.get(clientId + "_instantSelection");
                setRowKey(root, nodeKey);
                TreeNode<?> node = getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.contextMenu)) {
                String nodeKey = params.get(clientId + "_instantSelection");
                setRowKey(root, nodeKey);
                TreeNode<?> node = getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node, true);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }

            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.unselect)) {
                String nodeKey = params.get(clientId + "_instantUnselection");
                setRowKey(root, nodeKey);
                TreeNode<?> node = getRowNode();

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.colResize)) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.sort)) {
                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), getSortByAsMap());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.filter)) {
                deferredEvents.put("filter", (AjaxBehaviorEvent) event);
                return;
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.rowEdit,
                    ClientBehaviorEventKeys.rowEditInit, ClientBehaviorEventKeys.rowEditCancel)) {
                String nodeKey = params.get(clientId + "_rowEditIndex");
                setRowKey(root, nodeKey);
                wrapperEvent = new RowEditEvent<>(this, behaviorEvent.getBehavior(), getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.cellEdit,
                    ClientBehaviorEventKeys.cellEditInit, ClientBehaviorEventKeys.cellEditCancel)) {
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

                wrapperEvent = new CellEditEvent<>(this, behaviorEvent.getBehavior(), column, rowKey);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.page)) {
                int rows = getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (first / rows) : 0;
                String rowsPerPageParam = params.get(clientId + "_rows");
                Integer rowsPerPage = LangUtils.isNotBlank(rowsPerPageParam) ? Integer.parseInt(rowsPerPageParam) : null;

                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page, rowsPerPage);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        super.processEvent(event);

        // restore "value" from "filteredValue"
        if (event instanceof PostRestoreStateEvent
                && isFilteringEnabled()
                && this == event.getComponent()) {
            TreeNode<?> filteredValue = getFilteredValue();
            if (filteredValue != null) {
                setValue(filteredValue);
            }
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

        //filters need to be decoded during PROCESS_VALIDATIONS phase,
        //so that local values of each filters are properly converted and validated
        FilterFeature feature = TreeTableFeatures.filterFeature();
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

    public Locale resolveDataLocale() {
        return resolveDataLocale(getFacesContext());
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

    @Override
    public Object saveState(FacesContext context) {
        // reset value when filtering is enabled
        // filtering stores the filtered values the value property, so it needs to be reset; see #7336
        if (isFilteringEnabled()) {
            setValue(null);
        }

        // reset component for MyFaces view pooling
        columns = null;
        filteredRowKeys = new ArrayList<>();

        return super.saveState(context);
    }

    @Override
    public int getRowCount() {
        TreeNode<?> root = getValue();
        if (root == null) {
            return (-1);
        }
        else {
            TreeNodeChildren<?> children = root.getChildren();
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
        int newRowsValue = "*".equals(rowsParam) ? getRowCount() : Integer.parseInt(rowsParam);

        setFirst(Integer.parseInt(firstParam));
        setRows(newRowsValue);

        ValueExpression firstVe = getValueExpression("first");
        ValueExpression rowsVe = getValueExpression("rows");

        if (firstVe != null && !firstVe.isReadOnly(elContext)) {
            firstVe.setValue(context.getELContext(), getFirst());
        }
        if (rowsVe != null && !rowsVe.isReadOnly(elContext)) {
            rowsVe.setValue(context.getELContext(), getRows());
        }
    }

    public void updateFilteredValue(FacesContext context, TreeNode<?> node) {
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

            setColumnMeta(ts.getColumnMeta());
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
        return (Map<String, FilterMeta>) getStateHelper().eval(InternalPropertyKeys.filterByAsMap.name(), () -> initFilterBy(getFacesContext()));
    }

    @Override
    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage, MessageFactory.getMessage(getFacesContext(), UIPageableData.EMPTY_MESSAGE));
    }

    @Override
    public void setFilterByAsMap(Map<String, FilterMeta> filterBy) {
        getStateHelper().put(InternalPropertyKeys.filterByAsMap.name(), filterBy);
    }

    @Override
    public boolean isFilterByAsMapDefined() {
        return getStateHelper().get(InternalPropertyKeys.filterByAsMap.name()) != null;
    }

    public boolean isMultiSort() {
        return "multiple".equals(getSortMode());
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
     * Recalculates filteredValue after adding, updating or removing TreeNodes to/from a filtered TreeTable.
     */
    @Override
    public void filterAndSort() {
        setValue(null);
        TreeTableFeatures.filterFeature().filter(FacesContext.getCurrentInstance(), this, getValue());
        TreeTableFeatures.sortFeature().sort(FacesContext.getCurrentInstance(), this);
    }

    public boolean shouldEncodeFeature(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_encodeFeature");
    }

    protected boolean isCacheableColumns(List<UIColumn> columns) {
        // lets cache it only when RENDER_RESPONSE is reached, the columns might change before reaching that phase
        // see https://github.com/primefaces/primefaces/issues/2110
        // do not cache if nested in iterator component and contains dynamic columns since number of columns may vary per iteration
        // see https://github.com/primefaces/primefaces/issues/2154
        return getFacesContext().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE
                && (!ComponentUtils.isNestedWithinIterator(this) || columns.stream().noneMatch(DynamicColumn.class::isInstance));
    }

    @Override
    protected void processNode(FacesContext context, PhaseId phaseId, TreeNode<?> root, TreeNode<?> treeNode, String rowKey) {
        if (!isPaginator() || root != treeNode) {
            super.processNode(context, phaseId, root, treeNode, rowKey);
        }
        else {
            if (treeNode != null && shouldVisitNode(treeNode) && treeNode.getChildCount() > 0) {
                int first = getFirst();
                int rows = getRows() == 0 ? getRowCount() : getRows();

                processColumnChildren(context, phaseId, root, rowKey);

                TreeNodeChildren<?> children = root.getChildren();
                int childCount = root.getChildCount();
                int last = (first + rows);
                if (last > childCount) {
                    last = childCount;
                }

                for (int i = first; i < last; i++) {
                    TreeNode<?> child = children.get(i);
                    String childRowKey = childRowKey(rowKey, i);
                    processNode(context, phaseId, root, child, childRowKey);
                }
            }
        }
    }

}
