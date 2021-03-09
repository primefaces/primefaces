/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.cascadeselect;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.Renderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;

import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class CascadeSelectRenderer extends SelectOneRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "javax.faces.SelectOne",
                "javax.faces.Listbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        CascadeSelect cascadeSelect = (CascadeSelect) component;

        encodeMarkup(context, cascadeSelect);
        encodeScript(context, cascadeSelect);
    }

    protected void encodeMarkup(FacesContext context, CascadeSelect cascadeSelect) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = cascadeSelect.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, cascadeSelect);

        String style = cascadeSelect.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(CascadeSelect.STYLE_CLASS)
                .add(cascadeSelect.isDisabled(), "ui-state-disabled")
                .add(cascadeSelect.getStyleClass())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        renderARIACombobox(context, cascadeSelect);

        String valueToRender = ComponentUtils.getValueToRender(context, cascadeSelect);
        encodeInput(context, cascadeSelect, valueToRender);
        encodeLabel(context, cascadeSelect, selectItems, valueToRender);
        encodeTrigger(context);
        encodePanel(context, cascadeSelect, selectItems);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, CascadeSelect cascadeSelect, String valueToRender) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = cascadeSelect.getInputClientId();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("tabindex", cascadeSelect.getTabindex(), null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }
        renderAccessibilityAttributes(context, cascadeSelect);
        renderDomEvents(context, cascadeSelect, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");
        writer.endElement("div");
    }

    protected void encodeLabel(FacesContext context, CascadeSelect cascadeSelect, List<SelectItem> itemList, String valueToRender) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Converter converter = ComponentUtils.getConverter(context, cascadeSelect);
        String itemLabel = valueToRender;
        SelectItem foundItem = findItemByValue(context, cascadeSelect, converter, itemList, valueToRender);
        if (foundItem != null) {
            itemLabel = foundItem.getLabel();
        }

        String placeholder = LangUtils.isNotBlank(itemLabel) ? itemLabel : cascadeSelect.getPlaceholder();
        String styleClass = getStyleClassBuilder(context)
                .add(placeholder != null, CascadeSelect.LABEL_CLASS)
                .add(placeholder == null, CascadeSelect.LABEL_EMPTY_CLASS)
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        if (LangUtils.isNotBlank(placeholder)) {
            writer.writeText(placeholder, null);
        }
        writer.endElement("span");
    }

    protected void encodeTrigger(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        writer.writeAttribute("class", CascadeSelect.TRIGGER_CLASS, null);
        writer.writeAttribute(HTML.ARIA_ROLE, "button", null);
        writer.writeAttribute(HTML.ARIA_HASPOPUP, "listbox", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", CascadeSelect.TRIGGER_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, CascadeSelect cascadeSelect, List<SelectItem> itemList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelId = cascadeSelect.getPanelClientId();
        SelectItem[] items = (itemList == null) ? null : itemList.toArray(new SelectItem[itemList.size()]);

        writer.startElement("div", null);
        writer.writeAttribute("id", panelId, null);
        writer.writeAttribute("class", CascadeSelect.PANEL_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", CascadeSelect.ITEMS_WRAPPER_CLASS, null);

        encodeList(context, cascadeSelect, items, false);

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, CascadeSelect cascadeSelect, SelectItem[] items, boolean isSublist) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = isSublist ? CascadeSelect.PANEL_ITEMS_CLASS + " " + CascadeSelect.SUBLIST_CLASS : CascadeSelect.PANEL_ITEMS_CLASS;

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "listbox", null);
        writer.writeAttribute("aria-orientation", "horizontal", null);
        renderARIARequired(context, cascadeSelect);

        encodeListItems(context, cascadeSelect, items);

        writer.endElement("ul");
    }

    protected void encodeListItems(FacesContext context, CascadeSelect cascadeSelect, SelectItem[] selectItems) throws IOException {
        if (selectItems != null && selectItems.length > 0) {
            ResponseWriter writer = context.getResponseWriter();
            UIComponent contentFacet = cascadeSelect.getFacet("content");
            Converter converter = ComponentUtils.getConverter(context, cascadeSelect);
            String var = cascadeSelect.getVar();

            for (SelectItem selectItem : selectItems) {
                boolean isGroup = selectItem instanceof SelectItemGroup;
                Object itemValue = selectItem.getValue();
                String itemLabel = selectItem.getLabel();
                String itemValueAsString = getOptionAsString(context, cascadeSelect, converter, selectItem.getValue());
                String itemStyleClass = getStyleClassBuilder(context)
                        .add(CascadeSelect.ITEM_CLASS)
                        .add(isGroup, CascadeSelect.ITEM_GROUP_CLASS)
                        .build();

                if (var != null) {
                    context.getExternalContext().getRequestMap().put(var, itemValue);
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", itemStyleClass, null);
                writer.writeAttribute("data-value", itemValueAsString, null);
                writer.writeAttribute("data-label", itemLabel, null);

                writer.startElement("div", null);
                writer.writeAttribute("class", CascadeSelect.ITEM_CONTENT_CLASS, null);
                writer.writeAttribute("tabindex", "0", null);

                if (ComponentUtils.shouldRenderFacet(contentFacet)) {
                    contentFacet.encodeAll(context);
                }
                else {
                    writer.startElement("span", cascadeSelect);
                    writer.writeAttribute("class", CascadeSelect.ITEM_TEXT_CLASS, null);
                    writer.writeText(itemLabel, null);
                    writer.endElement("span");
                }

                if (isGroup) {
                    writer.startElement("span", cascadeSelect);
                    writer.writeAttribute("class", CascadeSelect.GROUP_ICON_CLASS, null);
                    writer.endElement("span");
                }

                writer.endElement("div");

                if (isGroup) {
                    SelectItemGroup group = (SelectItemGroup) selectItem;
                    SelectItem[] groupItems = group.getSelectItems();

                    if (groupItems != null && groupItems.length > 0) {
                        encodeList(context, cascadeSelect, group.getSelectItems(), true);
                    }
                }

                writer.endElement("li");
            }

            if (var != null) {
                context.getExternalContext().getRequestMap().put(var, null);
            }
        }
    }

    /**
     * Recursive method used to find a SelectItem by its value.
     * @param context FacesContext
     * @param the current UI component to find value for
     * @param converter the converter for the select items
     * @param selectItems the List of SelectItems
     * @param value the input value to search for
     * @return either the SelectItem found or NULL if not found
     */
    protected SelectItem findItemByValue(FacesContext context, UIComponent component, Converter converter, List<SelectItem> selectItems, String value) {
        SelectItem foundValue = null;
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            if (item instanceof SelectItemGroup) {
                SelectItemGroup selectItemGroup = (SelectItemGroup) item;
                if (selectItemGroup.getSelectItems() == null) {
                    continue;
                }
                foundValue = findItemByValue(context, component,  converter, Arrays.asList(selectItemGroup.getSelectItems()), value);
                if (foundValue != null) {
                    break;
                }
            }
            else {
                String itemValueAsString = getOptionAsString(context, component, converter, item.getValue());
                if (Objects.equals(value, itemValueAsString)) {
                    foundValue = item;
                    break;
                }
            }
        }

        return foundValue;
    }

    protected void encodeScript(FacesContext context, CascadeSelect cascadeSelect) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("CascadeSelect", cascadeSelect)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, cascadeSelect, cascadeSelect.getAppendTo(),
                        SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE), null);

        encodeClientBehaviors(context, cascadeSelect);
        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context) + "_input";
    }
}
