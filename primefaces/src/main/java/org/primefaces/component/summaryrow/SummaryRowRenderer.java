/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.summaryrow;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = SummaryRow.DEFAULT_RENDERER, componentFamily = SummaryRow.COMPONENT_FAMILY)
public class SummaryRowRenderer extends CoreRenderer<SummaryRow> {

    @Override
    public void encodeEnd(FacesContext context, SummaryRow component) throws IOException {
        DataTable table = (DataTable) component.getParent();
        ResponseWriter writer = context.getResponseWriter();
        HeaderRow headerRow = table.getHeaderRow();

        // GitHub #7296 prevent issue with PanelGrid rendering
        Object helperRenderer = context.getAttributes().remove(Constants.HELPER_RENDERER);

        writer.startElement("tr", null);
        writer.writeAttribute("class", DataTable.SUMMARY_ROW_CLASS, null);

        if (headerRow != null && !headerRow.isExpanded()) {
            writer.writeAttribute("style", "display: none;", null);
        }

        for (UIComponent kid : component.getChildren()) {
            if (kid.isRendered() && kid instanceof Column) {
                Column column = (Column) kid;
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

                column.encodeAll(context);

                writer.endElement("td");
            }
        }

        writer.endElement("tr");
        context.getAttributes().put(Constants.HELPER_RENDERER, helperRenderer);
    }

    @Override
    public void encodeChildren(FacesContext context, SummaryRow component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
