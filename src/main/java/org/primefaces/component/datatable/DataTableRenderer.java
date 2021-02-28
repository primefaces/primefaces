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

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.feature.*;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.event.data.PostRenderEvent;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.*;

public class DataTableRenderer extends DataRenderer {

    private static final Logger LOGGER = Logger.getLogger(DataTableRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        DataTable table = (DataTable) component;

        for (Iterator<DataTableFeature> it = DataTable.FEATURES.values().iterator(); it.hasNext(); ) {
            DataTableFeature feature = it.next();

            if (feature.shouldDecode(context, table)) {
                feature.decode(context, table);
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataTable table = (DataTable) component;

        if (table.shouldEncodeFeature(context)) {
            for (Iterator<DataTableFeature> it = DataTable.FEATURES.values().iterator(); it.hasNext(); ) {
                DataTableFeature feature = it.next();

                if (feature.shouldEncode(context, table)) {
                    feature.encode(context, this, table);
                }
            }
        }
        else {
            preRender(context, table);

            encodeMarkup(context, table);
            encodeScript(context, table);
        }

        context.getApplication().publishEvent(context, PostRenderEvent.class, table);
    }

    protected void preRender(FacesContext context, DataTable table) {
        table.initFilterBy(context);

        if (table.isMultiViewState()) {
            table.restoreMultiViewState();
        }

        if (table.isLiveScroll()) {
            table.setScrollOffset(0);
        }

        if (table.isLazy()) {
            if (table.isLiveScroll()) {
                table.loadLazyScrollData(0, table.getScrollRows());
            }
            else if (table.isVirtualScroll()) {
                int rows = table.getRows();
                int scrollRows = table.getScrollRows();
                int virtualScrollRows = (scrollRows * 2);
                scrollRows = (rows == 0) ? virtualScrollRows : Math.min(virtualScrollRows, rows);

                table.loadLazyScrollData(0, scrollRows);
            }
            else {
                table.loadLazyData();
            }
        }
        else {
            if (table.isDefaultSort()) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);
                sortFeature.sort(context, table);
                table.setRowIndex(-1);
            }

            if (table.isDefaultFilter()) {
                FilterFeature filterFeature = (FilterFeature) table.getFeature(DataTableFeatureKey.FILTER);
                filterFeature.filter(context, table);
            }
        }

        if (table.isSelectionEnabled()) {
            SelectionFeature selectionFeature = (SelectionFeature) table.getFeature(DataTableFeatureKey.SELECT);
            selectionFeature.decodeSelectionRowKeys(context, table);
        }

        if (table.isPaginator()) {
            table.calculateFirst();
        }

        table.resetDynamicColumns();
    }

