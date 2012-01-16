/*
 * Copyright 2009-2012 Prime Technology.
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
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
        
        UIComponent header = grid.getFacet("header");
        UIComponent footer = grid.getFacet("footer");
        
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
        
        int i = 0;
        for(UIComponent child : grid.getChildren()) {
            if((i % columns) == 0) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", PanelGrid.ROW_CLASS, null);
                writer.writeAttribute("role", "row", null);
            }
           
            if(child.isRendered()) {
                writer.startElement("td", null);
                writer.writeAttribute("role", "gridcell", null);
                child.encodeAll(context);
                writer.endElement("td");
                i++;
            }
            
            if((i % columns) == 0) {
                writer.endElement("tr");
            }
        }

        writer.endElement("tbody");
    }
    
    public void encodeFacet(FacesContext context, PanelGrid grid, int columns, String facet, String tag, String styleClass) throws IOException {
        UIComponent component = grid.getFacet(facet);
        if(component != null && component.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();
        
            writer.startElement(tag, null);
            writer.startElement("tr", null);
            writer.writeAttribute("class", "ui-widget-header", null);
            writer.writeAttribute("role", "row", null);
            
            writer.startElement("td", null);
            writer.writeAttribute("class", styleClass, null);
            writer.writeAttribute("colspan", columns, null);
            writer.writeAttribute("role", "columnheader", null);

            component.encodeAll(context);

            writer.endElement("td");
            writer.endElement("tr");
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
