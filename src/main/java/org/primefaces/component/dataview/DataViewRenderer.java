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
package org.primefaces.component.dataview;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class DataViewRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataView dataview = (DataView) component;
        String clientId = dataview.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        dataview.findViewItems();

        if (dataview.isPaginationRequest(context)) {
            dataview.updatePaginationData(context, dataview);

            encodeLayout(context, dataview);
        }
        else if (dataview.isLayoutRequest(context)) {
            String layout = params.get(clientId + "_layout");
            dataview.setLayout(layout);

            encodeLayout(context, dataview);
        }
        else {
            encodeMarkup(context, dataview);
            encodeScript(context, dataview);
        }
    }

    protected void encodeMarkup(FacesContext context, DataView dataview) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dataview.getClientId(context);
        String layout = dataview.getLayout();
        boolean hasPaginator = dataview.isPaginator();
        String paginatorPosition = dataview.getPaginatorPosition();
        String style = dataview.getStyle();
        String styleClass = dataview.getStyleClass();
        styleClass = (styleClass == null) ? DataView.DATAVIEW_CLASS : DataView.DATAVIEW_CLASS + " " + styleClass;
        styleClass += " " + (layout.contains("grid") ? DataView.GRID_LAYOUT_CLASS : DataView.LIST_LAYOUT_CLASS);

        if (hasPaginator) {
            dataview.calculateFirst();
        }

        writer.startElement("div", dataview);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeHeader(context, dataview);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, dataview, "top");
        }

        encodeContent(context, dataview);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, dataview, "bottom");
        }

        encodeFacet(context, dataview, "footer", DataView.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeHeader(FacesContext context, DataView dataview) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent fHeader = dataview.getFacet("header");

        writer.startElement("div", dataview);
        writer.writeAttribute("class", DataView.HEADER_CLASS, null);

        if (fHeader != null && fHeader.isRendered()) {
            fHeader.encodeAll(context);
        }

        encodeLayoutOptions(context, dataview);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, DataView dataview) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dataview.getClientId(context);
        writer.startElement("div", dataview);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", DataView.CONTENT_CLASS, null);

        encodeLayout(context, dataview);

        writer.endElement("div");
    }

    protected void encodeLayoutOptions(FacesContext context, DataView dataview) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean hasGridItem = (dataview.getGridItem() != null);
        boolean hasListItem = (dataview.getListItem() != null);
        String layout = dataview.getLayout();
        boolean isGridLayout = layout.contains("grid");
        String containerClass = DataView.BUTTON_CONTAINER_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        if (hasListItem) {
            String listIcon = dataview.getListIcon() != null ? dataview.getListIcon() : "ui-icon-grip-dotted-horizontal";

            encodeButton(context, dataview, "list", listIcon, !isGridLayout);
        }

        if (hasGridItem) {
            String gridIcon = dataview.getGridIcon() != null ? dataview.getGridIcon() : "ui-icon-grip-dotted-vertical";

            encodeButton(context, dataview, "grid", gridIcon, isGridLayout);
        }

        writer.endElement("div");

    }

    protected void encodeButton(FacesContext context, DataView dataview, String layout, String icon, boolean isActive) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dataview.getClientId(context);
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

    protected void encodeLayout(FacesContext context, DataView dataview) throws IOException {
        String layout = dataview.getLayout();

        if (layout.contains("grid")) {
            encodeGridLayout(context, dataview);
        }
        else {
            encodeListLayout(context, dataview);
        }
    }

    protected void encodeGridLayout(FacesContext context, DataView dataview) throws IOException {
        DataViewGridItem grid = dataview.getGridItem();

        if (grid != null) {
            ResponseWriter writer = context.getResponseWriter();

            int columns = grid.getColumns();
            int rowIndex = dataview.getFirst();
            int rows = dataview.getRows();
            int itemsToRender = rows != 0 ? rows : dataview.getRowCount();
            int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
            String columnClass = DataView.GRID_LAYOUT_COLUMN_CLASS + " " + GridLayoutUtils.getColumnClass(columns);

            for (int i = 0; i < numberOfRowsToRender; i++) {
                dataview.setRowIndex(rowIndex);
                if (!dataview.isRowAvailable()) {
                    break;
                }

                writer.startElement("div", null);
                writer.writeAttribute("class", DataView.GRID_LAYOUT_ROW_CLASS, null);

                for (int j = 0; j < columns; j++) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", columnClass, null);

                    dataview.setRowIndex(rowIndex);
                    if (dataview.isRowAvailable()) {
                        renderChildren(context, grid);
                    }
                    rowIndex++;

                    writer.endElement("div");
                }

                writer.endElement("div");
            }

            dataview.setRowIndex(-1); //cleanup
        }
    }

    protected void encodeListLayout(FacesContext context, DataView dataview) throws IOException {
        DataViewListItem list = dataview.getListItem();

        if (list != null) {
            ResponseWriter writer = context.getResponseWriter();

            int first = dataview.getFirst();
            int rows = dataview.getRows() == 0 ? dataview.getRowCount() : dataview.getRows();
            int pageSize = first + rows;

            writer.startElement("ul", null);
            writer.writeAttribute("class", DataView.LIST_LAYOUT_CONTAINER_CLASS, null);

            for (int i = first; i < pageSize; i++) {
                dataview.setRowIndex(i);

                if (!dataview.isRowAvailable()) {
                    return;
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", DataView.ROW_CLASS, null);

                dataview.setRowIndex(i);
                if (dataview.isRowAvailable()) {
                    renderChildren(context, list);
                }

                writer.endElement("li");
            }

            writer.endElement("ul");

            //cleanup
            dataview.setRowIndex(-1);
        }
    }

    protected void encodeScript(FacesContext context, DataView dataview) throws IOException {
        String clientId = dataview.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataView", dataview.resolveWidgetVar(), clientId);

        if (dataview.isPaginator()) {
            encodePaginatorConfig(context, dataview, wb);
        }

        encodeClientBehaviors(context, dataview);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