    protected void encodeScript(FacesContext context, DataTable table) throws IOException {
        String selectionMode = table.resolveSelectionMode();
        String widgetClass = (table.getFrozenColumns() == 0) ? "DataTable" : "FrozenDataTable";

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(widgetClass, table);

        //Pagination
        if (table.isPaginator()) {
            encodePaginatorConfig(context, table, wb);
        }

        //Selection
        wb.attr("selectionMode", selectionMode, null)
                .attr("selectionPageOnly", table.isSelectionPageOnly(), true)
                .attr("rowSelectMode", table.getRowSelectMode(), "new")
                .attr("nativeElements", table.isNativeElements(), false)
                .attr("rowSelector", table.getRowSelector(), null)
                .attr("disabledTextSelection", table.isDisabledTextSelection(), true);

        //Filtering
        if (table.isFilteringEnabled()) {
            wb.attr("filter", true)
                    .attr("filterEvent", table.getFilterEvent(), null)
                    .attr("filterDelay", table.getFilterDelay(), Integer.MAX_VALUE);
        }

        //Row expansion
        if (table.getRowExpansion() != null) {
            wb.attr("expansion", true).attr("rowExpandMode", table.getRowExpandMode());
        }

        //Scrolling
        if (table.isScrollable()) {
            wb.attr("scrollable", true)
                    .attr("liveScroll", table.isLiveScroll())
                    .attr("scrollStep", table.getScrollRows())
                    .attr("scrollLimit", table.getRowCount())
                    .attr("scrollWidth", table.getScrollWidth(), null)
                    .attr("scrollHeight", table.getScrollHeight(), null)
                    .attr("frozenColumns", table.getFrozenColumns(), 0)
                    .attr("liveScrollBuffer", table.getLiveScrollBuffer())
                    .attr("virtualScroll", table.isVirtualScroll())
                    .attr("touchable", false,  true);
        }
        else {
            // only allow swipe if not scrollable
            wb.attr("touchable", ComponentUtils.isTouchable(context, table),  true);
        }

        //Resizable/Draggable Columns
        wb.attr("resizableColumns", table.isResizableColumns(), false)
                .attr("liveResize", table.isLiveResize(), false)
                .attr("draggableColumns", table.isDraggableColumns(), false)
                .attr("resizeMode", table.getResizeMode(), "fit");

        //Draggable Rows
        wb.attr("draggableRows", table.isDraggableRows(), false)
                .attr("rowDragSelector", table.getRowDragSelector(), null);

        //Editing
        if (table.isEditable()) {
            wb.attr("editable", true)
                    .attr("editMode", table.getEditMode())
                    .attr("cellSeparator", table.getCellSeparator(), null)
                    .attr("saveOnCellBlur", table.isSaveOnCellBlur(), true)
                    .attr("cellEditMode", table.getCellEditMode(), "eager")
                    .attr("editInitEvent", table.getEditInitEvent())
                    .attr("rowEditMode", table.getRowEditMode(), "eager");
        }

        //Sorting
        if (table.isSortingEnabled()) {
            wb.attr("sorting", true);

            if (table.isMultiSort()) {
                wb.attr("multiSort", true)
                        .nativeAttr("sortMetaOrder", table.getSortMetaAsString(), null);
            }

            if (table.isAllowUnsorting()) {
                wb.attr("allowUnsorting", true);
            }
        }

        if (table.isStickyHeader()) {
            wb.attr("stickyHeader", true)
                    .attr("stickyTopAt", table.getStickyTopAt(), null);
        }

        wb.attr("tabindex", table.getTabindex(), "0")
                .attr("reflow", table.isReflow(), false)
                .attr("rowHover", table.isRowHover(), false)
                .attr("clientCache", table.isClientCache(), false)
                .attr("multiViewState", table.isMultiViewState(), false)
                .nativeAttr("groupColumnIndexes", table.getGroupedColumnIndexes(), null)
                .callback("onRowClick", "function(row)", table.getOnRowClick());

        wb.attr("disableContextMenuIfEmpty", table.isDisableContextMenuIfEmpty());

        //Behaviors
        encodeClientBehaviors(context, table);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        boolean scrollable = table.isScrollable();
        boolean hasPaginator = table.isPaginator();
        boolean resizable = table.isResizableColumns();
        String style = table.getStyle();
        String paginatorPosition = table.getPaginatorPosition();
        int frozenColumns = table.getFrozenColumns();
        boolean hasFrozenColumns = (frozenColumns != 0);
        String summary = table.getSummary();

        if (table.isReflow()) {
            style = style + ";visibility:hidden;";
        }

        //style class
        String containerClass = getStyleClassBuilder(context)
                .add(DataTable.CONTAINER_CLASS)
                .add(scrollable, DataTable.SCROLLABLE_CONTAINER_CLASS)
                .add(table.getStyleClass())
                .add(resizable, DataTable.RESIZABLE_CONTAINER_CLASS)
                .add(table.isStickyHeader(), DataTable.STICKY_HEADER_CLASS)
                .add(ComponentUtils.isRTL(context, table), DataTable.RTL_CLASS)
                .add(table.isReflow(), DataTable.REFLOW_CLASS)
                .add(hasFrozenColumns, "ui-datatable-frozencolumn")
                .add(table.isStripedRows(), DataTable.STRIPED_ROWS_CLASS)
                .add(table.isShowGridlines(), DataTable.GRIDLINES_CLASS)
                .add("small".equals(table.getSize()), DataTable.SMALL_SIZE_CLASS)
                .add("large".equals(table.getSize()), DataTable.LARGE_SIZE_CLASS)
                .build();

        //aria
        if (summary != null) {
            writer.startElement("span", null);
            writer.writeAttribute("id", clientId + "_summary", null);
            writer.writeAttribute("class", "ui-datatable-summary", null);
            writer.writeText(summary, null);
            writer.endElement("span");
        }

        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, "styleClass");
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, "style");
        }

        if (table.isReflow()) {
            encodeSortableHeaderOnReflow(context, table);
        }

        encodeFacet(context, table, table.getHeader(), DataTable.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, table, "top");
        }

        if (scrollable) {
            encodeScrollableTable(context, table);
        }
        else {
            encodeRegularTable(context, table);
        }

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, table, "bottom");
        }

        encodeFacet(context, table, table.getFooter(), DataTable.FOOTER_CLASS);

        if (table.isSelectionEnabled()) {
            encodeStateHolder(context, table, table.getClientId(context) + "_selection", table.getSelectedRowKeysAsString());
        }

        if (table.isDraggableColumns()) {
            encodeStateHolder(context, table, table.getClientId(context) + "_columnOrder", table.getOrderedColumnKeys());
        }

        if (scrollable) {
            encodeStateHolder(context, table, table.getClientId(context) + "_scrollState", table.getScrollState());
        }

        if (resizable) {
            encodeStateHolder(context, table, table.getClientId(context) + "_resizableColumnState", table.getColumnsWidthForClientSide());
        }

        if (table.getRowExpansion() != null) {
            encodeStateHolder(context, table, table.getClientId(context) + "_rowExpansionState", null);
        }

        writer.endElement("div");
    }

    protected void encodeRegularTable(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.TABLE_WRAPPER_CLASS, null);

        String tableStyle = table.getTableStyle();

        if (table.isResizableColumns()) {
            String width = table.getWidth();
            if (width != null) {
                if (tableStyle != null) {
                    tableStyle = tableStyle + ";width:" + width + "px";
                }
                else {
                    tableStyle = "width:" + width + "px";
                }
            }
        }

        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        if (tableStyle != null) {
            writer.writeAttribute("style", tableStyle, null);
        }
        if (table.getTableStyleClass() != null) {
            writer.writeAttribute("class", table.getTableStyleClass(), null);
        }

        String summary = table.getSummary();
        String clientId = table.getClientId(context);

        if (summary != null) {
            writer.writeAttribute("summary", summary, null);
            writer.writeAttribute(HTML.ARIA_DESCRIBEDBY, clientId + "_summary", null);
        }

        encodeThead(context, table);
        encodeTbody(context, table, false);
        encodeTFoot(context, table);

        writer.endElement("table");
        writer.endElement("div");
    }

    protected void encodeScrollableTable(FacesContext context, DataTable table) throws IOException {
        String tableStyle = table.getTableStyle();
        String tableStyleClass = table.getTableStyleClass();
        int frozenColumns = table.getFrozenColumns();
        boolean hasFrozenColumns = (frozenColumns != 0);
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        int columnsCount = table.getColumns().size();
        boolean isVirtualScroll = table.isVirtualScroll();

        if (hasFrozenColumns) {
            writer.startElement("table", null);
            writer.writeAttribute("class", "ui-datatable-fs", null);
            writer.startElement("tbody", null);
            writer.startElement("tr", null);

            //frozen columns
            writer.startElement("td", null);
            writer.writeAttribute("class", "ui-datatable-frozenlayout-left", null);
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-datatable-frozen-container", null);
            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_HEADER_CLASS, DataTable.SCROLLABLE_HEADER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeThead(context, table, 0, frozenColumns, clientId + "_frozenThead", "frozenHeader");
            encodeFrozenRows(context, table, 0, frozenColumns);
            encodeScrollAreaEnd(context);

            if (isVirtualScroll) {
                encodeVirtualScrollBody(context, table, tableStyle, tableStyleClass, 0, frozenColumns, clientId + "_frozenTbody");
            }
            else {
                encodeScrollBody(context, table, tableStyle, tableStyleClass, 0, frozenColumns, clientId + "_frozenTbody");
            }

            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_FOOTER_CLASS, DataTable.SCROLLABLE_FOOTER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeTFoot(context, table, 0, frozenColumns, clientId + "_frozenTfoot", "frozenFooter");
            encodeScrollAreaEnd(context);
            writer.endElement("div");
            writer.endElement("td");

            //scrollable columns
            writer.startElement("td", null);
            writer.writeAttribute("class", "ui-datatable-frozenlayout-right", null);
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-datatable-scrollable-container", null);

            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_HEADER_CLASS, DataTable.SCROLLABLE_HEADER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeThead(context, table, frozenColumns, columnsCount, clientId + "_scrollableThead", "scrollableHeader");
            encodeFrozenRows(context, table, frozenColumns, columnsCount);
            encodeScrollAreaEnd(context);

            if (isVirtualScroll) {
                encodeVirtualScrollBody(context, table, tableStyle, tableStyleClass, frozenColumns, columnsCount, clientId + "_scrollableTbody");
            }
            else {
                encodeScrollBody(context, table, tableStyle, tableStyleClass, frozenColumns, columnsCount, clientId + "_scrollableTbody");
            }

            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_FOOTER_CLASS, DataTable.SCROLLABLE_FOOTER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeTFoot(context, table, frozenColumns, columnsCount, clientId + "_scrollableTfoot", "scrollableFooter");
            encodeScrollAreaEnd(context);
            writer.endElement("div");
            writer.endElement("td");

            writer.endElement("tr");
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_HEADER_CLASS, DataTable.SCROLLABLE_HEADER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeThead(context, table);
            encodeFrozenRows(context, table, 0, columnsCount);
            encodeScrollAreaEnd(context);

            if (isVirtualScroll) {
                encodeVirtualScrollBody(context, table, tableStyle, tableStyleClass, 0, columnsCount, null);
            }
            else {
                encodeScrollBody(context, table, tableStyle, tableStyleClass, 0, columnsCount, null);
            }

            encodeScrollAreaStart(context, table, DataTable.SCROLLABLE_FOOTER_CLASS, DataTable.SCROLLABLE_FOOTER_BOX_CLASS, tableStyle, tableStyleClass);
            encodeTFoot(context, table);
            encodeScrollAreaEnd(context);
        }
    }

    protected void encodeFrozenScrollableTable(FacesContext context, DataTable table, int frozenColumns) throws IOException {

    }

    protected void encodeScrollAreaStart(FacesContext context, DataTable table, String containerClass, String containerBoxClass,
                                         String tableStyle, String tableStyleClass) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

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

    protected void encodeScrollBody(FacesContext context, DataTable table, String tableStyle, String tableStyleClass, int columnStart,
                                    int columnEnd, String tbodyId) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String scrollHeight = table.getScrollHeight();

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_BODY_CLASS, null);
        writer.writeAttribute("tabindex", "-1", null);
        if (!LangUtils.isValueBlank(scrollHeight) && scrollHeight.indexOf('%') == -1) {
            writer.writeAttribute("style", "max-height:" + scrollHeight + "px", null);
        }
        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);

        if (tableStyle != null) {
            writer.writeAttribute("style", tableStyle, null);
        }
        if (table.getTableStyleClass() != null) {
            writer.writeAttribute("class", tableStyleClass, null);
        }

        encodeTbody(context, table, false, columnStart, columnEnd, tbodyId);

        writer.endElement("table");
        writer.endElement("div");
    }

    protected void encodeVirtualScrollBody(FacesContext context, DataTable table, String tableStyle, String tableStyleClass, int columnStart,
                                           int columnEnd, String tbodyId) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String scrollHeight = table.getScrollHeight();
        tableStyleClass = (tableStyleClass == null) ? DataTable.VIRTUALSCROLL_TABLE_CLASS : tableStyleClass + " " + DataTable.VIRTUALSCROLL_TABLE_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_BODY_CLASS, null);
        writer.writeAttribute("tabindex", "-1", null);
        if (scrollHeight != null && scrollHeight.indexOf('%') == -1) {
            writer.writeAttribute("style", "max-height:" + scrollHeight + "px", null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.VIRTUALSCROLL_WRAPPER_CLASS, null);

        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        writer.writeAttribute("class", tableStyleClass, null);

        if (tableStyle != null) {
            writer.writeAttribute("style", tableStyle, null);
        }

        encodeTbody(context, table, false, columnStart, columnEnd, tbodyId);

        writer.endElement("table");
        writer.endElement("div");

        writer.endElement("div");
    }

    public void encodeColumnHeader(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = table.getColumnMeta().get(column.getColumnKey());

        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getContainerClientId(context);

        boolean sortable = table.isColumnSortable(context, column);
        boolean filterable = table.isColumnFilterable(column);
        String selectionMode = column.getSelectionMode();
        SortMeta sortMeta = null;
        boolean resizable = table.isResizableColumns() && column.isResizable();
        boolean draggable = table.isDraggableColumns() && column.isDraggable();
        int responsivePriority = column.getResponsivePriority();

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        String columnClass = getStyleClassBuilder(context)
                .add(DataTable.COLUMN_HEADER_CLASS)
                .add(sortable, DataTable.SORTABLE_COLUMN_CLASS)
                .add(filterable, DataTable.FILTER_COLUMN_CLASS)
                .add(selectionMode != null, DataTable.SELECTION_COLUMN_CLASS)
                .add(resizable,  DataTable.RESIZABLE_COLUMN_CLASS)
                .add(draggable, DataTable.DRAGGABLE_COLUMN_CLASS)
                .add(!column.isToggleable(), DataTable.STATIC_COLUMN_CLASS)
                .add(!columnVisible, DataTable.HIDDEN_COLUMN_CLASS)
                .add(column.getStyleClass())
                .add(responsivePriority > 0, "ui-column-p-" + responsivePriority)
                .build();

        if (sortable) {
            sortMeta = table.getSortByAsMap().get(column.getColumnKey());
            if (sortMeta.isActive()) {
                columnClass += " ui-state-active";
            }
        }

        String style = column.getStyle();
        String width = column.getWidth();

        if (columnMeta != null && columnMeta.getWidth() != null) {
            width = columnMeta.getWidth();
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
        UIComponent component = (column instanceof UIComponent) ? (UIComponent) column : null;

        writer.startElement("th", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", columnClass, null);
        writer.writeAttribute("role", "columnheader", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaHeaderLabel, null);
        writer.writeAttribute("scope", "col", null);
        if (component != null) {
            renderDynamicPassThruAttributes(context, component);
        }
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (column.getRowspan() != 1) {
            writer.writeAttribute("rowspan", column.getRowspan(), null);
        }
        if (column.getColspan() != 1) {
            writer.writeAttribute("colspan", column.getColspan(), null);
        }

        if (filterable) {
            String filterPosition = column.getFilterPosition();

            if ("bottom".equals(filterPosition)) {
                encodeColumnHeaderContent(context, table, column, sortMeta);
                encodeFilter(context, table, column);
            }
            else if ("top".equals(filterPosition)) {
                encodeFilter(context, table, column);
                encodeColumnHeaderContent(context, table, column, sortMeta);
            }
            else {
                throw new FacesException(filterPosition + " is an invalid option for filterPosition, valid values are 'bottom' or 'top'.");
            }
        }
        else {
            encodeColumnHeaderContent(context, table, column, sortMeta);
        }

        if (selectionMode != null && "multiple".equalsIgnoreCase(selectionMode)) {
            encodeCheckbox(context, table, false, false, HTML.CHECKBOX_ALL_CLASS, true);
        }

        writer.endElement("th");
    }

    protected String resolveDefaultSortIcon(SortMeta sortMeta) {
        SortOrder sortOrder = sortMeta.getOrder();
        String sortIcon = DataTable.SORTABLE_COLUMN_ICON_CLASS;
        if (sortOrder.isAscending()) {
            sortIcon = DataTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
        }
        else if (sortOrder.isDescending()) {
            sortIcon = DataTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
        }

        return sortIcon;
    }

    protected void encodeColumnHeaderContent(FacesContext context, DataTable table, UIColumn column,
                SortMeta sortMeta) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();

        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.COLUMN_TITLE_CLASS, null);

        if (ComponentUtils.shouldRenderFacet(header, table.isRenderEmptyFacets())) {
            header.encodeAll(context);
        }
        else if (headerText != null) {
            if (table.isEscapeText()) {
                writer.writeText(headerText, "headerText");
            }
            else {
                writer.write(headerText);
            }
        }

        writer.endElement("span");

        if (sortMeta != null) {
            String sortIcon = resolveDefaultSortIcon(sortMeta);
            if (sortIcon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", sortIcon, null);
                writer.endElement("span");

                if (table.isMultiSort()) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", DataTable.SORTABLE_PRIORITY_CLASS, null);
                    writer.endElement("span");
                }
            }
        }
    }

    protected void encodeFilter(FacesContext context, DataTable table, UIColumn column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");

        if (!ComponentUtils.shouldRenderFacet(filterFacet, table.isRenderEmptyFacets())) {
            encodeDefaultFilter(context, table, column, writer);
        }
        else {
            Object filterValue = table.getFilterValue(column);
            if (filterValue != null) {
                ((ValueHolder) filterFacet).setValue(filterValue);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CUSTOM_FILTER_CLASS, null);
            filterFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected void encodeDefaultFilter(FacesContext context, DataTable table, UIColumn column,
        ResponseWriter writer) throws IOException {
        String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
        boolean disableTabbing = table.getScrollWidth() != null;

        String filterId = column.getContainerClientId(context) + separator + "filter";
        String filterStyleClass = column.getFilterStyleClass();
        Object filterValue = findFilterValueForColumn(context, table, column, filterId);

        //aria
        String ariaLabelId = filterId + "_label";
        String ariaHeaderLabel = getHeaderLabel(context, column);

        String ariaMessage = MessageFactory.getMessage(DataTable.ARIA_FILTER_BY, ariaHeaderLabel);

        writer.startElement("label", null);
        writer.writeAttribute("id", ariaLabelId, null);
        writer.writeAttribute("for", filterId, null);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        writer.writeText(ariaMessage, null);
        writer.endElement("label");

        encodeFilterInput(column, writer, disableTabbing, filterId, filterStyleClass, filterValue, ariaLabelId);
    }

    protected void encodeFilterInput(UIColumn column, ResponseWriter writer, boolean disableTabbing,
        String filterId, String filterStyleClass, Object filterValue, String ariaLabelId) throws IOException {

        if (hasFilterOptions(column)) {
            encodeFilterInputSelect(column, writer, disableTabbing, filterId, filterStyleClass, filterValue, ariaLabelId);
        }
        else {
            encodeFilterInputText(column, writer, disableTabbing, filterId, filterStyleClass, filterValue, ariaLabelId);
        }
    }

    private boolean hasFilterOptions(UIColumn column) {
        boolean hasFilterOptionsVE = column.getValueExpression(Column.PropertyKeys.filterOptions.toString()) != null;
        if (!hasFilterOptionsVE) {
            return false;
        }

        SelectItem[] filterOptions = getFilterOptions(column);
        return filterOptions != null && filterOptions.length != 0;
    }

    protected void encodeFilterInputSelect(UIColumn column, ResponseWriter writer, boolean disableTabbing,
        String filterId, String filterStyleClass, Object filterValue, String ariaLabelId) throws IOException {

        filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_FILTER_CLASS : DataTable.COLUMN_FILTER_CLASS + " " + filterStyleClass;

        writer.startElement("select", null);
        writer.writeAttribute("id", filterId, null);
        writer.writeAttribute("name", filterId, null);
        writer.writeAttribute("class", filterStyleClass, null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, ariaLabelId, null);

        if (disableTabbing) {
            writer.writeAttribute("tabindex", "-1", null);
        }

        SelectItem[] itemsArray = getFilterOptions(column);

        for (SelectItem item : itemsArray) {
            Object itemValue = item.getValue();

            writer.startElement("option", null);
            writer.writeAttribute("value", item.getValue(), null);
            if (itemValue != null && String.valueOf(itemValue).equals(filterValue)) {
                writer.writeAttribute("selected", "selected", null);
            }

            if (item.isEscape()) {
                writer.writeText(item.getLabel(), "value");
            }
            else {
                writer.write(item.getLabel());
            }

            writer.endElement("option");
        }

        writer.endElement("select");
    }

    protected void encodeFilterInputText(UIColumn column, ResponseWriter writer, boolean disableTabbing,
        String filterId, String filterStyleClass, Object filterValue, String ariaLabelId) throws IOException {

        filterStyleClass = filterStyleClass == null
                           ? DataTable.COLUMN_INPUT_FILTER_CLASS
                           : DataTable.COLUMN_INPUT_FILTER_CLASS + " " + filterStyleClass;

        writer.startElement("input", null);
        writer.writeAttribute("id", filterId, null);
        writer.writeAttribute("name", filterId, null);
        writer.writeAttribute("class", filterStyleClass, null);
        writer.writeAttribute("value", filterValue, null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, ariaLabelId, null);

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

    protected Object findFilterValueForColumn(FacesContext context, DataTable table,
        UIColumn column, String filterId) {

        Object filterValue;
        if (table.isReset()) {
            filterValue = Constants.EMPTY_STRING;
        }
        else {
            filterValue = table.getFilterValue(column);
            if (filterValue == null) {
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                if (params.containsKey(filterId)) {
                    filterValue = params.get(filterId);
                }
                else {
                    Object columnFilterValue = column.getFilterValue();
                    filterValue = (columnFilterValue == null) ? Constants.EMPTY_STRING : columnFilterValue.toString();
                }
            }
        }
        return filterValue;
    }

    protected SelectItem[] getFilterOptions(UIColumn column) {
        Object options = column.getFilterOptions();

        if (options instanceof SelectItem[]) {
            return (SelectItem[]) options;
        }
        else if (options instanceof Collection<?>) {
            return ((Collection<SelectItem>) column.getFilterOptions()).toArray(new SelectItem[]{});
        }
        else {
            throw new FacesException("Filter options for column " + column.getClientId() + " should be a SelectItem array or collection");
        }
    }

    public void encodeColumnFooter(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = table.getColumnMeta().get(column.getColumnKey());

        ResponseWriter writer = context.getResponseWriter();

        int responsivePriority = column.getResponsivePriority();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = styleClass == null ? DataTable.COLUMN_FOOTER_CLASS : DataTable.COLUMN_FOOTER_CLASS + " " + styleClass;

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        if (!columnVisible) {
            styleClass = styleClass + " " + DataTable.HIDDEN_COLUMN_CLASS;
        }

        if (responsivePriority > 0) {
            styleClass = styleClass + " ui-column-p-" + responsivePriority;
        }

        writer.startElement("td", null);
        writer.writeAttribute("class", styleClass, null);

        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (column.getRowspan() != 1) {
            writer.writeAttribute("rowspan", column.getRowspan(), null);
        }
        if (column.getColspan() != 1) {
            writer.writeAttribute("colspan", column.getColspan(), null);
        }

        //Footer content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if (ComponentUtils.shouldRenderFacet(facet, table.isRenderEmptyFacets())) {
            facet.encodeAll(context);
        }
        else if (text != null) {
            if (table.isEscapeText()) {
                writer.writeText(text, "footerText");
            }
            else {
                writer.write(text);
            }
        }

        writer.endElement("td");
    }

    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        encodeThead(context, table, 0, table.getColumns().size(), null, null);
    }

    protected void encodeThead(FacesContext context, DataTable table, int columnStart, int columnEnd, String theadId,
                               String columnGroupType) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();
        String theadClientId = (theadId == null) ? table.getClientId(context) + "_head" : theadId;
        String colGroupType = (columnGroupType == null) ? "header" : columnGroupType;
        ColumnGroup group = table.getColumnGroup(colGroupType);

        writer.startElement("thead", null);
        writer.writeAttribute("id", theadClientId, null);

        if (group != null && group.isRendered()) {
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for (UIComponent child : group.getChildren()) {
                if (child.isRendered()) {
                    if (child instanceof Row) {
                        Row headerRow = (Row) child;
                        String rowClass = headerRow.getStyleClass();
                        String rowStyle = headerRow.getStyle();

                        writer.startElement("tr", null);
                        writer.writeAttribute("role", "row", null);
                        if (rowClass != null) {
                            writer.writeAttribute("class", rowClass, null);
                        }
                        if (rowStyle != null) {
                            writer.writeAttribute("style", rowStyle, null);
                        }

                        for (UIComponent headerRowChild : headerRow.getChildren()) {
                            if (headerRowChild.isRendered()) {
                                if (headerRowChild instanceof Column) {
                                    encodeColumnHeader(context, table, (Column) headerRowChild);
                                }
                                else if (headerRowChild instanceof Columns) {
                                    List<DynamicColumn> dynamicColumns = ((Columns) headerRowChild).getDynamicColumns();
                                    for (DynamicColumn dynaColumn : dynamicColumns) {
                                        dynaColumn.applyModel();
                                        encodeColumnHeader(context, table, dynaColumn);
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
            writer.writeAttribute("role", "row", null);

            for (int i = columnStart; i < columnEnd; i++) {
                UIColumn column = columns.get(i);

                if (column instanceof Column) {
                    encodeColumnHeader(context, table, column);
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnHeader(context, table, dynamicColumn);
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("thead");
    }

    public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly) throws IOException {
        encodeTbody(context, table, dataOnly, 0, table.getColumns().size(), null);
    }

    public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly, int columnStart, int columnEnd, String tbodyId)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);
        String emptyMessage = table.getEmptyMessage();
        UIComponent emptyFacet = table.getFacet("emptyMessage");
        SubTable subTable = table.getSubTable();
        String tbodyClientId = (tbodyId == null) ? clientId + "_data" : tbodyId;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        int rows = table.getRows();
        int first = table.isClientCacheRequest(context) ? Integer.valueOf(params.get(clientId + "_first")) + rows : table.getFirst();
        int rowCount = table.getRowCount();
        int rowCountToRender = rows == 0 ? (table.isLiveScroll() ? (table.getScrollRows() + table.getScrollOffset()) : rowCount) : rows;

        if (table.isVirtualScroll()) {
            int virtualScrollRowCount = (table.getScrollRows() * 2);
            rowCountToRender = virtualScrollRowCount > rowCount ? rowCount : virtualScrollRowCount;
        }

        int frozenRows = table.getFrozenRows();
        boolean hasData = rowCount > 0;

        if (first == 0 && frozenRows > 0) {
            first += frozenRows;
        }

        if (!dataOnly) {
            writer.startElement("tbody", null);
            writer.writeAttribute("id", tbodyClientId, null);
            writer.writeAttribute("class", DataTable.DATA_CLASS, null);

            if (table.isRowSelectionEnabled()) {
                writer.writeAttribute("tabindex", table.getTabindex(), null);
            }
        }

        if (hasData) {
            if (subTable != null) {
                encodeSubTable(context, table, subTable, first, (first + rowCountToRender));
            }
            else {
                encodeRows(context, table, first, (first + rowCountToRender), columnStart, columnEnd);
            }
        }
        else {
            //Empty message
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.EMPTY_MESSAGE_ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumnsCountWithSpan(), null);

            if (ComponentUtils.shouldRenderFacet(emptyFacet, table.isRenderEmptyFacets())) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(emptyMessage, "emptyMessage");
            }

            writer.endElement("td");

            writer.endElement("tr");
        }

        if (!dataOnly) {
            writer.endElement("tbody");
        }

        //Cleanup
        table.setRowIndex(-1);
        if (rowIndexVar != null) {
            context.getExternalContext().getRequestMap().remove(rowIndexVar);
        }
    }

    protected void encodeRows(FacesContext context, DataTable table, int first, int last, int columnStart, int columnEnd) throws IOException {
        String clientId = table.getClientId(context);
        SummaryRow summaryRow = table.getSummaryRow();
        HeaderRow headerRow = table.getHeaderRow();
        ELContext eLContext = context.getELContext();

        SortMeta sort = table.getHighestPriorityActiveSortMeta();
        boolean encodeHeaderRow = headerRow != null && headerRow.isEnabled() && sort != null;
        boolean encodeSummaryRow = (summaryRow != null && sort != null);

        for (int i = first; i < last; i++) {
            table.resetDynamicColumns();

            table.setRowIndex(i);
            if (!table.isRowAvailable()) {
                break;
            }

            table.setRowIndex(i);

            if (encodeHeaderRow && (i == first || !isInSameGroup(context, table, i, -1, sort.getSortBy(), eLContext))) {
                table.setRowIndex(i);
                encodeHeaderRow(context, table, headerRow);
            }

            table.setRowIndex(i);
            encodeRow(context, table, clientId, i, columnStart, columnEnd);

            if (encodeSummaryRow && !isInSameGroup(context, table, i, 1, sort.getSortBy(), eLContext)) {
                table.setRowIndex(i);
                encodeSummaryRow(context, summaryRow, sort);
            }
        }
    }

    protected void encodeFrozenRows(FacesContext context, DataTable table, int columnStart, int columnEnd) throws IOException {
        int frozenRows = table.getFrozenRows();
        if (frozenRows == 0) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);

        writer.startElement("tbody", null);
        writer.writeAttribute("class", DataTable.DATA_CLASS, null);

        for (int i = 0; i < frozenRows; i++) {
            table.setRowIndex(i);
            encodeRow(context, table, clientId, i, columnStart, columnEnd);
        }

        writer.endElement("tbody");
    }

    protected void encodeSummaryRow(FacesContext context, SummaryRow summaryRow, SortMeta sort) throws IOException {
        MethodExpression me = summaryRow.getListener();
        if (me != null) {
            me.invoke(context.getELContext(), new Object[]{sort.getSortBy()});
        }

        summaryRow.encodeAll(context);
    }

    protected void encodeHeaderRow(FacesContext context, DataTable table, HeaderRow headerRow) throws IOException {
        headerRow.encodeAll(context);
    }

    public boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex) throws IOException {
        return encodeRow(context, table, clientId, rowIndex, 0, table.getColumns().size());
    }

    public boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex, int columnStart, int columnEnd)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = table.isSelectionEnabled();
        String rowKey = null;
        List<UIColumn> columns = table.getColumns();
        HeaderRow headerRow = table.getHeaderRow();

        if (selectionEnabled) {
            rowKey = table.getRowKey(table.getRowData());
        }

        //Preselection
        boolean selected = table.getSelectedRowKeys().contains(rowKey);
        boolean disabled = table.isDisabledSelection();

        String rowStyleClass = getStyleClassBuilder(context)
                .add(DataTable.ROW_CLASS)
                .add(rowIndex % 2 == 0, DataTable.EVEN_ROW_CLASS, DataTable.ODD_ROW_CLASS)
                .add(selectionEnabled && !disabled, DataTable.SELECTABLE_ROW_CLASS)
                .add(selected && !disabled, "ui-state-highlight")
                .add(table.isEditingRow(),  DataTable.EDITING_ROW_CLASS)
                .add(table.getRowStyleClass())
                .add(table.isExpandedRow(), DataTable.EXPANDED_ROW_CLASS)
                .build();

        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        if (rowKey != null) {
            writer.writeAttribute("data-rk", rowKey, null);
        }
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("role", "row", null);
        if (selectionEnabled) {
            writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(selected), null);
        }
        if (headerRow != null && !headerRow.isExpanded()) {
            writer.writeAttribute("style", "display: none;", null);
        }

        for (int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);

            if (column instanceof Column) {
                encodeCell(context, table, column, selected, disabled, rowIndex);
            }
            else if (column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn, false, disabled, rowIndex);
            }
        }

        writer.endElement("tr");

        if (table.isExpandedRow()) {
            ((RowExpandFeature) table.getFeature(DataTableFeatureKey.ROW_EXPAND)).encodeExpansion(context, this, table, rowIndex);
        }

        return true;
    }

    protected void encodeCell(FacesContext context, DataTable table, UIColumn column, boolean selected,
            boolean disabled, int rowIndex) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = table.getColumnMeta().get(column.getColumnKey(table, rowIndex));

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = column.getSelectionMode() != null;
        CellEditor editor = column.getCellEditor();
        boolean editorEnabled = editor != null && editor.isRendered();
        int responsivePriority = column.getResponsivePriority();
        String style = column.getStyle();

        String styleClass = getStyleClassBuilder(context)
                .add(selectionEnabled, DataTable.SELECTION_COLUMN_CLASS)
                .add(editorEnabled && editor.isDisabled(), DataTable.CELL_EDITOR_DISABLED_CLASS)
                .add(editorEnabled && !editor.isDisabled(), DataTable.EDITABLE_COLUMN_CLASS)
                .add(!column.isSelectRow(), DataTable.UNSELECTABLE_COLUMN_CLASS)
                .add(!columnVisible, DataTable.HIDDEN_COLUMN_CLASS)
                .add(column.getStyleClass())
                .add(responsivePriority > 0, "ui-column-p-" + responsivePriority)
                .build();

        int colspan = column.getColspan();
        int rowspan = column.getRowspan();

        writer.startElement("td", null);
        writer.writeAttribute("role", "gridcell", null);
        if (colspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }
        UIComponent component = (column instanceof UIComponent) ? (UIComponent) column : null;
        if (component != null) {
            renderDynamicPassThruAttributes(context, component);
        }

        if (selectionEnabled) {
            encodeColumnSelection(context, table, column, selected, disabled);
        }

        if (hasColumnDefaultRendering(table, column)) {
            encodeDefaultFieldCell(context, table, column, writer);
        }
        else if (column instanceof DynamicColumn) {
            column.encodeAll(context);
        }
        else {
            column.renderChildren(context);
        }

        writer.endElement("td");
    }

    protected void encodeDefaultFieldCell(FacesContext context, DataTable table, UIColumn column, ResponseWriter writer) throws IOException {
        Object value = table.getConvertedFieldValue(context, column);
        if (value != null) {
            writer.writeText(value, null);
        }
    }

    protected void encodeTFoot(FacesContext context, DataTable table) throws IOException {
        encodeTFoot(context, table, 0, table.getColumns().size(), null, null);
    }

    protected void encodeTFoot(FacesContext context, DataTable table, int columnStart, int columnEnd, String tfootId, String columnGroupType)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();
        String tfootClientId = (tfootId == null) ? table.getClientId(context) + "_foot" : tfootId;
        String colGroupType = (columnGroupType == null) ? "footer" : columnGroupType;
        ColumnGroup group = table.getColumnGroup(colGroupType);
        boolean hasFooterColumn = table.hasFooterColumn();
        boolean shouldRenderFooter = (hasFooterColumn || group != null);

        if (!shouldRenderFooter) {
            return;
        }

        writer.startElement("tfoot", null);
        writer.writeAttribute("id", tfootClientId, null);

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
                                    encodeColumnFooter(context, table, (Column) footerRowChild);
                                }
                                else if (footerRowChild instanceof Columns) {
                                    List<DynamicColumn> dynamicColumns = ((Columns) footerRowChild).getDynamicColumns();
                                    for (DynamicColumn dynaColumn : dynamicColumns) {
                                        dynaColumn.applyModel();
                                        encodeColumnFooter(context, table, dynaColumn);
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
        else if (table.hasFooterColumn()) {
            writer.startElement("tr", null);

            for (int i = columnStart; i < columnEnd; i++) {
                UIColumn column = columns.get(i);

                if (column instanceof Column) {
                    encodeColumnFooter(context, table, column);
                }
                else if (column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnFooter(context, table, dynamicColumn);
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("tfoot");
    }

    protected void encodeFacet(FacesContext context, DataTable table, UIComponent facet, String styleClass) throws IOException {
        if (facet == null) {
            return;
        }

        if (!ComponentUtils.shouldRenderFacet(facet, table.isRenderEmptyFacets())) {
            return;
        }
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        facet.encodeAll(context);

        writer.endElement("div");
    }

    protected void encodeStateHolder(FacesContext context, DataTable table, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("autocomplete", "off", null);
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        writer.endElement("input");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (table.isNativeElements()) {
            encodeNativeRadio(context, table, checked, disabled);
        }
        else {
            String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
            String iconClass = checked ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS : HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;
            boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
            boxClass = checked ? boxClass + " ui-state-active" : boxClass;

            writer.startElement("div", null);
            writer.writeAttribute("class", HTML.RADIOBUTTON_CLASS, null);

            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
            encodeNativeRadio(context, table, checked, disabled);
            writer.endElement("div");

            writer.startElement("div", null);
            writer.writeAttribute("class", boxClass, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");

            writer.endElement("div");
            writer.endElement("div");
        }
    }

    protected void encodeColumnSelection(FacesContext context, DataTable table, UIColumn column, boolean selected, boolean disabled)
            throws IOException {

        String selectionMode = column.getSelectionMode();

        if ("single".equalsIgnoreCase(selectionMode)) {
            encodeRadio(context, table, selected, disabled);
        }
        else if ("multiple".equalsIgnoreCase(selectionMode)) {
            encodeCheckbox(context, table, selected, disabled, HTML.CHECKBOX_CLASS, false);
        }
        else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }
    }

    protected void encodeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled, String styleClass,
                                  boolean isHeaderCheckbox) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        if (table.isNativeElements()) {
            encodeNativeCheckbox(context, table, checked, disabled, isHeaderCheckbox);
        }
        else {
            String ariaRowLabel = table.getAriaRowLabel();
            Object rowKey = null;
            String boxClass = HTML.CHECKBOX_BOX_CLASS;
            boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
            boxClass = checked ? boxClass + " ui-state-active" : boxClass;
            String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

            if (isHeaderCheckbox) {
                rowKey = "head";
                ariaRowLabel = MessageFactory.getMessage(DataTable.ARIA_HEADER_CHECKBOX_ALL);
            }
            else {
                rowKey = table.getRowKey();
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, "styleClass");

            writer.startElement("div", null);

            writer.writeAttribute("id", table.getClientId(context) + "_" + rowKey + "_checkbox", null);
            writer.writeAttribute("role", "checkbox", null);
            writer.writeAttribute("tabindex", "0", null);
            writer.writeAttribute(HTML.ARIA_LABEL, ariaRowLabel, null);
            writer.writeAttribute(HTML.ARIA_CHECKED, String.valueOf(checked), null);

            if (disabled) {
                writer.writeAttribute("aria-disabled", "true", null);
            }

            writer.writeAttribute("class", boxClass, null);

            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");

            writer.endElement("div");
            writer.endElement("div");
        }
    }

    protected void encodeNativeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled,
                                        boolean isHeaderCheckbox) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        String ariaRowLabel = table.getAriaRowLabel();
        if (isHeaderCheckbox) {
            ariaRowLabel = MessageFactory.getMessage(DataTable.ARIA_HEADER_CHECKBOX_ALL);
        }

        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("name", table.getClientId(context) + "_checkbox", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaRowLabel, null);
        writer.writeAttribute(HTML.ARIA_CHECKED, String.valueOf(checked), null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");
    }

    protected void encodeNativeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String ariaRowLabel = table.getAriaRowLabel();

        writer.startElement("input", null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("name", table.getClientId(context) + "_radio", null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaRowLabel, null);

        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }

        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");
    }

    protected void encodeSubTable(FacesContext context, DataTable table, SubTable subTable, int first, int last) throws IOException {
        LOGGER.info("SubTable has been deprecated, use row grouping instead");
        for (int i = first; i < last; i++) {
            table.setRowIndex(i);
            if (!table.isRowAvailable()) {
                break;
            }

            subTable.encodeAll(context);
        }
    }

    protected boolean isInSameGroup(FacesContext context, DataTable table, int currentRowIndex, int step, ValueExpression groupByVE,
                                    ELContext eLContext) {

        table.setRowIndex(currentRowIndex);
        Object currentGroupByData = groupByVE.getValue(eLContext);

        table.setRowIndex(currentRowIndex + step);
        if (!table.isRowAvailable()) {
            return false;
        }

        Object nextGroupByData = groupByVE.getValue(eLContext);

        return Objects.equals(nextGroupByData, currentGroupByData);
    }

    protected void encodeSortableHeaderOnReflow(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<String> options = getSortableHeadersText(context, table);

        if (!options.isEmpty()) {
            String reflowId = table.getContainerClientId(context) + "_reflowDD";

            writer.startElement("label", null);
            writer.writeAttribute("id", reflowId + "_label", null);
            writer.writeAttribute("for", reflowId, null);
            writer.writeAttribute("class", "ui-reflow-label", null);
            writer.writeText(MessageFactory.getMessage(DataTable.SORT_LABEL), null);
            writer.endElement("label");

            writer.startElement("select", null);
            writer.writeAttribute("id", reflowId, null);
            writer.writeAttribute("name", reflowId, null);
            writer.writeAttribute("class", "ui-reflow-dropdown ui-state-default", null);
            writer.writeAttribute("autocomplete", "off", null);

            for (int headerIndex = 0; headerIndex < options.size(); headerIndex++) {
                for (int order = 0; order < 2; order++) {
                    String orderVal = (order == 0)
                                      ? MessageFactory.getMessage(DataTable.SORT_ASC)
                                      : MessageFactory.getMessage(DataTable.SORT_DESC);

                    writer.startElement("option", null);
                    writer.writeAttribute("value", headerIndex + "_" + order, null);
                    writer.writeText(options.get(headerIndex) + " " + orderVal, null);
                    writer.endElement("option");
                }
            }

            writer.endElement("select");
        }
    }

    protected List<String> getSortableHeadersText(FacesContext context, DataTable table) {
        return table.getSortByAsMap().values().stream()
                .filter(s -> s.getComponent() instanceof UIColumn)
                .map(s -> getHeaderLabel(context, (UIColumn) s.getComponent()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected boolean hasColumnDefaultRendering(DataTable table, UIColumn column) {
        return column.getChildren().isEmpty()
                && (table.getSortByAsMap().containsKey(column.getColumnKey())
                || table.getFilterByAsMap().containsKey(column.getColumnKey())
                || !LangUtils.isValueBlank(column.getField()));

    }
}
