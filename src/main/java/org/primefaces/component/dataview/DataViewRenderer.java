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
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class DataViewRenderer extends DataRenderer {

   
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataView view = (DataView) component;

        if (view.isPaginationRequest(context)) {
            view.updatePaginationData(context, view);

            if (view.isLazy()) {
                view.loadLazyData();
            }

            encodeContent(context, view);
        }
        else {
            encodeMarkup(context, view);
            encodeScript(context, view);
        }
    }

    protected void encodeScript(FacesContext context, DataView view) throws IOException {
        String clientId = view.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataView", view.resolveWidgetVar(), clientId);

        if (view.isPaginator()) {
            encodePaginatorConfig(context, view, wb);
        }

        encodeClientBehaviors(context, view);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, DataView view) throws IOException {
        if (view.isLazy()) {
            view.loadLazyData();
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = view.getClientId(context);
        boolean hasPaginator = view.isPaginator();
        boolean empty = view.getRowCount() == 0;
        String layout = view.getLayout();
        String paginatorPosition = view.getPaginatorPosition();
        String style = view.getStyle();
        String styleClass = view.getStyleClass() == null ? DataView.DATAVIEW_CLASS : DataView.DATAVIEW_CLASS + " " + view.getStyleClass();
        String contentClass = empty
                ? DataView.EMPTY_CONTENT_CLASS
                : (layout.equals("tabular") ? DataView.TABLE_CONTENT_CLASS : DataView.GRID_CONTENT_CLASS);

        if (hasPaginator) {
            view.calculateFirst();
        }

        writer.startElement("div", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

       // encodeFacet(context, view, "header", DataView.HEADER_CLASS);
        writer.startElement("div", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", DataView.HEADER_CLASS , "class");
        writer.writeAttribute("style", "height: 55px; width: 100%" , style);  
        

        // buttonList
        writer.startElement("button", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_ICON_ONLY_BUTTON_CLASS , "class");
        writer.writeAttribute("style", "height: 20px; width: 20px; margin: 18px 0px 0px 94%" , style);
        writer.endElement("button");
        
        // buttonGrid
        writer.startElement("button", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_ICON_ONLY_BUTTON_CLASS, "class");
        writer.writeAttribute("style", "height: 20px; width: 20px; margin: 0px 10px" , style);        
        writer.endElement("button");

        writer.endElement("div");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom")) {
            encodePaginatorMarkup(context, view, "top");
        }

        writer.startElement("div", view);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        if (empty) {
            UIComponent emptyFacet = view.getFacet("emptyMessage");
            if (emptyFacet != null) {
                emptyFacet.encodeAll(context);
            }
            else {
                writer.write(view.getEmptyMessage());
            }
        }
        else {
            encodeContent(context, view);
        }

        writer.endElement("div");

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top")) {
            encodePaginatorMarkup(context, view, "bottom");
        }

        encodeFacet(context, view, "footer", DataView.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, DataView view) throws IOException {
        String layout = view.getLayout();

        if (layout.equals("tabular")) {
            encodeTable(context, view);
        }
        else if (layout.equals("grid")) {
            encodeGrid(context, view);
        }
        else {
            throw new FacesException(layout + " is not a valid value for DataGrid layout. Possible values are 'tabular' and 'grid'.");
        }
    }

    protected void encodeGrid(FacesContext context, DataView view) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        int columns = view.getColumns();
        int rowIndex = view.getFirst();
        int rows = view.getRows();
        int itemsToRender = rows != 0 ? rows : view.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;
        String columnClass = DataView.COLUMN_CLASS + " " + GridLayoutUtils.getColumnClass(columns);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            view.setRowIndex(rowIndex);
            if (!view.isRowAvailable()) {
                break;
            }

            writer.startElement("div", null);
            writer.writeAttribute("class", DataView.GRID_ROW_CLASS, null);

            for (int j = 0; j < columns; j++) {
                writer.startElement("div", null);
                writer.writeAttribute("class", columnClass, null);

                view.setRowIndex(rowIndex);
                if (view.isRowAvailable()) {
                    renderChildren(context, view);
                }
                rowIndex++;

                writer.endElement("div");
            }

            writer.endElement("div");
        }

        view.setRowIndex(-1); //cleanup
    }

    protected void encodeTable(FacesContext context, DataView view) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        int columns = view.getColumns();
        int rowIndex = view.getFirst();
        int rows = view.getRows();
        int itemsToRender = rows != 0 ? rows : view.getRowCount();
        int numberOfRowsToRender = (itemsToRender + columns - 1) / columns;

        writer.startElement("table", view);
        writer.writeAttribute("class", DataView.TABLE_CLASS, null);
        writer.startElement("tbody", null);

        for (int i = 0; i < numberOfRowsToRender; i++) {
            view.setRowIndex(rowIndex);
            if (!view.isRowAvailable()) {
                break;
            }

            writer.startElement("tr", null);
            writer.writeAttribute("class", DataView.TABLE_ROW_CLASS, null);

            for (int j = 0; j < columns; j++) {
                writer.startElement("td", null);
                writer.writeAttribute("class", DataView.COLUMN_CLASS, null);

                view.setRowIndex(rowIndex);
                if (view.isRowAvailable()) {
                    renderChildren(context, view);
                }
                rowIndex++;

                writer.endElement("td");
            }
            writer.endElement("tr");
        }

        view.setRowIndex(-1); //cleanup

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
