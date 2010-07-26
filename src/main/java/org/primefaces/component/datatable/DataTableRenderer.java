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
package org.primefaces.component.datatable;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer implements PartialRenderer {

	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		DataTable dataTable = (DataTable) component;
		
		//If row selection is enabled, find the selected rows
		if(dataTable.getSelectionMode() != null || dataTable.getColumnSelectionMode() != null) {
			Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
			String rowSelectParam = dataTable.getClientId(facesContext) + "_selectedRows";
			String firstRowParam = dataTable.getClientId(facesContext) + "_firstRow";
			
			if(params.containsKey(rowSelectParam) && params.containsKey(firstRowParam)) {
				String rowSelectParamValue = params.get(rowSelectParam);
				String firstRowParamValue = params.get(firstRowParam);
				
				if(ComponentUtils.isValueBlank(rowSelectParamValue)) {
					dataTable.setSelection(null);
					
					return;
				}
				
				String[] rowSelectValues = rowSelectParamValue.split(",");
				Object[] data = new Object[rowSelectValues.length];
				
				for(int i = 0; i < rowSelectValues.length; i++) {
					String rowELIndex = rowSelectValues[i].trim().substring(7);
					String firstRowELIndex = firstRowParamValue.trim().substring(7);
					int rowIndex = Integer.parseInt(rowELIndex) - Integer.parseInt(firstRowELIndex);
					dataTable.setRowIndex(rowIndex);

					data[i] = dataTable.getRowData();
				}
				
				dataTable.setSelection(data);		//will be used in update model to set the selection value
			}
			
			dataTable.setRowIndex(-1);	//clean	
		}
	}

	@SuppressWarnings("unchecked")
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		DataTable dataTable = (DataTable) component;
		String first = facesContext.getExternalContext().getRequestParameterMap().get("first");
		String sortKey = facesContext.getExternalContext().getRequestParameterMap().get("sortKey");
		String sortDir = facesContext.getExternalContext().getRequestParameterMap().get("sortDir");
		
		//Ajax paging request
		if(first != null) {
			dataTable.setFirst(Integer.parseInt(first));
		}
		
		//Ajax sorting request
		if(sortKey != null && sortDir != null) {
			List value = (List) dataTable.getValue();
			Collections.sort(value, new BeanPropertyComparator(findSortByExpression(dataTable, sortKey), dataTable.getVar(), sortDir));
		}
		
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("div", null);
		encodeTable(facesContext, dataTable);
		
		StateManager stateManager = facesContext.getApplication().getStateManager();
		stateManager.writeState(facesContext, stateManager.saveView(facesContext));
		writer.endElement("div");
	}
	
	private ValueExpression findSortByExpression(DataTable dataTable, String sortKey) {
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				if(kid.getId().equals(sortKey)) {
					return kid.getValueExpression("sortBy");
				}
			}
		}
		
		return null;
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;
		
		encodeScript(facesContext, table);
		encodeMarkup(facesContext, table);
	}
	
	private void encodeScript(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, dataTable);
		
		writer.startElement("script", dataTable);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		
		String columnDefVar = encodeColumnDefinition(facesContext, dataTable, widgetVar);
		String datasourceVar = encodeDatasource(facesContext, dataTable, widgetVar);
	
		writer.write(widgetVar + " = new PrimeFaces.widget.DataTable('" + clientId + "'," + columnDefVar + "," + datasourceVar + ", {");
		
		encodeConfig(facesContext, dataTable);
		
		writer.write("});\n});\n");
		
		writer.endElement("script");
	}
	
	private String encodeDatasource(FacesContext facesContext, DataTable dataTable, String var) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String datasourceVar = var + "_datasource";
		boolean dynamic = dataTable.isDynamic();
		
		if(dynamic) {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource('" + getActionURL(facesContext) + "');\n");
			writer.write(datasourceVar + ".connMethodPost = true;\n");
		}	
		else {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource(YAHOO.util.Dom.get('" + clientId + "_table'));\n");
		}
		
		writer.write(datasourceVar + ".responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;\n");
		writer.write(datasourceVar + ".responseSchema = {fields:[");
		
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				
				if(column.getSelectionMode() == null) {
					writer.write("{key:'" + kid.getId() + "'");
					if(column.getParser() != null)
						writer.write(",parser:'" + column.getParser() + "'");
					
					writer.write("}");
					
					if(children.hasNext())
						writer.write(",");
				}
			}
		}
		
		writer.write("]");
		
		writer.write("};\n");
		
		return datasourceVar;
	}
	
	private String encodeColumnDefinition(FacesContext facesContext, DataTable dataTable, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String columnDefVar = datatableVar + "_columnDef";
		
		writer.write("var " + columnDefVar + " = [");
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				UIComponent header = column.getFacet("header");
				
				writer.write("{key:'" + column.getId()  + "'");
				if(header != null) {
					writer.write(",label:'");
					if(header.getClass().getName().equals("com.sun.faces.facelets.compiler.UIInstructions")) {		//cant use instanceof
						String instructionsValue = header.toString();
						if(instructionsValue != null)
							writer.write(instructionsValue.trim());
					} else {						
						renderChild(facesContext, column.getFacet("header"));
					}
					writer.write("'");
				}
				else {
					writer.write(",label:''");
				}
				
				if(column.getValueExpression("sortBy") != null) writer.write(",sortable:true");
				if(column.isResizable()) writer.write(",resizeable:true");
				if(column.isFilter()) writer.write(",filter:true");
				if(column.getWidth() != Integer.MIN_VALUE) writer.write(",width:" + column.getWidth());
				if(column.getSelectionMode() != null) {
					String selector = column.getSelectionMode().equals("single") ? "radio" : "checkbox";
					
					writer.write(",formatter:'" + selector + "'");
				}
				
				writer.write("}");
				
				if(children.hasNext())
					writer.write(",");
			}
		}
		writer.write("];\n");
		
		return columnDefVar;
	}
	
	private void encodeConfig(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		if(dataTable.isScrollable())
			writer.write("scrollable:true");
		else
			writer.write("scrollable:false");
			
		if(dataTable.getWidth() != null) writer.write(",width:'" + dataTable.getWidth() + "'");
		if(dataTable.getHeight() != null) writer.write(",height:'" + dataTable.getHeight() + "'");
		
		if(dataTable.isPaginator()) {				
			writer.write(",paginator:new YAHOO.widget.Paginator({\n");
			writer.write("rowsPerPage:" + dataTable.getRows());
			writer.write(",totalRecords:" + dataTable.getRowCount());
			
			if(dataTable.getPaginatorTemplate() != null) writer.write(",template:'" + dataTable.getPaginatorTemplate() + "'");
			if(dataTable.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + dataTable.getRowsPerPageTemplate() + "]");
			if(dataTable.getFirstPageLinkLabel() != null) writer.write(",firstPageLinkLabel:'" + dataTable.getFirstPageLinkLabel() + "'");
			if(dataTable.getPreviousPageLinkLabel() != null) writer.write(",previousPageLinkLabel:'" + dataTable.getPreviousPageLinkLabel() + "'");
			if(dataTable.getNextPageLinkLabel() != null) writer.write(",nextPageLinkLabel:'" + dataTable.getNextPageLinkLabel() + "'");
			if(dataTable.getLastPageLinkLabel() != null) writer.write(",lastPageLinkLabel:'" + dataTable.getLastPageLinkLabel() + "'");
			
			String paginatorPosition = dataTable.getPaginatorPosition();
			String paginatorContainer = null;
			if(paginatorPosition.equals("top"))
				paginatorContainer = clientId + "_paginatorTop";
			else if(paginatorPosition.equals("bottom"))
				paginatorContainer = clientId + "_paginatorBottom";
			
			if(paginatorContainer != null)  writer.write(",containers:['" + paginatorContainer + "']");
			
			writer.write("})\n");
		}
		
		if(dataTable.getEmptyMessage() != null) writer.write(",MSG_EMPTY : '" + dataTable.getEmptyMessage() + "'");
		if(dataTable.getErrorMessage() != null) writer.write(",MSG_ERROR : '" + dataTable.getErrorMessage() + "'");
		if(dataTable.getLoadingMessage() != null) writer.write(",MSG_LOADING : '" + dataTable.getLoadingMessage() + "'");
		if(dataTable.getSortAscMessage() != null) writer.write(",MSG_SORTASC : '" + dataTable.getSortAscMessage() + "'");
		if(dataTable.getSortDescMessage() != null) writer.write(",MSG_SORTDESC : '" + dataTable.getSortDescMessage() + "'");
		
		if(dataTable.getSelectionMode() != null) {
			String mode = dataTable.getSelectionMode().equals("multiple") ? "standard" : "single";
			writer.write(",selectionMode:'" + mode + "'");
			
			if(dataTable.getUpdate() != null) {
				String formClientId = ComponentUtils.findParentForm(facesContext, dataTable).getClientId(facesContext);
				
				writer.write(",update:'" + dataTable.getUpdate() + "'");
				writer.write(",formId:'" + formClientId + "'");
				writer.write(",url:'" + getActionURL(facesContext) + "'");
				if(dataTable.getOnselectStart() != null) writer.write(",onselectStart:function(){" + dataTable.getOnselectStart() + ";}");
				if(dataTable.getOnselectComplete() != null) writer.write(",onselectComplete:function(){" + dataTable.getOnselectComplete() + ";}");
			}
			
		}
		
		if(dataTable.getColumnSelectionMode() != null) {
			writer.write(",columnSelectionMode:'" + dataTable.getColumnSelectionMode() + "'");
		}
		
		if(dataTable.isDynamic()) {
			String formClientId = ComponentUtils.findParentForm(facesContext, dataTable).getClientId(facesContext);
			
			writer.write(",formId:'" + formClientId + "'");
			writer.write(",dynamicData:true");
			writer.write(",generateRequest:PrimeFaces.widget.DataTableUtils.loadDynamicData");
			writer.write(",initialRequest:PrimeFaces.widget.DataTableUtils.loadInitialData('" + clientId + "','" + formClientId + "')");
		}
	}

	private void encodeMarkup(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		if(dataTable.getStyle() != null)
			writer.writeAttribute("style", dataTable.getStyle(), "style");
		
		if(dataTable.getPaginatorPosition().equals("top")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorTop");
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		
		if(!dataTable.isDynamic()) {
			encodeTable(facesContext, dataTable);
		}
		
		writer.endElement("div");
		
		if(dataTable.getPaginatorPosition().equals("bottom")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
		}
		
		encodeHiddenInput(facesContext, clientId + "_selectedRows");
		encodeHiddenInput(facesContext, clientId + "_firstRow");
		
		writer.endElement("div");
	}
	
	private void encodeHiddenInput(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.endElement("input");
		
	}
	private void encodeTable(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("table", null);
		writer.writeAttribute("id", clientId + "_table", null);
		
		encodeHeaders(facesContext, dataTable);
		encodeRows(facesContext, dataTable);
		
		writer.endElement("table");
	}
	
	private void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", id, null);
		writer.endElement("div");
	}

	private void encodeHeaders(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("thead", null);
		writer.startElement("tr", null);
		
		for (Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
			UIComponent kid = iterator.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid; 
				
				if(column.getSelectionMode() == null) {
					writer.startElement("th", column);
					if(column.getFacet("header") != null) {
						renderChild(facesContext, column.getFacet("header"));
					}
					writer.endElement("th");
				}
			}
		}
		
		writer.endElement("tr");
		writer.endElement("thead");
	}
	
	private void encodeRows(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
	
		writer.startElement("tbody", null);
		
		if(dataTable.isLazy()) {
			dataTable.loadLazyData();
		}
		
		int rowCountToRender = getNumberOfRowsToRender(dataTable);
		int first = dataTable.getFirst();
		
		for(int i = first; i < (first + rowCountToRender); i++) {
			dataTable.setRowIndex(i);
			if(!dataTable.isRowAvailable())
				continue;
			
			if(dataTable.getRowIndexVar() != null) {
				facesContext.getExternalContext().getRequestMap().put(dataTable.getRowIndexVar(), i);
			}
			
			writer.startElement("tr", null);
			
			for(Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
				UIComponent kid = iterator.next();
				
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					if(column.getSelectionMode() == null) {
						writer.startElement("td", null);
					
						renderChildren(facesContext, column);
					
						writer.endElement("td");
					}
				}
			}
			
			writer.endElement("tr");
		}
		
		//cleanup
		dataTable.setRowIndex(-1);		
		if(dataTable.getRowIndexVar() != null) {
			facesContext.getExternalContext().getRequestMap().remove(dataTable.getRowIndexVar());
		}
		
		writer.endElement("tbody");
	}
	
	private int getNumberOfRowsToRender(DataTable dataTable) {
		if(dataTable.isDynamic()) {
			return  dataTable.getRows() == 0 ? dataTable.getRowCount() : dataTable.getRows();
		} else {
			return dataTable.getRowCount();
		}
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}