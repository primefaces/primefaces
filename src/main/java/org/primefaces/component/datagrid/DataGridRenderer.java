/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.datagrid;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.GridLayoutUtils;
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
            grid.updatePaginationData(context, grid);

            if (grid.isLazy()) {
                grid.loadLazyData();
            }

            encodeContent(context, grid);
        }
        else {
            encodeMarkup(context, grid);
            encodeScript(context, grid);
        }
    }

    protected void encodeScript(FacesContext context, DataGrid grid) throws IOException {
        String clientId = grid.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataGrid", grid.resolveWidgetVar(), clientId);

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
        String style = grid.getStyle();
        String styleClass = grid.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + grid.getStyleClass();
        String contentClass = empty
                              ? DataGrid.EMPTY_CONTENT_CLASS
                              : (layout.equals("tabular") ? DataGrid.TABLE_CONTENT_CLASS : DataGrid.GRID_CONTENT_CLASS);

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

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, grid, "top");
        }

        writer.startElement("div", grid);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (empty) {
            UIComponent emptyFacet = grid.getFacet("emptyMessage");
            if (emptyFacet != null) {
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

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, grid, "bottom");
        }

        encodeFacet(context, grid, "footer", DataGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, DataGrid grid) throws IOException {
        String layout = grid.getLayout();

        if (layout.equals("tabular")) {
            encodeTable(context, grid);
        }
        else if (layout.equals("grid")) {
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
        String columnClass = DataGrid.COLUMN_CLASS + " " + GridLayoutUtils.getColumnClass(columns);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            grid.setRowIndex(rowIndex);
            if (!grid.isRowAvailable()) {
                break;
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", DataGrid.GRID_ROW_CLASS, null);

            for (int j = 0; j < columns; j++) {
                writer.startElement("div", null);
                writer.writeAttribute("class", columnClass, null);

                grid.setRowIndex(rowIndex);
                if (grid.isRowAvailable()) {
                    renderChildren(context, grid);
                }
                rowIndex++;

                writer.endElement("div");
            }

            writer.endElement("div");
        }

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
