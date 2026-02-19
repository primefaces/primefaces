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

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITree;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.component.tree.Tree;
import org.primefaces.component.treetable.feature.TreeTableFeature;
import org.primefaces.component.treetable.feature.TreeTableFeatures;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = TreeTable.DEFAULT_RENDERER, componentFamily = TreeTable.COMPONENT_FAMILY)
public class TreeTableRenderer extends DataRenderer<TreeTable> {

    @Override
    public void decode(FacesContext context, TreeTable component) {
        for (TreeTableFeature feature : TreeTableFeatures.all()) {
            if (feature.shouldDecode(context, component)) {
                feature.decode(context, component);
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, TreeTable component) throws IOException {
        if (component.shouldEncodeFeature(context)) {
            for (TreeTableFeature feature : TreeTableFeatures.all()) {
                if (feature.shouldEncode(context, component)) {
                    feature.encode(context, this, component);
                }
            }
        }
        else {
            render(context, component);
        }
    }

    protected void render(FacesContext context, TreeTable component) throws IOException {
        preRender(context, component);

        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void preRender(FacesContext context, TreeTable component) {
        // trigger init, otherwise column state might be confused when rendering and init at the same time
        component.getSortByAsMap();
        component.getFilterByAsMap();

        if (component.isMultiViewState()) {
            component.restoreMultiViewState();
        }

        if (component.isFilteringCurrentlyActive()) {
            TreeTableFeatures.filterFeature().filter(context, component, component.getValue());
        }
        if (component.isSortingCurrentlyActive()) {
            TreeTableFeatures.sortFeature().sort(context, component);
        }
    }

    protected void encodeScript(FacesContext context, TreeTable component) throws IOException {
        String selectionMode = component.getSelectionMode();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TreeTable", component)
                .attr("selectionMode", selectionMode, null)
                .attr("resizableColumns", component.isResizableColumns(), false)
                .attr("liveResize", component.isLiveResize(), false)
                .attr("resizeMode", component.getResizeMode())
                .attr("scrollable", component.isScrollable(), false)
                .attr("scrollHeight", component.getScrollHeight(), null)
                .attr("scrollWidth", component.getScrollWidth(), null)
                .attr("nativeElements", component.isNativeElements(), false)
                .attr("expandMode", component.getExpandMode(), "children")
                .attr("propagateSelectionUp", component.isPropagateSelectionUp(), true)
                .attr("propagateSelectionDown", component.isPropagateSelectionDown(), true)
                .attr("disabledTextSelection", component.isDisabledTextSelection(), true);

        if (component.isStickyHeader()) {
            wb.attr("stickyHeader", true);
        }

        //Editing
        if (component.isEditable()) {
            wb.attr("editable", true)
                    .attr("editMode", component.getEditMode())
                    .attr("saveOnCellBlur", component.isSaveOnCellBlur(), true)
                    .attr("cellEditMode", component.getCellEditMode(), "eager")
                    .attr("cellSeparator", component.getCellSeparator(), null)
                    .attr("editInitEvent", component.getEditInitEvent());
        }

        //Filtering
        if (component.isFilteringEnabled()) {
            wb.attr("filter", true)
                    .attr("filterEvent", component.getFilterEvent(), null)
                    .attr("filterDelay", component.getFilterDelay(), Integer.MAX_VALUE);
        }

        if (component.isSortingEnabled()) {
            wb.attr("sorting", true);

            if (component.isMultiSort()) {
                wb.attr("multiSort", true)
                        .nativeAttr("sortMetaOrder", component.getSortMetaAsString(), null);
            }

            if (component.isAllowUnsorting()) {
                wb.attr("allowUnsorting", true);
            }
        }

        if (component.isPaginator()) {
            encodePaginatorConfig(context, component, wb);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, TreeTable component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean scrollable = component.isScrollable();
        boolean resizable = component.isResizableColumns();
        TreeNode root = component.getValue();
        boolean hasPaginator = component.isPaginator();

        if (root == null) {
            throw new FacesException("treeTable's value must not be null. ClientId: " + clientId);
        }
        if (!(root instanceof TreeNode)) {
            throw new FacesException("treeTable's value must be an instance of " + TreeNode.class.getName() + ". ClientId: " + clientId);
        }

        if (hasPaginator) {
            component.calculateFirst();
        }

        if (root.getRowKey() == null) {
            root.setRowKey(UITree.ROOT_ROW_KEY);
            component.buildRowKeys(root);
            component.initPreselection();
        }

        String containerClass = getStyleClassBuilder(context)
                .add(component.isResizableColumns(), TreeTable.RESIZABLE_CONTAINER_CLASS, TreeTable.CONTAINER_CLASS)
                .add(scrollable, TreeTable.SCROLLABLE_CONTAINER_CLASS)
                .add(component.getStyleClass())
                .add(component.isShowUnselectableCheckbox(), "ui-treetable-checkbox-all")
                .add(component.isShowGridlines(), TreeTable.GRIDLINES_CLASS)
                .add("small".equals(component.getSize()), TreeTable.SMALL_SIZE_CLASS)
                .add("large".equals(component.getSize()), TreeTable.LARGE_SIZE_CLASS)
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        if (scrollable) {
            encodeScrollableMarkup(context, component, root);
        }
        else {
            encodeRegularMarkup(context, component, root);
        }

        if (component.getSelectionMode() != null) {
            encodeStateHolder(context, component, clientId + "_selection", component.getSelectedRowKeysAsString());
        }

        if (scrollable) {
            encodeStateHolder(context, component, clientId + "_scrollState", component.getScrollState());
        }

        if (resizable) {
            encodeStateHolder(context, component, component.getClientId(context) + "_resizableColumnState", component.getColumnsWidthForClientSide());
        }

        writer.endElement("div");
    }

    protected void encodeScrollableMarkup(FacesContext context, TreeTable component, TreeNode<?> root) throws IOException {
        String tableStyle = component.getTableStyle();
        String tableStyleClass = component.getTableStyleClass();
        boolean hasPaginator = component.isPaginator();
        String paginatorPosition = component.getPaginatorPosition();

        encodeScrollAreaStart(context, component, TreeTable.SCROLLABLE_HEADER_CLASS, TreeTable.SCROLLABLE_HEADER_BOX_CLASS,
                tableStyle, tableStyleClass, "header", TreeTable.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "top");
        }

        encodeThead(context, component);
        encodeScrollAreaEnd(context);

        encodeScrollBody(context, component, root, tableStyle, tableStyleClass);

        encodeScrollAreaStart(context, component, TreeTable.SCROLLABLE_FOOTER_CLASS, TreeTable.SCROLLABLE_FOOTER_BOX_CLASS,
                tableStyle, tableStyleClass, "footer", TreeTable.FOOTER_CLASS);
        encodeTfoot(context, component);

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "bottom");
        }
        encodeScrollAreaEnd(context);
    }

    protected void encodeScrollBody(FacesContext context, TreeTable component, TreeNode root, String tableStyle, String tableStyleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String scrollHeight = component.getScrollHeight();

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

        encodeTbody(context, component, root, false);

        writer.endElement("table");
        writer.endElement("div");
    }

    protected void encodeScrollAreaStart(FacesContext context, TreeTable component, String containerClass, String containerBoxClass,
                                         String tableStyle, String tableStyleClass, String facet, String facetClass) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        encodeFacet(context, component, component.getFacet(facet), facetClass);

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

    protected void encodeRegularMarkup(FacesContext context, TreeTable component, TreeNode root) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean hasPaginator = component.isPaginator();
        String paginatorPosition = component.getPaginatorPosition();

        encodeFacet(context, component, component.getHeaderFacet(), TreeTable.HEADER_CLASS);

        if (component.isPaginator() && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "top");
        }

