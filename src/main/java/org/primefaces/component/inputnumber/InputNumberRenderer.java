/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.inputnumber;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class InputNumberRenderer extends InputRenderer {

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
            throws ConverterException {

        String submittedValueString = (String) submittedValue;

        if (ComponentUtils.isValueBlank(submittedValueString)) {
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

        if (inputNumber.isDisabled() || inputNumber.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputNumber);

        String inputId = inputNumber.getClientId(context) + "_hinput";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(inputId);

        if (submittedValue != null) {
            inputNumber.setSubmittedValue(submittedValue);
        }

    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputNumber inputNumber = (InputNumber) component;
        encodeMarkup(context, inputNumber);
        encodeScript(context, inputNumber);
    }

    protected void encodeMarkup(FacesContext context, InputNumber inputNumber) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputNumber.getClientId(context);

        String styleClass = inputNumber.getStyleClass();
        styleClass = styleClass == null ? InputNumber.STYLE_CLASS : InputNumber.STYLE_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");

        if (inputNumber.getStyle() != null) {
            writer.writeAttribute("style", inputNumber.getStyle(), "style");
        }

        encodeOutput(context, inputNumber, clientId);
        encodeInput(context, inputNumber, clientId);

        writer.endElement("span");
    }

    protected void encodeInput(FacesContext context, InputNumber inputNumber, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_hinput";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);

        if (inputNumber.getOnchange() != null) {
            writer.writeAttribute("onchange", inputNumber.getOnchange(), null);
        }

        writer.endElement("input");

    }

    protected void encodeOutput(FacesContext context, InputNumber inputNumber, String clientId) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        String defaultClass = InputText.STYLE_CLASS;
        defaultClass = inputNumber.isValid() ? defaultClass : defaultClass + " ui-state-error";
        defaultClass = !inputNumber.isDisabled() ? defaultClass : defaultClass + " ui-state-disabled";

        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", inputNumber.getType(), null);

        renderPassThruAttributes(context, inputNumber, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputNumber, HTML.INPUT_TEXT_EVENTS);

        if (inputNumber.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", "readonly");
        }
        if (inputNumber.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }

        writer.writeAttribute("class", defaultClass, "");

        writer.endElement("input");
    }

    protected void encodeScript(FacesContext context, InputNumber inputNumber) throws IOException {
        Object value = inputNumber.getValue();
        String valueToRender = ComponentUtils.getValueToRender(context, inputNumber, value);
        if (valueToRender == null) {
            valueToRender = "";
        }

        WidgetBuilder wb = RequestContext.getCurrentInstance().getWidgetBuilder();
        wb.initWithDomReady(InputNumber.class.getSimpleName(), inputNumber.resolveWidgetVar(), inputNumber.getClientId());
        wb.attr("disabled", inputNumber.isDisabled())
                .attr("valueToRender", formatForPlugin(valueToRender, inputNumber, value));

        String metaOptions = getOptions(inputNumber);
        if (!metaOptions.isEmpty()) {
            wb.nativeAttr("pluginOptions", metaOptions);
        }

        wb.finish();
    }

    private String getOptions(InputNumber inputNumber) {

        String decimalSeparator = inputNumber.getDecimalSeparator();
        String thousandSeparator = inputNumber.getThousandSeparator();
        String symbol = inputNumber.getSymbol();
        String symbolPosition = inputNumber.getSymbolPosition();
        String minValue = inputNumber.getMinValue();
        String maxValue = inputNumber.getMaxValue();
        String roundMethod = inputNumber.getRoundMethod();
        String decimalPlaces = inputNumber.getDecimalPlaces();
        String emptyValue = inputNumber.getEmptyValue();

        String options = "";
        options += isValueBlank(decimalSeparator) ? "" : "aDec:\"" + escapeText(decimalSeparator) + "\",";
        //empty thousandSeparator must be explicity defined.
        options += isValueBlank(thousandSeparator) ? "aSep:''," : "aSep:\"" + escapeText(thousandSeparator) + "\",";
        options += isValueBlank(symbol) ? "" : "aSign:\"" + escapeText(symbol) + "\",";
        options += isValueBlank(symbolPosition) ? "" : "pSign:\"" + escapeText(symbolPosition) + "\",";
        options += isValueBlank(minValue) ? "" : "vMin:\"" + escapeText(minValue) + "\",";
        options += isValueBlank(maxValue) ? "" : "vMax:\"" + escapeText(maxValue) + "\",";
        options += isValueBlank(roundMethod) ? "" : "mRound:\"" + escapeText(roundMethod) + "\",";
        options += isValueBlank(decimalPlaces) ? "" : "mDec:\"" + escapeText(decimalPlaces) + "\",";
        options += "wEmpty:\"" + escapeText(emptyValue) + "\",";

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
                if (value instanceof BigDecimal) {
                    objectToRender = new BigDecimal(valueToRender);
                } else {
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

}
