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
package org.primefaces.component.chips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class ChipsRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Chips chips = (Chips) component;
        String clientId = chips.getClientId(context);

        if (!shouldDecode(chips)) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        Map<String, String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
        String[] hInputValues = paramValues.get(clientId + "_hinput");
        String[] submittedValues = (hInputValues != null) ? hInputValues : new String[]{};
        String inputValue = params.get(clientId + "_input");

        if (!isValueBlank(inputValue)) {
            submittedValues = LangUtils.concat(submittedValues, new String[]{inputValue});
        }

        if (submittedValues.length > chips.getMax()) {
            return;
        }

        if (submittedValues.length > 0) {
            chips.setSubmittedValue(submittedValues);
        }
        else {
            chips.setSubmittedValue("");
        }

        decodeBehaviors(context, chips);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Chips chips = (Chips) component;

        encodeMarkup(context, chips);
        encodeScript(context, chips);
    }

    protected void encodeMarkup(FacesContext context, Chips chips) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = chips.getClientId(context);
        String inputId = clientId + "_input";
        List values = (List) chips.getValue();
        List<String> stringValues = new ArrayList<>();
        boolean disabled = chips.isDisabled();
        String title = chips.getTitle();

        String style = chips.getStyle();
        String styleClass = chips.getStyleClass();
        styleClass = styleClass == null ? Chips.STYLE_CLASS : Chips.STYLE_CLASS + " " + styleClass;

        String inputStyle = chips.getInputStyle();
        String inputStyleClass = chips.getInputStyleClass();

        String listClass = disabled ? Chips.CONTAINER_CLASS + " ui-state-disabled" : Chips.CONTAINER_CLASS;
        listClass = (inputStyleClass == null) ? listClass : listClass + " " + inputStyleClass;
        listClass = chips.isValid() ? listClass : listClass + " ui-state-error";

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", listClass, null);
        if (inputStyle != null) {
            writer.writeAttribute("style", inputStyle, null);
        }
        renderARIARequired(context, chips);

        if (values != null && !values.isEmpty()) {
            Converter converter = ComponentUtils.getConverter(context, chips);

            for (Iterator<Object> it = values.iterator(); it.hasNext(); ) {
                Object value = it.next();

                String tokenValue = converter != null ? converter.getAsString(context, chips, value) : String.valueOf(value);

                writer.startElement("li", null);
                writer.writeAttribute("data-token-value", tokenValue, null);
                writer.writeAttribute("class", Chips.TOKEN_DISPLAY_CLASS, null);

                writer.startElement("span", null);
                writer.writeAttribute("class", Chips.TOKEN_LABEL_CLASS, null);
                writer.writeText(tokenValue, null);
                writer.endElement("span");

                writer.startElement("span", null);
                writer.writeAttribute("class", Chips.TOKEN_CLOSE_ICON_CLASS, null);
                writer.endElement("span");

                writer.endElement("li");

                stringValues.add(tokenValue);
            }
        }

        writer.startElement("li", null);
        writer.writeAttribute("class", Chips.TOKEN_INPUT_CLASS, null);
        writer.startElement("input", null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("class", "ui-widget", null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("autocomplete", "off", null);

        renderAccessibilityAttributes(context, chips);
        renderPassThruAttributes(context, chips, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, chips, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("input");
        writer.endElement("li");

        writer.endElement("ul");

        encodeHiddenSelect(context, chips, clientId, stringValues);

        writer.endElement("div");
    }

    protected void encodeHiddenSelect(FacesContext context, Chips chips, String clientId, List<String> values) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = clientId + "_hinput";

        writer.startElement("select", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        if (chips.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        for (String value : values) {
            writer.startElement("option", null);
            writer.writeAttribute("value", value, null);
            writer.writeAttribute("selected", "selected", null);
            writer.endElement("option");
        }

        writer.endElement("select");
    }

    protected void encodeScript(FacesContext context, Chips chips) throws IOException {
        String clientId = chips.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chips", chips.resolveWidgetVar(context), clientId)
                .attr("max", chips.getMax(), Integer.MAX_VALUE)
                .attr("addOnBlur", chips.isAddOnBlur(), false);

        encodeClientBehaviors(context, chips);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Chips chips = (Chips) component;

        if (submittedValue == null || submittedValue.equals("")) {
            return null;
        }

        Converter converter = ComponentUtils.getConverter(context, component);
        String[] values = (String[]) submittedValue;
        List list = new ArrayList();

        for (String value : values) {
            if (isValueBlank(value)) {
                continue;
            }

            Object convertedValue = converter != null ? converter.getAsObject(context, chips, value) : value;

            if (convertedValue != null) {
                list.add(convertedValue);
            }
        }

        return list;
    }
}
