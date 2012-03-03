/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.datagrid;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.DataRenderer;

public class DataGridRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        DataGrid grid = (DataGrid) component;
        String clientId = grid.getClientId();

        if(grid.isPagingRequest(context)) {
            grid.setFirst(Integer.valueOf(params.get(clientId + "_first")));
            grid.setRows(Integer.valueOf(params.get(clientId + "_rows")));
            
            if(grid.isLazy()) {
                grid.loadLazyData();
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataGrid grid = (DataGrid) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if(grid.isPagingRequest(context)) {      
            encodeTable(context, grid);
        } 
        else {
            encodeMarkup(context, grid);
            encodeScript(context, grid);
        }
    }

    protected void encodeMarkup(FacesContext context, DataGrid grid) throws IOException {
        if(grid.isLazy()) {
            grid.loadLazyData();
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId();
        boolean hasPaginator = grid.isPaginator();
        boolean empty = grid.getRowCount() == 0;
        String paginatorPosition = grid.getPaginatorPosition();
        String styleClass = grid.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + grid.getStyleClass();
        String contentClass = empty ? DataGrid.EMPTY_CONTENT_CLASS : DataGrid.CONTENT_CLASS;

        if(hasPaginator) {
            grid.calculatePage();
        }
        
        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, grid, "top");
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if(empty) {
            writer.write(grid.getEmptyMessage());
        } 
        else {
            encodeTable(context, grid);
        }
        
        writer.endElement("div");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, grid, "bottom");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId();

        startScript(writer, clientId);

        writer.write("$(function() { ");
        
        writer.write("PrimeFaces.cw('DataGrid','" + grid.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        //Pagination
        if(grid.isPaginator()) {
            encodePaginatorConfig(context, grid);
        }
        
        writer.write("});});");

        endScript(writer);
    }

    protected void encodeTable(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
                
        int columns = grid.getColumns();
        int rowIndex = grid.getFirst();
        int rows = grid.getRows();
        int itemsToRender = rows != 0 ? rows : grid.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
        int renderedItems = 0;
        String rowIndexVar = grid.getRowIndexVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        writer.startElement("table", grid);
        writer.writeAttribute("class", DataGrid.TABLE_CLASS, null);
        writer.startElement("tbody", null);

        for(int i = 0; i < numberOfRowsToRender; i++) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataGrid.TABLE_ROW_CLASS, null);

            for(int j = 0; j < columns; j++) {
                writer.startElement("td", null);
                writer.writeAttribute("class", DataGrid.TABLE_COLUMN_CLASS, null);
                
                if(renderedItems < itemsToRender) {
                    grid.setRowIndex(rowIndex);

                    if(rowIndexVar != null) {
                        requestMap.put(rowIndexVar, j);
                    }

                    if(grid.isRowAvailable()) {
                        renderChildren(context, grid);
                        rowIndex++;
                        renderedItems++;
                    }
                }
                
                writer.endElement("td");
            }
            writer.endElement("tr");
        }

        grid.setRowIndex(-1);	//cleanup

        if(rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }

        writer.endElement("tbody");
        writer.endElement("table");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}