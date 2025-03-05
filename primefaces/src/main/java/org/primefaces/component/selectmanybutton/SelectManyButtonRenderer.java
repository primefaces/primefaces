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
package org.primefaces.component.selectmanybutton;

import org.primefaces.renderkit.SelectManyRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
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
import jakarta.faces.render.Renderer;

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

    protected void encodeMarkup(FacesContext context, SelectManyButton button) throws IOException {
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

    protected void encodeScript(FacesContext context, SelectManyButton component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectManyButton", component).finish();
    }

    @Override
    protected String getSubmitParam(FacesContext context, SelectManyButton component) {
        return component.getClientId(context);
    }

}
