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
package org.primefaces.component.rowtoggler;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = RowToggler.DEFAULT_RENDERER, componentFamily = RowToggler.COMPONENT_FAMILY)
public class RowTogglerRenderer extends CoreRenderer<RowToggler> {

    @Override
    public void encodeEnd(FacesContext context, RowToggler component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DataTable parentTable = component.getParentTable(context);
        String rowKey = parentTable.getRowKey(parentTable.getRowData());
        boolean expanded = parentTable.isExpandedRow() || parentTable.getExpandedRowKeys().contains(rowKey);
        String expandIcon = component.getExpandIcon();
        String collapseIcon = component.getCollapseIcon();
        String icon = "ui-icon " + (expanded ? collapseIcon : expandIcon);
        String expandLabel = component.getExpandLabel();
        String collapseLabel = component.getCollapseLabel();
        boolean iconOnly = (expandLabel == null && collapseLabel == null);
        String togglerClass = iconOnly ? DataTable.ROW_TOGGLER_CLASS + " " + icon : DataTable.ROW_TOGGLER_CLASS;

        writer.startElement("div", component);
        writer.writeAttribute("class", togglerClass, null);
        writer.writeAttribute("tabindex", component.getTabindex(), null);
        writer.writeAttribute("role", "button", null);
        writer.writeAttribute("data-expand-icon", expandIcon, null);
        writer.writeAttribute("data-collapse-icon", collapseIcon, null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(expanded), null);

        if (!iconOnly) {
            writeLabel(writer, expandLabel, !expanded);
            writeLabel(writer, collapseLabel, expanded);
        }

        writer.endElement("div");
    }

    protected void writeLabel(ResponseWriter writer, String label, boolean visible) throws IOException {
        writer.startElement("span", null);
        if (!visible) {
            writer.writeAttribute("class", "ui-helper-hidden", null);
        }
        writer.writeText(label, null);
        writer.endElement("span");
    }
}
