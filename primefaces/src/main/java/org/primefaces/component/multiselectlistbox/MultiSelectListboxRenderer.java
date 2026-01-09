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
package org.primefaces.component.multiselectlistbox;

import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;

@FacesRenderer(rendererType = MultiSelectListbox.DEFAULT_RENDERER, componentFamily = MultiSelectListbox.COMPONENT_FAMILY)
public class MultiSelectListboxRenderer extends SelectOneRenderer<MultiSelectListbox> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Listbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, MultiSelectListbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, MultiSelectListbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);
        String style = component.getStyle();
        String styleClass = createStyleClass(component, MultiSelectListbox.CONTAINER_CLASS);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, component);
        encodeLists(context, component, selectItems);

        writer.endElement("div");
    }

    protected void encodeLists(FacesContext context, MultiSelectListbox component, List<SelectItem> itemList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectItem[] items = (itemList == null) ? null : itemList.toArray(new SelectItem[itemList.size()]);
        String header = component.getHeader();
        String listStyleClass = MultiSelectListbox.LIST_CLASS;

        writer.startElement("div", component);
        writer.writeAttribute("class", MultiSelectListbox.LIST_CONTAINER_CLASS, null);

        if (header != null) {
            writer.startElement("div", component);
            writer.writeAttribute("class", MultiSelectListbox.LIST_HEADER_CLASS, null);
            writer.writeText(header, null);
            writer.endElement("div");
        }

        writer.startElement("ul", component);
        writer.writeAttribute("class", listStyleClass, null);
        renderARIARequired(context, component);

        if (items != null) {
            encodeListItems(context, component, items);
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeListItems(FacesContext context, MultiSelectListbox component, SelectItem[] selectItems) throws IOException {
        if (selectItems != null && selectItems.length > 0) {
            ResponseWriter writer = context.getResponseWriter();
            Converter converter = ComponentUtils.getConverter(context, component);
            String itemValue = null;

            for (int i = 0; i < selectItems.length; i++) {
                SelectItem selectItem = selectItems[i];
                itemValue = converter != null ? converter.getAsString(context, component, selectItem.getValue()) : String.valueOf(selectItem.getValue());
                writer.startElement("li", null);
                writer.writeAttribute("class", MultiSelectListbox.ITEM_CLASS, null);
                writer.writeAttribute("data-value", itemValue, null);

                writer.startElement("span", component);
                writer.writeText(selectItem.getLabel(), null);
                writer.endElement("span");

                if (selectItem instanceof SelectItemGroup) {
                    SelectItemGroup group = (SelectItemGroup) selectItem;
                    SelectItem[] groupItems = group.getSelectItems();

                    if (groupItems != null && groupItems.length > 0) {
                        encodeGroupItems(context, component, group.getSelectItems());
                    }
                }

                writer.endElement("li");
            }
        }
    }

    protected void encodeGroupItems(FacesContext context, MultiSelectListbox component, SelectItem[] selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", component);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        encodeListItems(context, component, selectItems);
        writer.endElement("ul");
    }

    protected void encodeScript(FacesContext context, MultiSelectListbox component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("MultiSelectListbox", component)
                .attr("effect", component.getEffect(), null)
                .attr("showHeaders", component.isShowHeaders(), false);
        encodeClientBehaviors(context, component);
        wb.finish();
    }

    protected void encodeInput(FacesContext context, MultiSelectListbox component) throws IOException {
        String inputId = component.getClientId(context) + "_input";
        String valueToRender = ComponentUtils.getValueToRender(context, component);

        renderHiddenInput(context, inputId, valueToRender, component.isDisabled());
    }

    @Override
    protected String getSubmitParam(FacesContext context, MultiSelectListbox component) {
        return component.getClientId(context) + "_input";
    }
}
