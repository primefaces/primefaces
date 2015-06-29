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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.row.Row;
import org.primefaces.mobile.renderkit.paginator.PaginatorRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.WidgetBuilder;

public class DataTableRenderer extends org.primefaces.component.datatable.DataTableRenderer {
 
    @Override
    protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		String clientId = table.getClientId(context);        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataTable", table.resolveWidgetVar(), clientId);
                
        wb.attr("selectionMode", table.getSelectionMode(), null);
        
        if(table.isPaginator()) {
            PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
            paginatorRenderer.encodeScript(context, table, wb);
        }
        
        encodeClientBehaviors(context, table);

        wb.finish();
	}
    
    @Override
    protected void encodeMarkup(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        String style = table.getStyle();
        String defaultStyleClass = DataTable.MOBILE_CONTAINER_CLASS;
        String styleClass = table.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass: defaultStyleClass + " " + styleClass;
        boolean hasPaginator = table.isPaginator();
        String paginatorPosition = table.getPaginatorPosition();
        PaginatorRenderer paginatorRenderer = getPaginatorRenderer(context);
        
        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            paginatorRenderer.encodeMarkup(context, table, "top");
        }
        
        encodeRegularTable(context, table);
        
        renderDynamicPassThruAttributes(context, table);
        
        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            paginatorRenderer.encodeMarkup(context, table, "bottom");
        }
        
        if(table.isSelectionEnabled()) {
            encodeStateHolder(context, table, table.getClientId(context) + "_selection", table.getSelectedRowKeysAsString());
        }
        
        writer.endElement("div");
    }
    
    @Override
    protected void encodeRegularTable(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = table.getTableStyleClass();
        styleClass = (styleClass == null) ? DataTable.MOBILE_TABLE_CLASS : DataTable.MOBILE_TABLE_CLASS + " " + styleClass;
        if(table.isReflow()) {
            styleClass = styleClass + " ui-table-reflow";
        }           
        
        writer.startElement("table", null);
        writer.writeAttribute("role", "grid", null);
        writer.writeAttribute("class", styleClass, null);
        if(table.getTableStyle() != null) writer.writeAttribute("style", table.getTableStyle(), null);
        if(table.getSummary() != null) writer.writeAttribute("summary", table.getSummary(), null);
        
        encodeThead(context, table);
        encodeTFoot(context, table);
        encodeTbody(context, table, false);
        
        writer.endElement("table");
    }
    
    @Override
    protected void encodeThead(FacesContext context, DataTable table, int columnStart, int columnEnd, String theadId, String columnGroupName) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = table.getColumnGroup("header");
        List<UIColumn> columns = table.getColumns();
        String theadClientId = (theadId == null) ? table.getClientId(context) + "_head" : theadId;
        
        writer.startElement("thead", null);
        writer.writeAttribute("id", theadClientId, null);
        
        if(group != null && group.isRendered()) {
            context.getAttributes().put(Constants.HELPER_RENDERER, "columnGroup");

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof Row) {
                        Row headerRow = (Row) child;

                        writer.startElement("tr", null);
                        writer.writeAttribute("class", "ui-bar-a", null);
                        
                        for(UIComponent headerRowChild: headerRow.getChildren()) {
                            if(headerRowChild.isRendered()) {
                                if(headerRowChild instanceof Column)
                                    encodeColumnHeader(context, table, (Column) headerRowChild);
                                else
                                    headerRowChild.encodeAll(context);
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
            writer.writeAttribute("class", "ui-bar-a", null);
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
        
        writer.endElement("thead");
    }
    
    @Override
    public void encodeColumnHeader(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getContainerClientId(context);
        ValueExpression columnSortByVE = column.getValueExpression("sortBy");
        int priority = column.getPriority();
        boolean sortable = (columnSortByVE != null);
        String sortIcon = null;
        String defaultStyleClass = sortable ? DataTable.MOBILE_COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.MOBILE_COLUMN_HEADER_CLASS; 
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass: defaultStyleClass + " " + styleClass;
              
        if(priority > 0) {
            styleClass = styleClass + " ui-table-priority-" + priority;
        }
        
        if(sortable) {
            ValueExpression tableSortByVE = table.getValueExpression("sortBy");
            boolean defaultSorted = (tableSortByVE != null);
                    
            if(defaultSorted) {
                 sortIcon = resolveDefaultSortIcon(table, column, table.getSortOrder());
            }
            
            if(sortIcon == null) {
                sortIcon = DataTable.MOBILE_SORT_ICON_CLASS;
            }
            else {
                styleClass = styleClass + " " + DataTable.MOBILE_SORTED_COLUMN_CLASS;;
            }
        }
        
        writer.startElement("th", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("role", "columnheader", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);
                
        encodeColumnHeaderContent(context, column, sortIcon);
        
        writer.endElement("th");
    }
    
    @Override
    public void encodeColumnFooter(FacesContext context, DataTable table, UIColumn column) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();

        writer.startElement("td", null);
        
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);
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
    
    @Override
    public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly, int columnStart, int columnEnd, String tbodyId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);
        String emptyMessage = table.getEmptyMessage();
        UIComponent emptyFacet = table.getFacet("emptyMessage");
        String tbodyClientId = (tbodyId == null) ? clientId + "_data" : tbodyId;
                       
        if(table.isSelectionEnabled()) {
            table.findSelectedRowKeys();
        }
        
        int rows = table.getRows();
		int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rowCountToRender = rows == 0 ? rowCount : rows;
        boolean hasData = rowCount > 0;
                      
        if(!dataOnly) {
            writer.startElement("tbody", null);
            writer.writeAttribute("id", tbodyClientId, null);
        }

        if(hasData) {
            encodeRows(context, table, first, (first + rowCountToRender), columnStart, columnEnd);
        }
        else {
            //Empty message
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.EMPTY_MESSAGE_ROW_CLASS, null);

            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumnsCount(), null);
            
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
    
    @Override
    protected void encodeRows(FacesContext context, DataTable table, int first, int last, int columnStart, int columnEnd) throws IOException {
        String clientId = table.getClientId(context);
        
        for(int i = first; i < last; i++) {
            table.setRowIndex(i);
            if(!table.isRowAvailable()) {
                break;
            }

            encodeRow(context, table, clientId, i, columnStart, columnEnd);
        }
    }
    
    @Override
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
        String rowStyleClass = DataTable.MOBILE_ROW_CLASS;
        if(selectionEnabled && !table.isDisabledSelection()) {
            rowStyleClass = rowStyleClass + " " + DataTable.SELECTABLE_ROW_CLASS;
        }
            
        if(selected) {
            rowStyleClass = rowStyleClass + " ui-bar-b";
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
        
        for(int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);
            
            if(column instanceof Column) {
                encodeCell(context, table, column, clientId, false);
            }
            else if(column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn, null, false);
            }
        }

        writer.endElement("tr");

        return true;
    }
    
    @Override
    protected void encodeCell(FacesContext context, DataTable table, UIColumn column, String clientId, boolean selected) throws IOException {
        if(!column.isRendered()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        int colspan = column.getColspan();
        int rowspan = column.getRowspan();
        int priority = column.getPriority();
        
        if(priority > 0) {
            styleClass = (styleClass == null) ? "ui-table-priority-" + priority : styleClass + " ui-table-priority-" + priority;
        }
        
        writer.startElement("td", null);
        writer.writeAttribute("role", "gridcell", null);
        if(colspan != 1) writer.writeAttribute("colspan", colspan, null);
        if(rowspan != 1) writer.writeAttribute("rowspan", rowspan, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(styleClass != null) writer.writeAttribute("class", styleClass, null);

        if(table.isReflow()) {
            writer.startElement("b", table);
            writer.writeAttribute("class", DataTable.MOBILE_CELL_LABEL, null);
            String headerText = column.getHeaderText();
            if (!ComponentUtils.isValueBlank(headerText)) {
                writer.writeText(headerText, null);
            }
            writer.endElement("b");
        }
        
        column.renderChildren(context);       

        writer.endElement("td");
    }
    
    @Override
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
                sortIcon = DataTable.MOBILE_SORT_ICON_ASC_CLASS;
            else if(sortOrder.equalsIgnoreCase("DESCENDING"))
                sortIcon = DataTable.MOBILE_SORT_ICON_DESC_CLASS;
        }
        
        return sortIcon;
    }
    
    private PaginatorRenderer getPaginatorRenderer(FacesContext context) {
        return (PaginatorRenderer) context.getRenderKit().getRenderer("org.primefaces.component", "org.primefaces.component.PaginatorRenderer");
    }
}
