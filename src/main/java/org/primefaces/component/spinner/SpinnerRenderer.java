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
package org.primefaces.component.spinner;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class SpinnerRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Spinner spinner = (Spinner) component;

        if (!shouldDecode(spinner)) {
            return;
        }

        decodeBehaviors(context, spinner);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(spinner.getClientId(context) + "_input");
        String prefix = spinner.getPrefix();
        String suffix = spinner.getSuffix();

        if (prefix != null && submittedValue.startsWith(prefix)) {
            submittedValue = submittedValue.substring(prefix.length());
        }
        if (suffix != null && submittedValue.endsWith(suffix)) {
            submittedValue = submittedValue.substring(0, (submittedValue.length() - suffix.length()));
        }
        if (!LangUtils.isValueEmpty(spinner.getThousandSeparator())) {
            submittedValue = submittedValue.replace(spinner.getThousandSeparator(), "");
        }
        submittedValue = submittedValue.replace(spinner.getDecimalSeparator(), ".");
        spinner.setSubmittedValue(submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Spinner spinner = (Spinner) component;

        encodeMarkup(context, spinner);
        encodeScript(context, spinner);
    }

    protected void encodeScript(FacesContext context, Spinner spinner) throws IOException {
        String clientId = spinner.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Spinner", spinner.resolveWidgetVar(context), clientId)
                .attr("step", spinner.getStepFactor(), 1.0)
                .attr("min", spinner.getMin(), Double.MIN_VALUE)
                .attr("max", spinner.getMax(), Double.MAX_VALUE)
                .attr("prefix", spinner.getPrefix(), null)
                .attr("suffix", spinner.getSuffix(), null)
                .attr("required", spinner.isRequired(), false)
                .attr("rotate", spinner.isRotate(), false)
                .attr("decimalPlaces", spinner.getDecimalPlaces(), null)
                .attr(SpinnerBase.PropertyKeys.thousandSeparator.name(), spinner.getThousandSeparator())
                .attr(SpinnerBase.PropertyKeys.decimalSeparator.name(), spinner.getDecimalSeparator());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Spinner spinner) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = spinner.getClientId(context);
        String styleClass = spinner.getStyleClass();
        boolean valid = spinner.isValid();
        styleClass = styleClass == null ? Spinner.CONTAINER_CLASS : Spinner.CONTAINER_CLASS + " " + styleClass;
        styleClass = spinner.isDisabled() ? styleClass + " ui-state-disabled" : styleClass;
        styleClass = !spinner.isValid() ? styleClass + " ui-state-error" : styleClass;
        String upButtonClass = (valid) ? Spinner.UP_BUTTON_CLASS : Spinner.UP_BUTTON_CLASS + " ui-state-error";
        String downButtonClass = (valid) ? Spinner.DOWN_BUTTON_CLASS : Spinner.DOWN_BUTTON_CLASS + " ui-state-error";

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (spinner.getStyle() != null) {
            writer.writeAttribute("style", spinner.getStyle(), null);
        }

        encodeInput(context, spinner);

        encodeButton(context, upButtonClass, Spinner.UP_ICON_CLASS);
        encodeButton(context, downButtonClass, Spinner.DOWN_ICON_CLASS);

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, Spinner spinner) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = spinner.getClientId(context) + "_input";
        String inputClass = spinner.isValid() ? Spinner.INPUT_CLASS : Spinner.INPUT_CLASS + " ui-state-error";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", inputClass, null);
        writer.writeAttribute("autocomplete", "off", null);

        String valueToRender = ComponentUtils.getValueToRender(context, spinner);
        if (valueToRender != null) {
            valueToRender = spinner.getPrefix() != null ? spinner.getPrefix() + valueToRender : valueToRender;
            valueToRender = spinner.getSuffix() != null ? valueToRender + spinner.getSuffix() : valueToRender;
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, spinner);
        renderPassThruAttributes(context, spinner, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, spinner, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, spinner);

        writer.endElement("input");
    }

    protected void encodeButton(FacesContext context, String styleClass, String iconClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("span");

        writer.endElement("a");
    }

    @Override
    public String getHighlighter() {
        return "spinner";
    }
}
