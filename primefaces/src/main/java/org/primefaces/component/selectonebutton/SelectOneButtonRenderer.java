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
package org.primefaces.component.selectonebutton;

import org.primefaces.renderkit.SelectOneRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UISelectOne;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.model.SelectItem;
import jakarta.faces.render.Renderer;

public class SelectOneButtonRenderer extends SelectOneRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                context,
                "jakarta.faces.SelectOne",
                "jakarta.faces.Radio");
        return renderer.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectOneButton button = (SelectOneButton) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    protected void encodeMarkup(FacesContext context, SelectOneButton button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = button.getClientId(context);
        List<SelectItem> selectItems = getSelectItems(context, button);
        int selectItemsSize = selectItems.size();
        String style = button.getStyle();
        String styleClass = createStyleClass(button, SelectOneButton.STYLE_CLASS);
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

    protected void encodeSelectItems(FacesContext context, SelectOneButton button, List<SelectItem> selectItems) throws IOException {
        int selectItemsSize = selectItems.size();
        Converter converter = button.getConverter();
        String name = button.getClientId(context);
        Object value = button.getSubmittedValue();
        if (value == null) {
            value = button.getValue();
        }

        Class type = value == null ? String.class : value.getClass();

        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem selectItem = selectItems.get(i);
            boolean disabled = selectItem.isDisabled() || button.isDisabled();
            String id = name + UINamingContainer.getSeparatorChar(context) + i;

            boolean selected;
            if (value == null && selectItem.getValue() == null) {
                selected = true;
            }
            else {
                Object coercedItemValue = coerceToModelType(context, selectItem.getValue(), type);
                selected = (coercedItemValue != null) && coercedItemValue.equals(value);
            }

            encodeOption(context, button, selectItem, id, name, converter, selected, disabled, i, selectItemsSize);
        }
    }

    protected void encodeOption(FacesContext context, SelectOneButton button, SelectItem option, String id, String name, Converter converter,
                                boolean selected, boolean disabled, int idx, int size) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String itemValueAsString = getOptionAsString(context, button, converter, option.getValue());

        String buttonStyle = HTML.BUTTON_TEXT_ONLY_BUTTON_FLAT_CLASS;
        buttonStyle = selected ? buttonStyle + " ui-state-active" : buttonStyle;
        buttonStyle = disabled ? buttonStyle + " ui-state-disabled" : buttonStyle;

        //button
        writer.startElement("div", null);
        writer.writeAttribute("class", buttonStyle, null);
        writer.writeAttribute("tabindex", button.getTabindex(), null);
        if (option.getDescription() != null) {
            writer.writeAttribute("title", option.getDescription(), null);
        }

        //input
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("value", itemValueAsString, null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeAttribute(HTML.ARIA_LABEL, LangUtils.isEmpty(option.getDescription()) ? option.getLabel() : option.getDescription(), null);

        if (selected) {
            writer.writeAttribute("checked", "checked", null);
        }

        renderAccessibilityAttributes(context, button);
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

    protected void encodeScript(FacesContext context, SelectOneButton button) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneButton", button)
                .attr("unselectable", button.isUnselectable(), true)
                .callback("change", "function()", button.getOnchange());

        encodeClientBehaviors(context, button);

        wb.finish();
    }

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context);
    }
}
