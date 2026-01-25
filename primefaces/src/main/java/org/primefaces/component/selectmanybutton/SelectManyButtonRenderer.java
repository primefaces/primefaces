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
package org.primefaces.component.selectmanybutton;

import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;

@FacesRenderer(rendererType = SelectManyButton.DEFAULT_RENDERER, componentFamily = SelectManyButton.COMPONENT_FAMILY)
public class SelectManyButtonRenderer extends SelectManyRenderer<SelectManyButton> {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectMany",
                "jakarta.faces.Checkbox");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, SelectManyButton component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, SelectManyButton component) throws IOException {
        String layout = component.getLayout();
        if (LangUtils.isEmpty(layout)) {
            layout = FacetUtils.shouldRenderFacet(component.getFacet("custom")) ? "custom" : null;
        }
        boolean custom = "custom".equals(layout);

        if (custom) {
            encodeCustomLayout(context, component);
        }
        else {
            encodeDefaultLayout(context, component);
        }
    }

    protected void encodeDefaultLayout(FacesContext context, SelectManyButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, button);
        int selectItemsSize = selectItems.size();
        String style = button.getStyle();
        String styleClass = createStyleClass(button, SelectManyButton.STYLE_CLASS);
        styleClass = styleClass + " ui-buttonset-" + selectItemsSize;

        writer.startElement("div", button);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeSelectItems(context, button, selectItems);

        writer.endElement("div");
    }

    protected void encodeSelectItems(FacesContext context, SelectManyButton component, List<SelectItem> selectItems) throws IOException {
        Converter<?> converter = component.getConverter();
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            encodeOption(context, component, values, submittedValues, converter, selectItem, i, selectItems.size());
        }
    }

    protected void encodeOption(FacesContext context, SelectManyButton component, Object values, Object submittedValues, Converter converter,
                                SelectItem option, int idx, int size) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, component, converter, option.getValue());
        String name = component.getClientId(context);
        String id = name + UINamingContainer.getSeparatorChar(context) + idx;
        boolean disabled = option.isDisabled() || component.isDisabled();
        String tabindex = component.getTabindex();

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

        String buttonStyle = HTML.BUTTON_TEXT_ONLY_BUTTON_FLAT_CLASS;
        buttonStyle = selected ? buttonStyle + " ui-state-active" : buttonStyle;
        buttonStyle = disabled ? buttonStyle + " ui-state-disabled" : buttonStyle;

        //button
        writer.startElement("div", getSelectItemComponent(option));
        writer.writeAttribute("class", buttonStyle, null);
        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute(HTML.ARIA_LABEL, option.getLabel(), null);

        renderOnchange(context, component);

        if (selected) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }

        renderAccessibilityAttributes(context, component);
        writer.endElement("input");

        //item label
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);

        if (option.isEscape()) {
            writer.writeText(option.getLabel(), "itemLabel");
        }
        else {
            writer.write(option.getLabel());
        }

        writer.endElement("span");

        writer.endElement("div");
    }

    protected void encodeCustomLayout(FacesContext context, SelectManyButton component) throws IOException {
        UIComponent customFacet = component.getFacet("custom");
        if (FacetUtils.shouldRenderFacet(customFacet)) {
            ResponseWriter writer = context.getResponseWriter();
            String style = component.getStyle();
            String styleClass = createStyleClass(component, SelectManyButton.STYLE_CLASS);
            writer.startElement("span", component);
            writer.writeAttribute("id", component.getClientId(context), "id");
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, "styleClass");
            }

            encodeCustomLayoutHelper(context, component, false);
            customFacet.encodeAll(context);

            writer.endElement("span");
        }
        else {
            encodeCustomLayoutHelper(context, component, true);
        }
    }

    protected void encodeCustomLayoutHelper(FacesContext context, SelectManyButton component, boolean addId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("span", component);
        if (addId) {
            writer.writeAttribute("id", component.getClientId(context), "id");
        }
        writer.writeAttribute("class", "ui-helper-hidden", null);

        Converter<?> converter = component.getConverter();
        String name = component.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, component);
        Object values = getValues(component);
        Object submittedValues = getSubmittedValues(component);

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            String id = name + UINamingContainer.getSeparatorChar(context) + i;
            boolean disabled = selectItem.isDisabled() || component.isDisabled();
            String itemValueAsString = getOptionAsString(context, component, converter, selectItem.getValue());

            Object valuesArray;
            Object itemValue;
            if (submittedValues != null) {
                valuesArray = submittedValues;
                itemValue = itemValueAsString;
            }
            else {
                valuesArray = values;
                itemValue = selectItem.getValue();
            }

            boolean selected = isSelected(context, component, itemValue, valuesArray, converter);
            if (selectItem.isNoSelectionOption() && values != null && !selected) {
                continue;
            }

            encodeOptionInput(context, component, id, name, selected, disabled, itemValueAsString);
        }

        writer.endElement("span");
    }

    protected void encodeOptionInput(FacesContext context, SelectManyButton component, String id, String name, boolean checked,
                                     boolean disabled, String value) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("value", value, null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        if (component.getTabindex() != null) {
            writer.writeAttribute("tabindex", component.getTabindex(), null);
        }
        if (checked) {
            writer.writeAttribute("checked", "checked", null);
        }
        if (disabled) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        renderValidationMetadata(context, component);

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, SelectManyButton component) throws IOException {
        String layout = component.getLayout();
        if (LangUtils.isEmpty(layout) && FacetUtils.shouldRenderFacet(component.getFacet("custom"))) {
            layout = "custom";
        }
        boolean custom = "custom".equals(layout);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectManyButton", component)
                .attr("custom", custom, false)
                .callback("change", "function()", component.getOnchange());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectManyButton component) {
        return component.getClientId(context);
    }

}
