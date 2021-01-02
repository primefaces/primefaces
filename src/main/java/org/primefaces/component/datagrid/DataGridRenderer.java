/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.datagrid;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class DataGridRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataGrid grid = (DataGrid) component;

        if (grid.isPaginationRequest(context)) {
            grid.updatePaginationData(context);

            if (grid.isLazy()) {
                grid.loadLazyData();
            }

            encodeContent(context, grid);

            if (grid.isMultiViewState()) {
                DataGridState gs = grid.getMultiViewState(true);
                gs.setFirst(grid.getFirst());
                gs.setRows(grid.getRows());
            }
        }
        else {
            if (grid.isMultiViewState()) {
                grid.restoreMultiViewState();
            }

            encodeMarkup(context, grid);
            encodeScript(context, grid);
        }
    }

    protected void encodeScript(FacesContext context, DataGrid grid) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataGrid", grid);

        if (grid.isPaginator()) {
            encodePaginatorConfig(context, grid, wb);
        }

        encodeClientBehaviors(context, grid);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, DataGrid grid) throws IOException {
        if (grid.isLazy()) {
            grid.loadLazyData();
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = grid.getClientId(context);
        boolean hasPaginator = grid.isPaginator();
        boolean empty = grid.getRowCount() == 0;
        String layout = grid.getLayout();
        String paginatorPosition = grid.getPaginatorPosition();
        boolean flex = ComponentUtils.isFlex(context, grid);
        String gridContentClass = flex ? DataGrid.FLEX_GRID_CONTENT_CLASS : DataGrid.GRID_CONTENT_CLASS;
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + grid.getStyleClass();
        String layoutClass = "tabular".equals(layout) ? DataGrid.TABLE_CONTENT_CLASS : gridContentClass;
        String contentClass = empty ? DataGrid.EMPTY_CONTENT_CLASS : layoutClass;

        if (hasPaginator) {
            grid.calculateFirst();
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeFacet(context, grid, "header", DataGrid.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, grid, "top");
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (empty) {
            UIComponent emptyFacet = grid.getFacet("emptyMessage");
            if (ComponentUtils.shouldRenderFacet(emptyFacet)) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(grid.getEmptyMessage(), "emptyMessage");
            }
        }
        else {
            encodeContent(context, grid);
        }

        writer.endElement("div");

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, grid, "bottom");
        }

        encodeFacet(context, grid, "footer", DataGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, DataGrid grid) throws IOException {
        String layout = grid.getLayout();

        if ("tabular".equals(layout)) {
            encodeTable(context, grid);
        }
        else if ("grid".equals(layout)) {
            encodeGrid(context, grid);
        }
        else {
            throw new FacesException(layout + " is not a valid value for DataGrid layout. Possible values are 'tabular' and 'grid'.");
        }
    }

    protected void encodeGrid(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        int columns = grid.getColumns();
        int rowIndex = grid.getFirst();
        int rows = grid.getRows();
        int itemsToRender = rows != 0 ? rows : grid.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
        int displayedItemsToRender = rowIndex + itemsToRender;
        String columnInlineStyle = grid.getRowStyle();
        boolean flex = ComponentUtils.isFlex(context, grid);

        String columnClass = getStyleClassBuilder(context)
                .add(DataGrid.COLUMN_CLASS)
                .add(flex, GridLayoutUtils.getFlexColumnClass(columns),  GridLayoutUtils.getColumnClass(columns))
                .add(grid.getRowStyleClass())
                .build();

        writer.startElement("div", null);

        if (flex) {
            writer.writeAttribute("class", DataGrid.FLEX_GRID_ROW_CLASS, null);
        }
        else {
            writer.writeAttribute("class", DataGrid.GRID_ROW_CLASS, null);
        }

        for (int i = 0; i < numberOfRowsToRender; i++) {
            grid.setRowIndex(rowIndex);
            if (!grid.isRowAvailable()) {
                break;
            }

            for (int j = 0; j < columns; j++) {
                writer.startElement("div", null);
                writer.writeAttribute("class", columnClass, null);
                if (!LangUtils.isValueEmpty(columnInlineStyle)) {
                    writer.writeAttribute("style", columnInlineStyle, null);
                }

                grid.setRowIndex(rowIndex);
                if (grid.isRowAvailable()) {
                    renderChildren(context, grid);
                }
                rowIndex++;

                writer.endElement("div");

                if (rowIndex >= displayedItemsToRender) {
                    break;
                }
            }
        }

        writer.endElement("div");

        grid.setRowIndex(-1); //cleanup
    }

    protected void encodeTable(FacesContext context, DataGrid grid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        int columns = grid.getColumns();
        int rowIndex = grid.getFirst();
        int rows = grid.getRows();
        int itemsToRender = rows != 0 ? rows : grid.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;

        writer.startElement("table", grid);
        writer.writeAttribute("class", DataGrid.TABLE_CLASS, null);
        writer.startElement("tbody", null);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            grid.setRowIndex(rowIndex);
            if (!grid.isRowAvailable()) {
                break;
            }

            writer.startElement("tr", null);
            writer.writeAttribute("class", DataGrid.TABLE_ROW_CLASS, null);

            for (int j = 0; j < columns; j++) {
                writer.startElement("td", null);
                writer.writeAttribute("class", DataGrid.COLUMN_CLASS, null);

                grid.setRowIndex(rowIndex);
                if (grid.isRowAvailable()) {
                    renderChildren(context, grid);
                }
                rowIndex++;

                writer.endElement("td");
            }
            writer.endElement("tr");
        }

        grid.setRowIndex(-1); //cleanup

        writer.endElement("tbody");
        writer.endElement("table");
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
