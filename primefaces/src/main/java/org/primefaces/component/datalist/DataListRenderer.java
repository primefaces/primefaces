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
package org.primefaces.component.datalist;

import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = DataList.DEFAULT_RENDERER, componentFamily = DataList.COMPONENT_FAMILY)
public class DataListRenderer extends DataRenderer<DataList> {

    private static final Logger LOGGER = Logger.getLogger(DataListRenderer.class.getName());

    @Override
    public void decode(FacesContext context, DataList component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, DataList component) throws IOException {
        if (component.isPaginationRequest(context)) {
            component.updatePaginationData(context);

            if (component.isLazy()) {
                component.loadLazyData();
            }

            if (component.getType().equals("none")) {
                encodeFreeList(context, component);
            }
            else {
                encodeStrictList(context, component);
            }

            if (component.isMultiViewState()) {
                DataListState ls = component.getMultiViewState(true);
                ls.setFirst(component.getFirst());
                ls.setRows(component.getRows());
            }
        }
        else {
            if (component.isMultiViewState()) {
                component.restoreMultiViewState();
            }

            encodeMarkup(context, component);
            encodeScript(context, component);

            if (component.isPaginator() && component.getRows() == 0) {
                LOGGER.log(Level.WARNING, "DataList with paginator=true should also set the rows attribute. ClientId: {0}", component.getClientId());
            }
        }
    }

    protected void encodeMarkup(FacesContext context, DataList component) throws IOException {
        if (component.isLazy()) {
            component.loadLazyData();
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        boolean hasPaginator = component.isPaginator();
        boolean empty = (component.getRowCount() == 0);
        String paginatorPosition = component.getPaginatorPosition();
        String styleClass = getStyleClassBuilder(context).add(DataList.DATALIST_CLASS).add(component.getStyleClass()).build();
        String style = component.getStyle();

        if (hasPaginator) {
            component.calculateFirst();
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeFacet(context, component, "header", DataList.HEADER_CLASS);

        if (hasPaginator && !"bottom".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "top");
        }

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", DataList.CONTENT_CLASS, "styleClass");

        if (empty) {
            writer.startElement("div", component);
            writer.writeAttribute("class", DataList.DATALIST_EMPTY_MESSAGE_CLASS, null);
            writer.writeText(component.getEmptyMessage(), "emptyMessage");
            writer.endElement("div");
        }
        else {
            if (component.getType().equals("none")) {
                encodeFreeList(context, component);
            }
            else {
                encodeStrictList(context, component);
            }
        }

        writer.endElement("div");

        if (hasPaginator && !"top".equalsIgnoreCase(paginatorPosition)) {
            encodePaginatorMarkup(context, component, "bottom");
        }

        encodeFacet(context, component, "footer", DataList.FOOTER_CLASS);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, DataList component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataList", component);

        if (component.isPaginator()) {
            encodePaginatorConfig(context, component, wb);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    /**
     * Renders items with no strict markup
     *
     * @param context FacesContext instance
     * @param list    DataList component
     * @throws IOException if an input/ output error occurs
     */
    protected void encodeStrictList(FacesContext context, DataList list) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = list.getClientId(context);
        boolean isDefinition = list.isDefinition();
        UIComponent definitionFacet = list.getDescriptionFacet();
        boolean renderDefinition = isDefinition && FacetUtils.shouldRenderFacet(definitionFacet);
        String itemType = list.getItemType();
        String listClass = DataList.LIST_CLASS;
        if ("none".equals(itemType)) {
            listClass = listClass + " " + DataList.NO_BULLETS_CLASS;
        }

        String listTag = list.getListTag();
        String listItemTag = isDefinition ? "dt" : "li";

        writer.startElement(listTag, null);
        writer.writeAttribute("id", clientId + "_list", null);
        writer.writeAttribute("class", listClass, null);
        if (list.getItemType() != null) {
            writer.writeAttribute("type", list.getItemType(), null);
        }

        list.forEachRow((status) -> {
            try {
                String itemStyleClass = getStyleClassBuilder(context).add(DataList.LIST_ITEM_CLASS, list.getItemStyleClass()).build();

                writer.startElement(listItemTag, null);
                writer.writeAttribute("class", itemStyleClass, null);
                renderChildren(context, list);
                writer.endElement(listItemTag);

                if (renderDefinition) {
                    writer.startElement("dd", null);
                    definitionFacet.encodeAll(context);
                    writer.endElement("dd");
                }
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        });

        writer.endElement(listTag);
    }

    /**
     * Renders items with no strict markup
     *
     * @param context FacesContext instance
     * @param component    DataList component
     * @throws IOException if an input/ output error occurs
     */
    protected void encodeFreeList(FacesContext context, DataList component) throws IOException {
        component.forEachRow((status) -> {
            try {
                renderChildren(context, component);
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        });
    }

    @Override
    public void encodeChildren(FacesContext context, DataList component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
