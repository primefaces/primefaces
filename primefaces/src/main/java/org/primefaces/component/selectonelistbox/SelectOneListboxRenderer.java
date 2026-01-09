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
package org.primefaces.component.selectonelistbox;

import org.primefaces.component.column.Column;
import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;

@FacesRenderer(rendererType = SelectOneListbox.DEFAULT_RENDERER, componentFamily = SelectOneListbox.COMPONENT_FAMILY)
public class SelectOneListboxRenderer extends SelectOneRenderer<SelectOneListbox> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Listbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectOneListbox component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, SelectOneListbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);

        String style = component.getStyle();
        String styleClass = createStyleClass(component, SelectOneListbox.CONTAINER_CLASS);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if (component.isFilter()) {
            encodeFilter(context, component);
        }

        encodeInput(context, component, clientId, selectItems);
        encodeList(context, component, selectItems);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SelectOneListbox component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneListbox", component)
                .attr("disabled", component.isDisabled(), false);

        if (component.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", component.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", component.getFilterFunction(), null)
                    .attr("caseSensitive", component.isCaseSensitive(), false)
                    .attr("filterNormalize", component.isFilterNormalize(), false);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeInput(FacesContext context, SelectOneListbox component, String clientId, List<SelectItem> selectItems)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", component);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("select", component);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.TAB_INDEX);
        renderDomEvents(context, component, SelectOneListbox.DOM_EVENTS);
        renderValidationMetadata(context, component);

        encodeSelectItems(context, component, selectItems);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectOneListbox component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);
        boolean customContent = component.getVar() != null;

        writer.startElement("div", component);
        writer.writeAttribute("class", SelectOneListbox.LIST_CONTAINER_CLASS, null);
        writer.writeAttribute("style", "height:" + calculateWrapperHeight(component, countSelectItems(selectItems)), null);

        int totalItems = selectItems.size();
        if (customContent) {
            writer.startElement("table", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
            writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "false", null);
            writer.startElement("tbody", null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                encodeItem(context, component, selectItem, values, submittedValues, converter, customContent, totalItems, i);
            }
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            writer.startElement("ul", null);
            writer.writeAttribute("class", SelectOneListbox.LIST_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
            writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "false", null);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                encodeItem(context, component, selectItem, values, submittedValues, converter, customContent, totalItems, i);
            }
            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    protected String encodeItem(FacesContext context, SelectOneListbox component, SelectItem option, Object values, Object submittedValues,
                              Converter converter, boolean customContent, int totalItems, int index) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        boolean disabled = option.isDisabled() || component.isDisabled();
        String itemClass = disabled ? SelectOneListbox.ITEM_CLASS + " ui-state-disabled" : SelectOneListbox.ITEM_CLASS;
        int currentIndex = index + 1;
        String id = component.getClientId(context) + "_option" + currentIndex;

        Object valuesArray;
        Object itemValue;
        if (submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        }
        else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if (option.isNoSelectionOption() && values != null && !selected) {
            return null;
        }

        if (selected) {
            itemClass = itemClass + " ui-state-highlight";
        }

        if (customContent) {
            String var = component.getVar();
            context.getExternalContext().getRequestMap().put(var, option.getValue());

            writer.startElement("tr", null);
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("id", id, null);
            writer.writeAttribute(HTML.ARIA_ROLE, "option", null);
            writer.writeAttribute(HTML.ARIA_LABEL, option.getLabel(), null);
            writer.writeAttribute(HTML.ARIA_DISABLED, "" + option.isDisabled(), null);
            writer.writeAttribute(HTML.ARIA_SELECTED, "" + selected, null);
            writer.writeAttribute(HTML.ARIA_SET_SIZE, totalItems, null);
            writer.writeAttribute(HTML.ARIA_SET_POSITION, currentIndex, null);

            if (option.getDescription() != null) {
                writer.writeAttribute("title", option.getDescription(), null);
            }

            for (UIComponent child : component.getChildren()) {
                if (child instanceof Column && child.isRendered()) {
                    writer.startElement("td", null);

                    writer.startElement("span", null);
                    renderChildren(context, child);
                    writer.endElement("span");

                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }
        else {
            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("id", id, null);
            writer.writeAttribute(HTML.ARIA_ROLE, "option", null);
            writer.writeAttribute(HTML.ARIA_LABEL, option.getLabel(), null);
            writer.writeAttribute(HTML.ARIA_DISABLED, "" + option.isDisabled(), null);
            writer.writeAttribute(HTML.ARIA_SELECTED, "" + selected, null);
            writer.writeAttribute(HTML.ARIA_SET_SIZE, totalItems, null);
            writer.writeAttribute(HTML.ARIA_SET_POSITION, currentIndex, null);

            writer.startElement("span", null);
            if (option.isEscape()) {
                writer.writeText(option.getLabel(), null);
            }
            else {
                writer.write(option.getLabel());
            }
            writer.endElement("span");

            writer.endElement("li");
        }

        if (selected) {
            return id;
        }

        return null;
    }

    protected void encodeSelectItems(FacesContext context, SelectOneListbox component, List<SelectItem> selectItems) throws IOException {
        Converter converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            encodeOption(context, component, selectItem, values, submittedValues, converter);
        }
    }

    protected void encodeOption(FacesContext context, SelectOneListbox component, SelectItem option, Object values, Object submittedValues,
                                Converter converter) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        boolean disabled = option.isDisabled() || component.isDisabled();

        Object valuesArray;
        Object itemValue;
        if (submittedValues != null) {
            valuesArray = submittedValues;
            itemValue = itemValueAsString;
        }
        else {
            valuesArray = values;
            itemValue = option.getValue();
        }

        boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
        if (option.isNoSelectionOption() && values != null && !selected) {
            return;
        }

        writer.startElement("option", null);
        writer.writeAttribute("value", itemValueAsString, null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (selected) {
            writer.writeAttribute("selected", "selected", null);
        }

        if (option.isEscape()) {
            writer.writeText(option.getLabel(), null);
        }
        else {
            writer.write(option.getLabel());
        }

        writer.endElement("option");
    }

    protected void encodeFilter(FacesContext context, SelectOneListbox component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = component.getClientId(context) + "_filter";
        boolean disabled = component.isDisabled();
        String filterClass = disabled ? SelectOneListbox.FILTER_CLASS + " ui-state-disabled" : SelectOneListbox.FILTER_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectOneListbox.FILTER_CONTAINER_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", SelectOneListbox.FILTER_ICON_CLASS, id);
        writer.endElement("span");

        writer.startElement("input", null);
        writer.writeAttribute("class", filterClass, null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");

        writer.endElement("div");
    }

    protected String calculateWrapperHeight(SelectOneListbox component, int itemSize) {
        int height = component.getScrollHeight();

        if (height != Integer.MAX_VALUE) {
            return height + "px";
        }
        else if (itemSize > 10) {
            return 200 + "px";
        }

        return "auto";
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectOneListbox component) {
        return component.getClientId(context) + "_input";
    }

    @Override
    public void encodeChildren(FacesContext context, SelectOneListbox component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public String getHighlighter() {
        return "listbox";
    }
}
