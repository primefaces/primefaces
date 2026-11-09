/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;

@FacesComponent(value = TreeTable.COMPONENT_TYPE, namespace = TreeTable.COMPONENT_FAMILY)
@FacesComponentInfo(description = "TreeTable is is used for displaying hierarchical data in tabular format.")
@FacesComponentHandler(TreeTableHandler.class)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class TreeTable extends TreeTableBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TreeTable";

    public static final String CONTAINER_CLASS = "p-treetable p-widget";
    public static final String RESIZABLE_CONTAINER_CLASS = "p-treetable p-treetable-resizable p-widget";
    public static final String HEADER_CLASS = "p-treetable-header p-widget-header";
    public static final String DATA_CLASS = "p-treetable-data p-widget-content";
    public static final String FOOTER_CLASS = "p-treetable-footer p-widget-header";
    public static final String COLUMN_HEADER_CLASS = "p-state-default";
    public static final String SORTABLE_COLUMN_HEADER_CLASS = "p-state-default p-sortable-column";
    public static final String ROW_CLASS = "p-widget-content";
    public static final String SELECTED_ROW_CLASS = "p-widget-content p-state-highlight p-selected";
    public static final String TOGGLER_CLASS = "p-treetable-toggler p-icon p-toggle-icon p-treetable-toggle-icon p-c";
    public static final String SCROLLABLE_CONTAINER_CLASS = "p-treetable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "p-widget-header p-treetable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "p-treetable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "p-treetable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "p-widget-header p-treetable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "p-treetable-scrollable-footer-box";
    public static final String SELECTABLE_NODE_CLASS = "p-treetable-selectable-node";
    public static final String RESIZABLE_COLUMN_CLASS = "p-resizable-column";
    public static final String INDENT_CLASS = "p-treetable-indent";
    public static final String EMPTY_MESSAGE_ROW_CLASS = "p-widget-content p-treetable-empty-message";
    public static final String PARTIAL_SELECTED_CLASS = "p-treetable-partialselected";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "p-sortable-column-icon p-icon p-sort-icon p-treetable-sort-icon";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = SORTABLE_COLUMN_ICON_CLASS + " p-treetable-sort-asc-icon";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = SORTABLE_COLUMN_ICON_CLASS + " p-treetable-sort-desc-icon";
    public static final String SORTABLE_PRIORITY_CLASS = "p-sortable-column-badge p-hidden";
    public static final String REFLOW_CLASS = "p-treetable-reflow";
    public static final String FILTER_COLUMN_CLASS = "p-filter-column";
    public static final String COLUMN_INPUT_FILTER_CLASS = "p-column-filter p-inputfield p-inputtext p-widget p-state-default";
    public static final String COLUMN_CUSTOM_FILTER_CLASS = "p-column-customfilter";
    public static final String HIDDEN_COLUMN_CLASS = "p-hidden";
    public static final String STATIC_COLUMN_CLASS = "p-static-column";
    public static final String EDITABLE_COLUMN_CLASS = "p-editable-column";
    public static final String EDITING_ROW_CLASS = "p-row-editing";
    public static final String GRIDLINES_CLASS = "p-treetable-gridlines";
    public static final String STRIPED_ROWS_CLASS = "p-treetable-striped";
    public static final String SMALL_SIZE_CLASS = "p-treetable-sm";
    public static final String LARGE_SIZE_CLASS = "p-treetable-lg";

    private List<UIColumn> columns;
    // the nodes passing the active filters, null if no filter is active; computed once per request
    private Set<TreeNode<?>> filteredNodes;
    private boolean filteredNodesComputed;
    private Map<PrimeClientBehaviorEventKeys, AjaxBehaviorEvent> deferredEvents = new HashMap<>(1);

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
                deferredEvents.put(ClientBehaviorEventKeys.filter, (AjaxBehaviorEvent) event);
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

            AjaxBehaviorEvent event = deferredEvents.get(ClientBehaviorEventKeys.filter);
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
        // reset component for MyFaces view pooling
        columns = null;
        resetFilteredNodes();

        return super.saveState(context);
    }

    @Override
    public int getRowCount() {
        TreeNode<?> root = getValue();
        if (root == null || root.getChildren() == null) {
            return (-1);
        }

        return getVisibleChildren(root).size();
    }

    /**
     * The nodes passing the active filters together with their ancestors (and descendants, unless pruned), computed
     * once per request.
     *
     * @return the displayed nodes (unmodifiable), or {@code null} if no filter is active and all nodes are displayed
     */
    public Set<TreeNode<?>> getFilteredNodes() {
        if (!filteredNodesComputed) {
            // the filters are initialized before the first rendering, there is nothing to filter before
            if (!isFilterByAsMapDefined()) {
                return null;
            }
            TreeTableFeatures.filterFeature().filter(getFacesContext(), this, getValue(), false);
        }
        return filteredNodes;
    }

    public void setFilteredNodes(Set<TreeNode<?>> filteredNodes) {
        this.filteredNodes = filteredNodes == null ? null : Collections.unmodifiableSet(filteredNodes);
        this.filteredNodesComputed = true;
    }

    /**
     * Discards the filtered nodes, so that the filters are applied again when the nodes are needed next.
     */
    public void resetFilteredNodes() {
        this.filteredNodes = null;
        this.filteredNodesComputed = false;
    }

    @Override
    protected boolean isNodeVisible(TreeNode<?> node) {
        Set<TreeNode<?>> nodes = getFilteredNodes();
        return nodes == null || nodes.contains(node);
    }

    /**
     * @param node the parent node
     * @return the children of the given node which are displayed, i.e. not hidden by a filter
     */
    public List<TreeNode<?>> getVisibleChildren(TreeNode<?> node) {
        List<? extends TreeNode<?>> children = node.getChildren();
        if (children == null) {
            return Collections.emptyList();
        }

        Set<TreeNode<?>> nodes = getFilteredNodes();
        if (nodes == null) {
            return Collections.unmodifiableList(children);
        }

        List<TreeNode<?>> visibleChildren = new ArrayList<>();
        for (TreeNode<?> child : children) {
            if (nodes.contains(child)) {
                visibleChildren.add(child);
            }
        }
        return visibleChildren;
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
        return getHeaderFacet();

    }

    @Override
    public UIComponent getFooter() {
        return getFooterFacet();
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
        resetFilteredNodes();
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

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
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
     * Applies the filters and the sorting again after adding, updating or removing TreeNodes to/from a filtered TreeTable.
     */
    @Override
    public void filterAndSort() {
        FacesContext context = getFacesContext();
        TreeTableFeatures.filterFeature().filter(context, this, getValue(), false);
        TreeTableFeatures.sortFeature().sort(context, this);
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

                // the page is a slice of the displayed children, the rowKey is built from the index of all children
                TreeNodeChildren<?> children = root.getChildren();
                int last = first + rows;
                int visibleIndex = 0;
                for (int i = 0; i < children.size() && visibleIndex < last; i++) {
                    TreeNode<?> child = children.get(i);
                    if (isNodeVisible(child)) {
                        if (visibleIndex >= first) {
                            processNode(context, phaseId, root, child, childRowKey(rowKey, i));
                        }
                        visibleIndex++;
                    }
                }
            }
        }
    }

}
