/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.roweditor;

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.renderkit.CoreRenderer;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = RowEditor.DEFAULT_RENDERER, componentFamily = RowEditor.COMPONENT_FAMILY)
@FacesRendererInfo
public class RowEditorRenderer extends CoreRenderer<RowEditor> {

    @Override
    public void encodeEnd(FacesContext context, RowEditor component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? DataTable.ROW_EDITOR_CLASS : DataTable.ROW_EDITOR_CLASS + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        // the icon tokens are per table component: --p-datatable-row-edit-icon / --p-treetable-row-edit-icon
        UIComponent parent = component.getParent();
        while (parent != null && !(parent instanceof DataTable) && !(parent instanceof TreeTable)) {
            parent = parent.getParent();
        }
        String table = parent instanceof TreeTable ? "treetable" : "datatable";

        encodeIcon(writer, "pencil", "p-icon p-edit-icon p-" + table + "-row-edit-icon", component.getEditTitle());
        encodeIcon(writer, "check", "p-icon p-check-icon p-" + table + "-row-save-icon p-c", component.getSaveTitle());
        encodeIcon(writer, "close", "p-icon p-close-icon p-" + table + "-row-cancel-icon p-c", component.getCancelTitle());

        writer.endElement("div");
    }

    protected void encodeIcon(ResponseWriter writer, String type, String iconClass, String title) throws IOException {
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", "p-row-editor-" + type, null);

        writer.startElement("span", null);
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }
}
