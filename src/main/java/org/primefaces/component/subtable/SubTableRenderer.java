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
package org.primefaces.component.subtable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;

public class SubTableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SubTable table = (SubTable) component;
        int rowCount = table.getRowCount();

        encodeHeader(context, table);

        for (int i = 0; i < rowCount; i++) {
            encodeRow(context, table, i);
        }

        encodeFooter(context, table);
    }

    public void encodeHeader(FacesContext context, SubTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = table.getFacet("header");

        if (header != null) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", "ui-widget-header", null);

            writer.startElement("td", null);
            writer.writeAttribute("class", DataTable.SUBTABLE_HEADER, null);
            writer.writeAttribute("colspan", table.getColumns().size(), null);

            header.encodeAll(context);

            writer.endElement("td");
            writer.endElement("tr");
        }

        ColumnGroup group = table.getColumnGroup("header");
        if (group != null && group.isRendered()) {
            for (UIComponent child : group.getChildren()) {
                if (child.isRendered() && child instanceof Row) {
                    Row headerRow = (Row) child;

                    writer.startElement("tr", null);
                    writer.writeAttribute("class", "ui-widget-header", null);

                    for (UIComponent headerRowChild : headerRow.getChildren()) {
                        if (headerRowChild.isRendered() && headerRowChild instanceof Column) {
                            Column footerColumn = (Column) headerRowChild;
                            encodeFacetColumn(context, table, footerColumn, "header", DataTable.COLUMN_HEADER_CLASS, footerColumn.getHeaderText());
                        }
                    }

                    writer.endElement("tr");
                }
            }
        }
    }

    public void encodeRow(FacesContext context, SubTable table, int rowIndex) throws IOException {
        table.setRowIndex(rowIndex);
        if (!table.isRowAvailable()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);

        writer.startElement("tr", null);
        writer.writeAttribute("id", clientId + "_row_" + rowIndex, null);
        writer.writeAttribute("class", DataTable.ROW_CLASS, null);

        for (Column column : table.getColumns()) {
            String style = column.getStyle();
            String styleClass = column.getStyleClass();

            writer.startElement("td", null);
            if (style != null) {
                writer.writeAttribute("style", style, null);
            }
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, null);
            }

            column.encodeAll(context);

            writer.endElement("td");
        }

        writer.endElement("tr");
    }

    public void encodeFooter(FacesContext context, SubTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent footer = table.getFacet("footer");

        if (footer != null) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", "ui-widget-header", null);

            writer.startElement("td", null);
            writer.writeAttribute("class", DataTable.SUBTABLE_FOOTER, null);
            writer.writeAttribute("colspan", table.getColumns().size(), null);

            footer.encodeAll(context);

            writer.endElement("td");
            writer.endElement("tr");
        }

        ColumnGroup group = table.getColumnGroup("footer");

        if (group == null || !group.isRendered()) {
            return;
        }

        for (UIComponent child : group.getChildren()) {
            if (child.isRendered() && child instanceof Row) {
                Row footerRow = (Row) child;

                writer.startElement("tr", null);
                writer.writeAttribute("class", "ui-widget-header", null);

                for (UIComponent footerRowChild : footerRow.getChildren()) {
                    if (footerRowChild.isRendered() && footerRowChild instanceof Column) {
                        Column footerColumn = (Column) footerRowChild;
                        encodeFacetColumn(context, table, footerColumn, "footer", DataTable.COLUMN_FOOTER_CLASS, footerColumn.getFooterText());
                    }
                }

                writer.endElement("tr");
            }
        }
    }

    protected void encodeFacetColumn(FacesContext context, SubTable table, Column column, String facetName, String styleClass, String text) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        String style = column.getStyle();
        String columnClass = column.getStyleClass();
        columnClass = (columnClass == null) ? styleClass : styleClass + " " + columnClass;

        writer.startElement("td", null);
        writer.writeAttribute("class", columnClass, null);
        if (column.getRowspan() != 1) {
            writer.writeAttribute("rowspan", column.getRowspan(), null);
        }
        if (column.getColspan() != 1) {
            writer.writeAttribute("colspan", column.getColspan(), null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        // Footer content
        UIComponent facet = column.getFacet(facetName);
        if (facet != null) {
            facet.encodeAll(context);
        }
        else if (text != null) {
            writer.write(text);
        }

        writer.endElement("td");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
