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
package org.primefaces.component.column.renderer;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.renderkit.CoreRenderer;

public class PanelGridBodyColumnRenderer extends CoreRenderer implements HelperColumnRenderer {

    public void encode(FacesContext context, Column column) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = (styleClass == null) ? PanelGrid.CELL_CLASS: PanelGrid.CELL_CLASS + " " + styleClass;
        
        writer.startElement("td", null);
        writer.writeAttribute("role", "gridcell", null);
        writer.writeAttribute("class", styleClass, null);

        if(style != null) writer.writeAttribute("style", style, null);
        if(column.getColspan() > 1) writer.writeAttribute("colspan", column.getColspan(), null);
        if(column.getRowspan() > 1) writer.writeAttribute("rowspan", column.getRowspan(), null);

        renderChildren(context, column);

        writer.endElement("td");
    }
    
}
