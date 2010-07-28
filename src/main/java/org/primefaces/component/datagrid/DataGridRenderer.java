/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DataGridRenderer extends CoreRenderer {

    public void decode(FacesContext facesContext, UIComponent component) {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        DataGrid grid = (DataGrid) component;
        String clientId = grid.getClientId();
        boolean isAjaxPaging = params.containsKey(clientId + "_ajaxPaging");

        if (isAjaxPaging) {
            grid.setFirst(Integer.valueOf(params.get(clientId + "_first")));
            grid.setRows(Integer.valueOf(params.get(clientId + "_rows")));
            grid.setPage(Integer.valueOf(params.get(clientId + "_page")));
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        DataGrid grid = (DataGrid) component;
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        boolean isAjaxPaging = params.containsKey(grid.getClientId() + "_ajaxPaging");

        if(isAjaxPaging) {
            encodeTable(facesContext, grid);
        } else {
            encodeMarkup(facesContext, grid);
            encodeScript(facesContext, grid);
        }
    }

    protected void encodeMarkup(FacesContext facesContext, DataGrid grid) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = grid.getClientId();
        boolean hasPaginator = grid.isPaginator();
        String paginatorPosition = grid.getPaginatorPosition();
        String styleClass = grid.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + grid.getStyleClass();

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorContainer(facesContext, clientId + "_paginatorTop");
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", DataGrid.CONTENT_CLASS, null);

        encodeTable(facesContext, grid);
        
        writer.endElement("div");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext facesContext, DataGrid grid) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = grid.getClientId();
        String widgetVar = createUniqueWidgetVar(facesContext, grid);

        UIComponent form = ComponentUtils.findParentForm(facesContext, grid);
        if (form == null) {
            throw new FacesException("DataGrid : \"" + clientId + "\" must be inside a form element");
        }

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write(widgetVar + " = new PrimeFaces.widget.DataGrid('" + clientId + "',{");
        writer.write("url:'" + getActionURL(facesContext) + "'");
        writer.write(",formId:'" + form.getClientId() + "'");

        if (grid.isPaginator()) {
            writer.write(",paginator:new YAHOO.widget.Paginator({");
            writer.write("rowsPerPage:" + grid.getRows());
            writer.write(",totalRecords:" + grid.getRowCount());
            writer.write(",initialPage:" + grid.getPage());

            if (grid.getPageLinks() != 10) {
                writer.write(",pageLinks:" + grid.getPageLinks());
            }
            if (grid.getPaginatorTemplate() != null) {
                writer.write(",template:'" + grid.getPaginatorTemplate() + "'");
            }
            if (grid.getRowsPerPageTemplate() != null) {
                writer.write(",rowsPerPageOptions : [" + grid.getRowsPerPageTemplate() + "]");
            }
            if (grid.getFirstPageLinkLabel() != null) {
                writer.write(",firstPageLinkLabel:'" + grid.getFirstPageLinkLabel() + "'");
            }
            if (grid.getPreviousPageLinkLabel() != null) {
                writer.write(",previousPageLinkLabel:'" + grid.getPreviousPageLinkLabel() + "'");
            }
            if (grid.getNextPageLinkLabel() != null) {
                writer.write(",nextPageLinkLabel:'" + grid.getNextPageLinkLabel() + "'");
            }
            if (grid.getLastPageLinkLabel() != null) {
                writer.write(",lastPageLinkLabel:'" + grid.getLastPageLinkLabel() + "'");
            }
            if (grid.getCurrentPageReportTemplate() != null) {
                writer.write(",pageReportTemplate:'" + grid.getCurrentPageReportTemplate() + "'");
            }
            if (!grid.isPaginatorAlwaysVisible()) {
                writer.write(",alwaysVisible:false");
            }

            String paginatorPosition = grid.getPaginatorPosition();
            String paginatorContainer = null;
            if (paginatorPosition.equals("both")) {
                paginatorContainer = clientId + "_paginatorTop','" + clientId + "_paginatorBottom";
            } else if (paginatorPosition.equals("top")) {
                paginatorContainer = clientId + "_paginatorTop";
            } else if (paginatorPosition.equals("bottom")) {
                paginatorContainer = clientId + "_paginatorBottom";
            }

            writer.write(",containers:['" + paginatorContainer + "']");

            writer.write("})");

            if (grid.isEffect()) {
                writer.write(",effect:true");
                writer.write(",effectSpeed:'" + grid.getEffectSpeed() + "'");
            }
        }

        writer.write("});");

        writer.endElement("script");
    }

    protected void encodeTable(FacesContext facesContext, DataGrid grid) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = grid.getClientId();
        int columns = grid.getColumns();
        int rowIndex = grid.getFirst();
        int numberOfRowsToRender = (grid.getRows() != 0 ? grid.getRows() : grid.getRowCount()) / columns;

        writer.startElement("table", grid);
        writer.writeAttribute("class", DataGrid.TABLE_CLASS, null);
        writer.startElement("tbody", null);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataGrid.TABLE_ROW_CLASS, null);

            for (int j = 0; j < columns; j++) {
                grid.setRowIndex(rowIndex);

                writer.startElement("td", null);
                writer.writeAttribute("class", DataGrid.TABLE_COLUMN_CLASS, null);
                if (grid.isRowAvailable()) {
                    renderChildren(facesContext, grid);
                    rowIndex++;
                }
                writer.endElement("td");
            }

            writer.endElement("tr");

            if (!grid.isRowAvailable()) {
                break;
            }
        }

        grid.setRowIndex(-1);	//cleanup

        writer.endElement("tbody");
        writer.endElement("table");
    }

    protected void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("class", "ui-paginator ui-widget-header ui-corner-all", null);
        writer.endElement("div");
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    public boolean getRendersChildren() {
        return true;
    }
}