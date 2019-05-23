/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.headerrow;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;

public class HeaderRowRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        HeaderRow row = (HeaderRow) component;
        DataTable table = (DataTable) row.getParent();
        ResponseWriter writer = context.getResponseWriter();
        boolean isExpandableRowGroups = table.isExpandableRowGroups();

        writer.startElement("tr", null);
        writer.writeAttribute("class", DataTable.HEADER_ROW_CLASS, null);

        boolean isFirstColumn = true;
        for (int i = 0; i < row.getChildCount(); i++) {
            UIComponent child = row.getChildren().get(i);
            if (child.isRendered() && child instanceof Column) {
                Column column = (Column) child;
                String style = column.getStyle();
                String styleClass = column.getStyleClass();

                writer.startElement("td", null);
                if (style != null) {
                    writer.writeAttribute("style", style, null);
                }
                if (styleClass != null) {
                    writer.writeAttribute("class", styleClass, null);
                }
                if (column.getRowspan() != 1) {
                    writer.writeAttribute("rowspan", column.getRowspan(), null);
                }
                if (column.getColspan() != 1) {
                    writer.writeAttribute("colspan", column.getColspan(), null);
                }

                if (isExpandableRowGroups && isFirstColumn) {
                    String ariaLabel = MessageFactory.getMessage(DataTable.ROW_GROUP_TOGGLER, null);

                    writer.startElement("a", null);
                    writer.writeAttribute("class", DataTable.ROW_GROUP_TOGGLER_CLASS, null);
                    writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(true), null);
                    writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
                    writer.writeAttribute("href", "#", null);
                    writer.startElement("span", null);
                    writer.writeAttribute("class", DataTable.ROW_GROUP_TOGGLER_ICON_CLASS, null);
                    writer.endElement("span");
                    writer.endElement("a");

                    isFirstColumn = false;
                }

                column.encodeAll(context);

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
