/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.panelgrid.PanelGrid;

public class PanelGridRenderer extends org.primefaces.component.panelgrid.PanelGridRenderer {
    
    private static final HashMap<Integer, String> GRID_MAP = new HashMap<Integer, String>();
    
    private static final HashMap<Integer, String> BLOCK_MAP = new HashMap<Integer, String>();
    
    static {
		GRID_MAP.put(2, "ui-grid-a");
        GRID_MAP.put(3, "ui-grid-b");
        GRID_MAP.put(4, "ui-grid-c");
        GRID_MAP.put(5, "ui-grid-d");
    }
    
    static {
		BLOCK_MAP.put(0, "ui-block-a");
        BLOCK_MAP.put(1, "ui-block-b");
        BLOCK_MAP.put(2, "ui-block-c");
        BLOCK_MAP.put(3, "ui-block-d");
        BLOCK_MAP.put(4, "ui-block-e");
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PanelGrid grid = (PanelGrid) component;
        String clientId = grid.getClientId(context);
        int columns = grid.getColumns();
        String gridClass = GRID_MAP.get(columns);
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass();
        styleClass = (styleClass == null) ? gridClass : gridClass + " " + styleClass;
        
        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        int i = 0;
        for(UIComponent child : grid.getChildren()) {
            if(child.isRendered()) {
                int blockKey = (i % columns);
                String blockClass = BLOCK_MAP.get(blockKey);
                writer.startElement("div", null);
                writer.writeAttribute("class", blockClass, null);
                child.encodeAll(context);
                writer.endElement("div");                
                i++;
            }
        }
        
        writer.endElement("div");
    }
}
