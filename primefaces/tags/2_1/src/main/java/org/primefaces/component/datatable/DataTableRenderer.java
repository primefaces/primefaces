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
import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.component.column.Column;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.Cell;
import org.primefaces.model.LazyDataModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer implements PartialRenderer {

	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		DataTable dataTable = (DataTable) component;
		String clientId = dataTable.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		boolean isAjaxFilterRequest = params.containsKey(clientId + "_ajaxFilter");
		boolean isAjaxSortRequest = params.containsKey(clientId + "_ajaxSort");
		boolean isAjaxPageRequest = params.containsKey(clientId + "_ajaxPage");
		
		//Decode events
		if(isAjaxSortRequest) {
			decodeAjaxSortRequest(facesContext, dataTable);
		} else if(isAjaxFilterRequest) {
			decodeAjaxFilterRequest(facesContext, dataTable);
		} else if(isAjaxPageRequest) {
			decodeAjaxPageRequest(facesContext, dataTable);
		} else if(dataTable.isSelectionEnabled()) {
			decodeSelection(facesContext, dataTable);
		}
	}
	
	/**
	 * Update datatable with new pagination state
	 * 
	 * @param facesContext
	 * @param dataTable
	 */
	protected void decodeAjaxPageRequest(FacesContext facesContext, DataTable dataTable) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = dataTable.getClientId(facesContext);
		String firstParam = params.get(clientId + "_first");
		String rowsParam = params.get(clientId + "_rows");
		String pageParam = params.get(clientId + "_page");
		
		dataTable.setFirst(Integer.valueOf(firstParam));
		dataTable.setRows(Integer.valueOf(rowsParam));
		dataTable.setPage(Integer.valueOf(pageParam));
	}

	@SuppressWarnings("unchecked")
	protected void decodeAjaxSortRequest(FacesContext facesContext, DataTable dataTable) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = dataTable.getClientId(facesContext);
		String sortKey = params.get(clientId + "_sortKey");
		String sortDir = params.get(clientId + "_sortDir");
		
		List list = (List) dataTable.getValue();
		Collections.sort(list, new BeanPropertyComparator(findSortColumm(dataTable, sortKey), dataTable.getVar(), sortDir));
		dataTable.setValue(list);
		
		//Reset paginator
		dataTable.setFirst(0);
		dataTable.setPage(1);
	}

	@SuppressWarnings("unchecked")
	protected void decodeAjaxFilterRequest(FacesContext facesContext, DataTable dataTable) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		Map<String,ValueExpression> filterMap = dataTable.getFilterMap();
		List filteredData = new ArrayList();
		dataTable.setValue(null);	//Always work with user data
		
		for(int i = 0; i < dataTable.getRowCount(); i++) {
			dataTable.setRowIndex(i);
			boolean shouldAdd = true;
			
			for(String filterName : filterMap.keySet()) {
				Object columnValue = filterMap.get(filterName).getValue(facesContext.getELContext());
				String filterValue = params.get(filterName + "_filter");
				
				if(isValueBlank(filterValue)) {
					shouldAdd = true;
				}
				else if(columnValue == null) {
					shouldAdd = false;
					break;
				} else if(!String.valueOf(columnValue).toLowerCase().startsWith(filterValue.toLowerCase())) {
					shouldAdd = false;
					break;
				}
			}
			
			if(shouldAdd)
				filteredData.add(dataTable.getRowData());
		}
		
		dataTable.setRowIndex(-1);	//cleanup
		
		dataTable.setValue(filteredData);
		
		//Reset paginator
		dataTable.setFirst(0);
		dataTable.setPage(1);
	}

	protected void decodeSelection(FacesContext facesContext, DataTable dataTable) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = dataTable.getClientId(facesContext);
		String rowSelectParam = clientId + "_selected";
		
		String rowSelectParamValue = params.get(rowSelectParam);
				
		if(dataTable.isSingleSelectionMode())
			decodeSingleSelection(dataTable, rowSelectParamValue);
		else
			decodeMultipleSelection(dataTable, rowSelectParamValue);
			
		dataTable.setRowIndex(-1);	//clean	
	}
	
	protected void decodeSingleSelection(DataTable dataTable, String rowSelectParamValue) {
		if(isValueBlank(rowSelectParamValue)) {
			dataTable.setSelection(null);
		} else {
			if(dataTable.isCellSelection()) {
				dataTable.setSelection(buildCell(dataTable, rowSelectParamValue));
			} else {
				dataTable.setRowIndex(Integer.parseInt(rowSelectParamValue));
				Object data = dataTable.getRowData();
				dataTable.setSelection(data);
			}
		}
	}
	
	protected void decodeMultipleSelection(DataTable dataTable, String rowSelectParamValue) {
		Class<?> clazz = dataTable.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());
		
		if(isValueBlank(rowSelectParamValue)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			dataTable.setSelection(data);
		} else {
			if(dataTable.isCellSelection()) {
				String[] cellInfos = rowSelectParamValue.split(",");
				Cell[] cells = new Cell[cellInfos.length];
				
				for(int i = 0; i < cellInfos.length; i++) {
					cells[i] = buildCell(dataTable, cellInfos[i]);
					dataTable.setRowIndex(-1);	//clean
				}
				
				dataTable.setSelection(cells);
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
	}
	
	protected Cell buildCell(DataTable dataTable, String value) {
		String[] cellInfo = value.split("#");
		
		//Column
		UIColumn column = dataTable.getColumnByClientId(cellInfo[1]);
		String columnId = cellInfo[1];
		
		//RowData
		dataTable.setRowIndex(Integer.parseInt(cellInfo[0]));
		Object rowData = dataTable.getRowData();
		
		//Cell value
		Object cellValue = null;
		UIComponent columnChild = column.getChildren().get(0);
		if(columnChild instanceof ValueHolder) {
			cellValue = ((ValueHolder) columnChild).getValue();
		}
		
		return new Cell(rowData, columnId, cellValue);
	}

	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		DataTable dataTable = (DataTable) component;
	
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<?xml version=\"1.0\" encoding=\"" + response.getCharacterEncoding() + "\"?>");
		writer.write("<data-response>");
		
		//Data
		writer.write("<table>");
		writer.startCDATA();
			encodeTable(facesContext, dataTable);
		writer.endCDATA();
		writer.write("</table>");
		
		//Total records
		writer.write("<row-count>");
			writer.write(String.valueOf(dataTable.getRowCount()));
		writer.write("</row-count>");
		
		//State
		writer.write("<state>");
		writer.startCDATA();
			StateManager stateManager = facesContext.getApplication().getStateManager();
			stateManager.writeState(facesContext, stateManager.saveView(facesContext));
		writer.endCDATA();
		writer.write("</state>");

		writer.write("</data-response>");
	}
	
	protected Column findSortColumm(DataTable dataTable, String sortKey) {
		for(Iterator<UIComponent> children = dataTable.getChildren().iterator(); children.hasNext();) {
			UIComponent kid = children.next();
			
			if(kid.isRendered() && kid instanceof Column) {
				if(kid.getClientId(FacesContext.getCurrentInstance()).equals(sortKey)) {
					return (Column) kid;
				}
			}
		}
		
		return null;
	}
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;
		
		encodeMarkup(facesContext, table);
		encodeScript(facesContext, table);
	}
	
	protected void encodeScript(FacesContext facesContext, DataTable dataTable) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, dataTable);
		
		writer.startElement("script", dataTable);
		writer.writeAttribute("type", "text/javascript", null);
		
		String columnDefVar = encodeColumnDefinition(facesContext, dataTable, widgetVar);
		String datasourceVar = encodeDatasource(facesContext, dataTable, widgetVar);
		String datatableType = dataTable.isScrollable() ? "ScrollingDataTable"  : "DataTable";
	
		writer.write(widgetVar + " = new PrimeFaces.widget." + datatableType + "('" + clientId + "'," + columnDefVar + "," + datasourceVar + ", {");
		encodeConfig(facesContext, dataTable);
		writer.write("});");
		
		writer.endElement("script");
	}
	
	protected String encodeDatasource(FacesContext facesContext, DataTable dataTable, String var) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String datasourceVar = var + "_datasource";
		
		writer.write("var " + datasourceVar + " = new YAHOO.util.DataSource(YAHOO.util.Dom.get('" + dataTable.getClientId(facesContext) + "_table'));\n");
		
		writer.write(datasourceVar + ".responseType = YAHOO.util.DataSource.TYPE_HTMLTABLE;\n");
		writer.write(datasourceVar + ".responseSchema = {fields:[");
		
		boolean firstWritten = false;
		for(UIComponent kid : dataTable.getChildren()) {
			if(kid.isRendered() && kid instanceof Column) {
				if(firstWritten)
					writer.write(",");
				else
					firstWritten=true;
				
				writer.write("{key:'" + kid.getClientId(facesContext) + "'}");
			}
		}
		
		writer.write(",{key:'rowIndex'}");
		
		writer.write("]");
		
		writer.write("};\n");
		
		return datasourceVar;
	}
	
	protected String encodeColumnDefinition(FacesContext facesContext, DataTable dataTable, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String columnDefVar = datatableVar + "_columnDef";
		boolean firstWritten = false;
		
		writer.write("var " + columnDefVar + " = [");
				
		for(UIComponent kid : dataTable.getChildren()) {
			if(kid.isRendered() && kid instanceof Column) {
				Column column = (Column) kid;
				
				if(firstWritten)
					writer.write(",");
				else
					firstWritten=true;
				
				writer.write("{key:'" + column.getClientId(facesContext)  + "'");
				
				//header
				UIComponent header = column.getFacet("header");
				writer.write(",label:'");
				if(header != null) {
					if(ComponentUtils.isLiteralText(header)) {
						String literalText = header.toString().trim();
						ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), literalText, Object.class);
						Object value = ve.getValue(facesContext.getELContext());
						if(value != null) {
							writer.write(value.toString());
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
						writer.write(",sortOptions:{field:'" + column.getClientId(facesContext) + "'");
						if(column.getSortFunction() != null) {
							writer.write(",sortFunction:" + column.getSortFunction().getExpressionString());
						}
						writer.write("}");
						
						if(column.getParser() != null) writer.write(",parser:'" + column.getParser() + "'");
					}
				}
				
				writer.write("}");
			}
		}
		
		writer.write(",{key:'rowIndex', hidden:true}");
		
		writer.write("];\n");
		
		return columnDefVar;
	}
	
	protected void encodeConfig(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		String formClientId = null;
		String url = getActionURL(facesContext);
		String selectionMode = dataTable.getSelectionMode();
		
		if(dataTable.isDynamic() || dataTable.getUpdate() != null) {
			UIComponent form = ComponentUtils.findParentForm(facesContext, dataTable);
			if(form == null) {
				throw new FacesException("DataTable : \"" + clientId + "\" must be inside a form element when dynamic data or ajax selection is enabled");
			}
			
			formClientId = form.getClientId(facesContext);
		}
		
		if(dataTable.isDynamic()) {
			writer.write("dynamicData:true");
			writer.write(",formId:'" + formClientId + "'");
			writer.write(",url:'" + url+ "'");
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
			
			if(dataTable.getPageLinks() != 10) writer.write(",pageLinks:" + dataTable.getPageLinks());
			if(dataTable.getPaginatorTemplate() != null) writer.write(",template:'" + dataTable.getPaginatorTemplate() + "'");
			if(dataTable.getRowsPerPageTemplate() != null) writer.write(",rowsPerPageOptions : [" + dataTable.getRowsPerPageTemplate() + "]");
			if(dataTable.getFirstPageLinkLabel() != null) writer.write(",firstPageLinkLabel:'" + dataTable.getFirstPageLinkLabel() + "'");
			if(dataTable.getPreviousPageLinkLabel() != null) writer.write(",previousPageLinkLabel:'" + dataTable.getPreviousPageLinkLabel() + "'");
			if(dataTable.getNextPageLinkLabel() != null) writer.write(",nextPageLinkLabel:'" + dataTable.getNextPageLinkLabel() + "'");
			if(dataTable.getLastPageLinkLabel() != null) writer.write(",lastPageLinkLabel:'" + dataTable.getLastPageLinkLabel() + "'");
			if(dataTable.getCurrentPageReportTemplate() != null) writer.write(",pageReportTemplate:'" + dataTable.getCurrentPageReportTemplate() + "'");
			if(!dataTable.isPaginatorAlwaysVisible()) writer.write(",alwaysVisible:false");
			
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
		
		if(selectionMode != null) {
			String mode = selectionMode.equals("multiple") ? "standard" : selectionMode;
			writer.write(",selectionMode:'" + mode + "'");
			
			if(dataTable.isDblClickSelect()) writer.write(",dblClickSelect:true");
			
			if(dataTable.getUpdate() != null) {
				writer.write(",update:'" + ComponentUtils.findClientIds(facesContext, dataTable.getParent(), dataTable.getUpdate()) + "'");
				
				if(dataTable.getOnselectStart() != null) writer.write(",onselectStart:function(xhr){" + dataTable.getOnselectStart() + ";}");
				if(dataTable.getOnselectComplete() != null) writer.write(",onselectComplete:function(xhr, status, args){" + dataTable.getOnselectComplete() + ";}");
				
				if(!dataTable.isDynamic()) {
					writer.write(",formId:'" + formClientId + "'");
					writer.write(",url:'" + url + "'");
				}
			}
		}
	}

	protected void encodeMarkup(FacesContext facesContext, DataTable dataTable) throws IOException{
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
		encodeTable(facesContext, dataTable);
		writer.endElement("div");
		
		if(dataTable.isPaginator() && dataTable.getPaginatorPosition().equals("bottom")) {
			encodePaginatorContainer(facesContext, clientId + "_paginatorBottom");
		}
		
		if(dataTable.isSelectionEnabled()) {
			encodeHiddenInput(facesContext, clientId + "_selected", dataTable.getSelectedAsString());
		}
		
		if(dataTable.isPaginator() && !dataTable.isDynamic()) {
			encodeHiddenInput(facesContext, clientId + "_page", dataTable.getPage());
		}
		
		writer.endElement("div");
	}

	protected void encodeColumnFilter(FacesContext facesContext, Column column, String datatableVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = column.getClientId(facesContext) + "_filter";
		String filterEvent = "on" + column.getFilterEvent();
		String filterFunction = datatableVar + ".filter(this.value, \"" + column.getClientId(facesContext) +"\")";
		String filterValue = params.containsKey(clientId) ? params.get(clientId) : "";
 		
		writer.write("<br />");
			
		writer.startElement("input", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("value", filterValue , null);
		writer.writeAttribute(filterEvent, filterFunction , null);

		if(column.getFilterStyle() != null) writer.writeAttribute("style", column.getFilterStyle(), null);
		if(column.getFilterStyleClass() != null) writer.writeAttribute("class", column.getFilterStyleClass(), null);
		
		writer.endElement("input");
	}
	
	protected void encodeHiddenInput(FacesContext facesContext, String id, Object value) throws IOException {
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
	
	protected void encodeTable(FacesContext facesContext, DataTable dataTable) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dataTable.getClientId(facesContext);
		Object selection = dataTable.getSelection();
		boolean selectionEnabled = dataTable.isSelectionEnabled() && selection != null;
		
		if(dataTable.isLazy() && dataTable.getValue() instanceof LazyDataModel<?>) {
			dataTable.loadLazyData();
		}
		
		writer.startElement("table", null);
		writer.writeAttribute("id", clientId + "_table", null);
		writer.writeAttribute("style", "display:none", null);
		
		writer.startElement("tbody", null);
		
		int rowCountToRender = getNumberOfRowsToRender(dataTable);
		int first = dataTable.getFirst();
		
		for(int i = first; i < (first + rowCountToRender); i++) {
			dataTable.setRowIndex(i);
			if(!dataTable.isRowAvailable())
				continue;
			
			//Preselection
			if(selectionEnabled) {
				handlePreselection(dataTable, selection);
			}
			
			//Row index var
			if(dataTable.getRowIndexVar() != null) {
				facesContext.getExternalContext().getRequestMap().put(dataTable.getRowIndexVar(), i);
			}
			
			writer.startElement("tr", null);
			
			for(Iterator<UIComponent> iterator = dataTable.getChildren().iterator(); iterator.hasNext();) {
				UIComponent kid = iterator.next();
				
				if(kid.isRendered() && kid instanceof Column) {
					Column column = (Column) kid;
					
					writer.startElement("td", null);
					renderChildren(facesContext, column);
					writer.endElement("td");
				}
			}
			
			//rowIndex
			writer.startElement("td", null);
			writer.write(String.valueOf(dataTable.getRowIndex()));
			writer.endElement("td");
			
			writer.endElement("tr");
		}
		
		//Cleanup
		dataTable.setRowIndex(-1);		
		if(dataTable.getRowIndexVar() != null) {
			facesContext.getExternalContext().getRequestMap().remove(dataTable.getRowIndexVar());
		}
		
		writer.endElement("tbody");
		
		writer.endElement("table");
	}
	
	protected void handlePreselection(DataTable dataTable, Object selection) {
		Object rowData = dataTable.getRowData();
		boolean isSingleSelection = dataTable.isSingleSelectionMode();
		boolean isCellSelection = dataTable.isCellSelection();
		
		if(isCellSelection) {
			String columnId = isCellSelected(rowData, selection, isSingleSelection);
			
			if(columnId != null) {
				//Multiple cells in a row
				if(columnId.contains(",")) {
					String[] columnIds = columnId.split(",");
					
					for(String cid : columnIds)
						dataTable.getSelected().add(dataTable.getRowIndex() + "#" + cid);
				}
				//Single cell in a row
				else {
					dataTable.getSelected().add(dataTable.getRowIndex() + "#" + columnId);
				}
			}
				
		} else if(isRowSelected(dataTable.getRowData(), selection, isSingleSelection)) {
			dataTable.getSelected().add(String.valueOf(dataTable.getRowIndex()));
		}
	}
	
	protected boolean isRowSelected(Object rowData, Object selection, boolean single) {
		if(single) {
			return rowData.equals(selection);
		} else {
			Object[] rows = (Object[]) selection;
			for(Object row : rows) {
				if(rowData.equals(row))
					return true;
			}
			
			return false;
		}
	}
	
	protected String isCellSelected(Object rowData, Object selection, boolean single) {
		if(single) {
			Cell cell = (Cell) selection;
			if(rowData.equals(cell.getRowData()))
				return cell.getColumnId();
			else
				return null;
			
		} else {
			StringBuffer buffer = new StringBuffer();
			Cell[] cells = (Cell[]) selection;
			
			for(Cell cell : cells) {
				if(rowData.equals(cell.getRowData())) {
					buffer.append(cell.getColumnId());
					buffer.append(",");
				}
			}
		
			return buffer.length() > 0 ? buffer.toString() : null;
		}
	}
	
	protected void encodePaginatorContainer(FacesContext facesContext, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("class", "ui-paginator ui-widget-header ui-corner-all", null);
		writer.endElement("div");
	}
	
	protected int getNumberOfRowsToRender(DataTable dataTable) {
		if(dataTable.isDynamic())
			return dataTable.getRows() == 0 ? dataTable.getRowCount() : dataTable.getRows();
		else
			return dataTable.getRowCount();
	}
	
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do Nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}