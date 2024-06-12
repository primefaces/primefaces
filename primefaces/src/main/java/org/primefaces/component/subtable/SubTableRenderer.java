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
package org.primefaces.component.subtable;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.ColumnAware;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LangUtils;

public class SubTableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SubTable table = (SubTable) component;
        int rowCount = table.getRowCount();

        encodeHeader(context, table);

        for (int i = 0; i < rowCount; i++) {
            encodeRow(context, table, i);
        }

        table.setRowIndex(-1);

        encodeFooter(context, table);
    }

    public void encodeHeader(FacesContext context, SubTable table) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = table.getFacet("header");

        if (FacetUtils.shouldRenderFacet(header)) {
            writer.startElement("tr", null);
            writer.writeAttribute("class", "ui-widget-header", null);

            writer.startElement("td", null);
            writer.writeAttribute("class", DataTable.SUBTABLE_HEADER, null);
            writer.writeAttribute("colspan", table.getColumns().size(), null);

            header.encodeAll(context);

            writer.endElement("td");
            writer.endElement("tr");
        }
    }

    public void encodeRow(FacesContext context, SubTable table, int rowIndex) throws IOException {
        table.setRowIndex(rowIndex);
        if (!table.isRowAvailable()) {
            return;
        }

        encodeRow(context, table, rowIndex, 0, table.getColumnsCount());
    }

    public void encodeFooter(FacesContext context, SubTable table) throws IOException {
        UIComponent footer = table.getFacet("footer");
        boolean hasFooterColumn = table.hasFooterColumn();
        if (footer == null && !hasFooterColumn) {
            return;
        }

        if (hasFooterColumn) {
            encodeColumnFooters(context, table, 0, table.getColumns().size());
        }
        if (footer != null) {
            footer.encodeAll(context);
        }
    }

    public void encodeRow(FacesContext context, SubTable table, int rowIndex, int columnStart, int columnEnd)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();

        String rowStyleClass = getStyleClassBuilder(context)
                .add(DataTable.ROW_CLASS)
                .add(rowIndex % 2 == 0, DataTable.EVEN_ROW_CLASS, DataTable.ODD_ROW_CLASS)
                .build();

        writer.startElement("tr", null);
        writer.writeAttribute("data-ri", rowIndex, null);
        if (LangUtils.isNotBlank(rowStyleClass)) {
            writer.writeAttribute("class", rowStyleClass, null);
        }

        for (int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);

            if (column instanceof Column) {
                encodeCell(context, table, column);
            }
            else if (column instanceof DynamicColumn) {
                DynamicColumn dynamicColumn = (DynamicColumn) column;
                dynamicColumn.applyModel();

                encodeCell(context, table, dynamicColumn);
            }
        }

        writer.endElement("tr");
    }

    protected void encodeCell(FacesContext context, SubTable table, UIColumn column) throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        CellEditor editor = column.getCellEditor();
        boolean editorEnabled = editor != null && editor.isRendered();
        int responsivePriority = column.getResponsivePriority();
        String title = column.getTitle();
        String style = column.getStyle();

        String styleClass = getStyleClassBuilder(context)
                .add(editorEnabled && editor.isDisabled(), DataTable.CELL_EDITOR_DISABLED_CLASS)
                .add(editorEnabled && !editor.isDisabled(), DataTable.EDITABLE_COLUMN_CLASS)
                .add(!column.isSelectRow(), DataTable.UNSELECTABLE_COLUMN_CLASS)
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
        UIComponent component = (column instanceof UIComponent) ? (UIComponent) column : null;
        if (component != null) {
            renderDynamicPassThruAttributes(context, component);
        }

        if (column.getChildren().isEmpty() && !LangUtils.isBlank(column.getField())) {
            encodeDefaultFieldCell(context, table, column, writer);
        }
        else if (column instanceof DynamicColumn) {
            encodeDynamicCell(context, table, column);
        }
        else {
            column.renderChildren(context);
        }

        writer.endElement("td");
    }

    /**
     * Encodes dynamic column. Allows to override default behavior.
     */
    protected void encodeDynamicCell(FacesContext context, SubTable table, UIColumn column) throws IOException {
        column.encodeAll(context);
    }

    protected void encodeDefaultFieldCell(FacesContext context, SubTable table, UIColumn column, ResponseWriter writer) throws IOException {
        Object value = UIColumn.createValueExpressionFromField(context, table.getVar(), column.getField()).getValue(context.getELContext());
        String str = ComponentUtils.getConvertedAsString(context, column.asUIComponent(), column.getConverter(), value);
        if (str != null) {
            writer.writeText(str, null);
        }
    }

    protected <C extends UIComponent & ColumnAware> void encodeColumnFooters(FacesContext context, C table, int columnStart, int columnEnd) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<UIColumn> columns = table.getColumns();
        writer.startElement("tr", null);
        writer.writeAttribute("role", "row", null);
        for (int i = columnStart; i < columnEnd; i++) {
            UIColumn column = columns.get(i);
            if (column instanceof DynamicColumn) {
                ((DynamicColumn) column).applyModel();
            }

            encodeColumnFooter(context, table, column, column.getRowspan(), column.getColspan());
        }

        writer.endElement("tr");
    }

    public <C extends UIComponent & ColumnAware> void encodeColumnFooter(FacesContext context, C table, UIColumn column, int rowspan, int colspan)
            throws IOException {
        if (!column.isRendered()) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();

        int responsivePriority = column.getResponsivePriority();
        String style = column.getStyle();
        String styleClass = column.getStyleClass();
        styleClass = styleClass == null ? DataTable.COLUMN_FOOTER_CLASS : DataTable.COLUMN_FOOTER_CLASS + " " + styleClass;

        if (responsivePriority > 0) {
            styleClass = styleClass + " ui-column-p-" + responsivePriority;
        }

        writer.startElement("td", null);
        if (LangUtils.isNotBlank(styleClass)) {
            writer.writeAttribute("class", styleClass, null);
        }
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("rowspan", rowspan, null);
        }
        if (rowspan != 1) {
            writer.writeAttribute("colspan", colspan, null);
        }

        //Footer content
        UIComponent facet = column.getFacet("footer");
        String text = column.getFooterText();
        if (FacetUtils.shouldRenderFacet(facet)) {
            facet.encodeAll(context);
        }
        else if (text != null) {
            writer.writeText(text, "footerText");
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
