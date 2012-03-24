/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.sheet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.ListDataModel;
import org.primefaces.component.column.Column;
import org.primefaces.model.BeanPropertyComparator;
import org.primefaces.model.SortOrder;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class SheetRenderer extends CoreRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        Sheet sheet = (Sheet) component;

        if(sheet.isColResizeRequest(context)) {
            sheet.syncColumnWidths();
        }
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Sheet sheet = (Sheet) component;
        
        if(sheet.isSortingRequest(context)) {
            sort(context, sheet);
            encodeBody(context, sheet);
        } 
        else {
            encodeMarkup(context, sheet);
            encodeScript(context, sheet);
        }
    }

    protected void encodeMarkup(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = sheet.getClientId(context);
        String styleClass = sheet.getStyleClass();
        styleClass = styleClass == null ? Sheet.CONTAINER_CLASS : Sheet.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", sheet);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if(sheet.getStyle() != null) {
            writer.writeAttribute("style", sheet.getStyle(), "style");
        }
        
        encodeCaption(context, sheet);
        encodeEditorBar(context, sheet);
        encodeContent(context, sheet);
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = sheet.getClientId(context);
        
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Sheet','" + sheet.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("},'sheet');");

		endScript(writer);
    }
    
    protected void encodeCaption(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent caption = sheet.getFacet("caption");
        
        if(caption != null) {
            writer.startElement("div", sheet);
            writer.writeAttribute("class", Sheet.CAPTION_CLASS, null);
            
            caption.encodeAll(context);
            
            writer.endElement("div");
        }
    }
    
    protected void encodeEditorBar(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", sheet);
        writer.writeAttribute("class", Sheet.EDITOR_BAR_CLASS, null);

        writer.startElement("table", null);
        writer.startElement("tbody", null);
        writer.startElement("tr", null);
        
        writer.startElement("td", null);
        writer.writeAttribute("class", Sheet.CELL_INFO_CLASS, null);
        writer.endElement("td");
        
        writer.startElement("td", null);
        writer.startElement("input", null);
        writer.writeAttribute("class", Sheet.EDITOR_CLASS, null);
        writer.writeAttribute("type", "text", null);
        writer.endElement("input");
        writer.endElement("td");
        
        writer.endElement("tr");
        writer.endElement("tbody");
        writer.endElement("table");

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int scrollHeight = sheet.getScrollHeight();
        int scrollWidth = sheet.getScrollWidth();
        boolean hasScrollWidth = scrollWidth != Integer.MIN_VALUE;
        StringBuilder style = new StringBuilder();
        
        if(scrollHeight != Integer.MIN_VALUE)
            style.append("height:").append(scrollHeight).append("px;");
        if(hasScrollWidth)
            style.append("width:").append(scrollWidth).append("px;");
        
        //header
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.HEADER_CLASS, null);
        if(hasScrollWidth) {
            writer.writeAttribute("style", "width:" + scrollWidth + "px", null);
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.HEADER_BOX_CLASS, null);
        
        writer.startElement("table", null);
        encodeHeader(context, sheet);
        writer.endElement("table");
        
        writer.endElement("div");
        writer.endElement("div");
        
        //body
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.BODY_CLASS, null);
        if(style.length() > 0) {
            writer.writeAttribute("style", style, null);
        }
        writer.startElement("table", null);
        encodeBody(context, sheet);
        writer.endElement("table");
        
        writer.endElement("div");
    }
    
    public void encodeHeader(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //fixed columns
        writer.startElement("thead", null);
        writer.startElement("tr", null);
        
        //index column header
        writer.startElement("th", null);
        writer.writeAttribute("class", Sheet.COLUMN_HEADER_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.INDEX_CELL_CLASS, null);
        writer.endElement("div");
        writer.endElement("th");
        
        int columnIndex = 0;
        for(UIComponent child : sheet.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                boolean sortable = column.getValueExpression("sortBy") != null;
                
                String style = column.getStyle();
                String columnHeaderClass = sortable ? Sheet.COLUMN_HEADER_CLASS + " " + Sheet.SORTABLE_COLUMN : Sheet.COLUMN_HEADER_CLASS;
                String userStyleClass = column.getStyleClass();
                if(userStyleClass != null) {
                    columnHeaderClass = columnHeaderClass + " " + userStyleClass;
                }
        
                writer.startElement("th", null);
                writer.writeAttribute("id", column.getClientId(context), style);
                writer.writeAttribute("class", columnHeaderClass, null);
                if(style != null) {
                    writer.writeAttribute("style", style, null);
                }
                
                writer.startElement("div", null);
                writer.writeAttribute("class", Sheet.CELL_CLASS, null);
                if(column.getWidth() != -1) {
                    writer.writeAttribute("style", "width:" + column.getWidth() + "px", null);
                }

                if(sortable) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", Sheet.SORTABLE_COLUMN_ICON, null);
                    writer.endElement("span");
                }
                
                writer.write(getColumnCode(columnIndex));

                writer.endElement("th");
                
                columnIndex++;
            }
        }
        
        writer.endElement("tr");
        writer.endElement("thead");
        
        //frozen headers
        writer.startElement("tbody", null);
        writer.startElement("tr", null);
        writer.writeAttribute("class", Sheet.ROW_CLASS, null);
        
        //index column header
        writer.startElement("td", null);
        writer.writeAttribute("class", Sheet.COLUMN_HEADER_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.INDEX_CELL_CLASS, null);
        writer.write("1");
        writer.endElement("div");
        writer.endElement("td");
        
        for(UIComponent child : sheet.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
        
                writer.startElement("td", null);
                if(style != null) writer.writeAttribute("style", style, null);
                if(styleClass != null) writer.writeAttribute("class", styleClass, null);

                writer.startElement("div", null);
                writer.writeAttribute("class", Sheet.CELL_CLASS, null);
                if(column.getWidth() != -1) {
                    writer.writeAttribute("style", "width:" + column.getWidth() + "px", null);
                }
                
                if(column.getHeader() != null) {
                    column.getHeader().encodeAll(context);
                } 
                else if(column.getHeaderText() != null) {
                    writer.write(column.getHeaderText());
                }

                writer.endElement("div");
                
                writer.endElement("td");
            }
         }
        
        writer.endElement("tr");
        writer.endElement("tbody");
    }
    
    public void encodeBody(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        int rows = sheet.getRows();
		int first = sheet.getFirst();
        int rowCount = sheet.getRowCount();
        int rowCountToRender = rows == 0 ? rowCount : rows;
        boolean hasData = rowCount > 0;
      
        writer.startElement("tbody", null);

        if(hasData) {            
            for(int i = first; i < (first + rowCountToRender); i++) {
                encodeRow(context, sheet, i);
            }
        }
        
        writer.endElement("tbody");

		//Cleanup
		sheet.setRowIndex(-1);
    }
    
    protected void encodeRow(FacesContext context, Sheet sheet, int rowIndex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = sheet.getClientId(context);
        sheet.setRowIndex(rowIndex);
        
        writer.startElement("tr", null);
        writer.writeAttribute("id", clientId + "_row_" + rowIndex , null);
        writer.writeAttribute("class", Sheet.ROW_CLASS, null);
        
        //index column
        writer.startElement("td", null);
        writer.writeAttribute("class", Sheet.COLUMN_HEADER_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", Sheet.INDEX_CELL_CLASS, null);
        writer.write(String.valueOf(rowIndex + 2));
        writer.endElement("div");
        writer.endElement("td");
        
        for(UIComponent child : sheet.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
        
                writer.startElement("td", null);
                if(style != null) writer.writeAttribute("style", style, null);
                if(styleClass != null) writer.writeAttribute("class", styleClass, null);
                
                writer.startElement("div", null);
                writer.writeAttribute("class", Sheet.CELL_CLASS, null);
                if(column.getWidth() != -1) {
                    writer.writeAttribute("style", "width:" + column.getWidth() + "px", null);
                }

                writer.startElement("div", null);
                writer.writeAttribute("class", Sheet.CELL_DISPLAY_CLASS, null);
                String valueToRender = ComponentUtils.getValueToRender(context, child.getChildren().get(0));
                if(valueToRender != null) {
                    writer.write(valueToRender);
                }
                writer.endElement("div");
                
                writer.startElement("div", null);
                writer.writeAttribute("class", Sheet.CELL_EDIT_CLASS, null);
                child.encodeAll(context);
                writer.endElement("div");

                writer.endElement("td");
            }
        }
        
        writer.endElement("tr");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    protected void sort(FacesContext context, Sheet sheet) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        Object value = sheet.getValue();
        List list = null;
        String clientId = sheet.getClientId(context);
        Column sortColumn = sheet.findColumn(params.get(clientId + "_sortKey"));
        ValueExpression sortByVE = sortColumn.getValueExpression("sortBy");
        SortOrder sortOrder = SortOrder.valueOf(params.get(clientId + "_sortDir"));

        if(value instanceof List) {
            list = (List) value;
        } else if(value instanceof ListDataModel) {
            list = (List) ((ListDataModel) value).getWrappedData();
        } else {
            throw new FacesException("Data type should be java.util.List or javax.faces.model.ListDataModel instance to be sortable.");
        }

        Collections.sort(list, new BeanPropertyComparator(sortByVE, sheet.getVar(), sortOrder, null));
    }
    
    protected String getColumnCode(int index){
        if(index >= 26) {
            return getColumnCode((index/26) - 1) + Sheet.LETTERS[index%26];
        }
            
        return Sheet.LETTERS[index];
    }
}
