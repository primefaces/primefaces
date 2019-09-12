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
package org.primefaces.component.inputnumber;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.*;

public class InputNumberRenderer extends InputRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
            throws ConverterException {

        String submittedValueString = (String) submittedValue;

        if (LangUtils.isValueBlank(submittedValueString)) {
            return null;
        }

        Converter converter = ComponentUtils.getConverter(context, component);
        if (converter != null) {
            return converter.getAsObject(context, component, submittedValueString);
        }

        return submittedValue;
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        InputNumber inputNumber = (InputNumber) component;

        if (!shouldDecode(inputNumber)) {
            return;
        }

        decodeBehaviors(context, inputNumber);

        String inputId = inputNumber.getClientId(context) + "_hinput";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(inputId);

        if (submittedValue == null) {
            return;
        }

        try {
            if (LangUtils.isValueBlank(submittedValue)) {
                ValueExpression valueExpression = inputNumber.getValueExpression("value");
                if (valueExpression != null) {
                    Class<?> type = valueExpression.getType(context.getELContext());
                    if (type != null && type.isPrimitive() && !LangUtils.isValueBlank(inputNumber.getMinValue())) {
                        // avoid coercion of null or empty string to 0 which may be out of [minValue, maxValue] range
                        submittedValue = String.valueOf(new BigDecimal(inputNumber.getMinValue()).doubleValue());
                    }
                    else if (type != null && type.isPrimitive() && !LangUtils.isValueBlank(inputNumber.getMaxValue())) {
                        // avoid coercion of null or empty string to 0 which may be out of [minValue, maxValue] range
                        submittedValue = String.valueOf(new BigDecimal(inputNumber.getMaxValue()).doubleValue());
                    }
                }
            }
            else {
                BigDecimal value = new BigDecimal(submittedValue);
                if (!LangUtils.isValueBlank(inputNumber.getMinValue())) {
                    BigDecimal min = new BigDecimal(inputNumber.getMinValue());
                    if (value.compareTo(min) < 0) {
                        submittedValue = String.valueOf(min.doubleValue());
                    }
                }
                if (!LangUtils.isValueBlank(inputNumber.getMaxValue())) {
                    BigDecimal max = new BigDecimal(inputNumber.getMaxValue());
                    if (value.compareTo(max) > 0) {
                        submittedValue = String.valueOf(max.doubleValue());
                    }
                }
            }
        }
        catch (NumberFormatException ex) {
            throw new FacesException("Invalid number", ex);
        }

        inputNumber.setSubmittedValue(submittedValue);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputNumber inputNumber = (InputNumber) component;

        Object value = inputNumber.getValue();
        String valueToRender = ComponentUtils.getValueToRender(context, inputNumber, value);
        if (valueToRender == null) {
            valueToRender = "";
        }

        encodeMarkup(context, inputNumber, value, valueToRender);
        encodeScript(context, inputNumber, value, valueToRender);
    }

    protected void encodeMarkup(FacesContext context, InputNumber inputNumber, Object value, String valueToRender)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputNumber.getClientId(context);

        String styleClass = inputNumber.getStyleClass();
        styleClass = styleClass == null ? InputNumber.STYLE_CLASS : InputNumber.STYLE_CLASS + " " + styleClass;
        styleClass = inputNumber.isValid() ? styleClass : styleClass + " ui-state-error"; // see #3706

        writer.startElement("span", inputNumber);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");

        if (inputNumber.getStyle() != null) {
            writer.writeAttribute("style", inputNumber.getStyle(), "style");
        }

        encodeInput(context, inputNumber, clientId, valueToRender);
        encodeHiddenInput(context, inputNumber, clientId, valueToRender);

        writer.endElement("span");
    }

    protected void encodeHiddenInput(FacesContext context, InputNumber inputNumber, String clientId, String valueToRender) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_hinput";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("value", valueToRender, null);

        if (inputNumber.getOnchange() != null) {
            writer.writeAttribute("onchange", inputNumber.getOnchange(), null);
        }

        if (inputNumber.getOnkeydown() != null) {
            writer.writeAttribute("onkeydown", inputNumber.getOnkeydown(), null);
        }

        if (inputNumber.getOnkeyup() != null) {
            writer.writeAttribute("onkeyup", inputNumber.getOnkeyup(), null);
        }

        renderValidationMetadata(context, inputNumber);

        writer.endElement("input");

    }

    protected void encodeInput(FacesContext context, InputNumber inputNumber, String clientId, String valueToRender)
            throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        String inputStyle = inputNumber.getInputStyle();
        String inputStyleClass = inputNumber.getInputStyleClass();

        String style = inputStyle;

        String styleClass = InputText.STYLE_CLASS;
        styleClass = inputNumber.isValid() ? styleClass : styleClass + " ui-state-error";
        styleClass = !inputNumber.isDisabled() ? styleClass : styleClass + " ui-state-disabled";
        if (!isValueBlank(inputStyleClass)) {
            styleClass += " " + inputStyleClass;
        }

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", inputNumber.getType(), null);
        writer.writeAttribute("value", valueToRender, null);

        if (!isValueBlank(style)) {
            writer.writeAttribute("style", style, null);
        }

        writer.writeAttribute("class", styleClass, null);

        renderAccessibilityAttributes(context, inputNumber);
        renderPassThruAttributes(context, inputNumber, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputNumber, HTML.INPUT_TEXT_EVENTS);
        renderValidationMetadata(context, inputNumber);

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, InputNumber inputNumber, Object value, String valueToRender)
            throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(InputNumber.class.getSimpleName(), inputNumber.resolveWidgetVar(context), inputNumber.getClientId());
        wb.attr("disabled", inputNumber.isDisabled())
                .attr("valueToRender", formatForPlugin(valueToRender, inputNumber, value));

        String metaOptions = getOptions(inputNumber);
        if (!metaOptions.isEmpty()) {
            wb.nativeAttr("pluginOptions", metaOptions);
        }

        wb.finish();
    }

    protected String getOptions(InputNumber inputNumber) {

        String decimalSeparator = inputNumber.getDecimalSeparator();
        String thousandSeparator = inputNumber.getThousandSeparator();
        String symbol = inputNumber.getSymbol();
        String symbolPosition = inputNumber.getSymbolPosition();
        String minValue = inputNumber.getMinValue();
        String maxValue = inputNumber.getMaxValue();
        String roundMethod = inputNumber.getRoundMethod();
        String decimalPlaces = inputNumber.getDecimalPlaces();
        String emptyValue = inputNumber.getEmptyValue();
        String lZero = inputNumber.getLeadingZero();
        boolean padControl = inputNumber.isPadControl();

        String options = "";
        options += isValueBlank(decimalSeparator) ? "" : "aDec:\"" + EscapeUtils.forJavaScript(decimalSeparator) + "\",";
        //empty thousandSeparator must be explicity defined.
        options += isValueBlank(thousandSeparator) ? "aSep:''," : "aSep:\"" + EscapeUtils.forJavaScript(thousandSeparator) + "\",";
        options += isValueBlank(symbol) ? "" : "aSign:\"" + EscapeUtils.forJavaScript(symbol) + "\",";
        options += isValueBlank(symbolPosition) ? "" : "pSign:\"" + EscapeUtils.forJavaScript(symbolPosition) + "\",";
        options += isValueBlank(minValue) ? "" : "vMin:\"" + EscapeUtils.forJavaScript(minValue) + "\",";
        options += isValueBlank(maxValue) ? "" : "vMax:\"" + EscapeUtils.forJavaScript(maxValue) + "\",";
        options += isValueBlank(roundMethod) ? "" : "mRound:\"" + EscapeUtils.forJavaScript(roundMethod) + "\",";
        options += isValueBlank(decimalPlaces) ? "" : "mDec:\"" + EscapeUtils.forJavaScript(decimalPlaces) + "\",";
        options += "wEmpty:\"" + EscapeUtils.forJavaScript(emptyValue) + "\",";
        options += "lZero:\"" + EscapeUtils.forJavaScript(lZero) + "\",";
        options += "aPad:" + padControl + ",";

        //if all options are empty return empty
        if (options.isEmpty()) {
            return "";
        }

        //delete the last comma
        int lastInd = options.length() - 1;
        if (options.charAt(lastInd) == ',') {
            options = options.substring(0, lastInd);
        }
        return "{" + options + "}";

    }

    private String formatForPlugin(String valueToRender, InputNumber inputNumber, Object value) {

        if (isValueBlank(valueToRender)) {
            return "";
        }
        else {
            try {
                Object objectToRender;
                if (value instanceof BigDecimal || doubleValueCheck(valueToRender)) {
                    objectToRender = new BigDecimal(valueToRender);
                }
                else {
                    objectToRender = new Double(valueToRender);
                }

                NumberFormat formatter = new DecimalFormat("#0.0#");
                formatter.setRoundingMode(RoundingMode.FLOOR);
                //autoNumeric jquery plugin max and min limits
                formatter.setMinimumFractionDigits(15);
                formatter.setMaximumFractionDigits(15);
                formatter.setMaximumIntegerDigits(20);
                String f = formatter.format(objectToRender);

                //force to english decimal separator
                f = f.replace(',', '.');
                return f;
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error converting  [" + valueToRender + "] to a double value;", e);
            }
        }
    }

    protected boolean doubleValueCheck(String valueToRender) {
        int counter = 0;
        int length = valueToRender.length();

        for (int i = 0; i < length; i++) {
            if (valueToRender.charAt(i) == '9') {
                counter++;
            }
        }

        return (counter > 15 || length > 15);
    }

    @Override
    protected String getHighlighter() {
        return "inputnumber";
    }

}
