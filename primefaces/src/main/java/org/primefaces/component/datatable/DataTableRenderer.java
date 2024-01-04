/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.feature.DataTableFeature;
import org.primefaces.component.datatable.feature.DataTableFeatures;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.event.data.PostRenderEvent;
import org.primefaces.model.ColumnMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.WidgetBuilder;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTableRenderer extends DataRenderer {

    private static final Logger LOGGER = Logger.getLogger(DataTableRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        DataTable table = (DataTable) component;

        for (DataTableFeature feature : DataTableFeatures.all()) {
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
            for (DataTableFeature feature : DataTableFeatures.all()) {
                if (feature.shouldEncode(context, table)) {
                    feature.encode(context, this, table);
                }
            }

            if (table.isFullUpdateRequest(context)) {
                render(context, table);
            }
        }
        else {
            render(context, table);
        }

        context.getApplication().publishEvent(context, PostRenderEvent.class, table);
    }

    protected void render(FacesContext context, DataTable table) throws IOException {
        preRender(context, table);

        encodeMarkup(context, table);
        encodeScript(context, table);

        if (table.isPaginator() && table.getRows() == 0) {
            LOGGER.log(Level.WARNING, "DataTable with paginator=true should also set the rows attribute. ClientId: {0}", table.getClientId());
        }
    }

    protected void preRender(FacesContext context, DataTable table) {
        // trigger init, otherwise column state might be confused when rendering and init at the same time
        table.getSortByAsMap();
        table.getFilterByAsMap();

        if (table.isMultiViewState()) {
            table.restoreMultiViewState();
        }

        if (table.isLiveScroll()) {
            table.setScrollOffset(0);
        }

        if (!table.loadLazyDataIfEnabled()) {
            if (table.isFilteringCurrentlyActive()) {
                DataTableFeatures.filterFeature().filter(context, table);
            }

            if (table.isSortingCurrentlyActive()) {
                DataTableFeatures.sortFeature().sort(context, table);
                table.setRowIndex(-1); // why?
            }
        }

        if (table.isSelectionEnabled()) {
            DataTableFeatures.selectionFeature().decodeSelectionRowKeys(context, table);
        }

        if (table.isPaginator()) {
            table.calculateRows();
            table.calculateFirst();
        }
    }

    protected void encodeScript(FacesContext context, DataTable table) throws IOException {
        String selectionMode = table.resolveSelectionMode();
        boolean isFrozenTable = (table.getFrozenColumns() > 0);
        String widgetClass = isFrozenTable ? "FrozenDataTable" : "DataTable";

        if (isFrozenTable && !table.isScrollable()) {
            throw new FacesException("Frozen columns can only be used with a table set scrollable='true'.");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(widgetClass, table);

        //Pagination
        if (table.isPaginator()) {
            encodePaginatorConfig(context, table, wb);
        }

        //Selection
        wb.attr("selectionMode", selectionMode, null)
                .attr("selectionPageOnly", table.isSelectionPageOnly(), true)
                .attr("selectionRowMode", table.getSelectionRowMode(), "new")
                .attr("nativeElements", table.isNativeElements(), false)
                .attr("rowSelector", table.getRowSelector(), null)
                .attr("disabledTextSelection", table.isSelectionTextDisabled(), true);

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
                .attr("partialUpdate", table.isPartialUpdate(), true)
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
        String style = Objects.toString(table.getStyle(), Constants.EMPTY_STRING);
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
        if (LangUtils.isNotBlank(scrollHeight)) {
            if (!endsWithLenghtUnit(scrollHeight)) {
                scrollHeight = scrollHeight + "px";
            }
            // % handle specially in the JS code
            if (scrollHeight.indexOf('%') == -1) {
                writer.writeAttribute("style", "max-height:" + scrollHeight, null);
            }
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
        boolean filterable = table.isColumnFilterable(context, column);
        boolean isGroupedColumn = column.isGroupRow();
        boolean selectionBox = column.isSelectionBox();
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
                .add(selectionBox, DataTable.SELECTION_COLUMN_CLASS)
                .add(isGroupedColumn, DataTable.GROUPED_COLUMN_CLASS)
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

        if (selectionBox && "multiple".equalsIgnoreCase(table.getSelectionMode()) && table.isShowSelectAll()) {
            encodeCheckbox(context, table, table.isSelectAll(), false, HTML.CHECKBOX_ALL_CLASS, true);
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

        if (FacetUtils.shouldRenderFacet(header, table.isRenderEmptyFacets())) {
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
        if (table.isGlobalFilterOnly()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");

        if (!FacetUtils.shouldRenderFacet(filterFacet, table.isRenderEmptyFacets())) {
            encodeDefaultFilter(context, table, column, writer);
        }
        else {
            Object filterValue = table.getFilterValue(column);
            column.setFilterValueToValueHolder(filterValue);

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
        Object filterValue = findFilterValueForColumn(context, table, column, filterId);
        String filterStyleClass = column.getFilterStyleClass();
        encodeFilterInput(column, writer, disableTabbing, filterId, filterStyleClass, filterValue);
    }

    protected void encodeFilterInput(UIColumn column, ResponseWriter writer, boolean disableTabbing,
        String filterId, String filterStyleClass, Object filterValue) throws IOException {

        filterStyleClass = filterStyleClass == null
                           ? DataTable.COLUMN_INPUT_FILTER_CLASS
                           : DataTable.COLUMN_INPUT_FILTER_CLASS + " " + filterStyleClass;

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

        if (LangUtils.isNotBlank(column.getFilterPlaceholder())) {
            writer.writeAttribute("placeholder", column.getFilterPlaceholder(), null);
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
        if (FacetUtils.shouldRenderFacet(facet, table.isRenderEmptyFacets())) {
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

            for (int i = columnStart; i < columnEnd; i++) {
                UIColumn column = columns.get(i);
                if (column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyModel();
                }
                encodeColumnHeader(context, table, column);
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
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        int rows = table.getRows();
        int first = table.isClientCacheRequest(context) ? Integer.parseInt(params.get(clientId + "_first")) + rows : table.getFirst();
        int rowCount = table.getRowCount();

        int rowCountToRender;
        if (table.isVirtualScroll()) {
            rowCountToRender = Math.min(table.getScrollRows() * 2, rowCount);
        }
        else if (table.isLiveScroll()) {
            rowCountToRender = rows == 0 ? (table.getScrollRows() + table.getScrollOffset()) : rows;
        }
        else {
            rowCountToRender = rows == 0 ? rowCount : rows;
        }

        int frozenRows = table.getFrozenRows();
        boolean hasData = rowCount > 0;

        if (first == 0 && frozenRows > 0) {
            first += frozenRows;
        }

        if (!dataOnly) {
            String tbodyClientId = (tbodyId == null) ? clientId + "_data" : tbodyId;
            writer.startElement("tbody", null);
            writer.writeAttribute("id", tbodyClientId, null);
            writer.writeAttribute("class", DataTable.DATA_CLASS, null);

            if (table.isSelectionEnabled()) {
                writer.writeAttribute("tabindex", table.getTabindex(), null);
            }
        }

        if (hasData) {
            SubTable subTable = table.getSubTable();
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

            UIComponent emptyFacet = table.getFacet("emptyMessage");
            if (FacetUtils.shouldRenderFacet(emptyFacet, table.isRenderEmptyFacets())) {
                emptyFacet.encodeAll(context);
            }
            else {
                String emptyMessage = table.getEmptyMessage();
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
        List<SummaryRow> summaryRows = table.getSummaryRows();
        HeaderRow headerRow = table.getHeaderRow();
        ELContext elContext = context.getELContext();

        SortMeta sort = table.getHighestPriorityActiveSortMeta();
        boolean encodeHeaderRow = headerRow != null && headerRow.isEnabled() && sort != null;
        boolean encodeSummaryRow = (!summaryRows.isEmpty() && sort != null);

        for (int i = first; i < last; i++) {
            table.setRowIndex(i);
            if (!table.isRowAvailable()) {
                break;
            }

            if (encodeHeaderRow && (i == first || !isInSameGroup(context, table, i, -1, sort.getSortBy(), elContext))) {
                encodeHeaderRow(context, table, headerRow);
            }

            encodeRow(context, table, i, columnStart, columnEnd);

            if (encodeSummaryRow && !isInSameGroup(context, table, i, 1, sort.getSortBy(), elContext)) {
                encodeSummaryRow(context, summaryRows, sort);
            }
        }
    }

    protected void encodeFrozenRows(FacesContext context, DataTable table, int columnStart, int columnEnd) throws IOException {
        int frozenRows = table.getFrozenRows();
        if (frozenRows == 0) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("tbody", null);
        writer.writeAttribute("class", DataTable.DATA_CLASS, null);

        for (int i = 0; i < frozenRows; i++) {
            table.setRowIndex(i);
            encodeRow(context, table, i, columnStart, columnEnd);
        }

        writer.endElement("tbody");
    }

    protected void encodeSummaryRow(FacesContext context, List<SummaryRow> summaryRows, SortMeta sort) throws IOException {
        for (int i = 0; i < summaryRows.size(); i++) {
            SummaryRow summaryRow = summaryRows.get(i);
            MethodExpression me = summaryRow.getListener();
            if (me != null) {
                me.invoke(context.getELContext(), new Object[]{sort.getSortBy()});
            }

            summaryRow.encodeAll(context);
        }
    }

    protected void encodeHeaderRow(FacesContext context, DataTable table, HeaderRow headerRow) throws IOException {
        headerRow.encodeAll(context);
    }

    public boolean encodeRow(FacesContext context, DataTable table, int rowIndex) throws IOException {
        return encodeRow(context, table, rowIndex, 0, table.getColumns().size());
    }

    public boolean encodeRow(FacesContext context, DataTable table, int rowIndex, int columnStart, int columnEnd)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = table.isSelectionEnabled();
        boolean rowExpansionAvailable = table.getRowExpansion() != null;
        String rowKey = null;
        List<UIColumn> columns = table.getColumns();
        HeaderRow headerRow = table.getHeaderRow();

        if (selectionEnabled || rowExpansionAvailable) {
            rowKey = table.getRowKey(table.getRowData());
        }

        //Preselection
        boolean selected = selectionEnabled && table.getSelectedRowKeys().contains(rowKey);
        boolean disabled = table.isSelectionDisabled();
        boolean allowSelection = selectionEnabled && !disabled;
        boolean expanded = table.isExpandedRow() || (rowExpansionAvailable && table.getExpandedRowKeys().contains(rowKey));

        String rowStyleClass = getStyleClassBuilder(context)
                .add(DataTable.ROW_CLASS)
                .add(rowIndex % 2 == 0, DataTable.EVEN_ROW_CLASS, DataTable.ODD_ROW_CLASS)
                .add(allowSelection, DataTable.SELECTABLE_ROW_CLASS)
                .add(selected, "ui-state-highlight")
                .add(table.isEditingRow(),  DataTable.EDITING_ROW_CLASS)
                .add(table.getRowStyleClass())
                .add(expanded, DataTable.EXPANDED_ROW_CLASS)
                .build();

        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        if (rowKey != null) {
            writer.writeAttribute("data-rk", rowKey, null);
        }
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("title", table.getRowTitle(), null);
        if (selectionEnabled) {
            writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(selected), null);
        }
        if (headerRow != null && !headerRow.isExpanded()) {
            writer.writeAttribute("style", "display: none;", null);
        }

        for (int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);

            if (column instanceof Column) {
                encodeCell(context, table, column, selected, allowSelection, rowIndex);
            }
            else if (column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn, false, allowSelection, rowIndex);
            }
        }

        writer.endElement("tr");

        if (expanded) {
            DataTableFeatures.rowExpandFeature().encodeExpansion(context, this, table, rowIndex);
        }

        return true;
    }

    protected void encodeCell(FacesContext context, DataTable table, UIColumn column, boolean selected,
            boolean rowSelectionEnabled, int rowIndex) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ColumnMeta columnMeta = table.getColumnMeta().get(column.getColumnKey(table, rowIndex));

        boolean columnVisible = column.isVisible();
        if (columnMeta != null && columnMeta.getVisible() != null) {
            columnVisible = columnMeta.getVisible();
        }

        ResponseWriter writer = context.getResponseWriter();
        boolean columnSelectionEnabled = column.isSelectionBox();
        boolean isGroupedColumn = column.isGroupRow();
        CellEditor editor = column.getCellEditor();
        boolean editorEnabled = editor != null && editor.isRendered();
        int responsivePriority = column.getResponsivePriority();
        String title = column.getTitle();
        String style = column.getStyle();

        String styleClass = getStyleClassBuilder(context)
                .add(columnSelectionEnabled, DataTable.SELECTION_COLUMN_CLASS)
                .add(isGroupedColumn, DataTable.GROUPED_COLUMN_CLASS)
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
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        UIComponent component = (column instanceof UIComponent) ? (UIComponent) column : null;
        if (component != null) {
            renderDynamicPassThruAttributes(context, component);
        }

        if (columnSelectionEnabled) {
            encodeColumnSelection(context, table, column, selected, rowSelectionEnabled);
        }

        if (hasColumnDefaultRendering(table, column)) {
            encodeDefaultFieldCell(context, table, column, writer);
        }
        else if (column instanceof DynamicColumn) {
            encodeDynamicCell(context, table, column);
        }
        else {
            column.renderChildren(context);
        }

        writer.endElement("td");
    }

    /**
     * Encodes dynamic column. Allows to override default behavior.
     */
    protected void encodeDynamicCell(FacesContext context, DataTable table, UIColumn column) throws IOException {
        column.encodeAll(context);
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
        if (!FacetUtils.shouldRenderFacet(facet, table.isRenderEmptyFacets())) {
            return;
        }
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        facet.encodeAll(context);

        writer.endElement("div");
    }

    protected void encodeStateHolder(FacesContext context, DataTable table, String id, String value) throws IOException {
        renderHiddenInput(context, id, value, false);
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
            String iconClass = checked ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS : HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;
            String boxClass = getStyleClassBuilder(context)
                        .add(HTML.RADIOBUTTON_BOX_CLASS)
                        .add(disabled, "ui-state-disabled")
                        .add(checked, "ui-state-active")
                        .build();

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

    protected void encodeColumnSelection(FacesContext context, DataTable table, UIColumn column, boolean selected, boolean rowSelectionEnabled)
            throws IOException {

        String selectionMode = table.getSelectionMode();

        if ("single".equalsIgnoreCase(selectionMode)) {
            encodeRadio(context, table, selected, !rowSelectionEnabled);
        }
        else if ("multiple".equalsIgnoreCase(selectionMode)) {
            encodeCheckbox(context, table, selected, !rowSelectionEnabled, HTML.CHECKBOX_CLASS, false);
        }
        else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }
    }

    protected void encodeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled, String styleClass,
                                  boolean isHeaderCheckbox) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        if (table.isNativeElements()) {
            encodeNativeCheckbox(context, table, checked, disabled);
        }
        else {
            String ariaRowLabel = table.getAriaRowLabel();
            String boxClass = getStyleClassBuilder(context)
                        .add(HTML.CHECKBOX_BOX_CLASS)
                        .add(disabled, "ui-state-disabled")
                        .add(checked, "ui-state-active")
                        .build();
            String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
            Object rowKey = isHeaderCheckbox ? "head" : table.getRowKey();

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

    protected void encodeNativeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String ariaRowLabel = table.getAriaRowLabel();
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
                                    ELContext elContext) {

        Object currentGroupByData = groupByVE.getValue(elContext);
        int nextRowIndex = currentRowIndex + step;
        Object nextGroupByData;

        // in case of a lazy DataTable, the LazyDataModel currently only loads rows inside the current page; we need a small hack here
        // 1) get the rowData manually for the next row
        // 2) put it into request-scope
        // 3) invoke the groupBy ValueExpression
        if (table.isLazy()) {
            Object nextRowData = table.getLazyDataModel().getRowData(nextRowIndex, table.getActiveSortMeta(), table.getActiveFilterMeta());
            if (nextRowData == null) {
                return false;
            }

            nextGroupByData = ComponentUtils.executeInRequestScope(context, table.getVar(), nextRowData, () -> groupByVE.getValue(elContext));
        }
        else {
            table.setRowIndex(nextRowIndex);
            if (!table.isRowAvailable()) {
                table.setRowIndex(currentRowIndex); // restore row index
                return false;
            }

            nextGroupByData = groupByVE.getValue(elContext);
            table.setRowIndex(currentRowIndex); // restore row index
        }

        return Objects.equals(nextGroupByData, currentGroupByData);
    }

    protected void encodeSortableHeaderOnReflow(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<SortMeta, String> headers = getSortableColumnHeaders(context, table);

        if (!headers.isEmpty()) {
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

            for (Map.Entry<SortMeta, String> header : headers.entrySet()) {
                for (int sortOrder = 0; sortOrder < 2; sortOrder++) {
                    String sortOrderLabel = (sortOrder == 0)
                                      ? MessageFactory.getMessage(DataTable.SORT_ASC)
                                      : MessageFactory.getMessage(DataTable.SORT_DESC);

                    writer.startElement("option", null);
                    writer.writeAttribute("value", header.getKey().getColumnKey() + "_" + sortOrder, null);
                    writer.writeAttribute("data-columnkey", header.getKey().getColumnKey(), null);
                    writer.writeAttribute("data-sortorder", sortOrder, null);
                    writer.writeText(header.getValue() + " " + sortOrderLabel, null);
                    writer.endElement("option");
                }
            }

            writer.endElement("select");
        }
    }

    protected Map<SortMeta, String> getSortableColumnHeaders(FacesContext context, DataTable table) {
        AtomicReference<String> headerLabel = new AtomicReference<>(null);

        Map<String, SortMeta> sortByAsMap = table.getSortByAsMap();
        Map<SortMeta, String> headers = new LinkedHashMap<>(sortByAsMap.size());
        for (SortMeta sortMeta : sortByAsMap.values()) {
            if (sortMeta.isHeaderRow()) {
                continue;
            }

            headerLabel.set(null);
            table.invokeOnColumn(sortMeta.getColumnKey(), (column) -> {
                headerLabel.set(getHeaderLabel(context, column));
            });
            headers.put(sortMeta, headerLabel.get());
        }

        return headers;
    }

    protected boolean hasColumnDefaultRendering(DataTable table, UIColumn column) {
        return column.getChildren().isEmpty()
                && (table.getSortByAsMap().containsKey(column.getColumnKey())
                || table.getFilterByAsMap().containsKey(column.getColumnKey())
                || LangUtils.isNotBlank(column.getField()));

    }
}
