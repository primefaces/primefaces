/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.datatable;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.feature.DataTableFeature;
import org.primefaces.component.datatable.feature.DataTableFeatureKey;
import org.primefaces.component.datatable.feature.RowExpandFeature;
import org.primefaces.component.datatable.feature.SortFeature;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class DataTableRenderer extends DataRenderer {
    
    private final static Logger logger = Logger.getLogger(DataTableRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        DataTable table = (DataTable) component;

        for(Iterator<DataTableFeature> it = DataTable.FEATURES.values().iterator(); it.hasNext();) {
            DataTableFeature feature = it.next();
            
            if(feature.shouldDecode(context, table)) {
                feature.decode(context, table);
            }
        }
        
        decodeBehaviors(context, component);        
    }
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;

        if(table.shouldEncodeFeature(context)) {
            for(Iterator<DataTableFeature> it = DataTable.FEATURES.values().iterator(); it.hasNext();) {
                DataTableFeature feature = it.next();

                if(feature.shouldEncode(context, table)) {
                    feature.encode(context, this, table);
                }
            }
        }
        else {  
            preRender(context, table);
            
            encodeMarkup(context, table);
            encodeScript(context, table);
        }
	}
    
    protected void preRender(FacesContext context, DataTable table) {
        if(table.isLazy()) {
            if(table.isLiveScroll())
                table.loadLazyScrollData(0, table.getScrollRows());
            else
                table.loadLazyData();
        }

        boolean defaultSorted = (table.getValueExpression("sortBy") != null || table.getSortBy() != null);
        if(defaultSorted && !table.isLazy()) {
            table.setDefaultSortByVE(table.getValueExpression("sortBy"));
            table.setDefaultSortOrder(table.getSortOrder());
            table.setDefaultSortFunction(table.getSortFunction());
            
            SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);

            if(table.isMultiSort())
                sortFeature.multiSort(context, table);
            else
                sortFeature.singleSort(context, table);  
            
            table.setRowIndex(-1);
        }

        if(table.isPaginator()) {
            table.calculateFirst();
        }

        Columns dynamicCols = table.getDynamicColumns();
        if(dynamicCols != null) {
            dynamicCols.setRowIndex(-1);
        }
    }   
    
	protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		String clientId = table.getClientId(context);
        String selectionMode = table.resolveSelectionMode();
        String widgetClass = (table.getFrozenColumns() == 0) ? "DataTable" : "FrozenDataTable";
        String initMode = table.getInitMode();
        
        WidgetBuilder wb = getWidgetBuilder(context);

        if(initMode.equals("load"))
            wb.initWithDomReady(widgetClass, table.resolveWidgetVar(), clientId);
        else if(initMode.equals("immediate"))
            wb.init(widgetClass, table.resolveWidgetVar(), clientId);
        else
            throw new FacesException(initMode + " is not a valid value for initMode, possible values are \"load\" and \"immediate.");
        
        //Pagination
        if(table.isPaginator()) {
            encodePaginatorConfig(context, table, wb);
        }
        
        //Selection
        wb.attr("selectionMode", selectionMode, null)
            .attr("rowSelectMode", table.getRowSelectMode(), "new")
            .attr("nativeElements", table.isNativeElements(), false)
            .attr("disabledTextSelection", table.isDisabledTextSelection(), true);
        
        //Filtering
        if(table.isFilteringEnabled()) {
            wb.attr("filter", true)
                .attr("filterEvent", table.getFilterEvent(), null)
                .attr("filterDelay", table.getFilterDelay(), Integer.MAX_VALUE);
        }
        
        //Row expansion
        if(table.getRowExpansion() != null) {
            wb.attr("expansion", true).attr("rowExpandMode", table.getRowExpandMode());
        }

        //Scrolling
        if(table.isScrollable()) {
            wb.attr("scrollable", true)
                .attr("liveScroll", table.isLiveScroll())
                .attr("scrollStep", table.getScrollRows())
                .attr("scrollLimit", table.getRowCount())
                .attr("scrollWidth", table.getScrollWidth(), null)
                .attr("scrollHeight", table.getScrollHeight(), null)
                .attr("frozenColumns", table.getFrozenColumns(), 0)
                .attr("liveScrollBuffer", table.getLiveScrollBuffer());
        }

        //Resizable/Draggable Columns
        wb.attr("resizableColumns", table.isResizableColumns(), false)
            .attr("liveResize", table.isLiveResize(), false)
            .attr("draggableColumns", table.isDraggableColumns(), false)
            .attr("resizeMode", table.getResizeMode(), "fit");
        
        //Draggable Rows
        wb.attr("draggableRows", table.isDraggableRows(), false);
        
        //Editing
        if(table.isEditable()) {
            wb.attr("editable", true).attr("editMode", table.getEditMode()).attr("cellSeparator", table.getCellSeparator(), null);
        }
        
        //MultiColumn Sorting
        if(table.isMultiSort()) {
            wb.attr("multiSort", true);
        }
        
        if(table.isStickyHeader()) {
            wb.attr("stickyHeader", true);
        }

        wb.attr("tabindex", table.getTabindex(), null)
            .attr("reflow", table.isReflow(), false)
            .attr("rowHover", table.isRowHover(), false);
        
        //Behaviors
        encodeClientBehaviors(context, table);

        wb.finish();
	}

	protected void encodeMarkup(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        boolean scrollable = table.isScrollable();
        boolean hasPaginator = table.isPaginator();
        String style = table.getStyle();
        String paginatorPosition = table.getPaginatorPosition();
        int frozenColumns = table.getFrozenColumns();
        boolean hasFrozenColumns = (frozenColumns != 0);
        
        //style class
        String containerClass = scrollable ? DataTable.CONTAINER_CLASS + " " + DataTable.SCROLLABLE_CONTAINER_CLASS : DataTable.CONTAINER_CLASS;
        containerClass = table.getStyleClass() != null ? containerClass + " " + table.getStyleClass() : containerClass;
        if(table.isResizableColumns()) containerClass = containerClass + " " + DataTable.RESIZABLE_CONTAINER_CLASS;
        if(table.isStickyHeader()) containerClass = containerClass + " " + DataTable.STICKY_HEADER_CLASS;
        if(ComponentUtils.isRTL(context, table)) containerClass = containerClass + " " + DataTable.RTL_CLASS;
        if(table.isReflow()) containerClass = containerClass + " " + DataTable.REFLOW_CLASS;
        if(hasFrozenColumns) containerClass = containerClass + " ui-datatable-frozencolumn";
        
        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        encodeFacet(context, table, table.getHeader(), DataTable.HEADER_CLASS);
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, table, "top");
        }

        if(scrollable) {
            encodeScrollableTable(context, table);
        } 
        else {
            encodeRegularTable(context, table);
        }
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, table, "bottom");
        }
        
        encodeFacet(context, table, table.getFooter(), DataTable.FOOTER_CLASS);

        if(table.isSelectionEnabled()) {
            encodeStateHolder(context, table, table.getClientId(context) + "_selection", table.getSelectedRowKeysAsString());
        }
        
        if(table.isDraggableColumns()) {
            encodeStateHolder(context, table, table.getClientId(context) + "_columnOrder", null);
        }
        
        if(scrollable) {
            encodeStateHolder(context, table, table.getClientId(context) + "_scrollState", table.getScrollState());
        }

        writer.endElement("div");
	}

    protected void encodeRegularTable(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.TABLE_WRAPPER_CLASS, null);
        
        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        if(table.getTableStyle() != null) writer.writeAttribute("style", table.getTableStyle(), null);
        if(table.getTableStyleClass() != null) writer.writeAttribute("class", table.getTableStyleClass(), null);
        if(table.getSummary() != null) writer.writeAttribute("summary", table.getSummary(), null);
        
        encodeThead(context, table);
        encodeTFoot(context, table);
        encodeTbody(context, table, false);
        
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
        
        if(hasFrozenColumns) {
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
            encodeScrollAreaEnd(context);

            encodeScrollBody(context, table, tableStyle, tableStyleClass, 0, frozenColumns, clientId + "_frozenTbody");

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
            encodeScrollAreaEnd(context);

            encodeScrollBody(context, table, tableStyle, tableStyleClass, frozenColumns, columnsCount, clientId + "_scrollableTbody");

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
            encodeScrollAreaEnd(context);

            encodeScrollBody(context, table, tableStyle, tableStyleClass, 0, columnsCount, null);

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
        if(tableStyle != null) writer.writeAttribute("style", tableStyle, null);
        if(tableStyleClass != null) writer.writeAttribute("class", tableStyleClass, null);        
    }
    
    protected void encodeScrollAreaEnd(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.endElement("table");
        writer.endElement("div");
        writer.endElement("div");
    }
       
    protected void encodeScrollBody(FacesContext context, DataTable table, String tableStyle, String tableStyleClass, int columnStart, int columnEnd, String tbodyId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String scrollHeight = table.getScrollHeight();

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_BODY_CLASS, null);
        writer.writeAttribute("tabindex", "-1", null);
        if(scrollHeight != null && scrollHeight.indexOf("%") == -1) {
            writer.writeAttribute("style", "height:" + scrollHeight + "px", null);
        }
        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        
        if(tableStyle != null) writer.writeAttribute("style", tableStyle, null);
        if(table.getTableStyleClass() != null) writer.writeAttribute("class", tableStyleClass, null);
        
        encodeTbody(context, table, false, columnStart, columnEnd, tbodyId);
        
        writer.endElement("table");
        writer.endElement("div");
    }

    public void encodeColumnHeader(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getContainerClientId(context);

        ValueExpression columnSortByVE = column.getValueExpression("sortBy");
        boolean sortable = (columnSortByVE != null && column.isSortable());
        boolean filterable = (column.getValueExpression("filterBy") != null && column.isFilterable());
        String selectionMode = column.getSelectionMode();
        String sortIcon = null;
        boolean resizable = table.isResizableColumns() && column.isResizable();
        int priority = column.getPriority();        
        
        String columnClass = sortable ? DataTable.COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.COLUMN_HEADER_CLASS;
        columnClass = filterable ? columnClass + " " + DataTable.FILTER_COLUMN_CLASS : columnClass;
        columnClass = selectionMode != null ? columnClass + " " + DataTable.SELECTION_COLUMN_CLASS : columnClass;
        columnClass = resizable ? columnClass + " " + DataTable.RESIZABLE_COLUMN_CLASS : columnClass;
        columnClass = !column.isToggleable() ? columnClass + " " + DataTable.STATIC_COLUMN_CLASS : columnClass;
        columnClass = !column.isVisible()? columnClass + " " + DataTable.HIDDEN_COLUMN_CLASS : columnClass;
        columnClass = column.getStyleClass() != null ? columnClass + " " + column.getStyleClass() : columnClass;
        
        if(priority > 0) {
            columnClass = columnClass + " ui-column-p-" + priority; 
        }
        
        if(sortable) {
            ValueExpression tableSortByVE = table.getValueExpression("sortBy");
            Object tableSortBy = table.getSortBy();
            boolean defaultSorted = (tableSortByVE != null || tableSortBy != null);
                    
            if(defaultSorted) {
                if(table.isMultiSort()) {
                    List<SortMeta> sortMeta = table.getMultiSortMeta();
                    
                    if(sortMeta != null) {
                        for(SortMeta meta : sortMeta) {
                            sortIcon = resolveDefaultSortIcon(column, meta);

                            if(sortIcon != null) {
                                break;
                            }
                        }
                    }
                }
                else {
                    sortIcon = resolveDefaultSortIcon(table, column, table.getSortOrder());
                }
            }
            
            if(sortIcon == null)
                sortIcon = DataTable.SORTABLE_COLUMN_ICON_CLASS;
            else
                columnClass += " ui-state-active";
        }
        
        String style = column.getStyle();
        String width = column.getWidth();
        if(width != null) {
            String unit = width.endsWith("%") ? "" : "px";
            if(style != null)
                style = style + ";width:" + width + unit;
            else
                style = "width:" + width + unit;
        }
        
        writer.startElement("th", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", columnClass, null);
        writer.writeAttribute("role", "columnheader", null);
        
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);
        
        if(filterable) {
            table.enableFiltering();

            String filterPosition = column.getFilterPosition();

            if(filterPosition.equals("bottom")) {
                encodeColumnHeaderContent(context, column, sortIcon);
                encodeFilter(context, table, column);
            }
            else if(filterPosition.equals("top")) {
                encodeFilter(context, table, column);
                encodeColumnHeaderContent(context, column, sortIcon);
            } 
            else {
                throw new FacesException(filterPosition + " is an invalid option for filterPosition, valid values are 'bottom' or 'top'.");
            }
        }
        else {
            encodeColumnHeaderContent(context, column, sortIcon);
        }
        
        if(selectionMode != null && selectionMode.equalsIgnoreCase("multiple")) {
            encodeCheckbox(context, table, false, false, HTML.CHECKBOX_ALL_CLASS);
        }
        
        writer.endElement("th");
    }
    
    protected String resolveDefaultSortIcon(UIColumn column, SortMeta sortMeta) {
        SortOrder sortOrder = sortMeta.getSortOrder();
        String sortIcon = null;
        
        if(column.getColumnKey().equals(sortMeta.getColumn().getColumnKey())) {
            if(sortOrder.equals(SortOrder.ASCENDING))
                sortIcon = DataTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
            else if(sortOrder.equals(SortOrder.DESCENDING))
                sortIcon = DataTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
        }
        
        return sortIcon;
    }
    
    protected String resolveDefaultSortIcon(DataTable table, UIColumn column, String sortOrder) {
        ValueExpression tableSortByVE = table.getValueExpression("sortBy");
        ValueExpression columnSortByVE = column.getValueExpression("sortBy");
        String columnSortByExpression = columnSortByVE.getExpressionString();
        String tableSortByExpression = tableSortByVE.getExpressionString();
        String field = column.getField();
        String sortField = table.getSortField();
        String sortIcon = null;

        if((sortField != null && field != null && sortField.equals(field)) || (tableSortByExpression != null && tableSortByExpression.equals(columnSortByExpression))) {
            if(sortOrder.equalsIgnoreCase("ASCENDING"))
                sortIcon = DataTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
            else if(sortOrder.equalsIgnoreCase("DESCENDING"))
                sortIcon = DataTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;
        }
        
        return sortIcon;
    }
            
    protected void encodeColumnHeaderContent(FacesContext context, UIColumn column, String sortIcon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
                        
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();
        
        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.COLUMN_TITLE_CLASS, null);
        
        if(header != null)
            header.encodeAll(context);
        else if(headerText != null)
            writer.write(headerText);
        
        writer.endElement("span");
        
        if(sortIcon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", sortIcon, null);
            writer.endElement("span");
        }
    }
    
    protected void encodeFilter(FacesContext context, DataTable table, UIColumn column) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();
        UIComponent filterFacet = column.getFacet("filter");
                
        if (filterFacet == null) {
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
            boolean disableTabbing = table.getScrollWidth() != null;

            String filterId = column.getContainerClientId(context) + separator + "filter";
            String filterStyleClass = column.getFilterStyleClass();

            String filterValue = null;
            if(table.isReset()) {
                filterValue = "";
            }
            else {
                if(params.containsKey(filterId)) {
                    filterValue = params.get(filterId);
                }
                else {
                    Object columnFilterValue = column.getFilterValue();
                    filterValue = (columnFilterValue == null) ? Constants.EMPTY_STRING: columnFilterValue.toString();
                }
            }

            if(column.getValueExpression("filterOptions") == null) {
                filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_INPUT_FILTER_CLASS : DataTable.COLUMN_INPUT_FILTER_CLASS + " " + filterStyleClass;

                writer.startElement("input", null);
                writer.writeAttribute("id", filterId, null);
                writer.writeAttribute("name", filterId, null);
                writer.writeAttribute("class", filterStyleClass, null);
                writer.writeAttribute("value", filterValue , null);
                writer.writeAttribute("autocomplete", "off", null);

                if(disableTabbing)
                    writer.writeAttribute("tabindex", "-1", null);

                if(column.getFilterStyle() != null)
                    writer.writeAttribute("style", column.getFilterStyle(), null);

                if(column.getFilterMaxLength() != Integer.MAX_VALUE)
                    writer.writeAttribute("maxlength", column.getFilterMaxLength(), null);

                writer.endElement("input");
            }
            else {
                filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_FILTER_CLASS : DataTable.COLUMN_FILTER_CLASS + " " + filterStyleClass;

                writer.startElement("select", null);
                writer.writeAttribute("id", filterId, null);
                writer.writeAttribute("name", filterId, null);
                writer.writeAttribute("class", filterStyleClass, null);

                if(disableTabbing)
                    writer.writeAttribute("tabindex", "-1", null);

                SelectItem[] itemsArray = (SelectItem[]) getFilterOptions(column);

                for(SelectItem item : itemsArray) {
                    Object itemValue = item.getValue();

                    writer.startElement("option", null);
                    writer.writeAttribute("value", item.getValue(), null);
                    if(itemValue != null && String.valueOf(itemValue).equals(filterValue)) {
                        writer.writeAttribute("selected", "selected", null);
                    }
                    
                    if(item.isEscape())
                        writer.writeText(item.getLabel(), "value");
                    else
                        writer.write(item.getLabel());
                    
                    writer.endElement("option");
                }

                writer.endElement("select");
            }
        }
        else {
            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CUSTOM_FILTER_CLASS, null);
            filterFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected SelectItem[] getFilterOptions(UIColumn column) {
        Object options = column.getFilterOptions();
        
        if(options instanceof SelectItem[]) {
            return (SelectItem[]) options;
        } else if(options instanceof Collection<?>) {
            return ((Collection<SelectItem>) column.getFilterOptions()).toArray(new SelectItem[] {});
        } else {
            throw new FacesException("Filter options for column " + column.getClientId() + " should be a SelectItem array or collection");
        }
    }

    public void encodeColumnFooter(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        
        int priority = column.getPriority();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = styleClass == null ? DataTable.COLUMN_FOOTER_CLASS : DataTable.COLUMN_FOOTER_CLASS + " " + styleClass;

        if(!column.isVisible()) {
            styleClass = styleClass + " " + DataTable.HIDDEN_COLUMN_CLASS;
        }
        
        if(priority > 0) {
            styleClass = styleClass + " ui-column-p-" + priority; 
        }
        
        writer.startElement("td", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);
        
        //Footer content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if(facet != null) {
            facet.encodeAll(context);
        } else if(text != null) {
            writer.write(text);
        }

        writer.endElement("td");
    }
    
    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        this.encodeThead(context, table, 0, table.getColumns().size(), null, null);
    }

    protected void encodeThead(FacesContext context, DataTable table, int columnStart, int columnEnd, String theadId, String columnGroupType) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();
        String theadClientId = (theadId == null) ? table.getClientId(context) + "_head" : theadId;
        String colGroupType = (columnGroupType == null) ? "header" : columnGroupType;
        ColumnGroup group = table.getColumnGroup(colGroupType);
        
        writer.startElement("thead", null);
        writer.writeAttribute("id", theadClientId, null);
        
        if(group != null && group.isRendered()) {
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof Row) {
                        Row headerRow = (Row) child;
                        String rowClass = headerRow.getStyleClass();
                        String rowStyle = headerRow.getStyle();
                        
                        writer.startElement("tr", null);
                        if(rowClass != null) writer.writeAttribute("class", rowClass, null);
                        if(rowStyle != null) writer.writeAttribute("style", rowStyle, null);
                        
                        for(UIComponent headerRowChild: headerRow.getChildren()) {
                            if(headerRowChild.isRendered()) {
                                if(headerRowChild instanceof Column) {
                                    encodeColumnHeader(context, table, (Column) headerRowChild);
                                }
                                else if(headerRowChild instanceof Columns) {
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
            
            for(int i = columnStart; i < columnEnd; i++) {
                UIColumn column = columns.get(i);

                if(column instanceof Column) {
                    encodeColumnHeader(context, table, column);
                }
                else if(column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyModel();

                    encodeColumnHeader(context, table, dynamicColumn);
                }
            }
            
            writer.endElement("tr");
        }
        
        encodeFrozenRows(context, table);

        writer.endElement("thead");
    }
    
    public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly) throws IOException {
        this.encodeTbody(context, table, dataOnly, 0, table.getColumns().size(), null);
    }

    public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly, int columnStart, int columnEnd, String tbodyId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);
        String emptyMessage = table.getEmptyMessage();
        UIComponent emptyFacet = table.getFacet("emptyMessage");
        SubTable subTable = table.getSubTable();
        String tbodyClientId = (tbodyId == null) ? clientId + "_data" : tbodyId;
        
        if(table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }
               
        int rows = table.getRows();
		int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rowCountToRender = rows == 0 ? (table.isLiveScroll() ? (table.getScrollRows() + table.getScrollOffset()) : rowCount) : rows;
        int frozenRows = table.getFrozenRows();
        boolean hasData = rowCount > 0;
        
        if(first == 0 && frozenRows > 0) {
            first += frozenRows;
        }
              
        if(!dataOnly) {
            writer.startElement("tbody", null);
            writer.writeAttribute("id", tbodyClientId, null);
            writer.writeAttribute("class", DataTable.DATA_CLASS, null);
        }

        if(hasData) {
            if(subTable != null)
                encodeSubTable(context, table, subTable, first, (first + rowCountToRender));
            else
                encodeRows(context, table, first, (first + rowCountToRender), columnStart, columnEnd);
        }
        else {
            //Empty message
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.EMPTY_MESSAGE_ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumnsCountWithSpan(), null);
            
            if(emptyFacet != null)
                emptyFacet.encodeAll(context);
            else
                writer.write(emptyMessage);

            writer.endElement("td");
            
            writer.endElement("tr");
        }
		
        if(!dataOnly) {
            writer.endElement("tbody");
        }

		//Cleanup
		table.setRowIndex(-1);
		if(rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
    }
    
    protected void encodeRows(FacesContext context, DataTable table, int first, int last, int columnStart, int columnEnd) throws IOException {
        String clientId = table.getClientId(context);
        SummaryRow summaryRow = table.getSummaryRow();
        ELContext eLContext = context.getELContext();
        ValueExpression groupByVE = null;
        ValueExpression tableSortByVE = table.getValueExpression("sortBy");
        if(tableSortByVE != null) {
            groupByVE = tableSortByVE;
        }
        else {
            groupByVE = (table.getSortBy() == null || table.isMultiSort()) ? null : context.getApplication().getExpressionFactory().createValueExpression(
                        eLContext, "#{" + table.getVar() + "." + table.getSortBy() + "}", Object.class);
        }
        
        boolean encodeSummaryRow = (summaryRow != null && groupByVE != null);
        
        for(int i = first; i < last; i++) {
            table.setRowIndex(i);
            if(!table.isRowAvailable()) {
                break;
            }

            encodeRow(context, table, clientId, i, columnStart, columnEnd);

            if(encodeSummaryRow && !isInSameGroup(context, table, i, groupByVE, eLContext)) {
                table.setRowIndex(i);
                encodeSummaryRow(context, table, summaryRow);
            }
        }
    }
        
    protected void encodeFrozenRows(FacesContext context, DataTable table) throws IOException {
        int frozenRows = table.getFrozenRows();
        if(frozenRows == 0 ) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        
        writer.startElement("tbody", null);
        writer.writeAttribute("class", DataTable.DATA_CLASS, null);
        
        for (int i = 0; i < frozenRows; i++) {
            table.setRowIndex(i);
            encodeRow(context, table, clientId, i, 0, table.getColumnsCount());
        }

        writer.endElement("tbody");
    }
    
    protected void encodeSummaryRow(FacesContext context, DataTable table, SummaryRow summaryRow) throws IOException{
        MethodExpression me = summaryRow.getListener();
        if(me != null) {
            me.invoke(context.getELContext(), new Object[]{table.getSortBy()});
        }
        
        summaryRow.encodeAll(context);
    }
    
    public boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex) throws IOException {
        return this.encodeRow(context, table, clientId, rowIndex, 0, table.getColumns().size());
    }

    public boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex, int columnStart, int columnEnd) throws IOException {
        ResponseWriter writer = context.getResponseWriter();        
        boolean selectionEnabled = table.isSelectionEnabled();
        Object rowKey = null;
        List<UIColumn> columns = table.getColumns();
        
        if(selectionEnabled) {
            //try rowKey attribute
            rowKey = table.getRowKey();
            
            //ask selectable datamodel
            if(rowKey == null)
                rowKey = table.getRowKeyFromModel(table.getRowData());
        }

        //Preselection
        boolean selected = table.getSelectedRowKeys().contains(rowKey);

        String userRowStyleClass = table.getRowStyleClass();
        String rowStyleClass = rowIndex % 2 == 0 ? DataTable.ROW_CLASS + " " + DataTable.EVEN_ROW_CLASS : DataTable.ROW_CLASS + " " + DataTable.ODD_ROW_CLASS;
        if(selectionEnabled && !table.isDisabledSelection())
            rowStyleClass = rowStyleClass + " " + DataTable.SELECTABLE_ROW_CLASS;
        
        if(selected)
            rowStyleClass = rowStyleClass + " ui-state-highlight";

        if(table.isEditingRow())
            rowStyleClass = rowStyleClass + " " + DataTable.EDITING_ROW_CLASS;
            
        if(userRowStyleClass != null)
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;

        if(table.isExpandedRow())
            rowStyleClass = rowStyleClass + " " + DataTable.EXPANDED_ROW_CLASS;
        
        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        if(rowKey != null) {
            writer.writeAttribute("data-rk", rowKey, null);
        }
        writer.writeAttribute("class", rowStyleClass, null);
        writer.writeAttribute("role", "row", null);
        if(selectionEnabled) {
            writer.writeAttribute("aria-selected", String.valueOf(selected), null);
        }
        
        for(int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);
            
            if(column instanceof Column) {
                encodeCell(context, table, column, clientId, selected);
            }
            else if(column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn, null, false);
            }
        }

        writer.endElement("tr");
        
        if(table.isExpandedRow()) {
            ((RowExpandFeature) table.getFeature(DataTableFeatureKey.ROW_EXPAND)).encodeExpansion(context, this, table, rowIndex);
        }

        return true;
    }

    protected void encodeCell(FacesContext context, DataTable table, UIColumn column, String clientId, boolean selected) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = column.getSelectionMode() != null;
        int priority = column.getPriority();
        String style = column.getStyle();
        String styleClass = selectionEnabled ? DataTable.SELECTION_COLUMN_CLASS : (column.getCellEditor() != null) ? DataTable.EDITABLE_COLUMN_CLASS : null;
        styleClass = (column.isSelectRow()) ? styleClass : (styleClass == null) ? DataTable.UNSELECTABLE_COLUMN_CLASS : styleClass + " " + DataTable.UNSELECTABLE_COLUMN_CLASS;
        styleClass = (column.isVisible()) ? styleClass : (styleClass == null) ? DataTable.HIDDEN_COLUMN_CLASS : styleClass + " " + DataTable.HIDDEN_COLUMN_CLASS;
        String userStyleClass = column.getStyleClass();
        styleClass = userStyleClass == null ? styleClass : (styleClass == null) ? userStyleClass : styleClass + " " + userStyleClass;
        
        if(priority > 0) {
            styleClass = (styleClass == null) ? "ui-column-p-" + priority : styleClass + " ui-column-p-" + priority; 
        }
        
        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        
        writer.startElement("td", null);
        writer.writeAttribute("role", "gridcell", null);
        if(colspan != 1) writer.writeAttribute("colspan", colspan, null);
        if(rowspan != 1) writer.writeAttribute("rowspan", rowspan, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);

        if(selectionEnabled) {
            encodeColumnSelection(context, table, clientId, column, selected);
        }

        column.renderChildren(context);       

        writer.endElement("td");
    }
    
    protected void encodeTFoot(FacesContext context, DataTable table) throws IOException {
        this.encodeTFoot(context, table, 0, table.getColumns().size(), null, null);
    }

    protected void encodeTFoot(FacesContext context, DataTable table, int columnStart, int columnEnd, String tfootId, String columnGroupType) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();
        String tfootClientId = (tfootId == null) ? table.getClientId(context) + "_foot" : tfootId;
        String colGroupType = (columnGroupType == null) ? "footer" : columnGroupType;
        ColumnGroup group = table.getColumnGroup(colGroupType);
        boolean hasFooterColumn = table.hasFooterColumn();
        boolean shouldRenderFooter = (hasFooterColumn || group != null); 

        if(!shouldRenderFooter) {
            return;
        }

        writer.startElement("tfoot", null);
        writer.writeAttribute("id", tfootClientId, null);

        if(group != null && group.isRendered()) {
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof Row) {
                        Row footerRow = (Row) child;
                        String rowClass = footerRow.getStyleClass();
                        String rowStyle = footerRow.getStyle();
                        
                        writer.startElement("tr", null);
                        if(rowClass != null) writer.writeAttribute("class", rowClass, null);
                        if(rowStyle != null) writer.writeAttribute("style", rowStyle, null);
                        
                        for(UIComponent footerRowChild : footerRow.getChildren()) {
                            if(footerRowChild.isRendered()) {
                                if(footerRowChild instanceof Column) {
                                    encodeColumnFooter(context, table, (Column) footerRowChild);
                                }
                                else if(footerRowChild instanceof Columns) {
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
        else if(table.hasFooterColumn()) {
            writer.startElement("tr", null);
            
            for(int i = columnStart; i < columnEnd; i++) {
                UIColumn column = columns.get(i);

                if(column instanceof Column) {
                    encodeColumnFooter(context, table, column);
                }
                else if(column instanceof DynamicColumn) {
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
        if(facet == null)
            return;
        
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
        if(value != null) {
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

    protected void encodeColumnSelection(FacesContext context, DataTable table, String clientId, UIColumn column, boolean selected) throws IOException {
        String selectionMode = column.getSelectionMode();
        boolean disabled = table.isDisabledSelection();

        if(selectionMode.equalsIgnoreCase("single")) {
            encodeRadio(context, table, selected, disabled);
        } 
        else if(selectionMode.equalsIgnoreCase("multiple")) {
            encodeCheckbox(context, table, selected, disabled, HTML.CHECKBOX_CLASS);
        } 
        else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }
    }
    
    protected void encodeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(table.isNativeElements()) {
            encodeNativeRadio(context, table, checked, disabled);
        }
        else {
            String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
            String iconClass = checked ? HTML.RADIOBUTTON_CHECKED_ICON_CLASS: HTML.RADIOBUTTON_UNCHECKED_ICON_CLASS;
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

    protected void encodeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(table.isNativeElements()) {
            encodeNativeCheckbox(context, table, checked, disabled);
        }
        else {
            String boxClass = HTML.CHECKBOX_BOX_CLASS;
            boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
            boxClass = checked ? boxClass + " ui-state-active" : boxClass;
            String iconClass = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, "styleClass");

            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
            encodeNativeCheckbox(context, table, checked, disabled);
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
    
    protected void encodeNativeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("name", table.getClientId(context) + "_checkbox", null);
        
        if(checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        
        if(disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        
        writer.endElement("input");
    }
    
    protected void encodeNativeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("name", table.getClientId(context) + "_radio", null);
        
        if(checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        
        if(disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        
        writer.endElement("input");
    }
        
    protected void encodeSubTable(FacesContext context, DataTable table, SubTable subTable, int first, int last) throws IOException {
        for(int i = first; i < last; i++) {
            table.setRowIndex(i);
            if(!table.isRowAvailable()) {
                break;
            }
            
            subTable.encodeAll(context);
        }
    }
    
    boolean isInSameGroup(FacesContext context, DataTable table, int currentRowIndex, ValueExpression groupByVE, ELContext eLContext) {
        table.setRowIndex(currentRowIndex); 
        Object currentGroupByData = groupByVE.getValue(eLContext);

        table.setRowIndex(currentRowIndex + 1);
        if(!table.isRowAvailable())
            return false;
        
        Object nextGroupByData = groupByVE.getValue(eLContext);
        if(currentGroupByData != null && nextGroupByData.equals(currentGroupByData)) {
            return true;
        }
        
        return false;
    }
}