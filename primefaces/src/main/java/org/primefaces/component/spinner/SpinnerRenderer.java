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
package org.primefaces.component.spinner;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.math.BigInteger;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Spinner.DEFAULT_RENDERER, componentFamily = Spinner.COMPONENT_FAMILY)
public class SpinnerRenderer extends InputRenderer<Spinner> {

    @Override
    public void decode(FacesContext context, Spinner component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String submittedValue = context.getExternalContext().getRequestParameterMap().get(component.getClientId(context) + "_input");

        if (LangUtils.isNotEmpty(submittedValue)) {
            String prefix = component.getPrefix();
            String suffix = component.getSuffix();

            if (prefix != null && submittedValue.startsWith(prefix)) {
                submittedValue = submittedValue.substring(prefix.length());
            }
            if (suffix != null && submittedValue.endsWith(suffix)) {
                submittedValue = submittedValue.substring(0, (submittedValue.length() - suffix.length()));
            }
            if (LangUtils.isNotEmpty(component.getThousandSeparator())) {
                submittedValue = submittedValue.replace(component.getThousandSeparator(), "");
            }
            if (LangUtils.isNotEmpty(component.getDecimalSeparator())) {
                submittedValue = submittedValue.replace(component.getDecimalSeparator(), ".");
            }

            try {
                // GitHub #11830 prevent value outside of minimum or maximum range
                double submittedNumber = Double.parseDouble(submittedValue);
                if (submittedNumber < component.getMin() || submittedNumber > component.getMax()) {
                    logDevelopmentWarning(context, this, String.format("Value is outside min/max range: %s", submittedValue));
                    return;
                }
            }
            catch (NumberFormatException e) {
                // GitHub #12365 prevent any invalid number like just the thousands separator
                logDevelopmentWarning(context, this, String.format("Invalid number format: %s", submittedValue));
                return;
            }
        }

        component.setSubmittedValue(submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, Spinner component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, Spinner component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);

        Object value = component.getValue();
        String defaultDecimalPlaces = null;
        if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof BigInteger) {
            defaultDecimalPlaces = "0";
        }
        String decimalPlaces = isValueBlank(component.getDecimalPlaces())
                ? defaultDecimalPlaces
                : component.getDecimalPlaces();

        wb.init("Spinner", component)
                .attr("step", component.getStepFactor(), 1.0)
                .attr("round", component.isRound(), false)
                .attr("min", component.getMin(), Spinner.MIN_VALUE)
                .attr("max", component.getMax(), Spinner.MAX_VALUE)
                .attr("prefix", component.getPrefix(), null)
                .attr("suffix", component.getSuffix(), null)
                .attr("rotate", component.isRotate(), false)
                .attr("decimalPlaces", decimalPlaces, null)
                .attr("modifyValueOnWheel", component.isModifyValueOnWheel(), true)
                .attr(Spinner.PropertyKeys.thousandSeparator.name(), component.getThousandSeparator())
                .attr(Spinner.PropertyKeys.decimalSeparator.name(), component.getDecimalSeparator());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Spinner component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = getStyleClassBuilder(context)
                .add(createStyleClass(component, Spinner.CONTAINER_CLASS))
                .add(Spinner.BUTTONS_CLASS_PREFIX + getButtonsClassSuffix(component))
                .add(ComponentUtils.isRTL(context, component), "ui-spinner-rtl")
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        encodeInput(context, component);

        boolean valid = component.isValid();
        String upButtonClass = getButtonClass(context, Spinner.UP_BUTTON_CLASS, component.getUpButtonStyleClass(), valid);
        String downButtonClass = getButtonClass(context, Spinner.DOWN_BUTTON_CLASS, component.getDownButtonStyleClass(), valid);

        boolean stacked = SpinnerBase.BUTTONS_STACKED.equals(component.getButtons());
        String upIconClass = getIconClass(context,
                component.getUpIcon(),
                stacked ? Spinner.STACKED_UP_ICON_CLASS : Spinner.HORIZONTAL_UP_ICON_CLASS);
        String downIconClass = getIconClass(context,
                component.getDownIcon(),
                stacked ? Spinner.STACKED_DOWN_ICON_CLASS : Spinner.HORIZONTAL_DOWN_ICON_CLASS);

        encodeButton(context, component, "increase", upButtonClass, upIconClass);
        encodeButton(context, component, "decrease", downButtonClass, downIconClass);

        writer.endElement("span");
    }

    protected String getButtonsClassSuffix(Spinner component) {
        switch (component.getButtons()) {
            case SpinnerBase.BUTTONS_HORIZONTAL:
                return SpinnerBase.BUTTONS_HORIZONTAL;
            case SpinnerBase.BUTTONS_HORIZONTAL_AFTER:
                return SpinnerBase.BUTTONS_HORIZONTAL_AFTER;
            case SpinnerBase.BUTTONS_VERTICAL:
                return SpinnerBase.BUTTONS_VERTICAL;
            default:
                return SpinnerBase.BUTTONS_STACKED;
        }
    }

    protected String getButtonClass(FacesContext context, String fixedStyleClass, String styleClass, boolean valid) {
        return getStyleClassBuilder(context)
                .add(fixedStyleClass)
                .add(styleClass)
                .add(!valid, "ui-state-error")
                .build();
    }

    protected String getIconClass(FacesContext context, String custom, String fallback) {
        return getStyleClassBuilder(context)
                .add(Spinner.ICON_BASE_CLASS)
                .addOrElse(custom, fallback)
                .build();
    }

    protected void encodeInput(FacesContext context, Spinner component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = component.getClientId(context) + "_input";
        String inputClass = createStyleClass(component, null, Spinner.INPUT_CLASS);

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", inputClass, null);
        writer.writeAttribute("autocomplete", "off", null);

        String valueToRender = ComponentUtils.getValueToRender(context, component);
        if (valueToRender != null) {
            valueToRender = component.getPrefix() != null ? component.getPrefix() + valueToRender : valueToRender;
            valueToRender = component.getSuffix() != null ? valueToRender + component.getSuffix() : valueToRender;
            writer.writeAttribute("value", valueToRender, null);
        }

        renderAccessibilityAttributes(context, component);
        renderRTLDirection(context, component);
        renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, component);

        writer.endElement("input");
    }

    protected void encodeButton(FacesContext context, Spinner component, String direction, String styleClass, String iconClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("id", component.getClientId(context) + "-" + direction, "id");
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", styleClass, null);

        if (component.getTabindex() != null) {
            writer.writeAttribute("tabindex", component.getTabindex(), null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-button-text", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("span");

        writer.endElement("button");
    }

    @Override
    public String getHighlighter() {
        return "spinner";
    }
}
