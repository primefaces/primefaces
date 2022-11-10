/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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

import static org.primefaces.component.api.UITree.ROOT_ROW_KEY;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITree;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.component.tree.Tree;
import org.primefaces.component.treetable.feature.FilterFeature;
import org.primefaces.component.treetable.feature.ResizableColumnsFeature;
import org.primefaces.component.treetable.feature.SelectionFeature;
import org.primefaces.component.treetable.feature.SortFeature;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.visit.ResetInputVisitCallback;

public class TreeTableRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TreeTable tt = (TreeTable) component;

        if (SelectionFeature.getInstance().shouldDecode(context, tt)) {
            SelectionFeature.getInstance().decode(context, tt);
        }
        if (SortFeature.getInstance().shouldDecode(context, tt)) {
            SortFeature.getInstance().decode(context, tt);
        }
        if (FilterFeature.getInstance().shouldDecode(context, tt)) {
            FilterFeature.getInstance().decode(context, tt);
        }
        if (ResizableColumnsFeature.getInstance().shouldDecode(context, tt)) {
            ResizableColumnsFeature.getInstance().decode(context, tt);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        TreeTable tt = (TreeTable) component;
        TreeNode root = tt.getValue();
        String clientId = tt.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (tt.isExpandRequest(context)) {
            String nodeKey = params.get(clientId + "_expand");
            tt.setRowKey(root, nodeKey);
            TreeNode node = tt.getRowNode();
            node.setExpanded(true);

            if (tt.getExpandMode().equals("self")) {
                encodeNode(context, tt, root, node);
            }
            else {
                encodeNodeChildren(context, tt, root, node);
            }
        }
        else if (tt.isCollapseRequest(context)) {
            String nodeKey = params.get(clientId + "_collapse");
            tt.setRowKey(root, nodeKey);
            TreeNode node = tt.getRowNode();
            node.setExpanded(false);
        }
        else if (FilterFeature.getInstance().shouldEncode(context, tt)) {
            FilterFeature.getInstance().encode(context, this, tt);
        }
        else if (SortFeature.getInstance().shouldEncode(context, tt)) {
            SortFeature.getInstance().encode(context, this, tt);
        }
        else if (tt.isRowEditRequest(context)) {
            encodeRowEdit(context, tt, root);
        }
        else if (tt.isCellEditRequest(context)) {
            encodeCellEdit(context, tt, root);
        }
        else if (tt.isPaginationRequest(context)) {
            tt.updatePaginationData(context);
            encodeNodeChildren(context, tt, root, root, tt.getFirst(), tt.getRows());
        }
        else {
            if (tt.isDefaultFilter()) {
                FilterFeature.getInstance().filter(context, tt, root);
            }
            if (tt.isDefaultSort()) {
                SortFeature.getInstance().sort(context, tt);
            }

            render(context, tt);
        }
    }

    protected void render(FacesContext context, TreeTable table) throws IOException {
        preRender(context, table);

        encodeMarkup(context, table);
        encodeScript(context, table);
    }

    protected void preRender(FacesContext context, TreeTable table) {
        // trigger init, otherwise column state might be confused when rendering and init at the same time
        table.getSortByAsMap();
        table.getFilterByAsMap();

        if (table.isMultiViewState()) {
            table.restoreMultiViewState();
        }

        table.resetDynamicColumns();
    }

    protected void encodeScript(FacesContext context, TreeTable tt) throws IOException {
        String selectionMode = tt.getSelectionMode();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TreeTable", tt)
                .attr("selectionMode", selectionMode, null)
                .attr("resizableColumns", tt.isResizableColumns(), false)
                .attr("liveResize", tt.isLiveResize(), false)
                .attr("scrollable", tt.isScrollable(), false)
                .attr("scrollHeight", tt.getScrollHeight(), null)
                .attr("scrollWidth", tt.getScrollWidth(), null)
                .attr("nativeElements", tt.isNativeElements(), false)
                .attr("expandMode", tt.getExpandMode(), "children")
                .attr("propagateSelectionUp", tt.isPropagateSelectionUp(), true)
                .attr("propagateSelectionDown", tt.isPropagateSelectionDown(), true)
                .attr("disabledTextSelection", tt.isDisabledTextSelection(), true);

        if (tt.isStickyHeader()) {
            wb.attr("stickyHeader", true);
        }

        //Editing
        if (tt.isEditable()) {
            wb.attr("editable", true)
                    .attr("editMode", tt.getEditMode())
                    .attr("saveOnCellBlur", tt.isSaveOnCellBlur(), true)
                    .attr("cellEditMode", tt.getCellEditMode(), "eager")
                    .attr("cellSeparator", tt.getCellSeparator(), null)
                    .attr("editInitEvent", tt.getEditInitEvent());
        }

        //Filtering
        if (tt.isFilteringEnabled()) {
            wb.attr("filter", true)
                    .attr("filterEvent", tt.getFilterEvent(), null)
                    .attr("filterDelay", tt.getFilterDelay(), Integer.MAX_VALUE);
        }

        if (tt.isSortingEnabled()) {
            wb.attr("sorting", true);

            if (tt.isMultiSort()) {
                wb.attr("multiSort", true)
                        .nativeAttr("sortMetaOrder", tt.getSortMetaAsString(), null);
            }

            if (tt.isAllowUnsorting()) {
                wb.attr("allowUnsorting", true);
            }
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
        boolean resizable = tt.isResizableColumns();
        TreeNode root = tt.getValue();
        boolean hasPaginator = tt.isPaginator();

        if (root == null) {
            throw new FacesException("treeTable's value must not be null. ClientId: " + clientId);
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

        String containerClass = tt.isResizableColumns() ? TreeTable.RESIZABLE_CONTAINER_CLASS : TreeTable.CONTAINER_CLASS;
        containerClass = scrollable ? containerClass + " " + TreeTable.SCROLLABLE_CONTAINER_CLASS : containerClass;
        containerClass = tt.getStyleClass() == null ? containerClass : containerClass + " " + tt.getStyleClass();
        containerClass = tt.isShowUnselectableCheckbox() ? containerClass + " ui-treetable-checkbox-all" : containerClass;
        containerClass = tt.isShowGridlines() ? containerClass + " " + TreeTable.GRIDLINES_CLASS : containerClass;
        containerClass = "small".equals(tt.getSize()) ? containerClass + " " + TreeTable.SMALL_SIZE_CLASS : containerClass;
        containerClass = "large".equals(tt.getSize()) ? containerClass + " " + TreeTable.LARGE_SIZE_CLASS : containerClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, null);
        if (tt.getStyle() != null) {
            writer.writeAttribute("style", tt.getStyle(), null);
        }

        if (scrollable) {
            encodeScrollableMarkup(context, tt, root);
        }
        else {
            encodeRegularMarkup(context, tt, root);
        }

        if (tt.getSelectionMode() != null) {
            encodeStateHolder(context, tt, clientId + "_selection", tt.getSelectedRowKeysAsString());
        }

        if (scrollable) {
            encodeStateHolder(context, tt, clientId + "_scrollState", tt.getScrollState());
        }

        if (resizable) {
            encodeStateHolder(context, tt, tt.getClientId(context) + "_resizableColumnState", tt.getColumnsWidthForClientSide());
        }

        writer.endElement("div");
    }

    protected void encodeScrollableMarkup(FacesContext context, TreeTable tt, TreeNode root) throws IOException {
        String tableStyle = tt.getTableStyle();
        String tableStyleClass = tt.getTableStyleClass();
        boolean hasPaginator = tt.isPaginator();
        String paginatorPosition = tt.getPaginatorPosition();

        encodeScrollAreaStart(context, tt, TreeTable.SCROLLABLE_HEADER_CLASS, TreeTable.SCROLLABLE_HEADER_BOX_CLASS,
                tableStyle, tableStyleClass, "header", TreeTable.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, tt, "top");
        }

        encodeThead(context, tt);
        encodeScrollAreaEnd(context);

        encodeScrollBody(context, tt, root, tableStyle, tableStyleClass);

        encodeScrollAreaStart(context, tt, TreeTable.SCROLLABLE_FOOTER_CLASS, TreeTable.SCROLLABLE_FOOTER_BOX_CLASS,
                tableStyle, tableStyleClass, "footer", TreeTable.FOOTER_CLASS);
        encodeTfoot(context, tt);

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, tt, "bottom");
        }
        encodeScrollAreaEnd(context);
    }

    protected void encodeScrollBody(FacesContext context, TreeTable tt, TreeNode root, String tableStyle, String tableStyleClass) throws IOException {
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

        encodeTbody(context, tt, root, false);

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

    protected void encodeRegularMarkup(FacesContext context, TreeTable tt, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean hasPaginator = tt.isPaginator();
        String paginatorPosition = tt.getPaginatorPosition();

        encodeFacet(context, tt, tt.getFacet("header"), TreeTable.HEADER_CLASS);

        if (tt.isPaginator() && !"bottom".equalsIgnoreCase(paginatorPosition)) {
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
        encodeTbody(context, tt, root, false);

        writer.endElement("table");

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
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
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for (UIComponent child : group.getChildren()) {
                if (child.isRendered()) {
                    if (child instanceof Row) {
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
                            if (headerRowChild.isRendered()) {
                                if (headerRowChild instanceof Column) {
                                    encodeColumnHeader(context, tt, (Column) headerRowChild);
                                }
                                else if (headerRowChild instanceof Columns) {
                                    List<DynamicColumn> dynamicColumns = ((Columns) headerRowChild).getDynamicColumns();
                                    for (DynamicColumn dynaColumn : dynamicColumns) {
                                        dynaColumn.applyModel();
                                        encodeColumnHeader(context, tt, dynaColumn);
                                    }
                                }
                                else {
                                    headerRowChild.encodeAll(context);
                                }
                            }
                        }

                        writer.endElement("tr");
                    }
                    else {
                        child.encodeAll(context);
                    }
                }
            }

            context.getAttributes().remove(Constants.HELPER_RENDERER);
        }
        else {
            writer.startElement("tr", null);

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

    public void encodeTbody(FacesContext context, TreeTable tt, TreeNode root, boolean dataOnly) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
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

            if (ComponentUtils.shouldRenderFacet(emptyFacet)) {
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
                encodeNodeChildren(context, tt, root, root, first, rows);
            }
            else {
                encodeNodeChildren(context, tt, root, root);
            }
        }

        tt.setRowKey(root, null);

        if (!dataOnly) {
            writer.endElement("tbody");
        }
    }

    protected void encodeNode(FacesContext context, TreeTable tt, TreeNode root, TreeNode treeNode) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowKey = treeNode.getRowKey();
        String parentRowKey = treeNode.getParent().getRowKey();
        tt.setRowKey(root, rowKey);
        String icon = treeNode.isExpanded() ? TreeTable.COLLAPSE_ICON : TreeTable.EXPAND_ICON;
        int depth = rowKey.split(UITree.SEPARATOR).length - 1;
        boolean selectionEnabled = tt.isSelectionEnabled();
        boolean selectable = treeNode.isSelectable() && selectionEnabled;
        boolean checkboxSelection = selectionEnabled && tt.isCheckboxSelectionMode();
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
        writer.writeAttribute("title", tt.getRowTitle(), null);
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
            ColumnMeta columnMeta = tt.getColumnMeta().get(column.getColumnKey(tt, rowKey));

            if (column.isDynamic()) {
                ((DynamicColumn) column).applyModel();
            }

            if (column.isRendered()) {
                boolean columnVisible = column.isVisible();
                if (columnMeta != null && columnMeta.getVisible() != null) {
                    columnVisible = columnMeta.getVisible();
                }

                String columnStyleClass = column.getStyleClass();
                String columnStyle = column.getStyle();
                int rowspan = column.getRowspan();
                int colspan = column.getColspan();
                int responsivePriority = column.getResponsivePriority();

                if (responsivePriority > 0) {
                    columnStyleClass = (columnStyleClass == null)
                            ? "ui-column-p-" + responsivePriority
                            : columnStyleClass + " ui-column-p-" + responsivePriority;
                }

                if (column.getCellEditor() != null) {
                    columnStyleClass = (columnStyleClass == null) ? TreeTable.EDITABLE_COLUMN_CLASS : TreeTable.EDITABLE_COLUMN_CLASS + " " + columnStyleClass;
                }

                if (!columnVisible) {
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
            encodeNodeChildren(context, tt, root, treeNode);
        }
    }

    public void encodeColumnHeader(FacesContext context, TreeTable tt, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = tt.getColumnMeta().get(column.getColumnKey());

        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        boolean sortable = tt.isColumnSortable(context, column);
        boolean filterable = tt.isColumnFilterable(context, column);
        SortMeta sortMeta = null;
        String style = column.getStyle();
        String width = column.getWidth();
        String columnClass = sortable ? TreeTable.SORTABLE_COLUMN_HEADER_CLASS : TreeTable.COLUMN_HEADER_CLASS;
        columnClass = !columnVisible ? columnClass + " " + TreeTable.HIDDEN_COLUMN_CLASS : columnClass;
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
            sortMeta = tt.getSortByAsMap().get(column.getColumnKey());
            if (sortMeta.isActive()) {
                columnClass += " ui-state-active";
            }
        }

        int responsivePriority = column.getResponsivePriority();
        if (responsivePriority > 0) {
            columnClass = columnClass + " ui-column-p-" + responsivePriority;
        }

        if (width != null) {
            String unit = endsWithLenghtUnit(width) ? Constants.EMPTY_STRING : "px";
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

        if (ComponentUtils.shouldRenderFacet(header)) {
            header.encodeAll(context);
        }
        else if (headerText != null) {
            writer.writeText(headerText, null);
        }

        writer.endElement("span");

        if (sortable && sortMeta != null) {
            String sortIcon = resolveDefaultSortIcon(sortMeta);
            if (sortIcon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", sortIcon, null);
                writer.endElement("span");

                if (tt.isMultiSort()) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", TreeTable.SORTABLE_PRIORITY_CLASS, null);
                    writer.endElement("span");
                }
            }
        }

        if (filterable) {
            encodeFilter(context, tt, column);
        }

        writer.endElement("th");
    }

    protected String resolveDefaultSortIcon(SortMeta sortMeta) {
        SortOrder sortOrder = sortMeta.getOrder();
        String sortIcon = TreeTable.SORTABLE_COLUMN_ICON_CLASS;
        if (sortOrder.isAscending()) {
            sortIcon = TreeTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
        }
        else if (sortOrder.isDescending()) {
            sortIcon = TreeTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
        }
        return sortIcon;
    }

    protected void encodeFilter(FacesContext context, TreeTable tt, UIColumn column) throws IOException {
        if (tt.isGlobalFilterOnly()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");

        if (!ComponentUtils.shouldRenderFacet(filterFacet)) {
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
            boolean disableTabbing = tt.getScrollWidth() != null;

            String filterId = column.getContainerClientId(context) + separator + "filter";
            Object filterValue = tt.getFilterValue(column);
            filterValue = (filterValue == null) ? Constants.EMPTY_STRING : filterValue.toString();

            String filterStyleClass = getStyleClassBuilder(context)
                        .add(TreeTable.COLUMN_INPUT_FILTER_CLASS)
                        .add(column.getFilterStyleClass())
                        .build();

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
            Object filterValue = tt.getFilterValue(column);
            if (filterValue != null) {
                ((ValueHolder) filterFacet).setValue(filterValue);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", TreeTable.COLUMN_CUSTOM_FILTER_CLASS, null);
            filterFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected void encodeNodeChildren(FacesContext context, TreeTable tt, TreeNode root, TreeNode treeNode) throws IOException {
        int childCount = treeNode.getChildCount();
        encodeNodeChildren(context, tt, root, treeNode, 0, childCount);
    }

    protected void encodeNodeChildren(FacesContext context, TreeTable tt, TreeNode root, TreeNode treeNode, int first, int size) throws IOException {
        if (size > 0) {
            List<TreeNode> children = treeNode.getChildren();
            int childCount = treeNode.getChildCount();
            int last = (first + size);
            if (last > childCount) {
                last = childCount;
            }

            for (int i = first; i < last; i++) {
                encodeNode(context, tt, root, children.get(i));
            }
        }
    }

    protected void encodeFacet(FacesContext context, TreeTable tt, UIComponent facet, String styleClass) throws IOException {
        if (!ComponentUtils.shouldRenderFacet(facet)) {
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
        boolean hasFooterColumn = tt.hasFooterColumn();
        boolean shouldRenderFooter = (hasFooterColumn || group != null);

        if (!shouldRenderFooter) {
            return;
        }

        writer.startElement("tfoot", null);

        if (group != null && group.isRendered()) {
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for (UIComponent child : group.getChildren()) {
                if (child.isRendered()) {
                    if (child instanceof Row) {
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
                            if (footerRowChild.isRendered()) {
                                if (footerRowChild instanceof Column) {
                                    encodeColumnFooter(context, tt, (Column) footerRowChild);
                                }
                                else if (footerRowChild instanceof Columns) {
                                    List<DynamicColumn> dynamicColumns = ((Columns) footerRowChild).getDynamicColumns();
                                    for (DynamicColumn dynaColumn : dynamicColumns) {
                                        dynaColumn.applyModel();
                                        encodeColumnFooter(context, tt, dynaColumn);
                                    }
                                }
                                else {
                                    footerRowChild.encodeAll(context);
                                }
                            }
                        }

                        writer.endElement("tr");
                    }
                    else {
                        child.encodeAll(context);
                    }
                }
            }

            context.getAttributes().remove(Constants.HELPER_RENDERER);
        }
        else if (hasFooterColumn) {
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

    public void encodeColumnFooter(FacesContext context, TreeTable table, UIColumn column) throws IOException {
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

        int responsivePriority = column.getResponsivePriority();
        if (responsivePriority > 0) {
            columnStyleClass = columnStyleClass + " ui-column-p-" + responsivePriority;
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

        if (ComponentUtils.shouldRenderFacet(footerFacet)) {
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
        renderHiddenInput(context, name, value, false);
    }

    protected String resolveSortIcon(SortMeta sortMeta) {
        if (sortMeta == null) {
            return null;
        }

        SortOrder sortOrder = sortMeta.getOrder();
        if (sortOrder == SortOrder.ASCENDING) {
            return TreeTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
        }
        else if (sortOrder == SortOrder.DESCENDING) {
            return TreeTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
        }

        return null;
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

    public void encodeRowEdit(FacesContext context, TreeTable tt, TreeNode root) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = tt.getClientId(context);
        String editedRowKey = params.get(clientId + "_rowEditIndex");
        String action = params.get(clientId + "_rowEditAction");

        tt.setRowKey(root, editedRowKey);
        TreeNode node = tt.getRowNode();

        if ("cancel".equals(action)) {
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

        encodeNode(context, tt, root, node);
    }

    public void encodeCellEdit(FacesContext context, TreeTable tt, TreeNode root) throws IOException {
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

        if (column == null) {
            throw new FacesException("No column found for cellIndex: " + cellIndex);
        }

        tt.setRowKey(root, rowKey);

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

}
