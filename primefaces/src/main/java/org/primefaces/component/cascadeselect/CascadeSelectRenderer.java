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
package org.primefaces.component.cascadeselect;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
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

@FacesRenderer(rendererType = CascadeSelect.DEFAULT_RENDERER, componentFamily = CascadeSelect.COMPONENT_FAMILY)
public class CascadeSelectRenderer extends SelectOneRenderer<CascadeSelect> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Listbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, CascadeSelect component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, CascadeSelect component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);

        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(CascadeSelect.STYLE_CLASS)
                .add(component.isDisabled(), "ui-state-disabled")
                .add(component.isReadonly(), "ui-state-readonly")
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        renderARIACombobox(context, component);

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        encodeInput(context, component, valueToRender);
        encodeLabel(context, component, selectItems, valueToRender);
        encodeTrigger(context);
        encodePanel(context, component, selectItems);

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, CascadeSelect component, String valueToRender) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getInputClientId();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("tabindex", component.getTabindex(), null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }
        renderAccessibilityAttributes(context, component);
        renderDomEvents(context, component, HTML.BLUR_FOCUS_EVENTS);

        writer.endElement("input");
        writer.endElement("div");
    }

    protected void encodeLabel(FacesContext context, CascadeSelect component, List<SelectItem> itemList, String valueToRender) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        Converter<?> converter = ComponentUtils.getConverter(context, component);
        String itemLabel = valueToRender;
        SelectItem foundItem = findSelectItemByValue(context, component, converter, itemList, valueToRender);
        if (foundItem != null) {
            itemLabel = foundItem.getLabel();
        }

        String placeholder = LangUtils.isNotBlank(itemLabel) ? itemLabel : component.getPlaceholder();
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
        writer.writeAttribute(HTML.ARIA_HASPOPUP, HTML.ARIA_ROLE_LISTBOX, null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", CascadeSelect.TRIGGER_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, CascadeSelect component, List<SelectItem> itemList) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String panelId = component.getPanelClientId();
        SelectItem[] items = (itemList == null) ? null : itemList.toArray(new SelectItem[itemList.size()]);

        writer.startElement("div", null);
        writer.writeAttribute("id", panelId, null);
        writer.writeAttribute("class", CascadeSelect.PANEL_CLASS, null);
        writer.startElement("div", null);
        writer.writeAttribute("class", CascadeSelect.ITEMS_WRAPPER_CLASS, null);

        encodeList(context, component, items, false);

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, CascadeSelect component, SelectItem[] items, boolean isSublist) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = isSublist ? CascadeSelect.PANEL_ITEMS_CLASS + " " + CascadeSelect.SUBLIST_CLASS : CascadeSelect.PANEL_ITEMS_CLASS;

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
        writer.writeAttribute("aria-orientation", "horizontal", null);
        renderARIARequired(context, component);

        encodeListItems(context, component, items);

        writer.endElement("ul");
    }

    protected void encodeListItems(FacesContext context, CascadeSelect component, SelectItem[] selectItems) throws IOException {
        if (selectItems != null && selectItems.length > 0) {
            ResponseWriter writer = context.getResponseWriter();
            UIComponent contentFacet = component.getContentFacet();
            Converter converter = ComponentUtils.getConverter(context, component);
            String var = component.getVar();

            for (SelectItem selectItem : selectItems) {
                boolean isGroup = selectItem instanceof SelectItemGroup;
                Object itemValue = selectItem.getValue();
                String itemLabel = selectItem.getLabel();
                String itemValueAsString = getOptionAsString(context, component, converter, selectItem.getValue());
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

                if (FacetUtils.shouldRenderFacet(contentFacet)) {
                    contentFacet.encodeAll(context);
                }
                else {
                    writer.startElement("span", component);
                    writer.writeAttribute("class", CascadeSelect.ITEM_TEXT_CLASS, null);
                    writer.writeText(itemLabel, null);
                    writer.endElement("span");
                }

                if (isGroup) {
                    writer.startElement("span", component);
                    writer.writeAttribute("class", CascadeSelect.GROUP_ICON_CLASS, null);
                    writer.endElement("span");
                }

                writer.endElement("div");

                if (isGroup) {
                    SelectItemGroup group = (SelectItemGroup) selectItem;
                    SelectItem[] groupItems = group.getSelectItems();

                    if (groupItems != null && groupItems.length > 0) {
                        encodeList(context, component, group.getSelectItems(), true);
                    }
                }

                writer.endElement("li");
            }

            if (var != null) {
                context.getExternalContext().getRequestMap().put(var, null);
            }
        }
    }

    protected void encodeScript(FacesContext context, CascadeSelect component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("CascadeSelect", component)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()), null);

        encodeClientBehaviors(context, component);
        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext facesContext, CascadeSelect component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected String getSubmitParam(FacesContext context, CascadeSelect component) {
        return component.getClientId(context) + "_input";
    }
}
