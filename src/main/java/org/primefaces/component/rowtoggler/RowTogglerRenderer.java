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
package org.primefaces.component.rowtoggler;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;

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
        String ariaLabel = MessageFactory.getMessage(RowToggler.ROW_TOGGLER, null);

        writer.startElement("div", toggler);
        writer.writeAttribute("class", togglerClass, null);
        writer.writeAttribute("tabindex", toggler.getTabindex(), null);
        writer.writeAttribute("role", "button", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(expanded), null);
        writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);

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
