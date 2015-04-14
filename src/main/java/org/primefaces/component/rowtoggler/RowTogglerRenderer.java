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
package org.primefaces.component.rowtoggler;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;

public class RowTogglerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RowToggler toggler = (RowToggler) component;
        DataTable parentTable = toggler.getParentTable(context);
        boolean expanded = parentTable.isExpandedRow();
        String icon = expanded ? RowToggler.EXPANDED_ICON : RowToggler.COLLAPSED_ICON;
        String expandLabel = toggler.getExpandLabel();
        String collapseLabel = toggler.getCollapseLabel();
        boolean iconOnly = (expandLabel == null && collapseLabel == null);
        String togglerClass = iconOnly ? DataTable.ROW_TOGGLER_CLASS + " " + icon : DataTable.ROW_TOGGLER_CLASS;
        
        writer.startElement("div", toggler);
        writer.writeAttribute("class", togglerClass, null);
        
        if(!iconOnly) {
            writeLabel(writer, expandLabel, !expanded);
            writeLabel(writer, collapseLabel, expanded);
        }
        
        writer.endElement("div");
    }
    
    protected void writeLabel(ResponseWriter writer, String label, boolean visible) throws IOException {
        writer.startElement("span", null);
        if(!visible) {
            writer.writeAttribute("class", "ui-helper-hidden", null);
        }
        writer.writeText(label, null);
        writer.endElement("span");
    }
}
