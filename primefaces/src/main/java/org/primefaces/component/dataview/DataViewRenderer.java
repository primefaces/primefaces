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
package org.primefaces.component.dataview;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = DataView.DEFAULT_RENDERER, componentFamily = DataView.COMPONENT_FAMILY)
public class DataViewRenderer extends DataRenderer<DataView> {

    private static final Logger LOGGER = Logger.getLogger(DataViewRenderer.class.getName());

    @Override
    public void decode(FacesContext context, DataView component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, DataView component) throws IOException {
        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        component.findViewItems();

        if (component.isPaginationRequest(context)) {
            component.updatePaginationData(context);

            if (component.isLazy()) {
                component.loadLazyData();
            }

            encodeLayout(context, component);

            if (component.isMultiViewState()) {
                saveMultiViewState(component);
            }
        }
        else if (component.isLayoutRequest(context)) {
            String layout = params.get(clientId + "_layout");
            component.setLayout(layout);

            encodeLayout(context, component);

            if (component.isMultiViewState()) {
                saveMultiViewState(component);
            }
        }
        else {
            if (component.isMultiViewState()) {
                component.restoreMultiViewState();
            }

            encodeMarkup(context, component);
            encodeScript(context, component);

            if (component.isPaginator() && component.getRows() == 0) {
                LOGGER.log(Level.WARNING, "DataView with paginator=true should also set the rows attribute. ClientId: {0}", component.getClientId());
            }
        }
    }

    protected void encodeMarkup(FacesContext context, DataView component) throws IOException {
        if (component.isLazy()) {
            component.loadLazyData();
        }
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String layout = component.getLayout();
        boolean hasPaginator = component.isPaginator();
        String paginatorPosition = component.getPaginatorPosition();
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(DataView.DATAVIEW_CLASS, component.getStyleClass())
                .add(layout.contains("grid"), DataView.GRID_LAYOUT_CLASS, DataView.LIST_LAYOUT_CLASS)
                .build();

        if (hasPaginator) {
            component.calculateFirst();
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeHeader(context, component);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "top");
        }