        writer.startElement("table", component);
        writer.writeAttribute("role", "treegrid", null);
        if (component.getTableStyle() != null) {
            writer.writeAttribute("style", component.getTableStyle(), null);
        }
        if (component.getTableStyleClass() != null) {
            writer.writeAttribute("class", component.getTableStyleClass(), null);
        }

        encodeThead(context, component);
        encodeTfoot(context, component);
        encodeTbody(context, component, root, false);

        writer.endElement("table");

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "bottom");
        }

        encodeFacet(context, component, component.getFooterFacet(), TreeTable.FOOTER_CLASS);
    }

    protected void encodeThead(FacesContext context, TreeTable component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = component.getColumnGroup("header");
        String clientId = component.getClientId(context);

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
                                    encodeColumnHeader(context, component, (Column) headerRowChild);
                                }
                                else if (headerRowChild instanceof Columns) {
                                    List<DynamicColumn> dynamicColumns = ((Columns) headerRowChild).getDynamicColumns();
                                    for (DynamicColumn dynaColumn : dynamicColumns) {
                                        dynaColumn.applyModel();
                                        encodeColumnHeader(context, component, dynaColumn);
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

            List<UIColumn> columns = component.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                UIColumn column = columns.get(i);

                if (column instanceof Column) {
                    encodeColumnHeader(context, component, column);
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnHeader(context, component, dynamicColumn);
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("thead");
    }

    public void encodeTbody(FacesContext context, TreeTable component, TreeNode root, boolean dataOnly) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean empty = (root == null || root.getChildCount() == 0);
        UIComponent emptyFacet = component.getEmptyMessageFacet();

        if (!dataOnly) {
            writer.startElement("tbody", null);
            writer.writeAttribute("id", clientId + "_data", null);
            writer.writeAttribute("class", TreeTable.DATA_CLASS, null);
        }

        if (empty) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", TreeTable.EMPTY_MESSAGE_ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", component.getColumnsCount(), null);

            if (FacetUtils.shouldRenderFacet(emptyFacet)) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(component.getEmptyMessage(), "emptyMessage");
            }

            writer.endElement("td");
            writer.endElement("tr");
        }

        if (root != null) {
            if (component.isPaginator()) {
                int first = component.getFirst();
                int rows = component.getRows() == 0 ? component.getRowCount() : component.getRows();
                encodeNodeChildren(context, component, root, root, first, rows);
            }
            else {
                encodeNodeChildren(context, component, root, root);
            }
        }

        component.setRowKey(root, null);

        if (!dataOnly) {
            writer.endElement("tbody");
        }
    }

    public void encodeNode(FacesContext context, TreeTable component, TreeNode root, TreeNode treeNode) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowKey = treeNode.getRowKey();
        String parentRowKey = treeNode.getParent() != null ? treeNode.getParent().getRowKey() : null;
        component.setRowKey(root, rowKey);
        String icon = treeNode.isExpanded() ? TreeTable.COLLAPSE_ICON : TreeTable.EXPAND_ICON;
        int depth = rowKey.split(UITree.SEPARATOR).length - 1;
        boolean selectionEnabled = component.isSelectionEnabled();
        boolean selectable = treeNode.isSelectable() && selectionEnabled;
        boolean checkboxSelection = selectionEnabled && component.isCheckboxSelectionMode();
        boolean selected = treeNode.isSelected();
        boolean partialSelected = treeNode.isPartialSelected();
        boolean nativeElements = component.isNativeElements();
        List<UIColumn> columns = component.getColumns();

        String rowStyleClass = selected ? TreeTable.SELECTED_ROW_CLASS : TreeTable.ROW_CLASS;
        rowStyleClass = selectable ? rowStyleClass + " " + TreeTable.SELECTABLE_NODE_CLASS : rowStyleClass;
        rowStyleClass = rowStyleClass + " " + treeNode.getType();
        rowStyleClass = rowStyleClass + " ui-node-level-" + (rowKey.split("_").length);

        if (partialSelected) {
            rowStyleClass = rowStyleClass + " " + TreeTable.PARTIAL_SELECTED_CLASS;
        }

        String userRowStyleClass = component.getRowStyleClass();
        if (userRowStyleClass != null) {
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;
        }

        if (component.isEditingRow()) {
            rowStyleClass = rowStyleClass + " " + TreeTable.EDITING_ROW_CLASS;
        }

        writer.startElement("tr", null);
        writer.writeAttribute("id", component.getClientId(context) + "_node_" + rowKey, null);
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("title", component.getRowTitle(), null);
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
            ColumnMeta columnMeta = component.getColumnMeta().get(column.getColumnKey(component, rowKey));

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
                String title = column.getTitle();
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
                if (title != null) {
                    writer.writeAttribute("title", title, null);
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
                            renderNativeCheckbox(context, component, selected, partialSelected);
                        }
                    }
                }

                if (hasColumnDefaultRendering(component, column)) {
                    encodeDefaultFieldCell(context, component, column, writer);
                }
                else if (column instanceof DynamicColumn) {
                    encodeDynamicCell(context, component, column);
                }
                else {
                    column.renderChildren(context);
                }

                writer.endElement("td");
            }
        }

        writer.endElement("tr");

        if (treeNode.isExpanded()) {
            encodeNodeChildren(context, component, root, treeNode);
        }
    }

    public void encodeColumnHeader(FacesContext context, TreeTable component, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = component.getColumnMeta().get(column.getColumnKey());

        ResponseWriter writer = context.getResponseWriter();
        String ariaHeaderText = resolveColumnAriaHeaderText(context, column);

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        boolean sortable = component.isColumnSortable(context, column);
        boolean filterable = component.isColumnFilterable(context, column);
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
            sortMeta = component.getSortByAsMap().get(column.getColumnKey());
            if (sortMeta.isActive()) {
                columnClass += " ui-state-active";
            }
        }

        int responsivePriority = column.getResponsivePriority();
        if (responsivePriority > 0) {
            columnClass = columnClass + " ui-column-p-" + responsivePriority;
        }

        if (width != null) {
            String unit = endsWithLengthUnit(width) ? Constants.EMPTY_STRING : "px";
            if (style != null) {
                style = style + ";width:" + width + unit;
            }
            else {
                style = "width:" + width + unit;
            }
        }

        writer.startElement("th", null);
        writer.writeAttribute("id", column.getContainerClientId(context), null);
        writer.writeAttribute("class", columnClass, null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaHeaderText, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }

        if (filterable) {
            String filterPosition = column.getFilterPosition();

            if ("bottom".equals(filterPosition)) {
                encodeColumnHeaderContent(context, component, column, sortMeta);
                encodeFilter(context, component, column);
            }
            else if ("top".equals(filterPosition)) {
                encodeFilter(context, component, column);
                encodeColumnHeaderContent(context, component, column, sortMeta);
            }
            else {
                throw new FacesException(filterPosition + " is an invalid option for filterPosition, valid values are 'bottom' or 'top'.");
            }
        }
        else {
            encodeColumnHeaderContent(context, component, column, sortMeta);
        }

        writer.endElement("th");
    }

    protected void encodeColumnHeaderContent(FacesContext context, TreeTable component, UIColumn column,
                SortMeta sortMeta) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent headerFacet = column.getFacet("header");
        String headerText = resolveColumnHeaderText(context, column);
        boolean sortable = component.isColumnSortable(context, column);
        String title = column.getTitle();
        String titlestyleClass = getStyleClassBuilder(context)
                .add("ui-column-title")
                .add(isColumnAriaHeaderTextDefined(context, column), "ui-helper-hidden-accessible")
                .build();
        writer.startElement("span", null);
        writer.writeAttribute("class", titlestyleClass, null);

        if (FacetUtils.shouldRenderFacet(headerFacet)) {
            headerFacet.encodeAll(context);
        }
        else if (headerText != null) {
            if (LangUtils.isNotBlank(title)) {
                writer.writeAttribute("title", title, null);
            }
            writer.writeText(headerText, "headerText");
        }

        writer.endElement("span");

        if (sortable && sortMeta != null) {
            String sortIcon = resolveDefaultSortIcon(sortMeta);
            if (sortIcon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", sortIcon, null);
                writer.endElement("span");

                if (component.isMultiSort()) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", TreeTable.SORTABLE_PRIORITY_CLASS, null);
                    writer.endElement("span");
                }
            }
        }
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

    protected void encodeFilter(FacesContext context, TreeTable component, UIColumn column) throws IOException {
        if (component.isGlobalFilterOnly()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");

        if (!FacetUtils.shouldRenderFacet(filterFacet)) {
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
            boolean disableTabbing = component.getScrollWidth() != null;

            String filterId = column.getContainerClientId(context) + separator + "filter";
            Object filterValue = component.getFilterValue(column);
            filterValue = (filterValue == null) ? Constants.EMPTY_STRING : filterValue.toString();

            String filterStyleClass = getStyleClassBuilder(context)
                        .add(TreeTable.COLUMN_INPUT_FILTER_CLASS)
                        .add(column.getFilterStyleClass())
                        .build();

            writer.startElement("input", null);
            writer.writeAttribute("id", filterId, null);
            writer.writeAttribute("name", filterId, null);
            writer.writeAttribute("type", "search", null);
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

            if (LangUtils.isNotBlank(column.getFilterPlaceholder())) {
                writer.writeAttribute("placeholder", column.getFilterPlaceholder(), null);
            }

            writer.endElement("input");
        }
        else {
            Object filterValue = component.getFilterValue(column);
            column.setFilterValueToValueHolder(context, filterValue);

            writer.startElement("div", null);
            writer.writeAttribute("class", TreeTable.COLUMN_CUSTOM_FILTER_CLASS, null);
            filterFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    public void encodeNodeChildren(FacesContext context, TreeTable component, TreeNode root, TreeNode treeNode) throws IOException {
        int childCount = treeNode.getChildCount();
        encodeNodeChildren(context, component, root, treeNode, 0, childCount);
    }

    public void encodeNodeChildren(FacesContext context, TreeTable component, TreeNode root, TreeNode treeNode, int first, int size)
            throws IOException {

        if (size > 0) {
            List<TreeNode> children = treeNode.getChildren();
            int childCount = treeNode.getChildCount();
            int last = (first + size);
            if (last > childCount) {
                last = childCount;
            }

            for (int i = first; i < last; i++) {
                encodeNode(context, component, root, children.get(i));
            }
        }
    }

    protected void encodeFacet(FacesContext context, TreeTable component, UIComponent facet, String styleClass) throws IOException {
        if (!FacetUtils.shouldRenderFacet(facet)) {
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

        if (FacetUtils.shouldRenderFacet(footerFacet)) {
            footerFacet.encodeAll(context);
        }
        else if (footerText != null) {
            writer.writeText(footerText, null);
        }

        writer.endElement("td");
    }

    protected void encodeDynamicCell(FacesContext context, TreeTable component, UIColumn column) throws IOException {
        column.encodeAll(context);
    }

    protected void encodeDefaultFieldCell(FacesContext context, TreeTable component, UIColumn column, ResponseWriter writer) throws IOException {
        Object value = component.getConvertedFieldValue(context, column);
        if (value != null) {
            writer.writeText(value, null);
        }
    }

    @Override
    public void encodeChildren(FacesContext facesContext, TreeTable component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private void encodeStateHolder(FacesContext context, TreeTable component, String name, String value) throws IOException {
        renderHiddenInput(context, name, value, false);
    }

    protected void renderNativeCheckbox(FacesContext context, TreeTable component, boolean checked, boolean partialSelected)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("name", component.getContainerClientId(context) + "_checkbox", null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (partialSelected) {
            writer.writeAttribute("class", "ui-treetable-indeterminate", null);
        }

        writer.endElement("input");
    }

    protected boolean hasColumnDefaultRendering(TreeTable table, UIColumn column) {
        return column.getChildren().isEmpty()
                && (table.getSortByAsMap().containsKey(column.getColumnKey())
                || table.getFilterByAsMap().containsKey(column.getColumnKey())
                || LangUtils.isNotBlank(column.getField()));

    }
}
