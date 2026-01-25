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
package org.primefaces.component.chips;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Chips.DEFAULT_RENDERER, componentFamily = Chips.COMPONENT_FAMILY)
public class ChipsRenderer extends InputRenderer<Chips> {

    @Override
    public void decode(FacesContext context, Chips component) {
        String clientId = component.getClientId(context);

        if (!shouldDecode(component)) {
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

        if (submittedValues.length > component.getMax()) {
            return;
        }

        if (submittedValues.length > 0) {
            if (component.isUnique()) {
                submittedValues = Stream.of(submittedValues).distinct().toArray(String[]::new);
            }
            component.setSubmittedValue(submittedValues);
        }
        else {
            component.setSubmittedValue("");
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Chips component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Chips component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String inputId = clientId + "_input";
        List<String> stringValues = new ArrayList<>();
        String title = component.getTitle();

        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? Chips.STYLE_CLASS : Chips.STYLE_CLASS + " " + styleClass;

        String inputStyle = component.getInputStyle();
        String listClass = createStyleClass(component, Chips.PropertyKeys.inputStyleClass.name(), Chips.CONTAINER_CLASS);

        Collection values;
        if (component.isValid()) {
            values = (Collection) component.getValue();
        }
        else {
            Object submittedValue = component.getSubmittedValue();
            try {
                values = (Collection) getConvertedValue(context, component, submittedValue);
            }
            catch (ConverterException ce) {
                values = Arrays.asList((String[]) submittedValue);
            }
        }

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
        renderARIARequired(context, component);

        if (values != null && !values.isEmpty()) {
            Converter converter = ComponentUtils.getConverter(context, component);

            Collection<Object> items = component.isUnique() ? new LinkedHashSet<>(values) : values;
            for (Object value : items) {
                String tokenValue = converter != null ? converter.getAsString(context, component, value) : String.valueOf(value);

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

        renderAccessibilityAttributes(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);

        writer.endElement("input");
        writer.endElement("li");

        writer.endElement("ul");

        encodeHiddenSelect(context, component, clientId, stringValues);

        writer.endElement("div");
    }

    protected void encodeHiddenSelect(FacesContext context, Chips component, String clientId, List<String> values) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = clientId + "_hinput";

        writer.startElement("select", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("multiple", "multiple", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        if (component.isDisabled()) {
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
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chips", chips)
                .attr("max", chips.getMax(), Integer.MAX_VALUE)
                .attr("addOnBlur", chips.isAddOnBlur(), false)
                .attr("addOnPaste", chips.isAddOnPaste(), false)
                .attr("unique", chips.isUnique(), false)
                .attr("separator", chips.getSeparator());

        encodeClientBehaviors(context, chips);

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Chips chips = (Chips) component;

        if (submittedValue == null || submittedValue.equals("")) {
            return null;
        }

        Converter<?> converter = ComponentUtils.getConverter(context, component);
        String[] values = (String[]) submittedValue;
        List<Object> list = new ArrayList<>();

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