        encodeContent(context, component);

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "bottom");
        }

        encodeFacet(context, component, "footer", DataView.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, DataView component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent fHeader = component.getFacet("header");
        boolean isRenderFacet = FacetUtils.shouldRenderFacet(fHeader);

        if (isRenderFacet || hasLayoutOptions(context, component)) {
            writer.startElement("div", component);
            writer.writeAttribute("class", DataView.HEADER_CLASS, null);

            if (isRenderFacet) {
                fHeader.encodeAll(context);
            }

            encodeLayoutOptions(context, component);

            writer.endElement("div");
        }
    }

    protected void encodeContent(FacesContext context, DataView component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", DataView.CONTENT_CLASS, null);

        encodeLayout(context, component);

        writer.endElement("div");
    }

    protected boolean hasLayoutOptions(FacesContext context, DataView component) {
        boolean hasGridItem = (component.getGridItem() != null);
        boolean hasListItem = (component.getListItem() != null);
        return hasGridItem && hasListItem;
    }

    protected void encodeLayoutOptions(FacesContext context, DataView component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = component.getLayout();
        boolean isGridLayout = layout.contains("grid");
        String containerClass = DataView.BUTTON_CONTAINER_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        if (hasLayoutOptions(context, component)) {
            String listIcon = component.getListIcon() != null ? component.getListIcon() : "ui-icon-grip-dotted-horizontal";
            encodeButton(context, component, "list", listIcon, !isGridLayout);

            String gridIcon = component.getGridIcon() != null ? component.getGridIcon() : "ui-icon-grip-dotted-vertical";
            encodeButton(context, component, "grid", gridIcon, isGridLayout);
        }

        writer.endElement("div");

    }

    protected void encodeButton(FacesContext context, DataView component, String layout, String icon, boolean isActive) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String buttonClass = isActive ? DataView.BUTTON_CLASS + " ui-state-active" : DataView.BUTTON_CLASS;

        //button
        writer.startElement("div", null);
        writer.writeAttribute("class", buttonClass, null);
        writer.writeAttribute("tabindex", 0, null);

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_" + layout, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", layout, null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute("tabindex", "-1", null);

        if (isActive) {
            writer.writeAttribute("checked", "checked", null);
        }

        writer.endElement("input");

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);

        writer.endElement("span");

        //label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeLayout(FacesContext context, DataView component) throws IOException {
        String layout = component.getLayout();

        if (component.getRowCount() == 0) {
            ResponseWriter writer = context.getResponseWriter();
            UIComponent emptyFacet = component.getFacet("emptyMessage");
            if (FacetUtils.shouldRenderFacet(emptyFacet)) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.writeText(component.getEmptyMessage(), "emptyMessage");
            }
        }
        else {
            if (layout.contains("grid")) {
                encodeGridLayout(context, component);
            }
            else {
                encodeListLayout(context, component);
            }
        }
    }

    protected void encodeGridLayout(FacesContext context, DataView component) throws IOException {
        DataViewGridItem grid = component.getGridItem();

        if (grid != null) {
            ResponseWriter writer = context.getResponseWriter();
            int columns = grid.getColumns();
            int rowIndex = component.getFirst();
            int rows = component.getRows();
            int itemsToRender = rows != 0 ? rows : component.getRowCount();
            int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
            boolean flex = ComponentUtils.isFlex(context, component);

            String rowClass = getStyleClassBuilder(context)
                    .add(DataView.GRID_LAYOUT_ROW_CLASS)
                    .add(GridLayoutUtils.getFlexGridClass(flex))
                    .build();

            String columnClass = getStyleClassBuilder(context)
                    .add(DataView.GRID_LAYOUT_COLUMN_CLASS)
                    .add(GridLayoutUtils.getColumnClass(flex, columns))
                    .add(component.getGridRowStyleClass())
                    .build();

            String columnInlineStyle = component.getGridRowStyle();

            writer.startElement("div", null);
            writer.writeAttribute("class", rowClass, null);
            writer.writeAttribute("title", component.getRowTitle(), null);

            int renderedCount = 0;
            for (int i = 0; i < numberOfRowsToRender; i++) {
                component.setRowIndex(rowIndex);
                if (!component.isRowAvailable()) {
                    break;
                }

                for (int j = 0; j < columns; j++) {
                    if (renderedCount >= itemsToRender) {
                        break;
                    }
                    renderedCount++;

                    writer.startElement("div", null);
                    writer.writeAttribute("class", columnClass, null);
                    if (!LangUtils.isEmpty(columnInlineStyle)) {
                        writer.writeAttribute("style", columnInlineStyle, null);
                    }

                    component.setRowIndex(rowIndex);
                    if (component.isRowAvailable()) {
                        renderChildren(context, grid);
                    }
                    rowIndex++;

                    writer.endElement("div");
                }
            }

            writer.endElement("div");

            component.setRowIndex(-1); //cleanup
        }
    }

    protected void encodeListLayout(FacesContext context, DataView component) throws IOException {
        DataViewListItem list = component.getListItem();

        if (list != null) {
            ResponseWriter writer = context.getResponseWriter();

            int first = component.getFirst();
            int rows = component.getRows() == 0 ? component.getRowCount() : component.getRows();
            int pageSize = first + rows;

            writer.startElement("ul", null);
            writer.writeAttribute("class", DataView.LIST_LAYOUT_CONTAINER_CLASS, null);

            for (int i = first; i < pageSize; i++) {
                component.setRowIndex(i);

                if (!component.isRowAvailable()) {
                    break;
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", DataView.ROW_CLASS, null);

                component.setRowIndex(i);
                if (component.isRowAvailable()) {
                    renderChildren(context, list);
                }

                writer.endElement("li");
            }

            writer.endElement("ul");

            //cleanup
            component.setRowIndex(-1);
        }
    }

    protected void encodeScript(FacesContext context, DataView component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataView", component);

        if (component.isPaginator()) {
            encodePaginatorConfig(context, component, wb);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, DataView component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private void saveMultiViewState(DataView component) {
        if (component.isMultiViewState()) {
            DataViewState viewState = component.getMultiViewState(true);
            viewState.setFirst(component.getFirst());
            viewState.setRows(component.getRows());
            viewState.setLayout(component.getLayout());
        }
    }
}
