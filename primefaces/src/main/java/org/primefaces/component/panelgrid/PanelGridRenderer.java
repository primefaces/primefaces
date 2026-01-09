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
package org.primefaces.component.panelgrid;

import org.primefaces.component.column.Column;
import org.primefaces.component.row.Row;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.LangUtils;

import java.io.IOException;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = PanelGrid.DEFAULT_RENDERER, componentFamily = PanelGrid.COMPONENT_FAMILY)
public class PanelGridRenderer extends CoreRenderer<PanelGrid> {

    @Override
    public void encodeEnd(FacesContext context, PanelGrid component) throws IOException {
        String layout = component.getLayout();

        if (PanelGrid.LAYOUT_TABULAR.equalsIgnoreCase(layout)) {
            encodeLegacyTableLayout(context, component);
        }
        else if (PanelGrid.LAYOUT_GRID.equalsIgnoreCase(layout) || PanelGrid.LAYOUT_FLEX.equalsIgnoreCase(layout)) {
            encodeGridLayout(context, component);
        }
        else if (PanelGrid.LAYOUT_TAILWIND.equalsIgnoreCase(layout)) {
            encodeTailwindLayout(context, component);
        }
        else {
            throw new FacesException("The value of 'layout' attribute of PanelGrid \"" + component.getClientId(
                    context) + "\" must be 'grid', 'tabular', 'flex' or 'tailwind'. Default value is 'grid'.");
        }
    }

