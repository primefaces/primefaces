/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.column.Column;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class OrderListRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        OrderList pickList = (OrderList) component;
        Map<String, String[]> params = context.getExternalContext().getRequestParameterValuesMap();
        String values = pickList.getClientId(context) + "_values";

        if (values != null) {
            pickList.setSubmittedValue(params.get(values));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OrderList ol = (OrderList) component;

        encodeMarkup(context, ol);
        encodeScript(context, ol);
    }

    protected void encodeMarkup(FacesContext context, OrderList ol) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ol.getClientId(context);
        String controlsLocation = ol.getControlsLocation();
        String style = ol.getStyle();
        String styleClass = ol.getStyleClass();
        styleClass = styleClass == null ? OrderList.CONTAINER_CLASS : OrderList.CONTAINER_CLASS + " " + styleClass;

        if (ol.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }

        if (ol.isResponsive()) {
            styleClass = styleClass + " ui-grid-responsive";
        }

        writer.startElement("div", ol);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-g", null);

        if (controlsLocation.equals("left")) {
            encodeControls(context, ol);
        }

        encodeList(context, ol);

        if (controlsLocation.equals("right")) {
            encodeControls(context, ol);
        }

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, OrderList ol) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ol.getClientId(context);
        UIComponent caption = ol.getFacet("caption");
        String listStyleClass = OrderList.LIST_CLASS;
        String columnGridClass = ol.getControlsLocation().equals("none") ? "ui-g-12 ui-md-12" : "ui-g-12 ui-md-10";

        writer.startElement("div", null);
        writer.writeAttribute("class", columnGridClass, null);

        if (caption != null) {
            encodeCaption(context, caption);
            listStyleClass += " ui-corner-bottom";
        }
        else {
            listStyleClass += " ui-corner-all";
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", listStyleClass, null);

        encodeOptions(context, ol, (List) ol.getValue());

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

    protected void encodeControls(FacesContext context, OrderList ol) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", OrderList.CONTROLS_CLASS, null);
        encodeButton(context, ol.getMoveUpLabel(), OrderList.MOVE_UP_BUTTON_CLASS, OrderList.MOVE_UP_BUTTON_ICON_CLASS);
        encodeButton(context, ol.getMoveTopLabel(), OrderList.MOVE_TOP_BUTTON_CLASS, OrderList.MOVE_TOP_BUTTON_ICON_CLASS);
        encodeButton(context, ol.getMoveDownLabel(), OrderList.MOVE_DOWN_BUTTON_CLASS, OrderList.MOVE_DOWN_BUTTON_ICON_CLASS);
        encodeButton(context, ol.getMoveBottomLabel(), OrderList.MOVE_BOTTOM_BUTTON_CLASS, OrderList.MOVE_BOTTOM_BUTTON_ICON_CLASS);
        writer.endElement("div");
    }

    @SuppressWarnings("unchecked")
    protected void encodeOptions(FacesContext context, OrderList old, List model) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = old.getVar();
        Converter converter = old.getConverter();

        for (Object item : model) {
            context.getExternalContext().getRequestMap().put(var, item);
            String value = converter != null ? converter.getAsString(context, old, old.getItemValue()) : old.getItemValue().toString();

            writer.startElement("li", null);
            writer.writeAttribute("class", OrderList.ITEM_CLASS, null);
            writer.writeAttribute("data-item-value", value, null);

            if (old.getChildCount() > 0) {

                writer.startElement("table", null);
                writer.startElement("tbody", null);
                writer.startElement("tr", null);

                for (UIComponent kid : old.getChildren()) {
                    if (kid instanceof Column && kid.isRendered()) {
                        Column column = (Column) kid;

                        writer.startElement("td", null);
                        if (column.getStyle() != null) {
                            writer.writeAttribute("style", column.getStyle(), null);
                        }
                        if (column.getStyleClass() != null) {
                            writer.writeAttribute("class", column.getStyleClass(), null);
                        }

                        renderChildren(context, column);
                        writer.endElement("td");
                    }
                }

                writer.endElement("tr");
                writer.endElement("tbody");
                writer.endElement("table");
            }
            else {
                writer.writeText(old.getItemLabel(), null);
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
        String clientId = ol.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OrderList", ol.resolveWidgetVar(context), clientId)
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
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
