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
import java.util.Collection;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DataTableRenderer extends CoreRenderer {

    private DataHelper dataHelper;

    public DataTableRenderer() {
        dataHelper = new DataHelper();
    }

	@Override
	public void decode(FacesContext context, UIComponent component) {
		DataTable table = (DataTable) component;

        if(table.isPaginationRequest(context)) {
            dataHelper.decodePageRequest(context, table);
        } else if(table.isSortRequest(context)) {
            dataHelper.decodeSortRequest(context, table);
        } else if(table.isFilterRequest(context)) {
            dataHelper.decodeFilterRequest(context, table);
        } else if(table.isClearFiltersRequest(context)) {
            table.resetValue();
            context.renderResponse();
            return;
        }

        if(table.isSelectionEnabled()) {
			dataHelper.decodeSelection(context, table);
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
        else if(table.isRowEditRequest(context)) {
            encodeEditedRow(context, table);
        }
        else if(table.isScrollingRequest(context)) {
            encodeLiveRows(context, table);
        }
        else {
            encodeMarkup(context, table);
            encodeScript(context, table);
        }
	}
	
	protected void encodeScript(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);

		writer.startElement("script", table);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write(table.resolveWidgetVar() + " = new PrimeFaces.widget.DataTable('" + clientId + "',{");

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
        if(table.isRowSelectionEnabled()) {
            encodeSelectionConfig(context, table);
        }

        if(table.isColumnSelectionEnabled()) {
            writer.write(",columnSelectionMode:'" + table.getColumnSelectionMode() + "'");
        }

        //Row expansion
        if(table.getFacet("expansion") != null) {
            writer.write(",expansion:true");
        }

        if(table.isScrollable()) {
            writer.write(",scrollable:true");
            writer.write(",liveScroll:" + table.isLiveScroll());
            writer.write(",scrollStep:" + table.getRows());
            writer.write(",scrollLimit:" + table.getRowCount());

            if(table.getHeight() != Integer.MIN_VALUE) {
                writer.write(",height:" + table.getHeight());
            }
        }

        if(table.getOnRowEditUpdate() != null) {
            writer.write(",onRowEditUpdate:'" + ComponentUtils.findClientIds(context, form, table.getOnRowEditUpdate()) + "'");
        }

        writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, DataTable table) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        String containerClass = table.getStyleClass() != null ? DataTable.CONTAINER_CLASS + " " + table.getStyleClass() : DataTable.CONTAINER_CLASS;
        String style = null;
        boolean hasPaginator = table.isPaginator();
        String paginatorPosition = table.getPaginatorPosition();

        writer.startElement("div", table);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, clientId);
        if((style = table.getStyle()) != null) {
            writer.writeAttribute("style", style, clientId);
        }

        encodeFacet(context, table, table.getHeader(), DataTable.HEADER_CLASS);

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, table, "top");
        }

        writer.startElement("table", null);
        encodeThead(context, table);
        encodeTbody(context, table);
        encodeTFoot(context, table);
        writer.endElement("table");

        if(hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, table, "bottom");
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
        String selectionMode = column.getSelectionMode();
        
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        String columnClass = isSortable ? DataTable.COLUMN_HEADER_CLASS + " " + DataTable.SORTABLE_COLUMN_CLASS : DataTable.COLUMN_HEADER_CLASS;
        columnClass = selectionMode != null ? columnClass + " " + DataTable.SELECTION_COLUMN_CLASS : columnClass;
        columnClass = styleClass != null ? columnClass + " " + styleClass : columnClass;

        writer.startElement("th", null);
        writer.writeAttribute("id", clientId, null);
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

        if(selectionMode != null && selectionMode.equalsIgnoreCase("multiple")) {
            writer.startElement("input", header);
            writer.writeAttribute("type", "checkbox", null);
            writer.writeAttribute("name", clientId + "_checkAll", null);
            writer.writeAttribute("onclick", table.resolveWidgetVar() + ".toggleCheckAll(this)", null);
            writer.endElement("input");
        }
        else {
            if(header != null) {
                header.encodeAll(context);
            } else if(headerText != null) {
                writer.write(headerText);
            }
        }

        

        //Filter
        if(hasFilter) {
            encodeFilter(context, table, column);
        }

        writer.endElement("th");
    }

    protected void encodeFilter(FacesContext context, DataTable table, Column column) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();

        String widgetVar = table.resolveWidgetVar(); 
        String filterId = column.getClientId(context) + "_filter";
        String filterFunction = widgetVar + ".filter()";
        String filterStyleClass = column.getFilterStyleClass();
        filterStyleClass = filterStyleClass == null ? DataTable.COLUMN_FILTER_CLASS : DataTable.COLUMN_FILTER_CLASS + " " + filterStyleClass;

        if(column.getValueExpression("filterOptions") == null) {
            String filterEvent = "on" + column.getFilterEvent();
            String filterValue = params.containsKey(filterId) ? params.get(filterId) : "";

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
        else {
            writer.startElement("select", null);
            writer.writeAttribute("id", filterId, null);
            writer.writeAttribute("name", filterId, null);
            writer.writeAttribute("class", filterStyleClass, null);
            writer.writeAttribute("onchange", filterFunction, null);

            SelectItem[] itemsArray = (SelectItem[]) getFilterOptions(column);

            for(SelectItem item : itemsArray) {
                writer.startElement("option", null);
                writer.writeAttribute("value", item.getValue(), null);
                writer.write(item.getLabel());
                writer.endElement("option");
            }

            writer.endElement("select");
        }
        
    }

    protected SelectItem[] getFilterOptions(Column column) {
        Object options = column.getFilterOptions();
        
        if(options instanceof SelectItem[]) {
            return (SelectItem[]) options;
        } else if(options instanceof Collection<?>) {
            return ((Collection<SelectItem>) column.getFilterOptions()).toArray(new SelectItem[] {});
        } else {
            throw new FacesException("Filter options for column " + column.getClientId() + " should be a SelectItem array or collection");
        }
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
        String emptyMessage = table.getEmptyMessage();
        String selectionMode = table.getSelectionMode();
        String columnSelectionMode = table.getColumnSelectionMode();
        String selMode = selectionMode != null ? selectionMode : columnSelectionMode != null ? columnSelectionMode : null;
        Object selection = table.getSelection();

        //Load lazy data initially
        if(table.isLazy() && !table.initiallyLoaded()) {
            table.loadLazyData();
            table.markAsLoaded();
        }
      
        writer.startElement("tbody", null);
        writer.writeAttribute("id", clientId + "_data", null);
        writer.writeAttribute("class", DataTable.DATA_CLASS, null);

        int rows = table.getRows();
		int first = table.getFirst();
        int rowCountToRender = rows == 0 ? table.getRowCount() : rows;

        if(rowCountToRender != 0) {
            for(int i = first; i < (first + rowCountToRender); i++) {
                encodeRow(context, table, clientId, i, rowIndexVar, dynamicColumns, selMode, selection);
            }
        }
        else if(emptyMessage != null){
            //Empty message
            writer.startElement("tr", null);
            writer.writeAttribute("class", DataTable.ROW_CLASS, null);
            writer.startElement("td", null);
            writer.writeAttribute("colspan", table.getColumns().size(), null);
            writer.write(emptyMessage);
            writer.endElement("td");
            writer.endElement("tr");
        }
		
        writer.endElement("tbody");

		//Cleanup
		table.setRowIndex(-1);
		if(rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
    }

    protected void encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex, String rowIndexVar, Columns dynamicColumns, String selectionMode, Object selection) throws IOException {
        table.setRowIndex(rowIndex);
        if(!table.isRowAvailable()) {
            return;
        }

        //Preselection
        boolean selected = handlePreselection(table, rowIndex, selectionMode, selection);

        ResponseWriter writer = context.getResponseWriter();
        String rowStyleClass = table.getRowStyleClass();
        rowStyleClass = rowStyleClass == null ? DataTable.ROW_CLASS : DataTable.ROW_CLASS + " " + rowStyleClass;

        if(selected && table.getSelectionMode() != null) {
            rowStyleClass = rowStyleClass + " ui-selected ui-state-highlight";
        }

        //Row index var
        if(rowIndexVar != null) {
            context.getExternalContext().getRequestMap().put(rowIndexVar, rowIndex);
        }

        writer.startElement("tr", null);
        writer.writeAttribute("id", clientId + "_row_" + rowIndex, null);
        writer.writeAttribute("class", rowStyleClass, null);

        if(dynamicColumns == null) {

            for(Column column : table.getColumns()) {
                writer.startElement("td", null);
                String columnStyleClass = column.getStyleClass();

                if(column.getStyle() != null)
                    writer.writeAttribute("style", column.getStyle(), null);

                if(column.getSelectionMode() != null) {
                    columnStyleClass = columnStyleClass == null ? DataTable.SELECTION_COLUMN_CLASS : DataTable.SELECTION_COLUMN_CLASS + " " + columnStyleClass;
                    writer.writeAttribute("class", columnStyleClass, null);

                    encodeColumnSelection(context, table, clientId, column, selected);
                }
                else {
                    CellEditor editor = column.getCellEditor();
                    if(editor != null)
                        columnStyleClass = columnStyleClass == null ? DataTable.EDITABLE_COLUMN_CLASS : DataTable.EDITABLE_COLUMN_CLASS + " " + columnStyleClass;
                    
                    if(columnStyleClass != null)
                        writer.writeAttribute("class", columnStyleClass, null);


                    column.encodeAll(context);
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

    protected boolean handlePreselection(DataTable table, int rowIndex, String selectionMode, Object selection) {
        boolean selected = false;
        
        if(selectionMode != null && selection != null) {

            if(selectionMode.equals("single")) {
                if(selection != null && selection.equals(table.getRowData())) {
                    table.addSelectedRowIndex(rowIndex);
                    selected = true;
                }
            }
            else if(selectionMode.equals("multiple")) {
                Object[] selections = (Object[]) selection;

                for(int i = 0; i < selections.length; i++) {
                    if(selections[i].equals(table.getRowData())) {
                        table.addSelectedRowIndex(rowIndex);
                        selected = true;
                        break;
                    }
                }
            }
            
        }

        return selected;
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
        String paginatorPosition = table.getPaginatorPosition();
        String paginatorContainers = null;
        if(paginatorPosition.equalsIgnoreCase("both"))
            paginatorContainers = "'" + clientId + "_paginatortop','" + clientId + "_paginatorbottom'";
        else
            paginatorContainers = "'" + clientId + "_paginator" + paginatorPosition + "'";

        writer.write(",paginator:new YAHOO.widget.Paginator({");
        writer.write("rowsPerPage:" + table.getRows());
        writer.write(",totalRecords:" + table.getRowCount());
        writer.write(",initialPage:" + table.getPage());
        writer.write(",containers:[" + paginatorContainers + "]");

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

        if(table.getRowUnselectListener() != null) {
            writer.write(",instantUnselect:true");
            
            if(table.getOnRowUnselectUpdate() != null) {
                writer.write(",onRowUnselectUpdate:'" + ComponentUtils.findClientIds(context, table.getParent(), table.getOnRowUnselectUpdate()) + "'");
            }
        }
    }

    protected void encodePaginatorMarkup(FacesContext context, DataTable table, String position) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        
        String styleClass = "ui-paginator ui-paginator-" + position + " ui-widget-header";

        if(!position.equals("top") && table.getFooter() == null)
            styleClass = styleClass + " ui-corner-bl ui-corner-br";
        else if(!position.equals("bottom") && table.getHeader() == null)
            styleClass = styleClass + " ui-corner-tl ui-corner-tr";
        
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_paginator" + position, null);
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
        writer.writeAttribute("value", table.getSelectedRowIndexesAsString(), null);
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

    protected void encodeRowEditor(FacesContext context, DataTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String widgetVar = table.resolveWidgetVar();

        writer.startElement("span", null);
        writer.writeAttribute("class", DataTable.ROW_EDITOR_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-pencil", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-check", null);
        writer.writeAttribute("style", "display:none", null);
        writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-close", null);
        writer.writeAttribute("style", "display:none", null);
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

    protected void encodeColumnSelection(FacesContext context, DataTable table, String clientId, Column column, boolean selected) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String selectionMode = column.getSelectionMode();
        String name = clientId + "_selection";

        if(selectionMode.equalsIgnoreCase("single")) {
            writer.startElement("input", null);
            writer.writeAttribute("type", "radio", null);
            writer.writeAttribute("name", name + "_radio", null);
            if(selected) {
                writer.writeAttribute("checked", "checked", null);
            }
            writer.endElement("input");
            
        } else if(selectionMode.equalsIgnoreCase("multiple")) {
            writer.startElement("input", null);
            writer.writeAttribute("type", "checkbox", null);
            writer.writeAttribute("name", name + "_checkbox", null);
            if(selected) {
                writer.writeAttribute("checked", "checked", null);
            }
            writer.endElement("input");

        } else {
            throw new FacesException("Invalid column selection mode:" + selectionMode);
        }

    }

    protected void encodeEditedRow(FacesContext context, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int editedRowId = Integer.parseInt(params.get(table.getClientId(context) + "_editedRowId"));

        table.setRowIndex(editedRowId);

        encodeRow(context, table, table.getClientId(context), editedRowId, table.getRowIndexVar(), table.getDynamicColumns(), table.getSelectionMode(), table.getSelection());
    }

    private void encodeLiveRows(FacesContext context, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int scrollOffset = Integer.parseInt(params.get(table.getClientId(context) + "_scrollOffset"));
        String clientId = table.getClientId(context);
        String rowIndexVar = table.getRowIndexVar();
        Columns columns = table.getDynamicColumns();
        String selectionMode = table.getSelectionMode();
        Object selection = table.getSelection();

        for(int i = scrollOffset; i < (scrollOffset + table.getRows()); i++) {
            encodeRow(context, table, clientId, i, rowIndexVar, columns, selectionMode, selection);
        }
    }
}