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
package org.primefaces.component.spreadsheet;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class SpreadsheetRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Spreadsheet ss = (Spreadsheet) component;
		
		encodeMarkup(facesContext, ss);
		encodeScript(facesContext, ss);
	}

	protected void encodeMarkup(FacesContext facesContext, Spreadsheet ss) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = ss.getClientId(facesContext);
		
		writer.startElement("div", ss);
		writer.writeAttribute("id", clientId, "id");
		
		writer.startElement("div", ss);
		writer.writeAttribute("id", clientId + "_datasources", "id");
		for(UIComponent component : ss.getChildren()) {
			encodeSheet(facesContext, (Sheet) component, true);
		}
		writer.endElement("div");
		
		writer.startElement("div", ss);
		writer.writeAttribute("id", clientId + "_datatransports", "id");
		for(UIComponent component : ss.getChildren()) {
			encodeSheet(facesContext, (Sheet) component, false);
		}
		writer.endElement("div");
		
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext facesContext, Spreadsheet ss) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String widgetVar = createUniqueWidgetVar(facesContext, ss);
		String clientId = ss.getClientId(facesContext);
	
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function() {");
		writer.write(widgetVar + " = new PrimeFaces.widget.Spreadsheet('" + clientId + "', {");
		writer.write("editable:" + ss.isEditable());
		if(ss.getTitle() != null) writer.write(",title:'" + ss.getTitle() + "'");
		if(ss.getColumnWidth() != Integer.MIN_VALUE) writer.write(",newColumnWidth:" + ss.getColumnWidth());
		
		writer.write("});});");
		
		writer.endElement("script");
	}
	
	protected void encodeSheet(FacesContext facesContext, Sheet sheet, boolean datasource) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = sheet.getClientId(facesContext);
		String tableId = datasource ? clientId + "_datasource" : clientId + "_datatransport";
		int first = sheet.getFirst();
		int rowCount = sheet.getRowCount();
		
		writer.startElement("table", sheet);
		writer.writeAttribute("id", tableId, "id");
		if(!datasource) writer.writeAttribute("style", "display:none", null);
		if(sheet.getTitle() != null) writer.writeAttribute("title", sheet.getTitle(), null);
		
		writer.startElement("tbody", null);
		
		for(int i=first; i < rowCount; i++) {
			sheet.setRowIndex(i);
			
			writer.startElement("tr", null);
			
			for(UIComponent child : sheet.getChildren()) {
				if(child.isRendered() && (child instanceof Column)) {
					Column column = (Column) child;
					
					writer.startElement("td", null);
					if(datasource) {
						writer.write(ComponentUtils.getStringValueToRender(facesContext, column.getChildren().get(0)));
					} else {
						renderChildren(facesContext, column);
					}
					writer.endElement("td");
				}
			}
			
			writer.endElement("tr");
		}
		
		writer.endElement("tbody");
		writer.endElement("table");
		
		sheet.setRowIndex(-1);		//cleanup
	}
	
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}