    public void encodeTailwindLayout(FacesContext context, PanelGrid component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        int columns = component.getColumns();

        if (columns <= 0 || columns > 12) {
            throw new FacesException("PanelGrid \"" + clientId + "\" columns must be between 1 and 12 for Tailwind layout.");
        }

        String style = component.getStyle();
        String containerClass = getStyleClassBuilder(context)
                .add(PanelGrid.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, "styleClass");
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, "style");
        }

        encodeTailwindFacet(context, component, columns, "header", PanelGrid.HEADER_CLASS);
        encodeTailwindBody(context, component, columns);
        encodeTailwindFacet(context, component, columns, "footer", PanelGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    public void encodeTailwindBody(FacesContext context, PanelGrid component, int columns) throws IOException {
        String clientId = component.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = component.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");

        // Build Tailwind grid class based on columns
        String gridClass = getTailwindGridClass(columns);

        // Check if custom gap is specified in contentStyleClass
        String contentStyleClass = component.getContentStyleClass();
        boolean hasCustomGap = contentStyleClass != null && contentStyleClass.contains("gap-");

        String contentClass = getStyleClassBuilder(context)
                .add(PanelGrid.CONTENT_CLASS)
                .add(gridClass)
                .add(hasCustomGap ? null : "gap-4") // Default gap only if not specified
                .add(contentStyleClass)
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (LangUtils.isNotBlank(component.getContentStyle())) {
            writer.writeAttribute("style", component.getContentStyle(), null);
        }

        int i = 0;
        for (UIComponent child : component.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            int colMod = i % columns;
            String columnClass = (colMod < columnClasses.length)
                    ? PanelGrid.CELL_CLASS + " " + columnClasses[colMod].trim()
                    : PanelGrid.CELL_CLASS;

            writer.startElement("div", null);
            Column column = child instanceof Column ? (Column) child : null;
            if (column != null) {
                if (column.getId() != null) writer.writeAttribute("id", column.getClientId(context), null);
                if (column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
                if (column.getStyleClass() != null) {
                    columnClass = columnClass + " " + column.getStyleClass();
                }
                // Handle colspan for Tailwind
                if (column.getColspan() > 1) {
                    columnClass = columnClass + " " + getTailwindColSpanClass(column.getColspan());
                }
                // Handle rowspan for Tailwind
                if (column.getRowspan() > 1) {
                    columnClass = columnClass + " " + getTailwindRowSpanClass(column.getRowspan());
                }
            }
            writer.writeAttribute("class", columnClass, null);
            child.encodeAll(context);
            writer.endElement("div");

            i++;
        }

        writer.endElement("div");
    }

    public void encodeTailwindFacet(FacesContext context, PanelGrid component, int columns, String facetName, String styleClass) throws IOException {
        UIComponent facet = component.getFacet(facetName);
        if (FacetUtils.shouldRenderFacet(facet)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass + " ui-widget-header", null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    private String getTailwindGridClass(int columns) {
        switch (columns) {
            case 1: return "grid grid-cols-1";
            case 2: return "grid grid-cols-1 sm:grid-cols-2";
            case 3: return "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3";
            case 4: return "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4";
            case 5: return "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5";
            case 6: return "grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6";
            case 7: return "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-7";
            case 8: return "grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8";
            case 9: return "grid grid-cols-3 sm:grid-cols-3 lg:grid-cols-9";
            case 10: return "grid grid-cols-2 sm:grid-cols-5 lg:grid-cols-10";
            case 11: return "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-11";
            case 12: return "grid grid-cols-3 sm:grid-cols-4 lg:grid-cols-12";
            default: return "grid grid-cols-" + columns;
        }
    }

    private String getTailwindColSpanClass(int colspan) {
        if (colspan <= 1) return "";
        if (colspan >= 12) return "col-span-full";
        return "col-span-" + colspan;
    }

    private String getTailwindRowSpanClass(int rowspan) {
        if (rowspan <= 1) return "";
        if (rowspan >= 6) return "row-span-6";
        return "row-span-" + rowspan;
    }

    public void encodeLegacyTableLayout(FacesContext context, PanelGrid component) throws IOException {
        String clientId = component.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        int columns = component.getColumns();
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? PanelGrid.CONTAINER_CLASS : PanelGrid.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("table", component);
        writer.writeAttribute("id", clientId, "id");
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, "style");
        }
        if (LangUtils.isNotBlank(styleClass)) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        if (LangUtils.isNotBlank(component.getRole())) {
            writer.writeAttribute("role", component.getRole(), null);
        }

        encodeTableFacet(context, component, columns, "header", "thead", PanelGrid.HEADER_CLASS);
        encodeTableFacet(context, component, columns, "footer", "tfoot", PanelGrid.FOOTER_CLASS);
        encodeTableBody(context, component, columns);

        writer.endElement("table");
    }

    public void encodeGridLayout(FacesContext context, PanelGrid component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String layout = component.getLayout();
        int columns = component.getColumns();
        if (columns <= 0 || columns > 12 || 12 % columns != 0) {
            throw new FacesException("PanelGrid \"" + clientId + "\" columns must be a number that factors into 12. For example: 1,2,3,4,6,12");
        }

        String style = component.getStyle();
        String containerClass = getStyleClassBuilder(context)
                .add(PanelGrid.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", containerClass, "styleClass");
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, "style");
        }

        encodeGridFacet(context, component, columns, "header", PanelGrid.HEADER_CLASS);

        if (PanelGrid.LAYOUT_FLEX.equalsIgnoreCase(layout)) {
            encodeFlexGridBody(context, component, columns);
        }
        else {
            encodeGridBody(context, component, columns);
        }

        encodeGridFacet(context, component, columns, "footer", PanelGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    public void encodeTableBody(FacesContext context, PanelGrid component, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("tbody", component);

        if (columns > 0) {
            encodeDynamicBody(context, component, component.getColumns());
        }
        else {
            encodeStaticBody(context, component);
        }

        writer.endElement("tbody");
    }

    public void encodeDynamicBody(FacesContext context, PanelGrid component, int columns) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = component.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");

        int openendRows = 0;
        int closedRows = 0;

        int i = 0;
        for (UIComponent child : component.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            int colMod = i % columns;
            if (colMod == 0) {
                openendRows++;

                writer.startElement("tr", null);
                writer.writeAttribute("class", PanelGrid.TABLE_ROW_CLASS, null);
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

    public void encodeStaticBody(FacesContext context, PanelGrid component) throws IOException {
        context.getAttributes().put(Constants.HELPER_RENDERER, "panelGridBody");
        int i = 0;

        for (UIComponent child : component.getChildren()) {
            if (child instanceof Row || ComponentUtils.isUIRepeat(child)) {
                // #6829 count row even though its not rendered
                // #7780 count row if a UIRepeat and assume the user knows what they are doing
                i++;
            }
            if (child.isRendered()) {
                if (child instanceof Row) {
                    String rowStyleClass = i % 2 == 0
                                           ? PanelGrid.TABLE_ROW_CLASS + " " + PanelGrid.EVEN_ROW_CLASS
                                           : PanelGrid.TABLE_ROW_CLASS + " " + PanelGrid.ODD_ROW_CLASS;
                    encodeRow(context, (Row) child, "gridcell", rowStyleClass, PanelGrid.CELL_CLASS);
                }
                else {
                    child.encodeAll(context);
                }
            }
        }

        if (i == 0) {
            throw new FacesException("PanelGrid \"" + component.getClientId(context)
                + "\" without a 'columns' attribute expects at least one <p:row> element.");
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
        if (LangUtils.isNotBlank(row.getStyleClass())) {
            rowClass += " " + row.getStyleClass();
        }
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, null);
        }

        writer.writeAttribute("class", rowClass, null);

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
                if (LangUtils.isNotBlank(styleClass)) {
                    writer.writeAttribute("class", styleClass, null);
                }
                if (LangUtils.isNotBlank(column.getStyle())) {
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

    public void encodeGridBody(FacesContext context, PanelGrid component, int columns) throws IOException {
        String clientId = component.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = component.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");
        String contentClass = getStyleClassBuilder(context)
                .add(PanelGrid.CONTENT_CLASS)
                .add(GridLayoutUtils.getResponsiveClass(false))
                .add(component.getContentStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (LangUtils.isNotBlank(component.getContentStyle())) {
            writer.writeAttribute("style", component.getContentStyle(), null);
        }

        int i = 0;
        for (UIComponent child : component.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            Row row = null;
            if (child instanceof Row) {
                if (i > 0) {
                    writer.endElement("div");
                }
                row = (Row) child;
                i = 0;
            }

            int colMod = i % columns;
            if (colMod == 0) {
                writer.startElement("div", null);
                String rowClass = (columnClasses.length > 0 && columnClasses[0].contains("ui-grid-col-")) ? "ui-grid-row" : PanelGrid.GRID_ROW_CLASS;
                if (row != null) {
                    if (row.getId() != null) writer.writeAttribute("id", row.getClientId(context), null);
                    if (row.getStyle() != null) writer.writeAttribute("style", row.getStyle(), null);
                    if (row.getStyleClass() != null) rowClass = rowClass + " " + row.getStyleClass();
                }
                writer.writeAttribute("class", rowClass, null);
            }

            if (row == null) {
                encodeColumn(context, columns, writer, columnClasses, child, colMod);
            }
            else {
                int iRow = 0;
                int classesLength = columnClasses.length > 0 ? columnClasses.length : 1;
                for (UIComponent rowChild : row.getChildren()) {
                    encodeColumn(context, columns, writer, columnClasses, rowChild, iRow % classesLength);
                    iRow++;
                }
            }

            i++;
            colMod = i % columns;

            if (colMod == 0 || row != null) {
                writer.endElement("div");
                i = 0;
            }
        }

        if (i != 0 && (i % columns) != 0) {
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    public void encodeFlexGridBody(FacesContext context, PanelGrid component, int columns) throws IOException {
        String clientId = component.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        String columnClassesValue = component.getColumnClasses();
        String[] columnClasses = columnClassesValue == null ? new String[0] : columnClassesValue.split(",");
        String contentClass = getStyleClassBuilder(context)
                .add(PanelGrid.CONTENT_CLASS)
                .add(GridLayoutUtils.getFlexGridClass(true))
                .add(component.getContentStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (component.getContentStyle() != null) {
            writer.writeAttribute("style", component.getContentStyle(), null);
        }

        int i = 0;
        for (UIComponent child : component.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            int colMod = i % columns;
            String columnClass = (colMod < columnClasses.length) ? PanelGrid.CELL_CLASS + " " + columnClasses[colMod].trim() : PanelGrid.CELL_CLASS;
            if (!columnClass.contains("md-") && !columnClass.contains("col-")) {
                columnClass = columnClass + " " + GridLayoutUtils.getFlexColumnClass(columns);
            }

            writer.startElement("div", null);
            Column column = child instanceof Column ? (Column) child : null;
            if (column != null) {
                if (column.getId() != null) writer.writeAttribute("id", column.getClientId(context), null);
                if (column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
                if (column.getStyleClass() != null) columnClass += columnClass + " " + column.getStyleClass();
            }
            writer.writeAttribute("class", columnClass, null);
            child.encodeAll(context);
            writer.endElement("div");

            i++;
        }

        writer.endElement("div");
    }


    private void encodeColumn(FacesContext context,
                              int columns,
                              ResponseWriter writer,
                              String[] columnClasses,
                              UIComponent child,
                              int colMod) throws IOException {


        String columnClass = (colMod < columnClasses.length) ? PanelGrid.CELL_CLASS + " " + columnClasses[colMod].trim() : PanelGrid.CELL_CLASS;
        if (!columnClass.contains("ui-md-") && !columnClass.contains("ui-g-") && !columnClass.contains("ui-grid-col-")) {
            columnClass = columnClass + " " + GridLayoutUtils.getColumnClass(columns);
        }

        writer.startElement("div", null);
        Column column = child instanceof Column ? (Column) child : null;
        if (column != null) {
            if (column.getId() != null) writer.writeAttribute("id", column.getClientId(context), null);
            if (column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
            if (column.getStyleClass() != null) columnClass = PanelGrid.CELL_CLASS + " " + column.getStyleClass();
        }
        writer.writeAttribute("class", columnClass, null);
        child.encodeAll(context);
        writer.endElement("div");
    }

    public void encodeTableFacet(FacesContext context, PanelGrid component, int columns, String facetName, String tag, String styleClass)
            throws IOException {

        UIComponent facet = component.getFacet(facetName);
        if (FacetUtils.shouldRenderFacet(facet)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement(tag, null);
            writer.writeAttribute("class", styleClass, null);

            if (columns > 0) {
                writer.startElement("tr", null);
                writer.writeAttribute("class", "ui-widget-header", null);

                writer.startElement("td", null);
                writer.writeAttribute("colspan", columns, null);
                writer.writeAttribute("role", "columnheader", null);
                writer.writeAttribute("class", PanelGrid.CELL_CLASS + " ui-widget-header", null);

                facet.encodeAll(context);

                writer.endElement("td");
                writer.endElement("tr");
            }
            else {
                context.getAttributes().put(Constants.HELPER_RENDERER, "panelGridFacet");
                if (facet instanceof Row) {
                    encodeRow(context, (Row) facet, "columnheader", "ui-widget-header", PanelGrid.CELL_CLASS + " ui-widget-header");
                }
                else if (facet instanceof UIPanel) {
                    for (UIComponent child : facet.getChildren()) {
                        if (child.isRendered()) {
                            if (child instanceof Row) {
                                encodeRow(context, (Row) child, "columnheader", "ui-widget-header", PanelGrid.CELL_CLASS + " ui-widget-header");
                            }
                            else {
                                facet.encodeAll(context);
                            }
                        }
                    }
                }
                else {
                    facet.encodeAll(context);
                }
                context.getAttributes().remove(Constants.HELPER_RENDERER);
            }

            writer.endElement(tag);
        }
    }

    public void encodeGridFacet(FacesContext context, PanelGrid component, int columns, String facetName, String styleClass) throws IOException {
        UIComponent facet = component.getFacet(facetName);
        if (FacetUtils.shouldRenderFacet(facet)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("div", null);
            writer.writeAttribute("class", styleClass + " ui-widget-header", null);
            facet.encodeAll(context);
            writer.endElement("div");
        }
    }

    @Override
    public void encodeChildren(FacesContext context, PanelGrid component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}