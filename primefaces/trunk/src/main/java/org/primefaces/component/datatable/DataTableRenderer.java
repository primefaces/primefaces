/*
 * Copyright 2009-2011 Prime Technology.
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
import java.util.Map;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.paginator.PaginatorElementRenderer;
import org.primefaces.component.row.Row;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.model.SortOrder;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.HTML;

public class DataTableRenderer extends DataRenderer {

    protected DataHelper dataHelper;

    public DataTableRenderer() {
        super();
        dataHelper = new DataHelper();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        DataTable table = (DataTable) component;
        boolean isSortRequest = table.isSortRequest(context);

        if(table.isFilteringEnabled()) {
            dataHelper.decodeFilters(context, table);
            
            if(!isSortRequest && table.getValueExpression("sortBy") != null && !table.isLazy()) {
                sort(context, table);
            }
        }

        if(table.isSelectionEnabled()) {
            dataHelper.decodeSelection(context, table);
        }

        if(table.isPaginationRequest(context)) {
            dataHelper.decodePageRequest(context, table);
        } 
        else if(isSortRequest) {
            dataHelper.decodeSortRequest(context, table);
        }

        decodeBehaviors(context, component);

        if(table.isPaginator()) {
            updatePaginationMetadata(context, table);
        }
    }
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;

        if(table.isBodyUpdate(context)) {
            encodeTbody(context, table);
        } 
        else if(table.isRowExpansionRequest(context)) {
            encodeRowExpansion(context, table);
        }
        else if(table.isRowEditRequest(context)) {
            encodeEditedRow(context, table);
        }
        else if(table.isScrollingRequest(context)) {
            encodeLiveRows(context, table);
        }
        else {
            encodeMarkup(context, table);
            encodeScript(context, table);
        }
	}
	
	protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);

        startScript(writer, clientId);
        
        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('DataTable','" + table.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        //Pagination
        if(table.isPaginator()) {
            encodePaginatorConfig(context, table);
        }

        //Selection
        if(table.isRowSelectionEnabled()) {
            encodeSelectionConfig(context, table);
        }

        if(table.isColumnSelectionEnabled()) {
            writer.write(",columnSelectionMode:'" + table.getColumnSelectionMode() + "'");
        }
        
        //Filtering
        if(table.isFilteringEnabled()) {
            writer.write(",filtering:true");
            writer.write(",filterEvent:'" + table.getFilterEvent() + "'");
        }

        //Row expansion
        if(table.getRowExpansion() != null) {
            writer.write(",expansion:true");
            if(table.getOnExpandStart() != null) {
                writer.write(",onExpandStart:function(row) {" + table.getOnExpandStart() + "}");
            }
        }

        //Scrolling
        if(table.isScrollable()) {
            writer.write(",scrollable:true");
            writer.write(",liveScroll:" + table.isLiveScroll());
            writer.write(",scrollStep:" + table.getScrollRows());
            writer.write(",scrollLimit:" + table.getRowCount());
            
            if(table.getScrollWidth() != Integer.MIN_VALUE)
                writer.write(",scrollWidth:" + table.getScrollWidth());
        }

        //Resizable Columns
        if(table.isResizableColumns()) {
            writer.write(",resizableColumns:true");
        }

        //Behaviors
        encodeClientBehaviors(context, table);

        writer.write("});});");

		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        boolean scrollable = table.isScrollable();
        boolean hasPaginator = table.isPaginator();
        
        if(table.isLazy()) {
            table.loadLazyData();
        }

        //style
        String containerClass = scrollable ? DataTable.CONTAINER_CLASS + " " + DataTable.SCROLLABLE_CONTAINER_CLASS : DataTable.CONTAINER_CLASS;
        containerClass = table.getStyleClass() != null ? containerClass + " " + table.getStyleClass() : containerClass;
        String style = null;
        
        if(table.isResizableColumns()) {
            containerClass = containerClass + " " + DataTable.RESIZABLE_CONTAINER_CLASS; 
        }
        
        //default sort
        if(!isPostBack() && table.getValueExpression("sortBy") != null && !table.isLazy()) {
            sort(context, table);
        }

        if(hasPaginator) {
            table.calculatePage();
        }

        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, "styleClass");
        if((style = table.getStyle()) != null) {
            writer.writeAttribute("style", style, "style");
        }

        if(scrollable) {
            encodeScrollableTable(context, table);
        } else {
            encodeRegularTable(context, table);
        }

        if(table.isSelectionEnabled()) {
            encodeSelectionHolder(context, table);
        }

        writer.endElement("div");
	}

    protected void encodeRegularTable(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("table", null);
        if(table.getTableStyle() != null) writer.writeAttribute("style", table.getTableStyle(), null);
        if(table.getTableStyleClass() != null) writer.writeAttribute("class", table.getTableStyleClass(), null);
        
        encodeThead(context, table);
        encodeTFoot(context, table);
        encodeTbody(context, table);
        writer.endElement("table");
    }

    protected void encodeScrollableTable(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int scrollHeight = table.getScrollHeight();        
        String tableStyle = table.getStyle();
        String tableStyleClass = table.getStyleClass();
                
        //header
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_HEADER_CLASS, null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_HEADER_BOX_CLASS, null);
        
        writer.startElement("table", null);
        if(tableStyle != null) writer.writeAttribute("style", tableStyle, null);
        if(tableStyleClass != null) writer.writeAttribute("class", tableStyleClass, null);
        
        encodeThead(context, table);
        writer.endElement("table");
        
        writer.endElement("div");
        writer.endElement("div");

        //body
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_BODY_CLASS, null);
        if(scrollHeight != Integer.MIN_VALUE) {
            writer.writeAttribute("style", "height:" + scrollHeight + "px", null);
        }
        writer.startElement("table", null);
        if(table.getRowCount() == 0) {
            tableStyle = tableStyle == null ? "width:100%" : tableStyle + ";width:100%";
        }
        
        if(tableStyle != null) writer.writeAttribute("style", tableStyle, null);
        if(table.getTableStyleClass() != null) writer.writeAttribute("class", tableStyleClass, null);
        
        encodeTbody(context, table);
        writer.endElement("table");
        writer.endElement("div");

        //footer
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_FOOTER_CLASS, null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.SCROLLABLE_FOOTER_BOX_CLASS, null);
        
        writer.startElement("table", null);
        if(tableStyle != null) writer.writeAttribute("style", tableStyle, null);
        if(tableStyleClass != null) writer.writeAttribute("class", tableStyleClass, null);
        
        encodeTFoot(context, table);
        writer.endElement("table");
        
        writer.endElement("div");
        
        writer.endElement("div");
    }

    protected void encodeColumnHeader(FacesContext context, DataTable table, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getClientId(context);
        ValueExpression tableSortByVe = table.getValueExpression("sortBy");
        ValueExpression columnSortByVe = column.getValueExpression("sortBy");
        boolean isSortable = columnSortByVe != null;
        boolean hasFilter = column.getValueExpression("filterBy") != null;
        String selectionMode = column.getSelectionMode();
        String sortIcon = isSortable ? DataTable.SORTABLE_COLUMN_ICON_CLASS : null;
        
        String columnClass = isSortable ? DataTable.COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.COLUMN_HEADER_CLASS;
        columnClass = hasFilter ? columnClass + " " + DataTable.FILTER_COLUMN_CLASS : columnClass;
        columnClass = selectionMode != null ? columnClass + " " + DataTable.SELECTION_COLUMN_CLASS : columnClass;
        columnClass = column.getStyleClass() != null ? columnClass + " " + column.getStyleClass() : columnClass;

        if(isSortable) {
            String columnSortByExpression = columnSortByVe.getExpressionString();
            
            if(tableSortByVe != null) {
                String tableSortByExpression = tableSortByVe.getExpressionString();

                if(tableSortByExpression != null && tableSortByExpression.equals(columnSortByExpression)) {
                    String sortOrder = table.getSortOrder().toUpperCase();

                    if(sortOrder.equals("ASCENDING"))
                        sortIcon = DataTable.SORTABLE_COLUMN_ASCENDING_ICON_CLASS;
                    else if(sortOrder.equals("DESCENDING"))
                        sortIcon = DataTable.SORTABLE_COLUMN_DESCENDING_ICON_CLASS;

                    columnClass = columnClass + " ui-state-active";
                }
            }
        }
        
        writer.startElement("th", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", columnClass, null);
        
        if(column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);
        
        //column content wrapper
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER , null);

        if(selectionMode != null && selectionMode.equalsIgnoreCase("multiple")) {
            encodeCheckbox(context, table, false, column.isDisabledSelection(), HTML.CHECKBOX_ALL_CLASS);
        }
        else {
            if(hasFilter) {
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
        }
        
        writer.endElement("div");

        writer.endElement("th");
    }
    
    protected void encodeColumnHeaderContent(FacesContext context, Column column, String sortIcon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        if(sortIcon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", sortIcon, null);
            writer.endElement("span");
        }
                
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();
        
        writer.startElement("span", null);
        
        if(header != null)
            header.encodeAll(context);
        else if(headerText != null)
            writer.write(headerText);
        
        writer.endElement("span");
    }

    protected void encodeColumnsHeader(FacesContext context, DataTable table, Columns columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String columnVar = columns.getVar();
        String columnIndexVar = columns.getColumnIndexVar();
        int colIndex = 0;
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        for(Object column : (Collection) columns.getValue()) {
            requestMap.put(columnVar, column);
            requestMap.put(columnIndexVar, colIndex);
            UIComponent header = columns.getFacet("header");
            String style = columns.getStyle();
            String styleClass = columns.getStyleClass() == null ? DataTable.COLUMN_HEADER_CLASS : DataTable.COLUMN_HEADER_CLASS + " " + columns.getStyleClass();
            
            writer.startElement("th", null);
            writer.writeAttribute("class", styleClass, null);
            if(style != null) {
                writer.writeAttribute("style", style, null);
            }
            
            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
            
            if(header != null) {
                header.encodeAll(context);
            }
            
            writer.endElement("div");

            writer.endElement("th");
            
            colIndex++;
        }

        context.getExternalContext().getRequestMap().remove(columnVar);
    }

    protected void encodeFilter(FacesContext context, DataTable table, Column column) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();

        String filterId = column.getClientId(context) + "_filter";
        String filterValue = params.containsKey(filterId) ? params.get(filterId) : "";
        String filterStyleClass = column.getFilterStyleClass();
        

        if(column.getValueExpression("filterOptions") == null) {
            filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_INPUT_FILTER_CLASS : DataTable.COLUMN_INPUT_FILTER_CLASS + " " + filterStyleClass;
            
            writer.startElement("input", null);
            writer.writeAttribute("id", filterId, null);
            writer.writeAttribute("name", filterId, null);
            writer.writeAttribute("class", filterStyleClass, null);
            writer.writeAttribute("value", filterValue , null);
            writer.writeAttribute("autocomplete", "off", null);

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

            SelectItem[] itemsArray = (SelectItem[]) getFilterOptions(column);

            for(SelectItem item : itemsArray) {
                Object itemValue = item.getValue();
                
                writer.startElement("option", null);
                writer.writeAttribute("value", item.getValue(), null);
                if(itemValue != null && String.valueOf(itemValue).equals(filterValue)) {
                    writer.writeAttribute("selected", "selected", null);
                }
                writer.write(item.getLabel());
                writer.endElement("option");
            }

            writer.endElement("select");
        }
        
    }

    protected SelectItem[] getFilterOptions(Column column) {
        Object options = column.getFilterOptions();
        
        if(options instanceof SelectItem[]) {
            return (SelectItem[]) options;
        } else if(options instanceof Collection<?>) {
            return ((Collection<SelectItem>) column.getFilterOptions()).toArray(new SelectItem[] {});
        } else {
            throw new FacesException("Filter options for column " + column.getClientId() + " should be a SelectItem array or collection");
        }
    }

    protected void encodeColumnFooter(FacesContext context, DataTable table, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = styleClass == null ? DataTable.COLUMN_FOOTER_CLASS : DataTable.COLUMN_FOOTER_CLASS + " " + styleClass;

        writer.startElement("td", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);

        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
        
        //Footer content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if(facet != null) {
            facet.encodeAll(context);
        } else if(text != null) {
            writer.write(text);
        }
        
        writer.endElement("div");

        writer.endElement("td");
    }

    /**
     * Render column headers either in single row or nested if a columnGroup is defined
     */
    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = table.getColumnGroup("header");

        writer.startElement("thead", null);
        
        encodeFacet(context, table, table.getHeader(), DataTable.HEADER_CLASS, "th");
        
        if(table.isPaginator() && !table.getPaginatorPosition().equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, table, "top", "th", org.primefaces.component.api.UIData.PAGINATOR_TOP_CONTAINER_CLASS);
        }

        if(group != null && group.isRendered()) {

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered() && child instanceof Row) {
                    Row headerRow = (Row) child;

                    writer.startElement("tr", null);

                    for(UIComponent headerRowChild : headerRow.getChildren()) {
                        if(headerRowChild.isRendered() && headerRowChild instanceof Column) {
                            encodeColumnHeader(context, table, (Column) headerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }

        } else {
            
            writer.startElement("tr", null);

            for(UIComponent kid : table.getChildren()) {
                if(kid.isRendered()) {
                    if(kid instanceof Column) {
                        encodeColumnHeader(context, table, (Column) kid);
                    }
                    else if(kid instanceof Columns) {
                        encodeColumnsHeader(context, table, (Columns) kid);
                    }
                }
            }

            writer.endElement("tr");
        }

        writer.endElement("thead");
    }

    protected void encodeTbody(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);
        String emptyMessage = table.getEmptyMessage();
        SubTable subTable = table.getSubTable();
        SummaryRow summaryRow = table.getSummaryRow();
                
        if(table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }
        
        int rows = table.getRows();
		int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rowCountToRender = rows == 0 ? (table.isLiveScroll() ? table.getScrollRows() : rowCount) : rows;
        boolean hasData = rowCount > 0;

        String tbodyClass = hasData ? DataTable.DATA_CLASS : DataTable.EMPTY_DATA_CLASS;
      
        writer.startElement("tbody", null);
        writer.writeAttribute("id", clientId + "_data", null);
        writer.writeAttribute("class", tbodyClass, null);

        if(hasData) {
            for(int i = first; i < (first + rowCountToRender); i++) {
                if(subTable != null) {
                    encodeSubTable(context, table, subTable, i, rowIndexVar);
                }
                else {

                    table.setRowIndex(i);
                    if(!table.isRowAvailable()) {
                        break;
                    }
                    
                    encodeRow(context, table, clientId, i, rowIndexVar);
                    
                    if(summaryRow != null && !dataHelper.isInSameGroup(context, table, i)) {
                        table.setRowIndex(i);   //restore
                        encodeSummaryRow(context, table, summaryRow);
                    }
                }
            }
        }
        else if(emptyMessage != null){
            //Empty message
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumns().size(), null);
            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
            writer.write(emptyMessage);
            writer.endElement("div");
            writer.endElement("td");
            
            writer.endElement("tr");
        }
		
        writer.endElement("tbody");

		//Cleanup
		table.setRowIndex(-1);
		if(rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
    }
    
    private void encodeSummaryRow(FacesContext context, DataTable table, SummaryRow summaryRow) throws IOException{
        MethodExpression me = summaryRow.getListener();
        if(me != null) {
            me.invoke(context.getELContext(), new Object[]{table.getSortBy()});
        }
        
        summaryRow.encodeAll(context);
    }

    protected boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex, String rowIndexVar) throws IOException {
        //Row index var
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().put(rowIndexVar, rowIndex);
        }
        
        Object rowKey = null;
        if(table.isSelectionEnabled()) {
            //try rowKey attribute
            rowKey = table.getRowKey();
            
            //ask selectable datamodel
            if(rowKey == null)
                rowKey = table.getRowKeyFromModel(table.getRowData());
        }

        //Preselection
        boolean selected = table.getSelectedRowKeys().contains(rowKey);

        ResponseWriter writer = context.getResponseWriter();

        String userRowStyleClass = table.getRowStyleClass();
        String rowStyleClass = rowIndex % 2 == 0 ? DataTable.ROW_CLASS + " " + DataTable.EVEN_ROW_CLASS : DataTable.ROW_CLASS + " " + DataTable.ODD_ROW_CLASS;
        
        if(selected) {
            rowStyleClass = rowStyleClass + " ui-state-highlight";
        }

        if(userRowStyleClass != null) {
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;
        }

        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        if(rowKey != null) {
            writer.writeAttribute("data-rk", rowKey, null);
        }
        writer.writeAttribute("class", rowStyleClass, null);

        for(UIComponent kid : table.getChildren()) {
            if(kid.isRendered()) {
                if(kid instanceof Column) {
                    encodeRegularCell(context, table, (Column) kid, clientId, selected);
                }
                else if(kid instanceof Columns) {
                    encodeDynamicCell(context, table, (Columns) kid);
                }
            }
        }

        writer.endElement("tr");

        return true;
    }

    protected void encodeRegularCell(FacesContext context, DataTable table, Column column, String clientId, boolean selected) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();

        writer.startElement("td", null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);

        if(column.getSelectionMode() != null) {
            writer.writeAttribute("class", DataTable.SELECTION_COLUMN_CLASS , null);
            
            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
            encodeColumnSelection(context, table, clientId, column, selected);
            writer.endElement("div");
        }
        else {
            CellEditor editor = column.getCellEditor();
            if(editor != null) {
                writer.writeAttribute("class", DataTable.EDITABLE_COLUMN_CLASS , null);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
            column.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("td");
    }

    protected void encodeDynamicCell(FacesContext context, DataTable table, Columns columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String columnVar = columns.getVar();
        String columnIndexVar = columns.getColumnIndexVar();
        int colIndex = 0;
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        for(Object column : (Collection) columns.getValue()) {
            requestMap.put(columnVar, column);
            requestMap.put(columnIndexVar, colIndex);

            writer.startElement("td", null);
            writer.startElement("div", null);
            writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
            columns.encodeAll(context);
            writer.endElement("div");
            writer.endElement("td");

            colIndex++;
        }

        context.getExternalContext().getRequestMap().remove(columnVar);
        context.getExternalContext().getRequestMap().remove(columnIndexVar);
    }

    protected void encodeTFoot(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = table.getColumnGroup("footer");

        writer.startElement("tfoot", null);

        if(group != null && group.isRendered()) {

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered() && child instanceof Row) {
                    Row footerRow = (Row) child;

                    writer.startElement("tr", null);

                    for(UIComponent footerRowChild : footerRow.getChildren()) {
                        if(footerRowChild.isRendered() && footerRowChild instanceof Column) {
                            encodeColumnFooter(context, table, (Column) footerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }

        }
        else if(table.hasFooterColumn()) {
            writer.startElement("tr", null);

            for(Column column : table.getColumns()) {
                encodeColumnFooter(context, table, column);
            }

            writer.endElement("tr");
        }
        
        if(table.isPaginator() && !table.getPaginatorPosition().equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, table, "bottom", "td", org.primefaces.component.api.UIData.PAGINATOR_BOTTOM_CONTAINER_CLASS);
        }
 
        encodeFacet(context, table, table.getFooter(), DataTable.FOOTER_CLASS, "td");
        
        writer.endElement("tfoot");
    }

    protected void encodeFacet(FacesContext context, DataTable table, UIComponent facet, String styleClass, String tag) throws IOException {
        if(facet == null)
            return;
        
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("tr", null);
        writer.startElement(tag, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("colspan", table.getColumnsCount(), null);

        facet.encodeAll(context);
        
        writer.endElement(tag);
        writer.endElement("tr");
    }
    
    protected void encodePaginatorMarkup(FacesContext context, DataTable table, String position, String tag, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if(!table.isPaginatorAlwaysVisible() && table.getPageCount() <= 1) {
            return;
        }
        
        String id = table.getClientId(context) + "_paginator_" + position; 

        writer.startElement("tr", null);
        writer.startElement(tag, null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("colspan", table.getColumnsCount(), null);
                      
        Pattern pattern = Pattern.compile("\\{([^\\{]+?)\\}");
        Matcher matcher = pattern.matcher(table.getPaginatorTemplate());
        
        while(matcher.find()) {
            String key = matcher.group(1);
            
            PaginatorElementRenderer renderer = paginatorElements.get(key);
            if(renderer != null) {
                renderer.render(context, table);
            }
        }
        
        writer.endElement(tag);
        writer.endElement("tr");
    }

    protected void encodeSelectionConfig(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write(",selectionMode:'" + table.getSelectionMode() + "'");

        if(table.isDblClickSelect()) {
            writer.write(",dblclickSelect:true");
        }
    }

    protected void encodeSelectionHolder(FacesContext context, DataTable table) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String id = table.getClientId(context) + "_selection";

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
        writer.writeAttribute("value", table.getSelectedRowKeysAsString(), null);
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

    protected void encodeRowEditor(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String widgetVar = table.resolveWidgetVar();

        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.ROW_EDITOR_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-pencil", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-check", null);
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-close", null);
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("span");

        writer.endElement("span");
    }

    protected void encodeRowExpansion(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int expandedRowIndex = Integer.parseInt(params.get(table.getClientId(context) + "_expandedRowIndex"));

        table.setRowIndex(expandedRowIndex);

        writer.startElement("tr", null);
        writer.writeAttribute("style", "display:none", null);
        writer.writeAttribute("class", DataTable.EXPANDED_ROW_CONTENT_CLASS + " ui-widget-content", null);

        writer.startElement("td", null);
        writer.writeAttribute("colspan", table.getColumnsCount(), null);

        table.getRowExpansion().encodeAll(context);

        writer.endElement("td");

        writer.endElement("tr");

        table.setRowIndex(-1);
    }

    protected void encodeColumnSelection(FacesContext context, DataTable table, String clientId, Column column, boolean selected) throws IOException {
        String selectionMode = column.getSelectionMode();
        boolean disabled = column.isDisabledSelection();

        if(selectionMode.equalsIgnoreCase("single")) {
            encodeRadio(context, table, selected, disabled);
        } else if(selectionMode.equalsIgnoreCase("multiple")) {
            encodeCheckbox(context, table, selected, disabled, HTML.CHECKBOX_CLASS);
        } else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }

    }
    
    protected void encodeRadio(FacesContext context, DataTable table, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.RADIOBUTTON_BOX_CLASS;
        String iconClass = HTML.RADIOBUTTON_ICON_CLASS;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = checked ? boxClass + " ui-state-active" : boxClass;
        iconClass = checked ? iconClass + " " + HTML.RADIOBUTTON_CHECKED_ICON_CLASS : iconClass;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.RADIOBUTTON_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeCheckbox(FacesContext context, DataTable table, boolean checked, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String boxClass = HTML.CHECKBOX_BOX_CLASS;
        String iconClass = HTML.CHECKBOX_ICON_CLASS;
        boxClass = disabled ? boxClass + " ui-state-disabled" : boxClass;
        boxClass = checked ? boxClass + " ui-state-active" : boxClass;
        iconClass = checked ? iconClass + " " + HTML.CHECKBOX_CHECKED_ICON_CLASS : iconClass;

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, "styleClass");
        
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    protected void encodeEditedRow(FacesContext context, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int editedRowId = Integer.parseInt(params.get(table.getClientId(context) + "_editedRowIndex"));

        table.setRowIndex(editedRowId);
        if(table.isRowAvailable()) {
            encodeRow(context, table, table.getClientId(context), editedRowId, table.getRowIndexVar());
        }
    }

    protected void encodeLiveRows(FacesContext context, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int scrollOffset = Integer.parseInt(params.get(table.getClientId(context) + "_scrollOffset"));
        String clientId = table.getClientId(context);
        String rowIndexVar = table.getRowIndexVar();

        for(int i = scrollOffset; i < (scrollOffset + table.getScrollRows()); i++) {
            table.setRowIndex(i);
            if(table.isRowAvailable()) {
                encodeRow(context, table, clientId, i, rowIndexVar);
            }
        }
    }
    
    protected void updatePaginationMetadata(FacesContext context, DataTable table) {
        ELContext elContext = context.getELContext();
        ValueExpression firstVe = table.getValueExpression("first");
        ValueExpression rowsVe = table.getValueExpression("rows");
        ValueExpression pageVE = table.getValueExpression("page");

        if(firstVe != null && !firstVe.isReadOnly(elContext))
            firstVe.setValue(context.getELContext(), table.getFirst());
        if(rowsVe != null && !rowsVe.isReadOnly(elContext))
            rowsVe.setValue(context.getELContext(), table.getRows());
        if(pageVE != null && !pageVE.isReadOnly(elContext))
            pageVE.setValue(context.getELContext(), table.getPage());
    }

    protected void sort(FacesContext context, DataTable table) {
        dataHelper.sort(context, table, table.getValueExpression("sortBy"), table.getVar(), SortOrder.valueOf(table.getSortOrder().toUpperCase(Locale.ENGLISH)), null);
    }

    protected void encodeSubTable(FacesContext context, DataTable table, SubTable subTable, int rowIndex, String rowIndexVar) throws IOException {
        table.setRowIndex(rowIndex);
        if(!table.isRowAvailable()) {
            return;
        }

        //Row index var
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().put(rowIndexVar, rowIndex);
        }
        
        subTable.encodeAll(context);
        
        if(rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
    }
}