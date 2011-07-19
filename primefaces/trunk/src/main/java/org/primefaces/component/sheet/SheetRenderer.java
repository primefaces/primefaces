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
package org.primefaces.component.sheet;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.renderkit.CoreRenderer;

public class SheetRenderer extends CoreRenderer {
    
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Sheet sheet = (Sheet) component;
        
        encodeMarkup(context, sheet);
        encodeScript(context, sheet);
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
        
        encodeContent(context, sheet);
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = sheet.getClientId(context);
        
        writer.startElement("script", sheet);
		writer.writeAttribute("type", "text/javascript", null);
        
        writer.write(sheet.resolveWidgetVar() + " = new PrimeFaces.widget.Sheet('" + clientId + "',{");
        writer.write("});");

		writer.endElement("script");
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
        encodeThead(context, sheet);
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
        encodeTbody(context, sheet);
        writer.endElement("table");
        
        writer.endElement("div");
    }

    public void encodeThead(FacesContext context, Sheet sheet) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("thead", null);
        writer.startElement("tr", null);
        
        for(UIComponent child : sheet.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
                styleClass = styleClass == null ? Sheet.CELL_CLASS : Sheet.CELL_CLASS  + " " + styleClass;
        
                writer.startElement("th", null);
                writer.writeAttribute("class", Sheet.COLUMN_HEADER_CLASS, null);

                writer.startElement("div", null);
                writer.writeAttribute("class", styleClass, null);
                if(style != null) {
                    writer.writeAttribute("style", style, null);
                }
                
                if(column.getHeader() != null) {
                    column.getHeader().encodeAll(context);
                } else if(column.getHeaderText() != null) {
                    writer.write(column.getHeaderText());
                }
                
                child.encodeAll(context);

                writer.endElement("th");
            }
        }
        
        writer.endElement("tr");
        writer.endElement("thead");
    }
    
    public void encodeTbody(FacesContext context, Sheet sheet) throws IOException {
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
        writer.writeAttribute("id", clientId + "_row_" + rowIndex, null);
        writer.writeAttribute("class", Sheet.ROW_CLASS, null);
        
        for(UIComponent child : sheet.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
                styleClass = styleClass == null ? Sheet.CELL_CLASS : Sheet.CELL_CLASS  + " " + styleClass;
        
                writer.startElement("td", null);

                writer.startElement("div", null);
                writer.writeAttribute("class", styleClass, null);
                if(style != null) {
                    writer.writeAttribute("style", style, null);
                }
                
                child.encodeAll(context);

                writer.endElement("td");
            }
        }
        
        writer.endElement("tr");
    }
}
