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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;

import org.primefaces.renderkit.CoreRenderer;

public class DataTableRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		DataTable table = (DataTable) component;
		String clientId = table.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		boolean isAjaxFilterRequest = params.containsKey(clientId + "_ajaxFilter");
		boolean isAjaxSortRequest = params.containsKey(clientId + "_ajaxSort");
		boolean isAjaxPageRequest = params.containsKey(clientId + "_ajaxPage");
		
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;
		
		encodeMarkup(context, table);
		encodeScript(context, table);
	}
	
	protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
		String widgetVar = createUniqueWidgetVar(context, table);
		
		writer.startElement("script", table);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(widgetVar + " = new PrimeFaces.widget.DataTable('" + clientId + "',{");

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

        encodeFacet(context, table, table.getFacet("header"), DataTable.HEADER_CLASS);

        writer.startElement("table", null);
        encodeThead(context, table);
        encodeTbody(context, table);
        writer.endElement("table");
        
        encodeFacet(context, table, table.getFacet("footer"), DataTable.FOOTER_CLASS);

        writer.endElement("div");
	}

    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("thead", null);
        writer.startElement("tr", null);

        //Header
        for(Column column : table.getColumns()) {
            writer.startElement("th", null);
            writer.writeAttribute("class", DataTable.COLUMN_HEADER_CLASS, null);

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
	
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}