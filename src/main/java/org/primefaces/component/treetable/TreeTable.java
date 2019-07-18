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

import java.util.*;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.event.*;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.model.filter.*;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
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

    public static final String STARTS_WITH_MATCH_MODE = "startsWith";
    public static final String ENDS_WITH_MATCH_MODE = "endsWith";
    public static final String CONTAINS_MATCH_MODE = "contains";
    public static final String EXACT_MATCH_MODE = "exact";
    public static final String LESS_THAN_MODE = "lt";
    public static final String LESS_THAN_EQUALS_MODE = "lte";
    public static final String GREATER_THAN_MODE = "gt";
    public static final String GREATER_THAN_EQUALS_MODE = "gte";
    public static final String EQUALS_MODE = "equals";
    public static final String IN_MODE = "in";
    public static final String GLOBAL_MODE = "global";
    static final Map<String, FilterConstraint> FILTER_CONSTRAINTS = MapBuilder.<String, FilterConstraint>builder()
            .put(STARTS_WITH_MATCH_MODE, new StartsWithFilterConstraint())
            .put(ENDS_WITH_MATCH_MODE, new EndsWithFilterConstraint())
            .put(CONTAINS_MATCH_MODE, new ContainsFilterConstraint())
            .put(EXACT_MATCH_MODE, new ExactFilterConstraint())
            .put(LESS_THAN_MODE, new LessThanFilterConstraint())
            .put(LESS_THAN_EQUALS_MODE, new LessThanEqualsFilterConstraint())
            .put(GREATER_THAN_MODE, new GreaterThanFilterConstraint())
            .put(GREATER_THAN_EQUALS_MODE, new GreaterThanEqualsFilterConstraint())
            .put(EQUALS_MODE, new EqualsFilterConstraint())
            .put(IN_MODE, new InFilterConstraint())
            .put(GLOBAL_MODE, new GlobalFilterConstraint())
            .build();

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("select", NodeSelectEvent.class)
            .put("unselect", NodeUnselectEvent.class)
            .put("expand", NodeExpandEvent.class)
            .put("collapse", NodeCollapseEvent.class)
            .put("colResize", ColumnResizeEvent.class)
            .put("sort", SortEvent.class)
            .put("rowEdit", RowEditEvent.class)
            .put("rowEditInit", RowEditEvent.class)
            .put("rowEditCancel", RowEditEvent.class)
            .put("cellEdit", CellEditEvent.class)
            .put("cellEditInit", CellEditEvent.class)
            .put("cellEditCancel", CellEditEvent.class)
            .put("page", PageEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private int columnsCount = -1;
    private UIColumn sortColumn;
    private List<UIColumn> columns;
    private Columns dynamicColumns;
    private List<String> filteredRowKeys = new ArrayList<>();
    private List filterMetadata;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
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

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("expand")) {
                String nodeKey = params.get(clientId + "_expand");
                setRowKey(nodeKey);
                TreeNode node = getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (eventName.equals("collapse")) {
                String nodeKey = params.get(clientId + "_collapse");
                setRowKey(nodeKey);
                TreeNode node = getRowNode();
                node.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if (eventName.equals("select")) {
                String nodeKey = params.get(clientId + "_instantSelection");
                setRowKey(nodeKey);
                TreeNode node = getRowNode();

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), node);
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if (eventName.equals("unselect")) {
                String nodeKey = params.get(clientId + "_instantUnselection");
                setRowKey(nodeKey);
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
                SortOrder order = SortOrder.valueOf(params.get(clientId + "_sortDir"));
                UIColumn sortColumn = findColumn(params.get(clientId + "_sortKey"));

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, order, 0);
            }
            else if (eventName.equals("rowEdit") || eventName.equals("rowEditCancel") || eventName.equals("rowEditInit")) {
                String nodeKey = params.get(clientId + "_rowEditIndex");
                setRowKey(nodeKey);
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

    public UIColumn findColumn(String clientId) {
        for (UIColumn column : getColumns()) {
            if (column.getColumnKey().equals(clientId)) {
                return column;
            }
        }

        FacesContext context = getFacesContext();
        ColumnGroup headerGroup = getColumnGroup("header");
        for (UIComponent row : headerGroup.getChildren()) {
            for (UIComponent col : row.getChildren()) {
                if (col.getClientId(context).equals(clientId)) {
                    return (UIColumn) col;
                }
            }
        }

        throw new FacesException("Cannot find column with key: " + clientId);
    }

    public boolean hasFooterColumn() {
        for (UIComponent child : getChildren()) {
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

    public int getColumnsCount() {
        if (columnsCount == -1) {
            columnsCount = 0;

            for (UIComponent kid : getChildren()) {
                if (kid.isRendered() && kid instanceof Column) {
                    columnsCount++;
                }
            }
        }

        return columnsCount;
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

    public UIColumn getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(UIColumn column) {
        sortColumn = column;
    }

    public void clearDefaultSorted() {
        getStateHelper().remove("defaultSorted");
    }

    public void setDefaultSorted() {
        getStateHelper().put("defaultSorted", "defaultSorted");
    }

    public boolean isDefaultSorted() {
        return getStateHelper().get("defaultSorted") != null;
    }

    public Locale resolveDataLocale() {
        FacesContext context = getFacesContext();
        return LocaleUtils.resolveLocale(getDataLocale(), getClientId(context));
    }

    public ColumnGroup getColumnGroup(String target) {
        for (UIComponent child : getChildren()) {
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

    public List<UIColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
            FacesContext context = getFacesContext();
            char separator = UINamingContainer.getSeparatorChar(context);

            for (UIComponent child : getChildren()) {
                if (child instanceof Column) {
                    columns.add((UIColumn) child);
                }
                else if (child instanceof Columns) {
                    Columns uiColumns = (Columns) child;
                    String uiColumnsClientId = uiColumns.getClientId(context);

                    for (int i = 0; i < uiColumns.getRowCount(); i++) {
                        DynamicColumn dynaColumn = new DynamicColumn(i, uiColumns);
                        dynaColumn.setColumnKey(uiColumnsClientId + separator + i);
                        columns.add(dynaColumn);
                    }
                }
            }
        }

        return columns;
    }

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
        Object value = getStateHelper().get("filtering");

        return value == null ? false : true;
    }

    public void enableFiltering() {
        getStateHelper().put("filtering", true);
    }

    public void updateFilteredNode(FacesContext context, TreeNode node) {
        ValueExpression ve = getValueExpression("filteredNode");

        if (ve != null) {
            ve.setValue(context.getELContext(), node);
        }
        else {
            setFilteredNode(node);
        }
    }

    public List<String> getFilteredRowKeys() {
        return filteredRowKeys;
    }

    public void setFilteredRowKeys(List<String> filteredRowKeys) {
        this.filteredRowKeys = filteredRowKeys;
    }

    public List getFilterMetadata() {
        return filterMetadata;
    }

    public void setFilterMetadata(List filterMetadata) {
        this.filterMetadata = filterMetadata;
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

    public void updateColumnsVisibility(FacesContext context) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String columnTogglerParam = params.get(getClientId(context) + "_columnTogglerState");
        if (columnTogglerParam != null) {
            String[] togglableColumns = columnTogglerParam.split(",");
            for (String togglableColumn : togglableColumns) {
                int sepIndex = togglableColumn.lastIndexOf('_');
                UIColumn column = findColumn(togglableColumn.substring(0, sepIndex));

                if (column != null) {
                    ((Column) column).setVisible(Boolean.valueOf(togglableColumn.substring(sepIndex + 1)));
                }
            }
        }
    }
}