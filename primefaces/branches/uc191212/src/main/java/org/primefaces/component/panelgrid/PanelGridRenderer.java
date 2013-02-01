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
package org.primefaces.component.panelgrid;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;

public class PanelGridRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        PanelGrid grid = (PanelGrid) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId(context);
        int columns = grid.getColumns();
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass();
        styleClass = styleClass == null ? PanelGrid.CONTAINER_CLASS : PanelGrid.CONTAINER_CLASS + " " + styleClass;
                
        writer.startElement("table", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "grid", null);
                
        encodeFacet(context, grid, columns, "header", "thead", PanelGrid.HEADER_CLASS);
        encodeFacet(context, grid, columns, "footer", "tfoot", PanelGrid.FOOTER_CLASS);
        encodeBody(context, grid, columns);
        
        writer.endElement("table");
    }
    
    public void encodeBody(FacesContext context, PanelGrid grid, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("tbody", grid);
        
        if(columns > 0) {
            encodeDynamicBody(context, grid, grid.getColumns());
        }
        else {
            encodeStaticBody(context, grid);
        }

        writer.endElement("tbody");
    }
    
    public void encodeDynamicBody(FacesContext context, PanelGrid grid, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = grid.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");
        
        int i = 0;
        for(UIComponent child : grid.getChildren()) {
            int colMod = i % columns;
            
            if(colMod == 0) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", PanelGrid.ROW_CLASS, null);
                writer.writeAttribute("role", "row", null);
            }
           
            if(child.isRendered()) {
                writer.startElement("td", null);
                writer.writeAttribute("role", "gridcell", null);
                if(colMod < columnClasses.length) {
                    writer.writeAttribute("class", columnClasses[colMod].trim(), null);
                }
                
                
                child.encodeAll(context);
                writer.endElement("td");
                i++;
                colMod = i % columns;
            }
            
            if(colMod == 0) {
                writer.endElement("tr");
            }
        }
    }
    
    public void encodeStaticBody(FacesContext context, PanelGrid grid) throws IOException {        
        for(UIComponent child : grid.getChildren()) {
            if(child instanceof Row && child.isRendered()) {
                encodeRow(context, (Row) child, "gridcell", PanelGrid.ROW_CLASS, null);
            }
        }
    }
    
    public void encodeRow(FacesContext context, Row row, String columnRole, String rowClass, String columnClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("tr", null);
        if(shouldWriteId(row)) {
            writer.writeAttribute("id", row.getClientId(context), null);
        }
        writer.writeAttribute("class", rowClass, null);
        writer.writeAttribute("role", "row", null);
        
        for(UIComponent child : row.getChildren()) {
            if(child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String styleClass = null;
                String userStyleClass = column.getStyleClass();
                
                if(userStyleClass != null && columnClass != null) styleClass = columnClass + " " + userStyleClass;
                else if(userStyleClass != null && columnClass == null) styleClass = userStyleClass;
                else if(userStyleClass == null && columnClass != null) styleClass = columnClass;
                
                writer.startElement("td", null);
                if(shouldWriteId(column)) {
                    writer.writeAttribute("id", column.getClientId(context), null);
                }
                writer.writeAttribute("role", columnRole, null);
                
                if(column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
                if(styleClass != null) writer.writeAttribute("class", styleClass, null);
                if(column.getColspan() > 1) writer.writeAttribute("colspan", column.getColspan(), null);
                if(column.getRowspan() > 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
                
                column.encodeAll(context);
                
                writer.endElement("td");
            }
        }
        
        writer.endElement("tr");
    }
    
    public void encodeFacet(FacesContext context, PanelGrid grid, int columns, String facet, String tag, String styleClass) throws IOException {
        UIComponent component = grid.getFacet(facet);
        
        if(component != null && component.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement(tag, null);
            writer.writeAttribute("class", styleClass, null);
            
            if(columns > 0) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", "ui-widget-header", null);
                writer.writeAttribute("role", "row", null);

                writer.startElement("td", null);
                writer.writeAttribute("colspan", columns, null);
                writer.writeAttribute("role", "columnheader", null);
                
                component.encodeAll(context);
                
                writer.endElement("td");
                writer.endElement("tr");
            }
            else {
                if(component instanceof Row) {
                    encodeRow(context, (Row) component, "columnheader", "ui-widget-header", "ui-widget-header");
                }
                else if(component instanceof UIPanel){
                    for(UIComponent row : component.getChildren()) {
                        if(row instanceof Row && row.isRendered()) {
                            encodeRow(context, (Row) row, "columnheader", "ui-widget-header", "ui-widget-header");
                        }
                    }
                }
            }
            
            writer.endElement(tag);
        }
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
