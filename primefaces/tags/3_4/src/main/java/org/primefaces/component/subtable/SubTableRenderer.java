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
package org.primefaces.component.subtable;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;

public class SubTableRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SubTable table = (SubTable) component;
        int rowCount = table.getRowCount();
        
        //header
        encodeHeader(context, table);
        
        //rows
        for(int i=0 ; i < rowCount; i++) {
            encodeRow(context, table, i);
        }
        
        //footer
        encodeFooter(context, table);
    }
    
    public void encodeHeader(FacesContext context, SubTable table) throws IOException {
        UIComponent header = table.getFacet("header");
        if(header == null)
            return;
        
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("tr", null);
        writer.writeAttribute("class", "ui-widget-header", null);
        
        writer.startElement("td", null);
        writer.writeAttribute("class", DataTable.SUBTABLE_HEADER, null);
        writer.writeAttribute("colspan", table.getColumns().size(), null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", DataTable.COLUMN_CONTENT_WRAPPER, null);
        header.encodeAll(context);
        writer.endElement("div");
        
        writer.endElement("td");
        writer.endElement("tr");
    }
    
    public void encodeRow(FacesContext context, SubTable table, int rowIndex) throws IOException {
        table.setRowIndex(rowIndex);
        if(!table.isRowAvailable()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);

        writer.startElement("tr", null);
        writer.writeAttribute("id", clientId + "_row_" + rowIndex, null);
        writer.writeAttribute("class", DataTable.ROW_CLASS, null);
        
        for(Column column : table.getColumns()) {
            String style = column.getStyle();
            String styleClass = column.getStyleClass();
            styleClass = styleClass == null ? DataTable.COLUMN_CONTENT_WRAPPER : DataTable.COLUMN_CONTENT_WRAPPER + " " + styleClass;
        
            writer.startElement("td", null);
            
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass, null);
            
            if(style != null) {
                writer.writeAttribute("style", style, null);
            }
            
            column.encodeAll(context);
            
            writer.endElement("div");
            writer.endElement("td");
        }
        
        writer.endElement("tr");
    }
    
    public void encodeFooter(FacesContext context, SubTable table) throws IOException {
        ColumnGroup group = table.getColumnGroup("footer");
        
        if(group == null || !group.isRendered())
            return;
        
        ResponseWriter writer = context.getResponseWriter();
        
        for(UIComponent child : group.getChildren()) {
            if(child.isRendered() && child instanceof Row) {
                Row footerRow = (Row) child;

                writer.startElement("tr", null);
                writer.writeAttribute("class", "ui-widget-header", null);

                for(UIComponent footerRowChild : footerRow.getChildren()) {
                    if(footerRowChild.isRendered() && footerRowChild instanceof Column) {
                        encodeColumnFooter(context, table, (Column) footerRowChild);
                    }
                }

                writer.endElement("tr");
            }
        }
    }
    
    protected void encodeColumnFooter(FacesContext context, SubTable table, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = styleClass == null ? DataTable.COLUMN_CONTENT_WRAPPER : DataTable.COLUMN_CONTENT_WRAPPER + " " + styleClass;

        writer.startElement("td", null);
        writer.writeAttribute("class", DataTable.COLUMN_FOOTER_CLASS, null);
        if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
        if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) 
            writer.writeAttribute("style", style, null);
        
        //Footer content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if(facet != null) {
            facet.encodeAll(context);
        } else if(text != null) {
            writer.write(text);
        }
        
        writer.endElement("div");

        writer.endElement("td");
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
