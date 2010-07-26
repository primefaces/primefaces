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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.ListDataModel;

import org.primefaces.component.column.Column;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer implements PartialRenderer {

	@Override
	@SuppressWarnings("unchecked")
	public void decode(FacesContext facesContext, UIComponent component) {
		DataTable dataTable = (DataTable) component;
		String clientId = dataTable.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		//If row selection is enabled, find the selected rows
		if(dataTable.isSelectionEnabled()) {
			String rowSelectParam = clientId + "_selectedRows";
			
			if(params.containsKey(rowSelectParam)) {
				String rowSelectParamValue = params.get(rowSelectParam);
						
				if(isMultipleSelection(dataTable))
					decodeMultipleSelection(dataTable, rowSelectParamValue);
				else
					decodeSingleSelection(dataTable, rowSelectParamValue);
				
				dataTable.setRowIndex(-1);	//clean	
			}
		}
		
		if(dataTable.hasFilter()) {
			Map<String,ValueExpression> filterMap = dataTable.getFilterMap();
			List filteredData = new ArrayList();
			
			for(int i = 0; i < dataTable.getRowCount(); i++) {
				dataTable.setRowIndex(i);
				boolean shouldAdd = false;
				
				for(String filterName : filterMap.keySet()) {
					Object columnValue = filterMap.get(filterName).getValue(facesContext.getELContext());
					String filterValue = params.get(filterName + "_filter");
					
					if(isValueBlank(filterValue))
						shouldAdd = true;
					else if(columnValue == null) {
						shouldAdd = false;
						break;
					} else {
						if(String.valueOf(columnValue).toLowerCase().startsWith(filterValue.toLowerCase()))
							shouldAdd = true;
						else {
							shouldAdd = false;
							break;
						}
					}
				}
				
				if(shouldAdd)
					filteredData.add(dataTable.getRowData());
			}
			
			dataTable.setRowIndex(-1);	//cleanup
			
			dataTable.assignDataModel(new ListDataModel(filteredData));
		}
	}
	
	private boolean isMultipleSelection(DataTable dataTable) {
		String selectionMode = dataTable.getSelectionMode();
		String columnSelectionMode = dataTable.getColumnSelectionMode();
		
		if(selectionMode != null && selectionMode.equals("multiple"))
			return true;
		else if(columnSelectionMode != null && columnSelectionMode.equals("multiple"))
			return true;
		else
			return false;
	}
	
	private void decodeSingleSelection(DataTable dataTable, String rowSelectParamValue) {
		if(isValueBlank(rowSelectParamValue)) {
			dataTable.setSelection(null);
		}
		else {
			dataTable.setRowIndex(Integer.parseInt(rowSelectParamValue));
			Object data = dataTable.getRowData();
			
			dataTable.setSelection(data);
		}
	}
	
	private void decodeMultipleSelection(DataTable dataTable, String rowSelectParamValue) {
		Class<?> clazz = dataTable.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());
		
		if(isValueBlank(rowSelectParamValue)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			dataTable.setSelection(data);
		} else {
			String[] rowSelectValues = rowSelectParamValue.split(",");
			Object data = Array.newInstance(clazz.getComponentType(), rowSelectValues.length);
			
			for(int i = 0; i < rowSelectValues.length; i++) {
				dataTable.setRowIndex(Integer.parseInt(rowSelectValues[i]));

				Array.set(data, i, dataTable.getRowData());
			}
			
			dataTable.setSelection(data);
		}
	}

	@SuppressWarnings("unchecked")
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		DataTable dataTable = (DataTable) component;
		String sortKey = facesContext.getExternalContext().getRequestParameterMap().get("sortKey");
		String sortDir = facesContext.getExternalContext().getRequestParameterMap().get("sortDir");
		
		//Ajax sorting request
		if(sortKey != null && sortDir != null) {
			List value = (List) dataTable.getValue();
			Collections.sort(value, new BeanPropertyComparator(findSortColumm(dataTable, sortKey), dataTable.getVar(), sortDir));
		}
		
		ResponseWriter writer = facesContext.getResponseWriter();
		writer.startElement("div", null);
		
		//data
		encodeTable(facesContext, dataTable);
		
		//total records
		writer.startElement("span", null);
		writer.write(String.valueOf(dataTable.getRowCount()));
		writer.endElement("span");
		
		//state
		StateManager stateManager = facesContext.getApplication().getStateManager();
		stateManager.writeState(facesContext, stateManager.saveView(facesContext));
		
		writer.endElement("div");
	}
	
	private Column findSortColumm(DataTable dataTable, String sortKey) {
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				if(kid.getId().equals(sortKey)) {
					return (Column) kid;
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
		String datatableType = dataTable.isScrollable() ? "ScrollingDataTable"  : "DataTable";
	
		writer.write(widgetVar + " = new PrimeFaces.widget." + datatableType + "('" + clientId + "'," + columnDefVar + "," + datasourceVar + ", {");
		
		encodeConfig(facesContext, dataTable);
		
		writer.write("});\n});\n");
		
		writer.endElement("script");
	}
	
	private String encodeDatasource(FacesContext facesContext, DataTable dataTable, String var) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String datasourceVar = var + "_datasource";
		
		if(dataTable.isDynamic()) {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource('" + getActionURL(facesContext) + "');\n");
			writer.write(datasourceVar + ".connMethodPost = true;\n");
		}
		else {
			writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource(YAHOO.util.Dom.get('" + dataTable.getClientId(facesContext) + "_table'));\n");
		}
		
		writer.write(datasourceVar + ".responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;\n");
		writer.write(datasourceVar + ".responseSchema = {fields:[");
		
		writer.write("{key:'rowIndex'}");
		
		for(UIComponent kid : dataTable.getChildren()) {
			if(kid.isRendered() && kid instanceof Column && ((Column) kid).getSelectionMode() == null)
				writer.write(",{key:'" + kid.getId() + "'}");
		}
		
		writer.write("]");
		
		writer.write("};\n");
		
		return datasourceVar;
	}
	
	private String encodeColumnDefinition(FacesContext facesContext, DataTable dataTable, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String columnDefVar = datatableVar + "_columnDef";
		
		writer.write("var " + columnDefVar + " = [");
		
		writer.write("{key:'rowIndex', hidden:true}");
		
		for(UIComponent kid : dataTable.getChildren()) {
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				
				writer.write(",");
						
				if(column.getSelectionMode() != null) {
					String selector = column.getSelectionMode().equals("single") ? "radio" : "checkbox";
					writer.write("{key:'pf_column_select'");			
					writer.write(",formatter:'" + selector + "'");
				} else {
					writer.write("{key:'" + column.getId()  + "'");
				}
				
				//header
				UIComponent header = column.getFacet("header");
				writer.write(",label:'");
				if(header != null) {
					if(header.getClass().getName().equals("com.sun.faces.facelets.compiler.UIInstructions")) {
						String instructionsValue = header.toString();
						if(instructionsValue != null) {
							writer.write(instructionsValue.trim());
						}
					} else {						
						renderChild(facesContext, column.getFacet("header"));
					}
					
					if(column.getValueExpression("filterBy") != null) {
						encodeColumnFilter(facesContext, column, datatableVar);
					}
				}
				writer.write("'");
				
				if(column.isResizable()) writer.write(",resizeable:true");
				if(column.getWidth() != Integer.MIN_VALUE) writer.write(",width:" + column.getWidth());
				if(column.getStyleClass() != null) writer.write(",className:'" + column.getStyleClass() + "'");
				
				//sorting
				if(column.getValueExpression("sortBy") != null) {
					writer.write(",sortable:true");
					
					if(!dataTable.isDynamic()) {
						String sortFunction = column.getSortFunction() == null ? "PrimeFaces.widget.DataTableUtils.genericSort" : column.getSortFunction().getExpressionString();
						writer.write(",sortOptions:{field:'" + column.getId() + "', sortFunction:" + sortFunction + "}");
						
						if(column.getParser() != null) writer.write(",parser:'" + column.getParser() + "'");
					}
				}
				
				writer.write("}");
			}
		}
		writer.write("];\n");
		
		return columnDefVar;
	}
	
	private void encodeConfig(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		if(dataTable.isDynamic()) {
			String formClientId = ComponentUtils.findParentForm(facesContext, dataTable).getClientId(facesContext);
			
			writer.write("dynamicData:true");
			writer.write(",formId:'" + formClientId + "'");
			writer.write(",generateRequest:PrimeFaces.widget.DataTableUtils.loadDynamicData");
			writer.write(",initialRequest:PrimeFaces.widget.DataTableUtils.loadInitialData('" + clientId + "','" + formClientId + "')");
		} else {
			writer.write("dynamicData:false");
		}
			
		if(dataTable.getWidth() != null) writer.write(",width:'" + dataTable.getWidth() + "'");
		if(dataTable.getHeight() != null) writer.write(",height:'" + dataTable.getHeight() + "'");
		
		if(dataTable.isPaginator()) {				
			writer.write(",paginator:new YAHOO.widget.Paginator({\n");
			writer.write("rowsPerPage:" + dataTable.getRows());
			writer.write(",totalRecords:" + dataTable.getRowCount());
			writer.write(",initialPage:" + dataTable.getPage());
			
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
			
			if(dataTable.isDblClickSelect()) writer.write(",dblClickSelect:true");
			
			if(dataTable.getUpdate() != null) {
				String formClientId = ComponentUtils.findParentForm(facesContext, dataTable).getClientId(facesContext);
				
				writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, dataTable.getParent(), dataTable.getUpdate()) + "'");
				writer.write(",formId:'" + formClientId + "'");
				writer.write(",url:'" + getActionURL(facesContext) + "'");
				if(dataTable.getOnselectStart() != null) writer.write(",onselectStart:function(){" + dataTable.getOnselectStart() + ";}");
				if(dataTable.getOnselectComplete() != null) writer.write(",onselectComplete:function(){" + dataTable.getOnselectComplete() + ";}");
			}
			
		}
		
		if(dataTable.getColumnSelectionMode() != null) {
			writer.write(",columnSelectionMode:'" + dataTable.getColumnSelectionMode() + "'");
		}
		
		if(dataTable.hasFilter()) {
			writer.write(",filter:true");
		}
	}

	private void encodeMarkup(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId , null);
		
		if(dataTable.getStyle() != null) writer.writeAttribute("style", dataTable.getStyle(), "style");
		if(dataTable.getStyleClass() != null) writer.writeAttribute("class", dataTable.getStyleClass(), "styleClass");
		
		if(dataTable.isPaginator() && dataTable.getPaginatorPosition().equals("top")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorTop");
		}
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_container", null);
		
		if(!dataTable.isDynamic()) {
			encodeTable(facesContext, dataTable);
		}
		
		writer.endElement("div");
		
		if(dataTable.isPaginator() && dataTable.getPaginatorPosition().equals("bottom")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
		}
		
		if(dataTable.isSelectionEnabled()) {
			if(dataTable.isSelectionEnabled() && dataTable.getSelection() != null) {
				setPreselection(dataTable);
			}
			encodeHiddenInput(facesContext, clientId + "_selectedRows", dataTable.getSelectedRowIndexesAsString());
		}
		
		if(dataTable.isPaginator()) {
			encodeHiddenInput(facesContext, clientId + "_currentPage", dataTable.getPage());
		}
		
		writer.endElement("div");
	}
	
	private void setPreselection(DataTable dataTable) {
		boolean isSingleSelectionMode = dataTable.isSingleSelectionMode();
		
		for(int i=0; i < dataTable.getRowCount(); i++) {
			dataTable.setRowIndex(i);
			
			Object data = dataTable.getRowData();
			
			if(isSingleSelectionMode) {
				if(data.equals(dataTable.getSelection()))
					dataTable.getSelectedRowIndexes().add(dataTable.getRowIndex());
			} else {
				for(Object selectedData : (Object[]) dataTable.getSelection()) {
					if(data.equals(selectedData))
						dataTable.getSelectedRowIndexes().add(dataTable.getRowIndex());
				}
			}
		}
		
		dataTable.setRowIndex(-1);
	}
	
	private void encodeColumnFilter(FacesContext facesContext, Column column, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = column.getClientId(facesContext) + "_filter";
		String filterEvent = "on" + column.getFilterEvent();
		String filterFunction = datatableVar + ".filter(this.value, \"" + column.getId() +"\")";
		
		writer.write("<br />");
			
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute(filterEvent, filterFunction , null);
		
		if(column.getFilterStyle() != null) writer.writeAttribute("style", column.getFilterStyle(), null);
		if(column.getFilterStyleClass() != null) writer.writeAttribute("class", column.getFilterStyleClass(), null);
		
		writer.endElement("input");
	}
	
	private void encodeHiddenInput(FacesContext facesContext, String id, Object value) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		if(value != null) {
			writer.writeAttribute("value", value, null);
		}
		writer.endElement("input");
		
	}
	private void encodeTable(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		
		writer.startElement("table", null);
		writer.writeAttribute("id", clientId + "_table", null);
		writer.writeAttribute("style", "display:none", null);
		
		writer.startElement("tbody", null);
		
		if(dataTable.isLazy()) {
			dataTable.loadLazyData();
		}
		
		int rowCountToRender = getNumberOfRowsToRender(dataTable);
		int first = dataTable.isDynamic() ? dataTable.getFirst() : 0;
		
		for(int i = first; i < (first + rowCountToRender); i++) {
			dataTable.setRowIndex(i);
			if(!dataTable.isRowAvailable())
				continue;
			
			if(dataTable.getRowIndexVar() != null) {
				facesContext.getExternalContext().getRequestMap().put(dataTable.getRowIndexVar(), i);
			}
			
			writer.startElement("tr", null);
			
			//rowIndex
			writer.startElement("td", null);
			writer.write(String.valueOf(dataTable.getRowIndex()));
			writer.endElement("td");
			
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
		
		writer.endElement("table");
	}
	
	private void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", id, null);
		writer.endElement("div");
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