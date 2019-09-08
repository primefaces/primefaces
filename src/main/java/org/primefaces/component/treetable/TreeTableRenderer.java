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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITree;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.component.tree.Tree;
import org.primefaces.model.*;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.GlobalFilterConstraint;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.*;
import org.primefaces.visit.ResetInputVisitCallback;

import static org.primefaces.component.api.UITree.ROOT_ROW_KEY;
import static org.primefaces.component.treetable.TreeTable.FILTER_CONSTRAINTS;
import static org.primefaces.component.treetable.TreeTable.GLOBAL_MODE;

public class TreeTableRenderer extends DataRenderer {

    private static final String SB_DECODE_SELECTION = TreeTableRenderer.class.getName() + "#decodeSelection";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TreeTable tt = (TreeTable) component;

        if (tt.isFilterRequest(context)) {
            List<FilterMeta> filterMetadata = populateFilterMetaData(context, tt);
            tt.setFilterMetadata(filterMetadata);
        }

        if (tt.getSelectionMode() != null) {
            decodeSelection(context, tt);
        }

        if (tt.isSortRequest(context)) {
            decodeSort(context, tt);
        }

        decodeBehaviors(context, component);
    }

    protected void decodeSelection(FacesContext context, TreeTable tt) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String selectionMode = tt.getSelectionMode();
        String clientId = tt.getClientId(context);

        //decode selection
        if (selectionMode != null) {
            String selectionValue = params.get(tt.getClientId(context) + "_selection");
            boolean isSingle = selectionMode.equalsIgnoreCase("single");

            if (isValueBlank(selectionValue)) {
                if (isSingle) {
                    tt.setSelection(null);
                }
                else {
                    tt.setSelection(new TreeNode[0]);
                }
            }
            else {
                String[] selectedRowKeys = selectionValue.split(",");

                if (isSingle) {
                    tt.setRowKey(selectedRowKeys[0]);
                    tt.setSelection(tt.getRowNode());
                }
                else {
                    List<TreeNode> selectedNodes = new ArrayList<>();

                    for (int i = 0; i < selectedRowKeys.length; i++) {
                        tt.setRowKey(selectedRowKeys[i]);
                        TreeNode rowNode = tt.getRowNode();
                        if (rowNode != null) {
                            selectedNodes.add(rowNode);
                        }
                    }

                    tt.setSelection(selectedNodes.toArray(new TreeNode[selectedNodes.size()]));
                }

                tt.setRowKey(null);     //cleanup
            }
        }

        if (tt.isCheckboxSelection() && tt.isSelectionRequest(context)) {
            String selectedNodeRowKey = params.get(clientId + "_instantSelection");
            tt.setRowKey(selectedNodeRowKey);
            TreeNode selectedNode = tt.getRowNode();
            List<String> descendantRowKeys = new ArrayList<>();
            tt.populateRowKeys(selectedNode, descendantRowKeys);
            int size = descendantRowKeys.size();
            StringBuilder sb = SharedStringBuilder.get(context, SB_DECODE_SELECTION);

            for (int i = 0; i < size; i++) {
                sb.append(descendantRowKeys.get(i));
                if (i != (size - 1)) {
                    sb.append(",");
                }
            }

            PrimeFaces.current().ajax().addCallbackParam("descendantRowKeys", sb.toString());
            sb.setLength(0);
            descendantRowKeys = null;
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        TreeTable tt = (TreeTable) component;
        String clientId = tt.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        preRender(context, tt);

        String nodeKey = params.get(clientId + "_expand");
        if (nodeKey != null) {
            tt.setRowKey(nodeKey);
            TreeNode node = tt.getRowNode();
            node.setExpanded(true);

            if (tt.getExpandMode().equals("self")) {
                encodeNode(context, tt, node);
            }
            else {
                encodeNodeChildren(context, tt, node);
            }
        }
        else if (tt.isFilterRequest(context)) {
            tt.updateFilteredNode(context, null);
            tt.setValue(null);
            tt.setFirst(0);

            //update rows with rpp value
            String rppValue = params.get(clientId + "_rppDD");
            if (rppValue != null) {
                tt.setRows(Integer.parseInt(rppValue));
            }

            String globalFilterParam = clientId + UINamingContainer.getSeparatorChar(context) + "globalFilter";
            String globalFilterValue = params.get(globalFilterParam);

            filter(context, tt, tt.getFilterMetadata(), globalFilterValue);

            //sort new filtered data to restore sort state
            boolean sorted = (tt.getValueExpression("sortBy") != null || tt.getSortBy() != null);
            if (sorted) {
                sort(tt);
            }

            encodeTbody(context, tt, true);
        }
        else if (tt.isSortRequest(context)) {
            encodeSort(context, tt);
        }
        else if (tt.isRowEditRequest(context)) {
            encodeRowEdit(context, tt);
        }
        else if (tt.isCellEditRequest(context)) {
            encodeCellEdit(context, tt);
        }
        else if (tt.isPaginationRequest(context)) {
            tt.updatePaginationData(context);
            TreeNode root = tt.getValue();
            encodeNodeChildren(context, tt, root, tt.getFirst(), tt.getRows());
        }
        else {
            encodeMarkup(context, tt);
            encodeScript(context, tt);
        }
    }

    protected void preRender(FacesContext context, TreeTable tt) {
        Columns dynamicCols = tt.getDynamicColumns();
        if (dynamicCols != null) {
            dynamicCols.setRowIndex(-1);
        }

        tt.updateColumnsVisibility(context);
    }

    protected void encodeScript(FacesContext context, TreeTable tt) throws IOException {
        String clientId = tt.getClientId(context);
        String selectionMode = tt.getSelectionMode();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TreeTable", tt.resolveWidgetVar(context), clientId)
                .attr("selectionMode", selectionMode, null)
                .attr("resizableColumns", tt.isResizableColumns(), false)
                .attr("liveResize", tt.isLiveResize(), false)
                .attr("scrollable", tt.isScrollable(), false)
                .attr("scrollHeight", tt.getScrollHeight(), null)
                .attr("scrollWidth", tt.getScrollWidth(), null)
                .attr("nativeElements", tt.isNativeElements(), false)
                .attr("expandMode", tt.getExpandMode(), "children")
                .attr("disabledTextSelection", tt.isDisabledTextSelection(), true);

        if (tt.isStickyHeader()) {
            wb.attr("stickyHeader", true);
        }

        //Editing
        if (tt.isEditable()) {
            wb.attr("editable", true)
                    .attr("editMode", tt.getEditMode())
                    .attr("cellEditMode", tt.getCellEditMode(), "eager")
                    .attr("cellSeparator", tt.getCellSeparator(), null);
        }

        //Filtering
        if (tt.isFilteringEnabled()) {
            wb.attr("filter", true)
                    .attr("filterEvent", tt.getFilterEvent(), null)
                    .attr("filterDelay", tt.getFilterDelay(), Integer.MAX_VALUE);
        }

        if (tt.isPaginator()) {
            encodePaginatorConfig(context, tt, wb);
        }

        encodeClientBehaviors(context, tt);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tt.getClientId(context);
        boolean scrollable = tt.isScrollable();
        TreeNode root = tt.getValue();
        boolean hasPaginator = tt.isPaginator();

        if (root == null) {
            throw new FacesException("treeTable's value must be null. ClientId: " + clientId);
        }
        if (!(root instanceof TreeNode)) {
            throw new FacesException("treeTable's value must be an instance of " + TreeNode.class.getName() + ". ClientId: " + clientId);
        }

        if (hasPaginator) {
            tt.calculateFirst();
        }

        if (root.getRowKey() == null) {
            root.setRowKey(ROOT_ROW_KEY);
            tt.buildRowKeys(root);
            tt.initPreselection();
        }

        //default sort
        if (tt.getValueExpression("sortBy") != null && !tt.isDefaultSorted()) {
            sort(tt);
        }

        String containerClass = tt.isResizableColumns() ? TreeTable.RESIZABLE_CONTAINER_CLASS : TreeTable.CONTAINER_CLASS;
        containerClass = scrollable ? containerClass + " " + TreeTable.SCROLLABLE_CONTAINER_CLASS : containerClass;
        containerClass = tt.getStyleClass() == null ? containerClass : containerClass + " " + tt.getStyleClass();
        containerClass = tt.isShowUnselectableCheckbox() ? containerClass + " ui-treetable-checkbox-all" : containerClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, null);
        if (tt.getStyle() != null) {
            writer.writeAttribute("style", tt.getStyle(), null);
        }

        if (scrollable) {
            encodeScrollableMarkup(context, tt);
        }
        else {
            encodeRegularMarkup(context, tt);
        }

        if (tt.getSelectionMode() != null) {
            encodeStateHolder(context, tt, clientId + "_selection", tt.getSelectedRowKeysAsString());
        }

        if (scrollable) {
            encodeStateHolder(context, tt, clientId + "_scrollState", tt.getScrollState());
        }

        writer.endElement("div");
    }

    protected void encodeScrollableMarkup(FacesContext context, TreeTable tt) throws IOException {
        String tableStyle = tt.getTableStyle();
        String tableStyleClass = tt.getTableStyleClass();
        boolean hasPaginator = tt.isPaginator();
        String paginatorPosition = tt.getPaginatorPosition();

        encodeScrollAreaStart(context, tt, TreeTable.SCROLLABLE_HEADER_CLASS, TreeTable.SCROLLABLE_HEADER_BOX_CLASS,
                tableStyle, tableStyleClass, "header", TreeTable.HEADER_CLASS);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, tt, "top");
        }

        encodeThead(context, tt);
        encodeScrollAreaEnd(context);

        encodeScrollBody(context, tt, tableStyle, tableStyleClass);

        encodeScrollAreaStart(context, tt, TreeTable.SCROLLABLE_FOOTER_CLASS, TreeTable.SCROLLABLE_FOOTER_BOX_CLASS,
                tableStyle, tableStyleClass, "footer", TreeTable.FOOTER_CLASS);
        encodeTfoot(context, tt);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, tt, "bottom");
        }
        encodeScrollAreaEnd(context);
    }

    protected void encodeScrollBody(FacesContext context, TreeTable tt, String tableStyle, String tableStyleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String scrollHeight = tt.getScrollHeight();

        writer.startElement("div", null);
        writer.writeAttribute("class", TreeTable.SCROLLABLE_BODY_CLASS, null);
        if (scrollHeight != null && scrollHeight.indexOf('%') == -1) {
            writer.writeAttribute("style", "height:" + scrollHeight + "px", null);
        }
        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);

        if (tableStyle != null) {
            writer.writeAttribute("style", tableStyle, null);
        }
        if (tableStyleClass != null) {
            writer.writeAttribute("class", tableStyleClass, null);
        }

        encodeTbody(context, tt, false);

        writer.endElement("table");
        writer.endElement("div");
    }

    protected void encodeScrollAreaStart(FacesContext context, TreeTable tt, String containerClass, String containerBoxClass,
                                         String tableStyle, String tableStyleClass, String facet, String facetClass) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        encodeFacet(context, tt, tt.getFacet(facet), facetClass);

        writer.startElement("div", null);
        writer.writeAttribute("class", containerBoxClass, null);

        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        if (tableStyle != null) {
            writer.writeAttribute("style", tableStyle, null);
        }
        if (tableStyleClass != null) {
            writer.writeAttribute("class", tableStyleClass, null);
        }
    }

    protected void encodeScrollAreaEnd(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.endElement("table");
        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeRegularMarkup(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean hasPaginator = tt.isPaginator();
        String paginatorPosition = tt.getPaginatorPosition();

        encodeFacet(context, tt, tt.getFacet("header"), TreeTable.HEADER_CLASS);

        if (tt.isPaginator() && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, tt, "top");
        }

        writer.startElement("table", tt);
        writer.writeAttribute("role", "treegrid", null);
        if (tt.getTableStyle() != null) {
            writer.writeAttribute("style", tt.getTableStyle(), null);
        }
        if (tt.getTableStyleClass() != null) {
            writer.writeAttribute("class", tt.getTableStyleClass(), null);
        }

        encodeThead(context, tt);
        encodeTfoot(context, tt);
        encodeTbody(context, tt, false);

        writer.endElement("table");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, tt, "bottom");
        }

        encodeFacet(context, tt, tt.getFacet("footer"), TreeTable.FOOTER_CLASS);
    }

    protected void encodeThead(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = tt.getColumnGroup("header");
        String clientId = tt.getClientId(context);

        writer.startElement("thead", null);
        writer.writeAttribute("id", clientId + "_head", null);

        if (group != null && group.isRendered()) {
            for (UIComponent child : group.getChildren()) {
                if (child.isRendered() && child instanceof Row) {
                    Row headerRow = (Row) child;
                    String rowClass = headerRow.getStyleClass();
                    String rowStyle = headerRow.getStyle();

                    writer.startElement("tr", null);
                    if (rowClass != null) {
                        writer.writeAttribute("class", rowClass, null);
                    }
                    if (rowStyle != null) {
                        writer.writeAttribute("style", rowStyle, null);
                    }

                    for (UIComponent headerRowChild : headerRow.getChildren()) {
                        if (headerRowChild.isRendered() && headerRowChild instanceof Column) {
                            encodeColumnHeader(context, tt, (Column) headerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }
        }
        else {
            writer.startElement("tr", null);
            writer.writeAttribute("role", "row", null);

            List<UIColumn> columns = tt.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                UIColumn column = columns.get(i);

                if (column instanceof Column) {
                    encodeColumnHeader(context, tt, column);
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnHeader(context, tt, dynamicColumn);
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("thead");
    }

    protected void encodeTbody(FacesContext context, TreeTable tt, boolean dataOnly) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TreeNode root = tt.getValue();
        String clientId = tt.getClientId(context);
        boolean empty = (root == null || root.getChildCount() == 0);
        UIComponent emptyFacet = tt.getFacet("emptyMessage");

        if (!dataOnly) {
            writer.startElement("tbody", null);
            writer.writeAttribute("id", clientId + "_data", null);
            writer.writeAttribute("class", TreeTable.DATA_CLASS, null);
        }

        if (empty) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", TreeTable.EMPTY_MESSAGE_ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", tt.getColumnsCount(), null);

            if (emptyFacet != null) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(tt.getEmptyMessage(), "emptyMessage");
            }

            writer.endElement("td");
            writer.endElement("tr");
        }

        if (root != null) {
            if (tt.isPaginator()) {
                int first = tt.getFirst();
                int rows = tt.getRows() == 0 ? tt.getRowCount() : tt.getRows();
                encodeNodeChildren(context, tt, root, first, rows);
            }
            else {
                encodeNodeChildren(context, tt, root);
            }
        }

        tt.setRowKey(null);

        if (!dataOnly) {
            writer.endElement("tbody");
        }
    }

    protected void encodeNode(FacesContext context, TreeTable tt, TreeNode treeNode) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowKey = treeNode.getRowKey();
        String parentRowKey = treeNode.getParent().getRowKey();
        tt.setRowKey(rowKey);
        String icon = treeNode.isExpanded() ? TreeTable.COLLAPSE_ICON : TreeTable.EXPAND_ICON;
        int depth = rowKey.split(UITree.SEPARATOR).length - 1;
        String selectionMode = tt.getSelectionMode();
        boolean selectionEnabled = selectionMode != null;
        boolean selectable = treeNode.isSelectable() && selectionEnabled;
        boolean checkboxSelection = selectionEnabled && selectionMode.equals("checkbox");
        boolean selected = treeNode.isSelected();
        boolean partialSelected = treeNode.isPartialSelected();
        boolean nativeElements = tt.isNativeElements();
        List<UIColumn> columns = tt.getColumns();

        String rowStyleClass = selected ? TreeTable.SELECTED_ROW_CLASS : TreeTable.ROW_CLASS;
        rowStyleClass = selectable ? rowStyleClass + " " + TreeTable.SELECTABLE_NODE_CLASS : rowStyleClass;
        rowStyleClass = rowStyleClass + " " + treeNode.getType();
        rowStyleClass = rowStyleClass + " ui-node-level-" + (rowKey.split("_").length);

        if (partialSelected) {
            rowStyleClass = rowStyleClass + " " + TreeTable.PARTIAL_SELECTED_CLASS;
        }

        String userRowStyleClass = tt.getRowStyleClass();
        if (userRowStyleClass != null) {
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;
        }

        if (tt.isEditingRow()) {
            rowStyleClass = rowStyleClass + " " + TreeTable.EDITING_ROW_CLASS;
        }

        writer.startElement("tr", null);
        writer.writeAttribute("id", tt.getClientId(context) + "_node_" + rowKey, null);
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("role", "row", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(treeNode.isExpanded()), null);
        writer.writeAttribute("data-rk", rowKey, null);

        if (parentRowKey != null) {
            writer.writeAttribute("data-prk", parentRowKey, null);
        }

        if (selectionEnabled) {
            writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(selected), null);
        }

        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = columns.get(i);

            if (column.isDynamic()) {
                ((DynamicColumn) column).applyModel();
            }

            if (column.isRendered()) {
                String columnStyleClass = column.getStyleClass();
                String columnStyle = column.getStyle();
                int rowspan = column.getRowspan();
                int colspan = column.getColspan();
                int priority = column.getPriority();
                boolean isColVisible = column.isVisible();

                if (priority > 0) {
                    columnStyleClass = (columnStyleClass == null) ? "ui-column-p-" + priority : columnStyleClass + " ui-column-p-" + priority;
                }

                if (column.getCellEditor() != null) {
                    columnStyleClass = (columnStyleClass == null) ? TreeTable.EDITABLE_COLUMN_CLASS : TreeTable.EDITABLE_COLUMN_CLASS + " " + columnStyleClass;
                }

                if (!isColVisible) {
                    columnStyleClass = (columnStyleClass == null) ? TreeTable.HIDDEN_COLUMN_CLASS : columnStyleClass + " " + TreeTable.HIDDEN_COLUMN_CLASS;
                }

                writer.startElement("td", null);
                writer.writeAttribute("role", "gridcell", null);
                if (columnStyle != null) {
                    writer.writeAttribute("style", columnStyle, null);
                }
                if (columnStyleClass != null) {
                    writer.writeAttribute("class", columnStyleClass, null);
                }
                if (rowspan != 1) {
                    writer.writeAttribute("rowspan", rowspan, null);
                }
                if (colspan != 1) {
                    writer.writeAttribute("colspan", colspan, null);
                }

                if (i == 0) {
                    for (int j = 0; j < depth; j++) {
                        writer.startElement("span", null);
                        writer.writeAttribute("class", TreeTable.INDENT_CLASS, null);
                        writer.endElement("span");
                    }

                    writer.startElement("span", null);
                    writer.writeAttribute("class", icon, null);
                    if (treeNode.isLeaf()) {
                        writer.writeAttribute("style", "visibility:hidden", null);
                    }
                    writer.endElement("span");

                    if (checkboxSelection) {
                        if (!nativeElements) {
                            RendererUtils.encodeCheckbox(context, selected, partialSelected, !selectable, Tree.CHECKBOX_CLASS);
                        }
                        else {
                            renderNativeCheckbox(context, tt, selected, partialSelected);
                        }
                    }
                }

                column.renderChildren(context);

                writer.endElement("td");
            }
        }

        writer.endElement("tr");

        if (treeNode.isExpanded()) {
            encodeNodeChildren(context, tt, treeNode);
        }
    }

    protected void encodeColumnHeader(FacesContext context, TreeTable tt, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();
        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        ValueExpression columnSortByVE = column.getValueExpression("sortBy");
        boolean sortable = (columnSortByVE != null);
        boolean filterable = (column.getValueExpression("filterBy") != null && column.isFilterable());
        String sortIcon = null;
        String style = column.getStyle();
        String width = column.getWidth();
        String columnClass = sortable ? TreeTable.SORTABLE_COLUMN_HEADER_CLASS : TreeTable.COLUMN_HEADER_CLASS;
        columnClass = !column.isVisible() ? columnClass + " " + TreeTable.HIDDEN_COLUMN_CLASS : columnClass;
        columnClass = !column.isToggleable() ? columnClass + " " + TreeTable.STATIC_COLUMN_CLASS : columnClass;
        String userColumnClass = column.getStyleClass();
        if (column.isResizable()) {
            columnClass = columnClass + " " + TreeTable.RESIZABLE_COLUMN_CLASS;
        }
        if (userColumnClass != null) {
            columnClass = columnClass + " " + userColumnClass;
        }
        columnClass = filterable ? columnClass + " " + TreeTable.FILTER_COLUMN_CLASS : columnClass;

        if (sortable) {
            ValueExpression tableSortByVE = tt.getValueExpression("sortBy");
            if (tableSortByVE != null) {
                sortIcon = resolveSortIcon(columnSortByVE, tableSortByVE, tt.getSortOrder());
            }

            if (sortIcon == null) {
                sortIcon = TreeTable.SORTABLE_COLUMN_ICON_CLASS;
            }
            else {
                columnClass += " ui-state-active";
            }
        }

        int priority = column.getPriority();
        if (priority > 0) {
            columnClass = columnClass + " ui-column-p-" + priority;
        }

        if (width != null) {
            String unit = width.endsWith("%") ? "" : "px";
            if (style != null) {
                style = style + ";width:" + width + unit;
            }
            else {
                style = "width:" + width + unit;
            }
        }

        String ariaHeaderLabel = getHeaderLabel(context, column);

        writer.startElement("th", null);
        writer.writeAttribute("id", column.getContainerClientId(context), null);
        writer.writeAttribute("class", columnClass, null);
        writer.writeAttribute("role", "columnheader", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaHeaderLabel, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-column-title", null);

        if (header != null) {
            header.encodeAll(context);
        }
        else if (headerText != null) {
            writer.writeText(headerText, null);
        }

        writer.endElement("span");

        if (sortable) {
            writer.startElement("span", null);
            writer.writeAttribute("class", sortIcon, null);
            writer.endElement("span");
        }

        if (filterable) {
            tt.enableFiltering();
            encodeFilter(context, tt, column);
        }

        writer.endElement("th");
    }

    protected void encodeFilter(FacesContext context, TreeTable tt, UIColumn column) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");

        if (filterFacet == null) {
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
            boolean disableTabbing = tt.getScrollWidth() != null;

            String filterId = column.getContainerClientId(context) + separator + "filter";
            String filterStyleClass = column.getFilterStyleClass();

            Object filterValue = null;
            if (params.containsKey(filterId)) {
                filterValue = params.get(filterId);
            }
            else {
                Object columnFilterValue = column.getFilterValue();
                filterValue = (columnFilterValue == null) ? Constants.EMPTY_STRING : columnFilterValue.toString();
            }

            filterStyleClass = filterStyleClass == null ? TreeTable.COLUMN_INPUT_FILTER_CLASS : TreeTable.COLUMN_INPUT_FILTER_CLASS + " " + filterStyleClass;

            writer.startElement("input", null);
            writer.writeAttribute("id", filterId, null);
            writer.writeAttribute("name", filterId, null);
            writer.writeAttribute("class", filterStyleClass, null);
            writer.writeAttribute("value", filterValue, null);
            writer.writeAttribute("autocomplete", "off", null);

            if (disableTabbing) {
                writer.writeAttribute("tabindex", "-1", null);
            }

            if (column.getFilterStyle() != null) {
                writer.writeAttribute("style", column.getFilterStyle(), null);
            }

            if (column.getFilterMaxLength() != Integer.MAX_VALUE) {
                writer.writeAttribute("maxlength", column.getFilterMaxLength(), null);
            }

            writer.endElement("input");
        }
        else {
            writer.startElement("div", null);
            writer.writeAttribute("class", TreeTable.COLUMN_CUSTOM_FILTER_CLASS, null);
            filterFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected void encodeNodeChildren(FacesContext context, TreeTable tt, TreeNode treeNode) throws IOException {
        int childCount = treeNode.getChildCount();
        encodeNodeChildren(context, tt, treeNode, 0, childCount);
    }

    protected void encodeNodeChildren(FacesContext context, TreeTable tt, TreeNode treeNode, int first, int size) throws IOException {
        if (size > 0) {
            List<TreeNode> children = treeNode.getChildren();
            int childCount = treeNode.getChildCount();
            int last = (first + size);
            if (last > childCount) {
                last = childCount;
            }

            for (int i = first; i < last; i++) {
                encodeNode(context, tt, children.get(i));
            }
        }
    }

    protected void encodeFacet(FacesContext context, TreeTable tt, UIComponent facet, String styleClass) throws IOException {
        if (facet == null) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        facet.encodeAll(context);

        writer.endElement("div");
    }

    protected void encodeTfoot(FacesContext context, TreeTable tt) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = tt.getColumnGroup("footer");

        writer.startElement("tfoot", null);

        if (group != null && group.isRendered()) {
            for (UIComponent child : group.getChildren()) {
                if (child.isRendered() && child instanceof Row) {
                    Row footerRow = (Row) child;
                    String rowClass = footerRow.getStyleClass();
                    String rowStyle = footerRow.getStyle();

                    writer.startElement("tr", null);
                    if (rowClass != null) {
                        writer.writeAttribute("class", rowClass, null);
                    }
                    if (rowStyle != null) {
                        writer.writeAttribute("style", rowStyle, null);
                    }

                    for (UIComponent footerRowChild : footerRow.getChildren()) {
                        if (footerRowChild.isRendered() && footerRowChild instanceof Column) {
                            encodeColumnFooter(context, tt, (Column) footerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }
        }
        else if (tt.hasFooterColumn()) {
            writer.startElement("tr", null);

            List<UIColumn> columns = tt.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                UIColumn column = columns.get(i);

                if (column instanceof Column) {
                    encodeColumnFooter(context, tt, column);
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnFooter(context, tt, dynamicColumn);
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("tfoot");
    }

    protected void encodeColumnFooter(FacesContext context, TreeTable table, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        UIComponent footerFacet = column.getFacet("footer");
        String footerText = column.getFooterText();

        String style = column.getStyle();
        String columnStyleClass = column.getStyleClass();
        columnStyleClass = (columnStyleClass == null) ? TreeTable.COLUMN_HEADER_CLASS : TreeTable.COLUMN_HEADER_CLASS + " " + columnStyleClass;

        int priority = column.getPriority();
        if (priority > 0) {
            columnStyleClass = columnStyleClass + " ui-column-p-" + priority;
        }

        writer.startElement("td", null);
        writer.writeAttribute("class", columnStyleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }

        if (footerFacet != null) {
            footerFacet.encodeAll(context);
        }
        else if (footerText != null) {
            writer.writeText(footerText, null);
        }

        writer.endElement("td");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private void encodeStateHolder(FacesContext context, TreeTable tt, String name, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("value", value, null);
        writer.endElement("input");
    }

    protected String resolveSortIcon(ValueExpression columnSortBy, ValueExpression ttSortBy, String sortOrder) {
        String columnSortByExpression = columnSortBy.getExpressionString();
        String ttSortByExpression = ttSortBy.getExpressionString();
        String sortIcon = null;

        if (ttSortByExpression != null && ttSortByExpression.equals(columnSortByExpression)) {
            if (sortOrder.equalsIgnoreCase("ASCENDING")) {
                sortIcon = TreeTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
            }
            else if (sortOrder.equalsIgnoreCase("DESCENDING")) {
                sortIcon = TreeTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
            }
        }

        return sortIcon;
    }

    protected void decodeSort(FacesContext context, TreeTable tt) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String sortKey = params.get(clientId + "_sortKey");
        String sortDir = params.get(clientId + "_sortDir");

        UIColumn sortColumn = tt.findColumn(sortKey);
        ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
        tt.setValueExpression("sortBy", sortByVE);
        tt.setSortColumn(sortColumn);
        tt.setSortFunction(sortColumn.getSortFunction());
        tt.setSortOrder(sortDir);
    }

    protected void encodeSort(FacesContext context, TreeTable tt) throws IOException {
        sort(tt);

        encodeTbody(context, tt, true);
    }

    public void sort(TreeTable tt) {
        TreeNode root = tt.getValue();
        if (root == null) {
            return;
        }

        UIColumn sortColumn = tt.getSortColumn();
        if (sortColumn != null && sortColumn.isDynamic()) {
            ((DynamicColumn) sortColumn).applyStatelessModel();
        }

        ValueExpression sortByVE = tt.getValueExpression("sortBy");
        SortOrder sortOrder = SortOrder.valueOf(tt.getSortOrder().toUpperCase(Locale.ENGLISH));
        TreeUtils.sortNode(root, new TreeNodeComparator(sortByVE, tt.getVar(), sortOrder, tt.getSortFunction(),
                tt.isCaseSensitiveSort(), tt.resolveDataLocale()));
        tt.updateRowKeys(root);

        String selectedRowKeys = tt.getSelectedRowKeysAsString();
        if (selectedRowKeys != null) {
            PrimeFaces.current().ajax().addCallbackParam("selection", selectedRowKeys);
        }
    }

    protected void renderNativeCheckbox(FacesContext context, TreeTable tt, boolean checked, boolean partialSelected) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("name", tt.getContainerClientId(context) + "_checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (partialSelected) {
            writer.writeAttribute("class", "ui-treetable-indeterminate", null);
        }

        writer.endElement("input");
    }

    public void encodeRowEdit(FacesContext context, TreeTable tt) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String editedRowKey = params.get(clientId + "_rowEditIndex");
        String action = params.get(clientId + "_rowEditAction");

        tt.setRowKey(editedRowKey);
        TreeNode node = tt.getRowNode();

        if (action.equals("cancel")) {
            VisitContext visitContext = null;

            for (UIColumn column : tt.getColumns()) {
                for (UIComponent grandkid : column.getChildren()) {
                    if (grandkid instanceof CellEditor) {
                        UIComponent inputFacet = grandkid.getFacet("input");

                        if (inputFacet instanceof EditableValueHolder) {
                            ((EditableValueHolder) inputFacet).resetValue();
                        }
                        else {
                            if (visitContext == null) {
                                visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                            }
                            inputFacet.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                        }
                    }
                }
            }
        }

        encodeNode(context, tt, node);
    }

    public void encodeCellEdit(FacesContext context, TreeTable tt) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String[] cellInfo = params.get(clientId + "_cellInfo").split(",");
        String rowKey = cellInfo[0];
        int cellIndex = Integer.parseInt(cellInfo[1]);
        int i = -1;
        UIColumn column = null;
        for (UIColumn col : tt.getColumns()) {
            if (col.isRendered()) {
                i++;

                if (i == cellIndex) {
                    column = col;
                    break;
                }
            }
        }

        tt.setRowKey(rowKey);

        if (column.isDynamic()) {
            DynamicColumn dynamicColumn = (DynamicColumn) column;
            dynamicColumn.applyStatelessModel();
        }

        if (tt.isCellEditCancelRequest(context) || tt.isCellEditInitRequest(context)) {
            column.getCellEditor().getFacet("input").encodeAll(context);
        }
        else {
            column.getCellEditor().getFacet("output").encodeAll(context);
        }

        if (column.isDynamic()) {
            ((DynamicColumn) column).cleanStatelessModel();
        }
    }

    public List<FilterMeta> populateFilterMetaData(FacesContext context, TreeTable tt) {
        List<FilterMeta> filterMetadata = new ArrayList<>();
        String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        for (UIColumn column : tt.getColumns()) {
            ValueExpression columnFilterByVE = column.getValueExpression("filterBy");

            if (columnFilterByVE != null) {
                UIComponent filterFacet = column.getFacet("filter");
                ValueExpression filterByVE = columnFilterByVE;
                Object filterValue = null;
                String filterId = null;

                if (column instanceof Column) {
                    filterId = column.getClientId(context) + separator + "filter";
                    filterValue = (filterFacet == null) ? params.get(filterId) : ((ValueHolder) filterFacet).getLocalValue();
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();
                    filterId = dynamicColumn.getContainerClientId(context) + separator + "filter";
                    filterValue = (filterFacet == null) ? params.get(filterId) : ((ValueHolder) filterFacet).getLocalValue();
                    dynamicColumn.cleanModel();
                }

                filterMetadata.add(new FilterMeta(column, filterByVE, filterValue));
            }
        }

        return filterMetadata;
    }

    public void filter(FacesContext context, TreeTable tt, List<FilterMeta> filterMetadata, String globalFilterValue) throws IOException {
        Locale filterLocale = LocaleUtils.getCurrentLocale(context);
        TreeNode root = tt.getValue();
        TreeNode filteredNode = null;

        tt.getFilteredRowKeys().clear();
        findFilteredRowKeys(context, tt, root, filterMetadata, filterLocale, globalFilterValue);

        filteredNode = createNewNode(root, root.getParent());

        List<String> filteredRowKeys = tt.getFilteredRowKeys();

        createFilteredNodeFromRowKeys(tt, root, filteredNode, filteredRowKeys);

        tt.updateFilteredNode(context, filteredNode);
        tt.setValue(filteredNode);
        tt.setRowKey(null);

        //Metadata for callback
        if (tt.isPaginator()) {
            PrimeFaces.current().ajax().addCallbackParam("totalRecords", filteredNode.getChildren().size());
        }
        if (tt.getSelectedRowKeysAsString() != null) {
            PrimeFaces.current().ajax().addCallbackParam("selection", tt.getSelectedRowKeysAsString());
        }
    }

    protected void findFilteredRowKeys(FacesContext context, TreeTable tt, TreeNode node, List<FilterMeta> filterMetadata, Locale filterLocale,
                                       String globalFilterValue) throws IOException {
        int childCount = node.getChildCount();
        boolean hasGlobalFilter = !LangUtils.isValueBlank(globalFilterValue);
        GlobalFilterConstraint globalFilterConstraint = (GlobalFilterConstraint) FILTER_CONSTRAINTS.get(GLOBAL_MODE);
        ELContext elContext = context.getELContext();

        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = node.getChildren().get(i);
            boolean localMatch = true;
            boolean globalMatch = false;
            String rowKey = childNode.getRowKey();
            tt.setRowKey(rowKey);

            for (FilterMeta filterMeta : filterMetadata) {
                Object filterValue = filterMeta.getFilterValue();
                UIColumn column = filterMeta.getColumn();
                MethodExpression filterFunction = column.getFilterFunction();
                ValueExpression filterByVE = filterMeta.getFilterByVE();

                if (column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyStatelessModel();
                }

                Object columnValue = filterByVE.getValue(elContext);
                FilterConstraint filterConstraint = getFilterConstraint(column);

                if (hasGlobalFilter && !globalMatch) {
                    globalMatch = globalFilterConstraint.applies(columnValue, globalFilterValue, filterLocale);
                }
                if (filterFunction != null) {
                    localMatch = (Boolean) filterFunction.invoke(elContext, new Object[]{columnValue, filterValue, filterLocale});
                }
                else if (!filterConstraint.applies(columnValue, filterValue, filterLocale)) {
                    localMatch = false;
                }

                if (!localMatch) {
                    break;
                }
            }

            boolean matches = localMatch;
            if (hasGlobalFilter) {
                matches = localMatch && globalMatch;
            }

            if (matches) {
                tt.getFilteredRowKeys().add(rowKey);
            }

            findFilteredRowKeys(context, tt, childNode, filterMetadata, filterLocale, globalFilterValue);
        }
    }

    private void createFilteredNodeFromRowKeys(TreeTable tt, TreeNode node, TreeNode filteredNode, List<String> filteredRowKeys) {
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeNode childNode = node.getChildren().get(i);
            String rowKeyOfChildNode = childNode.getRowKey();

            for (String rk : filteredRowKeys) {
                if (rk.equals(rowKeyOfChildNode) || rk.startsWith(rowKeyOfChildNode + "_") || rowKeyOfChildNode.startsWith(rk + "_")) {
                    TreeNode newNode = createNewNode(childNode, filteredNode);
                    if (rk.startsWith(rowKeyOfChildNode + "_")) {
                        newNode.setExpanded(true);
                    }

                    createFilteredNodeFromRowKeys(tt, childNode, newNode, filteredRowKeys);
                    break;
                }
            }
        }
    }

    protected TreeNode createNewNode(TreeNode node, TreeNode parent) {
        TreeNode newNode = null;

        if (node instanceof CheckboxTreeNode) {
            newNode = new CheckboxTreeNode(node.getType(), node.getData(), parent);
        }
        else {
            newNode = new DefaultTreeNode(node.getType(), node.getData(), parent);
        }

        newNode.setSelected(node.isSelected());
        newNode.setExpanded(node.isExpanded());

        return newNode;
    }

    public FilterConstraint getFilterConstraint(UIColumn column) {
        String filterMatchMode = column.getFilterMatchMode();
        FilterConstraint filterConstraint = TreeTable.FILTER_CONSTRAINTS.get(filterMatchMode);

        if (filterConstraint == null) {
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        return filterConstraint;
    }
}
