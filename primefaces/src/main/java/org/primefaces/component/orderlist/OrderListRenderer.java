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
package org.primefaces.component.orderlist;

import org.primefaces.component.column.Column;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.GridLayoutUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = OrderList.DEFAULT_RENDERER, componentFamily = OrderList.COMPONENT_FAMILY)
public class OrderListRenderer extends CoreRenderer<OrderList> {

    @Override
    public void decode(FacesContext context, OrderList component) {
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();

        String[] values = params.get(component.getClientId(context) + "_values");
        component.setSubmittedValue(values);

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, OrderList component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, OrderList component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String controlsLocation = component.getControlsLocation();
        String style = component.getStyle();
        boolean flex = ComponentUtils.isFlex(context, component);

        //style class
        String containerClass = getStyleClassBuilder(context)
                .add(OrderList.CONTAINER_CLASS)
                .add(flex, GridLayoutUtils.getResponsiveClass(flex))
                .add(component.isResponsive() && !flex, GridLayoutUtils.getResponsiveClass(false))
                .add(component.getStyleClass())
                .add(component.isDisabled(), "ui-state-disabled")
                .add("right".equals(component.getControlsLocation()), OrderList.CONTROLS_RIGHT_CLASS)
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", containerClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", GridLayoutUtils.getFlexGridClass(flex), null);

        if ("left".equals(controlsLocation)) {
            encodeControls(context, component, flex);
        }

        encodeList(context, component, flex);

        if ("right".equals(controlsLocation)) {
            encodeControls(context, component, flex);
        }

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, OrderList component, boolean flex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        UIComponent caption = component.getCaptionFacet();
        String listStyleClass = OrderList.LIST_CLASS;

        String columnGridClass;
        if ("none".equals(component.getControlsLocation())) {
            columnGridClass = GridLayoutUtils.getColumnClass(flex, 1);
        }
        else {
            columnGridClass = flex ? "col-12 md:col-10" : "ui-g-12 ui-md-10";
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", columnGridClass, null);

        if (FacetUtils.shouldRenderFacet(caption)) {
            encodeCaption(context, caption);
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", listStyleClass, null);

        encodeOptions(context, component, (List) component.getValue());

        writer.endElement("ul");

        encodeInput(context, clientId + "_values");

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("select", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("multiple", "true", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        //options generated at client side
        writer.endElement("select");
    }

    protected void encodeControls(FacesContext context, OrderList component, boolean flex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String css = OrderList.CONTROLS_CLASS + " " + GridLayoutUtils.getColumnClass(flex, 6);
        writer.startElement("div", null);
        writer.writeAttribute("class", css, null);
        encodeButton(context, component.getMoveUpLabel(), OrderList.MOVE_UP_BUTTON_CLASS, OrderList.MOVE_UP_BUTTON_ICON_CLASS);
        encodeButton(context, component.getMoveTopLabel(), OrderList.MOVE_TOP_BUTTON_CLASS, OrderList.MOVE_TOP_BUTTON_ICON_CLASS);
        encodeButton(context, component.getMoveDownLabel(), OrderList.MOVE_DOWN_BUTTON_CLASS, OrderList.MOVE_DOWN_BUTTON_ICON_CLASS);
        encodeButton(context, component.getMoveBottomLabel(), OrderList.MOVE_BOTTOM_BUTTON_CLASS, OrderList.MOVE_BOTTOM_BUTTON_ICON_CLASS);
        writer.endElement("div");
    }

    @SuppressWarnings("unchecked")
    protected void encodeOptions(FacesContext context, OrderList component, List model) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = component.getVar();
        Converter converter = component.getConverter();
        int index = 0;

        for (Object item : model) {
            context.getExternalContext().getRequestMap().put(var, item);
            String value = converter != null ? converter.getAsString(context, component, component.getItemValue()) : component.getItemValue().toString();

            writer.startElement("li", null);
            writer.writeAttribute("class", OrderList.ITEM_CLASS, null);
            writer.writeAttribute("data-item-value", value, null);

            if (component.getChildCount() > 0) {

                writer.startElement("table", null);
                writer.startElement("tbody", null);
                writer.startElement("tr", null);

                for (UIComponent kid : component.getChildren()) {
                    if (kid instanceof Column && kid.isRendered()) {
                        Column column = (Column) kid;

                        writer.startElement("td", null);
                        if (column.getStyle() != null) {
                            writer.writeAttribute("style", column.getStyle(), null);
                        }
                        if (column.getStyleClass() != null) {
                            writer.writeAttribute("class", column.getStyleClass(), null);
                        }

                        encodeIndexedId(context, column, index++);
                        writer.endElement("td");
                    }
                }

                writer.endElement("tr");
                writer.endElement("tbody");
                writer.endElement("table");
            }
            else {
                writer.writeText(component.getItemLabel(), null);
            }

            writer.endElement("li");
        }

        context.getExternalContext().getRequestMap().remove(var);
    }

    protected void encodeButton(FacesContext context, String title, String styleClass, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_ICON_ONLY_BUTTON_CLASS + " " + styleClass, null);
        writer.writeAttribute("title", title, null);

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");
        writer.endElement("span");

        writer.endElement("button");
    }

    protected void encodeScript(FacesContext context, OrderList ol) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OrderList", ol)
                .attr("effect", ol.getEffect(), null);

        encodeClientBehaviors(context, ol);

        wb.finish();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        try {
            OrderList ol = (OrderList) component;
            List orderedList = new ArrayList();
            Converter converter = ol.getConverter();
            String[] values = (String[]) submittedValue;

            for (String item : values) {
                if (isValueBlank(item)) {
                    continue;
                }

                Object convertedValue = converter != null ? converter.getAsObject(context, ol, item) : item;

                if (convertedValue != null) {
                    orderedList.add(convertedValue);
                }
            }

            return orderedList;
        }
        catch (Exception exception) {
            throw new ConverterException(exception);
        }
    }

    protected void encodeCaption(FacesContext context, UIComponent caption) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", OrderList.CAPTION_CLASS, null);
        caption.encodeAll(context);
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, OrderList component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
