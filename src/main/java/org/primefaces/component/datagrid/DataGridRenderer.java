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
package org.primefaces.component.datagrid;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class DataGridRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        DataGrid grid = (DataGrid) component;
        String clientId = grid.getClientId();

        if(grid.isPagingRequest(context)) {
            grid.setFirst(Integer.valueOf(params.get(clientId + "_first")));
            grid.setRows(Integer.valueOf(params.get(clientId + "_rows")));
            grid.setPage(Integer.valueOf(params.get(clientId + "_page")));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataGrid grid = (DataGrid) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if(grid.isPagingRequest(context)) {
            encodeTable(context, grid);
        } else {
            encodeMarkup(context, grid);
            encodeScript(context, grid);
        }
    }

    protected void encodeMarkup(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId();
        boolean hasPaginator = grid.isPaginator();
        String paginatorPosition = grid.getPaginatorPosition();
        String styleClass = grid.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + grid.getStyleClass();

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorContainer(context, clientId + "_paginatorTop");
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", DataGrid.CONTENT_CLASS, null);

        encodeTable(context, grid);
        
        writer.endElement("div");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorContainer(context, clientId + "_paginatorBottom");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() { ");

        writer.write(grid.resolveWidgetVar() + " = new PrimeFaces.widget.DataGrid('" + clientId + "',{");

        if(grid.isPaginator()) {
            writer.write("paginator:new YAHOO.widget.Paginator({");
            writer.write("rowsPerPage:" + grid.getRows());
            writer.write(",totalRecords:" + grid.getRowCount());
            writer.write(",initialPage:" + grid.getPage());

            if(grid.getPageLinks() != 10) writer.write(",pageLinks:" + grid.getPageLinks());
            if(grid.getPaginatorTemplate() != null) writer.write(",template:'" + grid.getPaginatorTemplate() + "'");
            if(grid.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + grid.getRowsPerPageTemplate() + "]");
            if(grid.getCurrentPageReportTemplate() != null) writer.write(",pageReportTemplate:'" + grid.getCurrentPageReportTemplate() + "'");
            if (!grid.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");

            String paginatorPosition = grid.getPaginatorPosition();
            String paginatorContainer = null;
            if(paginatorPosition.equals("both"))
                paginatorContainer = clientId + "_paginatorTop','" + clientId + "_paginatorBottom";
            else if (paginatorPosition.equals("top"))
                paginatorContainer = clientId + "_paginatorTop";
            else if (paginatorPosition.equals("bottom"))
                paginatorContainer = clientId + "_paginatorBottom";

            writer.write(",containers:['" + paginatorContainer + "']");

            writer.write("})");

        } else {
            writer.write("paginator:false");
        }

        writer.write("});});");

        writer.endElement("script");
    }

    protected void encodeTable(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int columns = grid.getColumns();
        int rowIndex = grid.getFirst();
        int rows = grid.getRows();
        int itemsToRender = rows != 0 ? rows : grid.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
        String rowIndexVar = grid.getRowIndexVar();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        writer.startElement("table", grid);
        writer.writeAttribute("class", DataGrid.TABLE_CLASS, null);
        writer.startElement("tbody", null);

        for(int i = 0; i < numberOfRowsToRender; i++) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataGrid.TABLE_ROW_CLASS, null);

            for(int j = 0; j < columns; j++) {
                grid.setRowIndex(rowIndex);

                if(rowIndexVar != null) {
                    requestMap.put(rowIndexVar, j);
                }

                if(!grid.isRowAvailable()) {
                    break;
                }

                writer.startElement("td", null);
                writer.writeAttribute("class", DataGrid.TABLE_COLUMN_CLASS, null);

                if (grid.isRowAvailable()) {
                    renderChildren(context, grid);
                    rowIndex++;
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

    protected void encodePaginatorContainer(FacesContext context, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("class", "ui-paginator ui-widget-header ui-corner-all", null);
        writer.endElement("div");
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