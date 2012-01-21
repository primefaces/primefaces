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
package org.primefaces.component.summaryrow;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;

public class SummaryRowRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SummaryRow row = (SummaryRow) component;
        DataTable table = (DataTable) row.getParent();
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("tr", null);
        writer.writeAttribute("class", DataTable.SUMMARY_ROW_CLASS, null);

        for(UIComponent kid : row.getChildren()) {
            if(kid.isRendered() && kid instanceof Column) {
                Column column = (Column) kid;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();
                styleClass = styleClass == null ? DataTable.COLUMN_CONTENT_WRAPPER : DataTable.COLUMN_CONTENT_WRAPPER + " " + styleClass;
        
                writer.startElement("td", null);
                //writer.writeAttribute("class", DataTable.COLUMN_FOOTER_CLASS, null);
                if(column.getRowspan() != 1) writer.writeAttribute("rowspan", column.getRowspan(), null);
                if(column.getColspan() != 1) writer.writeAttribute("colspan", column.getColspan(), null);

                writer.startElement("div", null);
                writer.writeAttribute("class", styleClass, null);

                if(style != null) {
                    writer.writeAttribute("style", style, null);
                }

                column.encodeAll(context);

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
}
