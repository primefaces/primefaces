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
package org.primefaces.component.panelgrid;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.column.Column;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.GridLayoutUtils;

public class PanelGridRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        PanelGrid grid = (PanelGrid) component;

        if (grid.getLayout().equals("tabular")) {
            encodeTableLayout(context, grid);
        }
        else if (grid.getLayout().equals("grid")) {
            encodeGridLayout(context, grid);
        }
        else {
            throw new FacesException("The value of 'layout' attribute must be 'grid' or 'tabular'. Default value is 'tabular'.");
        }
    }

    public void encodeTableLayout(FacesContext context, PanelGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId(context);
        int columns = grid.getColumns();
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass();
        styleClass = styleClass == null ? PanelGrid.CONTAINER_CLASS : PanelGrid.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("table", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", grid.getRole(), null);

        encodeTableFacet(context, grid, columns, "header", "thead", PanelGrid.HEADER_CLASS);
        encodeTableFacet(context, grid, columns, "footer", "tfoot", PanelGrid.FOOTER_CLASS);
        encodeTableBody(context, grid, columns);

        writer.endElement("table");
    }

    public void encodeGridLayout(FacesContext context, PanelGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId(context);
        int columns = grid.getColumns();
        if (columns == 0) {
            throw new FacesException("Columns of PanelGrid \"" + grid.getClientId(context) + "\" must be greater than zero in grid layout.");
        }

        String style = grid.getStyle();
        String styleClass = grid.getStyleClass();
        styleClass = styleClass == null ? PanelGrid.CONTAINER_CLASS : PanelGrid.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeGridFacet(context, grid, columns, "header", PanelGrid.HEADER_CLASS);
        encodeGridBody(context, grid, columns);
        encodeGridFacet(context, grid, columns, "footer", PanelGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    public void encodeTableBody(FacesContext context, PanelGrid grid, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("tbody", grid);

        if (columns > 0) {
            encodeDynamicBody(context, grid, grid.getColumns());
        }
        else {
            encodeStaticBody(context, grid);
        }

        writer.endElement("tbody");
    }

    public void encodeDynamicBody(FacesContext context, PanelGrid grid, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = grid.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");

        int openendRows = 0;
        int closedRows = 0;

        int i = 0;
        for (UIComponent child : grid.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            int colMod = i % columns;
            if (colMod == 0) {
                openendRows++;

                writer.startElement("tr", null);
                writer.writeAttribute("class", PanelGrid.TABLE_ROW_CLASS, null);
                writer.writeAttribute("role", "row", null);
            }

            String columnClass = (colMod < columnClasses.length)
                                 ? PanelGrid.CELL_CLASS + " " + columnClasses[colMod].trim()
                                 : PanelGrid.CELL_CLASS;
            writer.startElement("td", null);
            writer.writeAttribute("role", "gridcell", null);
            writer.writeAttribute("class", columnClass, null);
            child.encodeAll(context);
            writer.endElement("td");

            i++;
            colMod = i % columns;

            if (colMod == 0) {
                closedRows++;

                writer.endElement("tr");
            }
        }

        // special handling for #4122, when the child count is not a multiple of the columns count
        if (openendRows > closedRows) {
            writer.endElement("tr");
        }
    }

    public void encodeStaticBody(FacesContext context, PanelGrid grid) throws IOException {
        context.getAttributes().put(Constants.HELPER_RENDERER, "panelGridBody");
        int i = 0;

        for (UIComponent child : grid.getChildren()) {
            if (child.isRendered()) {
                if (child instanceof Row) {
                    String rowStyleClass = i % 2 == 0
                                           ? PanelGrid.TABLE_ROW_CLASS + " " + PanelGrid.EVEN_ROW_CLASS
                                           : PanelGrid.TABLE_ROW_CLASS + " " + PanelGrid.ODD_ROW_CLASS;
                    encodeRow(context, (Row) child, "gridcell", rowStyleClass, PanelGrid.CELL_CLASS);
                    i++;
                }
                else {
                    child.encodeAll(context);
                }
            }
        }

        context.getAttributes().remove(Constants.HELPER_RENDERER);
    }

    public void encodeRow(FacesContext context, Row row, String columnRole, String rowClass, String columnClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = row.getStyle();

        writer.startElement("tr", null);
        if (shouldWriteId(row)) {
            writer.writeAttribute("id", row.getClientId(context), null);
        }
        if (row.getStyleClass() != null) {
            rowClass += " " + row.getStyleClass();
        }
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.writeAttribute("class", rowClass, null);
        writer.writeAttribute("role", "row", null);

        for (UIComponent child : row.getChildren()) {
            if (child instanceof Column && child.isRendered()) {
                Column column = (Column) child;
                String userStyleClass = column.getStyleClass();
                String styleClass = (userStyleClass == null) ? columnClass : columnClass + " " + userStyleClass;

                writer.startElement("td", null);
                if (shouldWriteId(column)) {
                    writer.writeAttribute("id", column.getClientId(context), null);
                }
                writer.writeAttribute("role", columnRole, null);
                writer.writeAttribute("class", styleClass, null);

                if (column.getStyle() != null) {
                    writer.writeAttribute("style", column.getStyle(), null);
                }
                if (column.getColspan() > 1) {
                    writer.writeAttribute("colspan", column.getColspan(), null);
                }
                if (column.getRowspan() > 1) {
                    writer.writeAttribute("rowspan", column.getRowspan(), null);
                }

                renderChildren(context, column);

                writer.endElement("td");
            }
            else {
                child.encodeAll(context);
            }
        }

        writer.endElement("tr");
    }

    public void encodeGridBody(FacesContext context, PanelGrid grid, int columns) throws IOException {
        String clientId = grid.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = grid.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", PanelGrid.CONTENT_CLASS, null);

        int i = 0;
        for (UIComponent child : grid.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            int colMod = i % columns;
            if (colMod == 0) {
                writer.startElement("div", null);
                String rowClass = (columnClasses.length > 0 && columnClasses[0].contains("ui-grid-col-")) ? "ui-grid-row" : PanelGrid.GRID_ROW_CLASS;
                writer.writeAttribute("class", rowClass, null);
            }

            String columnClass = (colMod < columnClasses.length) ? PanelGrid.CELL_CLASS + " " + columnClasses[colMod].trim() : PanelGrid.CELL_CLASS;
            if (!columnClass.contains("ui-md-") && !columnClass.contains("ui-g-") && !columnClass.contains("ui-grid-col-")) {
                columnClass = columnClass + " " + GridLayoutUtils.getColumnClass(columns);
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", columnClass, null);
            child.encodeAll(context);
            writer.endElement("div");

            i++;
            colMod = i % columns;

            if (colMod == 0) {
                writer.endElement("div");
            }
        }

        if (i != 0 && (i % columns) != 0) {
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    public void encodeTableFacet(FacesContext context, PanelGrid grid, int columns, String facet, String tag, String styleClass)
            throws IOException {

        UIComponent component = grid.getFacet(facet);

        if (component != null && component.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement(tag, null);
            writer.writeAttribute("class", styleClass, null);

            if (columns > 0) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", "ui-widget-header", null);
                writer.writeAttribute("role", "row", null);

                writer.startElement("td", null);
                writer.writeAttribute("colspan", columns, null);
                writer.writeAttribute("role", "columnheader", null);
                writer.writeAttribute("class", PanelGrid.CELL_CLASS + " ui-widget-header", null);

                component.encodeAll(context);

                writer.endElement("td");
                writer.endElement("tr");
            }
            else {
                context.getAttributes().put(Constants.HELPER_RENDERER, "panelGridFacet");
                if (component instanceof Row) {
                    encodeRow(context, (Row) component, "columnheader", "ui-widget-header", PanelGrid.CELL_CLASS + " ui-widget-header");
                }
                else if (component instanceof UIPanel) {
                    for (UIComponent child : component.getChildren()) {
                        if (child.isRendered()) {
                            if (child instanceof Row) {
                                encodeRow(context, (Row) child, "columnheader", "ui-widget-header", PanelGrid.CELL_CLASS + " ui-widget-header");
                            }
                            else {
                                component.encodeAll(context);
                            }
                        }
                    }
                }
                else {
                    component.encodeAll(context);
                }
                context.getAttributes().remove(Constants.HELPER_RENDERER);
            }

            writer.endElement(tag);
        }
    }

    public void encodeGridFacet(FacesContext context, PanelGrid grid, int columns, String facet, String styleClass) throws IOException {
        UIComponent component = grid.getFacet(facet);

        if (component != null && component.isRendered()) {
            ResponseWriter writer = context.getResponseWriter();

            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass + " ui-widget-header", null);
            component.encodeAll(context);
            writer.endElement("div");
        }
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
