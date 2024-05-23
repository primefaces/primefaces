/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.row;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.ForEachRowColumn;
import org.primefaces.component.api.RowColumnVisitor;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.row.renderer.HelperRowRenderer;
import org.primefaces.component.row.renderer.PanelGridBodyRowRenderer;
import org.primefaces.component.row.renderer.PanelGridFacetRowRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

public class RowRenderer extends CoreRenderer {

    static final Map<String, HelperRowRenderer> RENDERERS = MapBuilder.<String, HelperRowRenderer>builder()
            .put("panelGridBody", new PanelGridBodyRowRenderer())
            .put("panelGridFacet", new PanelGridFacetRowRenderer())
            .build();

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Row row = (Row) component;
        String helperKey = (String) context.getAttributes().get(Constants.HELPER_RENDERER);

        if (helperKey != null) {
            HelperRowRenderer renderer = RENDERERS.get(helperKey);

            if (renderer != null) {
                renderer.encode(context, row);
            }
        }
        else {
            encodeRow(context, row);
        }
    }

    protected void encodeRow(FacesContext context, Row row) {
        ResponseWriter writer = context.getResponseWriter();

        ForEachRowColumn
                .from(row)
                .invoke(new RowColumnVisitor.Adapter() {

                    @Override
                    public void visitRow(int index, Row row) throws IOException {
                        String rowClass = row.getStyleClass();
                        String rowStyle = row.getStyle();

                        writer.startElement("tr", null);
                        if (rowClass != null) {
                            writer.writeAttribute("class", rowClass, null);
                        }
                        if (rowStyle != null) {
                            writer.writeAttribute("style", rowStyle, null);
                        }
                    }

                    @Override
                    public void visitRowEnd(int index, Row row) throws IOException {
                        writer.endElement("tr");
                    }

                    @Override
                    public void visitColumn(int index, UIColumn column) throws IOException {
                        String title = column.getTitle();
                        String style = column.getStyle();
                        int responsivePriority = column.getResponsivePriority();

                        String styleClass = getStyleClassBuilder(context)
                                .add(column.getStyleClass())
                                .add(responsivePriority > 0, "ui-column-p-" + responsivePriority)
                                .build();

                        int colspan = column.getColspan();
                        int rowspan = column.getRowspan();

                        writer.startElement("td", null);
                        writer.writeAttribute("role", "gridcell", null);
                        if (colspan != 1) {
                            writer.writeAttribute("colspan", colspan, null);
                        }
                        if (rowspan != 1) {
                            writer.writeAttribute("rowspan", rowspan, null);
                        }
                        if (LangUtils.isNotBlank(style)) {
                            writer.writeAttribute("style", style, null);
                        }
                        if (LangUtils.isNotBlank(styleClass)) {
                            writer.writeAttribute("class", styleClass, null);
                        }
                        if (LangUtils.isNotBlank(title)) {
                            writer.writeAttribute("title", title, null);
                        }
                        UIComponent component = column.asUIComponent();
                        renderDynamicPassThruAttributes(context, component);

                        if (column instanceof DynamicColumn) {
                            column.encodeAll(context);
                        }
                        else {
                            column.renderChildren(context);
                        }

                        writer.endElement("td");
                    }
                });
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
