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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Array;
import java.util.Collection;
import javax.el.ValueExpression;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
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
            decodeSortRequest(context, table, clientId, params);
        } else if(table.isFilterRequest(context)) {
            decodeFilterRequest(context, table, clientId, params);
        } else if(table.isSelectionEnabled()) {
			decodeSelection(context, table, clientId, params);
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

	protected void decodeSortRequest(FacesContext context, DataTable table, String clientId, Map<String,String> params) {
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

	protected void decodeFilterRequest(FacesContext context, DataTable table, String clientId, Map<String,String> params) {
		Map<String,ValueExpression> filterMap = table.getFilterMap();
		List filteredData = new ArrayList();
		table.setValue(null);	//Always work with user data

		for(int i = 0; i < table.getRowCount(); i++) {
			table.setRowIndex(i);
			boolean shouldAdd = true;

			for(String filterName : filterMap.keySet()) {
				Object columnValue = filterMap.get(filterName).getValue(context.getELContext());
				String filterValue = params.get(filterName);

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

			if(shouldAdd) {
				filteredData.add(table.getRowData());
            }
		}

		table.setRowIndex(-1);	//cleanup

		table.setValue(filteredData);

		//Reset paginator
		table.setFirst(0);
		table.setPage(1);

        //Metadata for callback
        if(table.isPaginator()) {
            RequestContext.getCurrentInstance().addCallbackParam("totalRecords", filteredData.size());
        }
	}

    protected void decodeSelection(FacesContext context, DataTable table, String clientId, Map<String,String> params) {
		String selection = params.get(clientId + "_selection");

		if(table.isSingleSelectionMode())
			decodeSingleSelection(table, selection);
		else
			decodeMultipleSelection(table, selection);

        table.setRowIndex(-1);	//clean

        //Instant selection and unselection
        queueInstantSelectionEvent(context, table, clientId, params);
        
		table.setRowIndex(-1);	//clean
	}

    protected void queueInstantSelectionEvent(FacesContext context, DataTable table, String clientId, Map<String,String> params) {

		if(table.isInstantSelectionRequest(context)) {
            int selectedRowIndex = Integer.parseInt(params.get(clientId + "_instantSelectedRowIndex"));
            table.setRowIndex(selectedRowIndex);
            SelectEvent selectEvent = new SelectEvent(table, table.getRowData());
            selectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
            table.queueEvent(selectEvent);
        }
        else if(table.isInstantUnselectionRequest(context)) {
            int unselectedRowIndex = Integer.parseInt(params.get(clientId + "_instantUnselectedRowIndex"));
            table.setRowIndex(unselectedRowIndex);
            UnselectEvent unselectEvent = new UnselectEvent(table, table.getRowData());
            unselectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
            table.queueEvent(unselectEvent);
        }
	}

    protected void decodeSingleSelection(DataTable table, String selection) {
		if(isValueBlank(selection)) {
			table.setSelection(null);
            table.setEmptySelected(true);
		} else {
            table.setRowIndex(Integer.parseInt(selection));
            Object data = table.getRowData();
            
            table.setSelection(data);
		}
	}
	
	protected void decodeMultipleSelection(DataTable table, String selection) {
		Class<?> clazz = table.getValueExpression("selection").getType(FacesContext.getCurrentInstance().getELContext());

		if(isValueBlank(selection)) {
			Object data = Array.newInstance(clazz.getComponentType(), 0);
			table.setSelection(data);
		} else {
            String[] rowSelectValues = selection.split(",");
            Object data = Array.newInstance(clazz.getComponentType(), rowSelectValues.length);

            for(int i = 0; i < rowSelectValues.length; i++) {
                table.setRowIndex(Integer.parseInt(rowSelectValues[i]));

                Array.set(data, i, table.getRowData());
            }

            table.setSelection(data);
		}
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		DataTable table = (DataTable) component;

        if(table.isDataManipulationRequest(context)) {
            encodeTbody(context, table);
        } 
        else if(table.isRowExpansionRequest(context)) {
            encodeRowExpansion(context, table);
        }
        else {
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

        //Selection
        if(table.isSelectionEnabled()) {
            encodeSelectionConfig(context, table);
        }

        //Row expansion
        if(table.getFacet("expansion") != null) {
            writer.write(",expansion:true");
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
        encodeTFoot(context, table);
        writer.endElement("table");

        if(table.isPaginator()) {
            encodePaginatorMarkup(context, table);
        }
        
        encodeFacet(context, table, table.getFooter(), DataTable.FOOTER_CLASS);

        if(table.isSelectionEnabled()) {
            encodeSelectionHolder(context, table);
        }

        writer.endElement("div");
	}

    protected void encodeColumnHeader(FacesContext context, DataTable table, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getClientId(context);
        boolean isSortable = column.getValueExpression("sortBy") != null;
        boolean hasFilter = column.getValueExpression("filterBy") != null;
        String widgetVar = createUniqueWidgetVar(context, table);
        
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        String columnClass = isSortable ? DataTable.COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.COLUMN_HEADER_CLASS;
        if(styleClass != null) {
            columnClass = columnClass + " " + styleClass;
        }

        writer.startElement("th", null);
        writer.writeAttribute("id", column.getClientId(context), null);
        writer.writeAttribute("class", columnClass, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowpan() != 1) writer.writeAttribute("rowspan", column.getRowpan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);

        //Sort icon
        if(isSortable) {
            writer.startElement("span", null);
            writer.writeAttribute("class", DataTable.SORTABLE_COLUMN_ICON_CLASS, null);
            writer.endElement("span");
        }

        //Header content
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();
        if(header != null) {
            header.encodeAll(context);
        } else if(headerText != null) {
            writer.write(headerText);
        } else {
            renderChildren(context, header);
        }

        //Filter
        if(hasFilter) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String filterId = clientId + "_filter";
            String filterStyleClass = column.getFilterStyleClass();
            filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_FILTER_CLASS : DataTable.COLUMN_FILTER_CLASS + " " + filterStyleClass;

            String filterEvent = "on" + column.getFilterEvent();
            String filterFunction = widgetVar + ".filter()";
            String filterValue = params.containsKey(clientId) ? params.get(clientId) : "";

            writer.startElement("input", null);
            writer.writeAttribute("id", filterId, null);
            writer.writeAttribute("name", filterId, null);
            writer.writeAttribute("class", filterStyleClass, null);
            writer.writeAttribute("value", filterValue , null);
            writer.writeAttribute(filterEvent, filterFunction , null);

            if(column.getFilterStyle() != null) {
                writer.writeAttribute("style", column.getFilterStyle(), null);
            }

            writer.endElement("input");
        }

        writer.endElement("th");
    }

    protected void encodeColumnFooter(FacesContext context, DataTable table, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        String footerClass = styleClass != null ? DataTable.COLUMN_FOOTER_CLASS + " " + styleClass : DataTable.COLUMN_FOOTER_CLASS;

        writer.startElement("td", null);
        writer.writeAttribute("class", footerClass, null);
        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getRowpan() != 1) writer.writeAttribute("rowspan", column.getRowpan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);

        //Header content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if(facet != null) {
            facet.encodeAll(context);
        } else if(text != null) {
            writer.write(text);
        } else {
            renderChildren(context, column);
        }

        writer.endElement("td");
    }

    /**
     * Render column headers either in single row or nested if a columnGroup is defined
     */
    protected void encodeThead(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = table.getColumnGroup("header");

        writer.startElement("thead", null);

        if(group != null) {

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered() && child instanceof Row) {
                    Row headerRow = (Row) child;

                    writer.startElement("tr", null);

                    for(UIComponent headerRowChild : headerRow.getChildren()) {
                        if(headerRowChild.isRendered() && headerRowChild instanceof Column) {
                            encodeColumnHeader(context, table, (Column) headerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }

        } else {
            Columns dynamicColumns = table.getDynamicColumns();

            writer.startElement("tr", null);

            if(dynamicColumns == null) {
                for(Column column : table.getColumns()) {
                    encodeColumnHeader(context, table, column);
                }
                
            } else {
                Collection columnCollection = (Collection) dynamicColumns.getValue();
                String columnVar = dynamicColumns.getVar();
                
                for(Object column : columnCollection) {
                    context.getExternalContext().getRequestMap().put(columnVar, column);
                    UIComponent header = dynamicColumns.getFacet("header");

                    writer.startElement("th", null);
                    writer.writeAttribute("class", DataTable.COLUMN_HEADER_CLASS, null);

                    if(header != null) {
                        header.encodeAll(context);
                    }

                    writer.endElement("th");
                }

                context.getExternalContext().getRequestMap().remove(columnVar);
            }

            writer.endElement("tr");
        }

        writer.endElement("thead");
    }

    protected void encodeTbody(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);
        Columns dynamicColumns = table.getDynamicColumns();
        
        writer.startElement("tbody", null);
        writer.writeAttribute("id", clientId + "_data", null);
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
            writer.writeAttribute("id", clientId + "_row_" + i, null);
            writer.writeAttribute("class", DataTable.ROW_CLASS, null);

            if(dynamicColumns == null) {

                for(Column column : table.getColumns()) {
                    writer.startElement("td", null);
                    if(column.getStyle() != null) {
                        writer.writeAttribute("style", column.getStyle(), null);
                    }

                    if(column.isExpansion()) {
                        writer.writeAttribute("class", DataTable.EXPANSION_COLUMN_CLASS, null);

                        encodeRowExpander(context, table);
                    }
                    else if(column.isEditor()) {
                        writer.writeAttribute("class", DataTable.ROW_EDITOR_COLUMN_CLASS, null);

                        encodeRowEditor(context, table);
                    }
                    else {
                        CellEditor editor = column.getCellEditor();
                        if(editor != null) {
                            writer.writeAttribute("class", DataTable.EDITABLE_CELL_CLASS, null);
                        }
                       
                        writer.startElement("span", null);
                        if(editor == null) {
                            column.encodeAll(context);
                        } else {
                            for(UIComponent columnChild : column.getChildren()) {
                                if(!(columnChild instanceof CellEditor)) {
                                    columnChild.encodeAll(context);
                                }
                            }
                        }
                        writer.endElement("span");

                        editor.encodeAll(context);
                    }

                    writer.endElement("td");
                }

            } else {

                Collection columnCollection = (Collection) dynamicColumns.getValue();
                String columnVar = dynamicColumns.getVar();
                String columnIndexVar = dynamicColumns.getColumnIndexVar();
                int colIndex = 0;

                for(Object column : columnCollection) {
                    context.getExternalContext().getRequestMap().put(columnVar, column);
                    context.getExternalContext().getRequestMap().put(columnIndexVar, colIndex);
                    UIComponent header = dynamicColumns.getFacet("header");

                    writer.startElement("td", null);
                    writer.startElement("span", null);
                    dynamicColumns.encodeAll(context);
                    writer.endElement("span");
                    writer.endElement("td");

                    colIndex++;
                }

                context.getExternalContext().getRequestMap().remove(columnVar);
                context.getExternalContext().getRequestMap().remove(columnIndexVar);
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

    protected void encodeTFoot(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ColumnGroup group = table.getColumnGroup("footer");
        boolean shouldRender = table.hasFooterColumn() || group != null;

        if(!shouldRender)
            return;

        writer.startElement("tfoot", null);

        if(group != null) {

            for(UIComponent child : group.getChildren()) {
                if(child.isRendered() && child instanceof Row) {
                    Row footerRow = (Row) child;

                    writer.startElement("tr", null);

                    for(UIComponent footerRowChild : footerRow.getChildren()) {
                        if(footerRowChild.isRendered() && footerRowChild instanceof Column) {
                            encodeColumnFooter(context, table, (Column) footerRowChild);
                        }
                    }

                    writer.endElement("tr");
                }
            }

        }
        else {
            writer.startElement("tr", null);

            for(Column column : table.getColumns()) {
                encodeColumnFooter(context, table, column);
            }

            writer.endElement("tr");
        }
        
        writer.endElement("tfoot");
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

    protected void encodeSelectionConfig(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write(",selectionMode:'" + table.getSelectionMode() + "'");

        if(table.isDblClickSelect()) {
            writer.write(",dblclickSelect:true");
        }

        //update is deprecated and used for backward compatibility
        String onRowSelectUpdate = table.getOnRowSelectUpdate() != null ? table.getOnRowSelectUpdate() : table.getUpdate();

        if(table.getRowSelectListener() != null || onRowSelectUpdate != null) {
            writer.write(",instantSelect:true");

            if(onRowSelectUpdate != null) {
                writer.write(",onRowSelectUpdate:'" + ComponentUtils.findClientIds(context, table.getParent(), onRowSelectUpdate) + "'");
            }

            //onselectstart and onselectcomplete are deprecated but still here for backward compatibility for some time
            if(table.getOnselectStart() != null) writer.write(",onRowSelectStart:function(xhr) {" + table.getOnselectStart() + "}");
            if(table.getOnselectComplete() != null) writer.write(",onRowSelectComplete:function(xhr, status, args) {" + table.getOnselectComplete() + "}");
            if(table.getOnRowSelectStart() != null) writer.write(",onRowSelectStart:function(xhr) {" + table.getOnRowSelectStart() + "}");
            if(table.getOnRowSelectComplete() != null) writer.write(",onRowSelectComplete:function(xhr, status, args) {" + table.getOnRowSelectComplete() + "}");
        }

        if(table.getRowSelectListener() != null) {
            writer.write(",instantUnselect:true");
            
            if(table.getOnRowUnselectUpdate() != null) {
                writer.write(",onRowUnselectUpdate:'" + ComponentUtils.findClientIds(context, table.getParent(), table.getOnRowUnselectUpdate()) + "'");
            }
        }
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

    protected void encodeSelectionHolder(FacesContext context, DataTable table) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String id = table.getClientId(context) + "_selection";

		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("name", id, null);
		writer.endElement("input");
	}
	
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}

    protected void encodeRowExpander(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.ROW_EXPANDER_CLASS + " ui-icon ui-icon-circle-triangle-e", null);
        writer.endElement("span");
    }

    protected void encodeRowEditor(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String widgetVar = createUniqueWidgetVar(context, table);

        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.ROW_EDITOR_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-pencil", null);
        writer.writeAttribute("onclick", widgetVar + ".showEditors(this)", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-check", null);
        writer.writeAttribute("style", "display:none", null);
        writer.writeAttribute("onclick", widgetVar + ".saveRowEdit(this)", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-close", null);
        writer.writeAttribute("style", "display:none", null);
        writer.writeAttribute("onclick", widgetVar + ".cancelRowEdit(this)", null);
        writer.endElement("span");

        writer.endElement("span");
    }

    protected void encodeRowExpansion(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int expandedRowId = Integer.parseInt(params.get(table.getClientId(context) + "_expandedRowId"));

        table.setRowIndex(expandedRowId);

        writer.startElement("tr", null);
        writer.writeAttribute("style", "display:none", null);
        writer.writeAttribute("class", DataTable.EXPANDED_ROW_CONTENT_CLASS + " ui-widget-content", null);

        writer.startElement("td", null);
        writer.writeAttribute("colspan", table.getColumns().size(), null);

        table.getFacet("expansion").encodeAll(context);

        writer.endElement("td");

        writer.endElement("tr");

        table.setRowIndex(-1);
    }

    
}