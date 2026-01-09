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
package org.primefaces.component.selectmanymenu;

import org.primefaces.component.column.Column;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.renderkit.SelectManyRenderer;
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

@FacesRenderer(rendererType = SelectManyMenu.DEFAULT_RENDERER, componentFamily = SelectManyMenu.COMPONENT_FAMILY)
public class SelectManyMenuRenderer extends SelectManyRenderer<SelectManyMenu> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectMany",
                "jakarta.faces.Menu");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectManyMenu component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, SelectManyMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);

        String style = component.getStyle();
        String styleClass = createStyleClass(component, SelectManyMenu.CONTAINER_CLASS);

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

    protected void encodeScript(FacesContext context, SelectManyMenu component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectManyMenu", component)
                .attr("disabled", component.isDisabled(), false)
                .attr("showCheckbox", component.isShowCheckbox(), false)
                .attr("metaKeySelection", component.isMetaKeySelection(), true);

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

    protected void encodeInput(FacesContext context, SelectManyMenu menu, String clientId, List<SelectItem> selectItems)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputid = clientId + "_input";

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("select", null);
        writer.writeAttribute("id", inputid, "id");
        writer.writeAttribute("name", inputid, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("size", "2", null);   //prevent browser to send value when no item is selected

        renderAccessibilityAttributes(context, menu);
        renderPassThruAttributes(context, menu, HTML.TAB_INDEX);
        renderDomEvents(context, menu, SelectManyMenu.DOM_EVENTS);
        renderValidationMetadata(context, menu);

        encodeSelectItems(context, menu, selectItems);

        writer.endElement("select");

        writer.endElement("div");
    }

    protected void encodeList(FacesContext context, SelectManyMenu component, List<SelectItem> selectItems) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Converter<?> converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);
        boolean customContent = component.getVar() != null;
        boolean showCheckbox = component.isShowCheckbox();

        writer.startElement("div", component);
        writer.writeAttribute("class", SelectManyMenu.LIST_CONTAINER_CLASS, null);
        writer.writeAttribute("style", "height:" + calculateWrapperHeight(component, countSelectItems(selectItems)), null);

        if (customContent) {
            writer.startElement("table", null);
            writer.writeAttribute("class", SelectManyMenu.LIST_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
            writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "" + component.isMetaKeySelection(), null);
            writer.startElement("tbody", null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_GROUP, null);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                encodeItem(context, component, selectItem, values, submittedValues, converter, customContent, showCheckbox);
            }
            writer.endElement("tbody");
            writer.endElement("table");
        }
        else {
            writer.startElement("ul", null);
            writer.writeAttribute("class", SelectManyMenu.LIST_CLASS, null);
            writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_LISTBOX, null);
            writer.writeAttribute(HTML.ARIA_MULITSELECTABLE, "" + component.isMetaKeySelection(), null);
            for (int i = 0; i < selectItems.size(); i++) {
                SelectItem selectItem = selectItems.get(i);
                encodeItem(context, component, selectItem, values, submittedValues, converter, customContent, showCheckbox);
            }
            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    protected void encodeItem(FacesContext context, SelectManyMenu component, SelectItem option, Object values, Object submittedValues,
                              Converter<?> converter, boolean customContent, boolean showCheckbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        boolean disabled = option.isDisabled() || component.isDisabled();
        String itemClass = disabled ? SelectManyMenu.ITEM_CLASS + " ui-state-disabled" : SelectManyMenu.ITEM_CLASS;

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

        if (selected) {
            itemClass = itemClass + " ui-state-highlight";
        }

        if (customContent) {
            String var = component.getVar();
            context.getExternalContext().getRequestMap().put(var, option.getValue());

            writer.startElement("tr", getSelectItemComponent(option));
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("tabindex", "0", null);
            writer.writeAttribute(HTML.ARIA_ROLE, "option", null);
            writer.writeAttribute(HTML.ARIA_LABEL, option.getLabel(), null);
            writer.writeAttribute(HTML.ARIA_DISABLED, "" + option.isDisabled(), null);
            writer.writeAttribute(HTML.ARIA_SELECTED, "" + selected, null);

            if (option.getDescription() != null) {
                writer.writeAttribute("title", option.getDescription(), null);
            }

            if (showCheckbox) {
                writer.startElement("td", null);
                writer.writeAttribute("class", SelectManyMenu.CHECKBOX_CLASS, "styleClass");
                RendererUtils.encodeCheckbox(context, selected);
                writer.endElement("td");
            }

            for (UIComponent child : component.getChildren()) {
                if (child instanceof Column && child.isRendered()) {
                    String style = ((Column) child).getStyle();
                    String styleClass = ((Column) child).getStyleClass();

                    writer.startElement("td", null);
                    if (styleClass != null) {
                        writer.writeAttribute("class", styleClass, "styleClass");
                    }
                    if (style != null) {
                        writer.writeAttribute("style", style, "style");
                    }

                    renderChildren(context, child);
                    writer.endElement("td");
                }
            }

            writer.endElement("tr");
        }
        else {
            writer.startElement("li", getSelectItemComponent(option));
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("tabindex", "0", null);
            writer.writeAttribute(HTML.ARIA_ROLE, "option", null);
            writer.writeAttribute(HTML.ARIA_LABEL, option.getLabel(), null);
            writer.writeAttribute(HTML.ARIA_DISABLED, "" + option.isDisabled(), null);
            writer.writeAttribute(HTML.ARIA_SELECTED, "" + selected, null);

            if (showCheckbox) {
                RendererUtils.encodeCheckbox(context, selected);
            }

            if (option.isEscape()) {
                writer.writeText(option.getLabel(), null);
            }
            else {
                writer.write(option.getLabel());
            }

            writer.endElement("li");
        }

    }

    protected void encodeSelectItems(FacesContext context, SelectManyMenu component, List<SelectItem> selectItems) throws IOException {
        Converter<?> converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            encodeOption(context, component, selectItem, values, submittedValues, converter);
        }
    }

    protected void encodeOption(FacesContext context, SelectManyMenu component, SelectItem option, Object values, Object submittedValues,
                                Converter<?> converter) throws IOException {

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

        writer.startElement("option", getSelectItemComponent(option));
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

    protected void encodeFilter(FacesContext context, SelectManyMenu component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = component.getClientId(context) + "_filter";
        boolean disabled = component.isDisabled();
        String filterClass = disabled ? SelectManyMenu.FILTER_CLASS + " ui-state-disabled" : SelectManyMenu.FILTER_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", SelectManyMenu.FILTER_CONTAINER_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", SelectManyMenu.FILTER_ICON_CLASS, id);
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

    protected String calculateWrapperHeight(SelectManyMenu component, int itemSize) {
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
    protected String getSubmitParam(FacesContext context, SelectManyMenu selectMany) {
        return selectMany.getClientId(context) + "_input";
    }

    @Override
    public void encodeChildren(FacesContext context, SelectManyMenu component) throws IOException {
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
