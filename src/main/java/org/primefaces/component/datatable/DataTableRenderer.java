/*
 * Copyright 2010 Prime Technology.
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.model.BeanPropertyComparator;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		DataTable table = (DataTable) component;
		String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        if(table.isPaginationRequest(context)) {
            decodePageRequest(context, table, clientId, params);
        } else if(table.isSortRequest(context)) {
            decodeSortRequest(context, table);
        }
	}

    protected void decodePageRequest(FacesContext facesContext, DataTable dataTable, String clientId, Map<String,String> params) {
		String firstParam = params.get(clientId + "_first");
		String rowsParam = params.get(clientId + "_rows");
		String pageParam = params.get(clientId + "_page");

		dataTable.setFirst(Integer.valueOf(firstParam));
		dataTable.setRows(Integer.valueOf(rowsParam));
		dataTable.setPage(Integer.valueOf(pageParam));
	}

	protected void decodeSortRequest(FacesContext context, DataTable table) {
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String clientId = table.getClientId(context);
		String sortKey = params.get(clientId + "_sortKey");
		boolean asc = Boolean.valueOf(params.get(clientId + "_sortDir"));
        Column sortColumn = null;
        
        for(Column column : table.getColumns()) {
            if(column.getClientId(context).equals(sortKey)) {
                sortColumn = column;
                break;
            }
        }

        List list = (List) table.getValue();
		Collections.sort(list, new BeanPropertyComparator(sortColumn, table.getVar(), asc));
		table.setValue(list);

		//Reset paginator
		table.setFirst(0);
		table.setPage(1);
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;

        if(table.isAjaxRequest(context)) {
            encodeTbody(context, table);
        } else {
            encodeMarkup(context, table);
            encodeScript(context, table);
        }
	}
	
	protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
		String widgetVar = createUniqueWidgetVar(context, table);
		
		writer.startElement("script", table);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(widgetVar + " = new PrimeFaces.widget.DataTable('" + clientId + "',{");

        //Connection
        UIComponent form = ComponentUtils.findParentForm(context, table);
        if(form == null) {
            throw new FacesException("DataTable : \"" + clientId + "\" must be inside a form element.");
        }
        writer.write("url:'" + getActionURL(context) + "'");
        writer.write(",formId:'" + form.getClientId(context) + "'");

        //Pagination
        if(table.isPaginator()) {
            encodePaginatorConfig(context, table);
        }

        writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        String containerClass = table.getStyleClass() != null ? DataTable.CONTAINER_CLASS + " " + table.getStyleClass() : DataTable.CONTAINER_CLASS;
        String style = null;

        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, clientId);
        if((style = table.getStyle()) != null) {
            writer.writeAttribute("style", style, clientId);
        }

        encodeFacet(context, table, table.getHeader(), DataTable.HEADER_CLASS);

        writer.startElement("table", null);
        encodeThead(context, table);
        encodeTbody(context, table);
        writer.endElement("table");

        if(table.isPaginator()) {
            encodePaginatorMarkup(context, table);
        }
        
        encodeFacet(context, table, table.getFooter(), DataTable.FOOTER_CLASS);

        writer.endElement("div");
	}

    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("thead", null);
        writer.startElement("tr", null);

        //Header
        for(Column column : table.getColumns()) {
            boolean sortable = column.getValueExpression("sortBy") != null;
            String columnClass = sortable ? DataTable.COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.COLUMN_HEADER_CLASS;

            writer.startElement("th", null);
            writer.writeAttribute("id", column.getClientId(context), null);
            writer.writeAttribute("class", columnClass, null);
            
            if(sortable) {
                writer.startElement("span", null);
                writer.writeAttribute("class", DataTable.SORTABLE_COLUMN_ICON_CLASS, null);
                writer.endElement("span");
            }

            UIComponent header = column.getFacet("header");
            if(header != null) {
                header.encodeAll(context);
            }

            writer.endElement("th");
        }

        writer.endElement("tr");
        writer.endElement("thead");
    }

    protected void encodeTbody(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();

        writer.startElement("tbody", null);
        writer.writeAttribute("id", table.getClientId(context) + "_data", null);
        writer.writeAttribute("class", DataTable.DATA_CLASS, null);

		int rowCountToRender = table.getRows() == 0 ? table.getRowCount() : table.getRows();
		int first = table.getFirst();

		for(int i = first; i < (first + rowCountToRender); i++) {
			table.setRowIndex(i);
			if(!table.isRowAvailable())
				continue;

			//Row index var
			if(rowIndexVar != null) {
				context.getExternalContext().getRequestMap().put(rowIndexVar, i);
			}

			writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.ROW_CLASS, null);

			for(Column column : table.getColumns()) {
                writer.startElement("td", null);
                column.encodeAll(context);
                writer.endElement("td");
			}

			writer.endElement("tr");
		}

        writer.endElement("tbody");

		//Cleanup
		table.setRowIndex(-1);
		if(rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
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

    protected void encodePaginatorConfig(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);

        writer.write(",paginator:new YAHOO.widget.Paginator({");
        writer.write("rowsPerPage:" + table.getRows());
        writer.write(",totalRecords:" + table.getRowCount());
        writer.write(",initialPage:" + table.getPage());
        writer.write(",containers:['" + clientId + "_paginator']");

        if(table.getPageLinks() != 10) writer.write(",pageLinks:" + table.getPageLinks());
        if(table.getPaginatorTemplate() != null) writer.write(",template:'" + table.getPaginatorTemplate() + "'");
        if(table.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + table.getRowsPerPageTemplate() + "]");
        if(table.getCurrentPageReportTemplate() != null)writer.write(",pageReportTemplate:'" + table.getCurrentPageReportTemplate() + "'");
        if(!table.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");

        writer.write("})");
    }

    protected void encodePaginatorMarkup(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        String styleClass = "ui-paginator ui-widget-header";
        if(table.getFooter() == null) {
            styleClass = styleClass + " ui-corner-bl ui-corner-br";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_paginator", null);
        writer.writeAttribute("class", styleClass, null);
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