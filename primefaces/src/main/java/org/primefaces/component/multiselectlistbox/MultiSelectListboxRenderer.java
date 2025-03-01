/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.render.Renderer;

public class MultiSelectListboxRenderer extends SelectOneRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Listbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        MultiSelectListbox listbox = (MultiSelectListbox) component;

        encodeMarkup(context, listbox);
        encodeScript(context, listbox);
    }

    protected void encodeMarkup(FacesContext context, MultiSelectListbox listbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = listbox.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, listbox);
        String style = listbox.getStyle();
        String styleClass = createStyleClass(listbox, MultiSelectListbox.CONTAINER_CLASS);

        writer.startElement("div", listbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeInput(context, listbox);
        encodeLists(context, listbox, selectItems);

        writer.endElement("div");
    }

    protected void encodeLists(FacesContext context, MultiSelectListbox listbox, List<SelectItem> itemList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        SelectItem[] items = (itemList == null) ? null : itemList.toArray(new SelectItem[itemList.size()]);
        String header = listbox.getHeader();
        String listStyleClass = MultiSelectListbox.LIST_CLASS;

        writer.startElement("div", listbox);
        writer.writeAttribute("class", MultiSelectListbox.LIST_CONTAINER_CLASS, null);

        if (header != null) {
            writer.startElement("div", listbox);
            writer.writeAttribute("class", MultiSelectListbox.LIST_HEADER_CLASS, null);
            writer.writeText(header, null);
            writer.endElement("div");
        }

        writer.startElement("ul", listbox);
        writer.writeAttribute("class", listStyleClass, null);
        renderARIARequired(context, listbox);

        if (items != null) {
            encodeListItems(context, listbox, items);
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeListItems(FacesContext context, MultiSelectListbox listbox, SelectItem[] selectItems) throws IOException {
        if (selectItems != null && selectItems.length > 0) {
            ResponseWriter writer = context.getResponseWriter();
            Converter converter = ComponentUtils.getConverter(context, listbox);
            String itemValue = null;

            for (int i = 0; i < selectItems.length; i++) {
                SelectItem selectItem = selectItems[i];
                itemValue = converter != null ? converter.getAsString(context, listbox, selectItem.getValue()) : String.valueOf(selectItem.getValue());
                writer.startElement("li", null);
                writer.writeAttribute("class", MultiSelectListbox.ITEM_CLASS, null);
                writer.writeAttribute("data-value", itemValue, null);

                writer.startElement("span", listbox);
                writer.writeText(selectItem.getLabel(), null);
                writer.endElement("span");

                if (selectItem instanceof SelectItemGroup) {
                    SelectItemGroup group = (SelectItemGroup) selectItem;
                    SelectItem[] groupItems = group.getSelectItems();

                    if (groupItems != null && groupItems.length > 0) {
                        encodeGroupItems(context, listbox, group.getSelectItems());
                    }
                }

                writer.endElement("li");
            }
        }
    }

    protected void encodeGroupItems(FacesContext context, MultiSelectListbox listbox, SelectItem[] selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("ul", listbox);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        encodeListItems(context, listbox, selectItems);
        writer.endElement("ul");
    }

    protected void encodeScript(FacesContext context, MultiSelectListbox listbox) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("MultiSelectListbox", listbox)
                .attr("effect", listbox.getEffect(), null)
                .attr("showHeaders", listbox.isShowHeaders(), false);
        encodeClientBehaviors(context, listbox);
        wb.finish();
    }

    protected void encodeInput(FacesContext context, MultiSelectListbox listbox) throws IOException {
        String inputId = listbox.getClientId(context) + "_input";
        String valueToRender = ComponentUtils.getValueToRender(context, listbox);

        renderHiddenInput(context, inputId, valueToRender, listbox.isDisabled());
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }
}
