/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.mobile.util.MobileUtils;

public class PanelGridRenderer extends org.primefaces.component.panelgrid.PanelGridRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PanelGrid grid = (PanelGrid) component;
        String clientId = grid.getClientId(context);
        int columns = grid.getColumns();
        if(columns == 0) {
            columns = 1;
        }
        String gridClass = MobileUtils.GRID_MAP.get(columns);
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass();
        styleClass = (styleClass == null) ? gridClass : gridClass + " " + styleClass;
        
        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        renderDynamicPassThruAttributes(context, grid);
        
        int i = 0;
        for(UIComponent child : grid.getChildren()) {
            if(child.isRendered()) {
                int blockKey = (i % columns);
                String blockClass = MobileUtils.BLOCK_MAP.get(blockKey);
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
