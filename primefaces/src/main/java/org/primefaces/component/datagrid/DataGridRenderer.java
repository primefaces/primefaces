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
package org.primefaces.component.datagrid;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = DataGrid.DEFAULT_RENDERER, componentFamily = DataGrid.COMPONENT_FAMILY)
public class DataGridRenderer extends DataRenderer<DataGrid> {

    @Override
    public void decode(FacesContext context, DataGrid component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, DataGrid component) throws IOException {
        if (component.isPaginationRequest(context)) {
            component.updatePaginationData(context);

            if (component.isLazy()) {
                component.loadLazyData();
            }

            encodeContent(context, component);

            if (component.isMultiViewState()) {
                DataGridState gs = component.getMultiViewState(true);
                gs.setFirst(component.getFirst());
                gs.setRows(component.getRows());
            }
        }
        else {
            if (component.isMultiViewState()) {
                component.restoreMultiViewState();
            }

            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeScript(FacesContext context, DataGrid component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataGrid", component);

        if (component.isPaginator()) {
            encodePaginatorConfig(context, component, wb);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, DataGrid component) throws IOException {
        if (component.isLazy()) {
            component.loadLazyData();
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean hasPaginator = component.isPaginator();
        boolean empty = component.getRowCount() == 0;
        String paginatorPosition = component.getPaginatorPosition();
        boolean flex = ComponentUtils.isFlex(context, component);
        String layoutClass = DataGrid.GRID_CONTENT_CLASS + " " + GridLayoutUtils.getResponsiveClass(flex);
        String style = component.getStyle();
        String styleClass = component.getStyleClass() == null ? DataGrid.DATAGRID_CLASS : DataGrid.DATAGRID_CLASS + " " + component.getStyleClass();
        String contentClass = empty ? DataGrid.EMPTY_CONTENT_CLASS : layoutClass;

        if (hasPaginator) {
            component.calculateFirst();
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeFacet(context, component, "header", DataGrid.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "top");
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (empty) {
            UIComponent emptyFacet = component.getFacet("emptyMessage");
            if (FacetUtils.shouldRenderFacet(emptyFacet)) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(component.getEmptyMessage(), "emptyMessage");
            }
        }
        else {
            encodeContent(context, component);
        }

        writer.endElement("div");

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "bottom");
        }

        encodeFacet(context, component, "footer", DataGrid.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, DataGrid component) throws IOException {
        encodeGrid(context, component);
    }

    protected void encodeGrid(FacesContext context, DataGrid component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        int columns = component.getColumns();
        int rowIndex = component.getFirst();
        int rows = component.getRows();
        int itemsToRender = rows != 0 ? rows : component.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
        int displayedItemsToRender = rowIndex + itemsToRender;
        String columnInlineStyle = component.getRowStyle();
        boolean flex = ComponentUtils.isFlex(context, component);

        String columnClass = getStyleClassBuilder(context)
                .add(DataGrid.COLUMN_CLASS)
                .add(GridLayoutUtils.getColumnClass(flex, columns))
                .add(component.getRowStyleClass())
                .build();

        writer.startElement("div", null);

        writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(flex), null);
        writer.writeAttribute("title", component.getRowTitle(), null);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            component.setRowIndex(rowIndex);
            if (!component.isRowAvailable()) {
                break;
            }

            for (int j = 0; j < columns; j++) {
                writer.startElement("div", null);
                writer.writeAttribute("class", columnClass, null);

                if (!LangUtils.isEmpty(columnInlineStyle)) {
                    writer.writeAttribute("style", columnInlineStyle, null);
                }

                component.setRowIndex(rowIndex);
                if (component.isRowAvailable()) {
                    renderChildren(context, component);
                }
                rowIndex++;

                writer.endElement("div");

                if (rowIndex >= displayedItemsToRender) {
                    break;
                }
            }
        }

        writer.endElement("div");

        component.setRowIndex(-1); //cleanup
    }

    @Override
    public void encodeChildren(FacesContext context, DataGrid component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
