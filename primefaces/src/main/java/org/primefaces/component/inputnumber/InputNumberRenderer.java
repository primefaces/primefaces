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
package org.primefaces.component.inputnumber;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.bean.NegativeClientValidationConstraint;
import org.primefaces.validate.bean.PositiveClientValidationConstraint;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

public class InputNumberRenderer extends InputRenderer {

    // Default values for "minValue"/"maxValue" properties of the AutoNumeric Plugin
    private static final String DEFAULT_MIN_VALUE = "-10000000000000";
    private static final String DEFAULT_MAX_VALUE = "10000000000000";

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
            throws ConverterException {
        return ComponentUtils.getConvertedValue(context, component, submittedValue);
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
            if (LangUtils.isBlank(submittedValue)) {
                Class<?> type = getTypeFromValueExpression(context, inputNumber);
                if (type != null && type.isPrimitive() && LangUtils.isNotBlank(inputNumber.getMinValue())) {
                    // avoid coercion of null or empty string to 0 which may be out of [minValue, maxValue] range
                    submittedValue = String.valueOf(new BigDecimal(inputNumber.getMinValue()).doubleValue());
                }
                else if (type != null && type.isPrimitive() && LangUtils.isNotBlank(inputNumber.getMaxValue())) {
                    // avoid coercion of null or empty string to 0 which may be out of [minValue, maxValue] range
                    submittedValue = String.valueOf(new BigDecimal(inputNumber.getMaxValue()).doubleValue());
                }
            }
            else {
                // Coerce submittedValue to (effective) range of [minValue, maxValue]
                BigDecimal value = new BigDecimal(submittedValue);
                submittedValue = coerceValueInRange(context, value, inputNumber).toString();
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
        if (isValueBlank(valueToRender)) {
            valueToRender = "";
        }
        else {
            // Rendered value must always be inside the effective interval [minValue, maxValue],
            // or else AutoNumeric will throw an error and the component will be broken
            BigDecimal decimalToRender;
            try {
                decimalToRender = new BigDecimal(valueToRender);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error converting  [" + valueToRender + "] to a decimal value;", e);
            }
            valueToRender = formatForPlugin(coerceValueInRange(context, decimalToRender, inputNumber));
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
        String style = inputStyle;
        String styleClass = createStyleClass(inputNumber, InputNumber.PropertyKeys.inputStyleClass.name(), InputText.STYLE_CLASS) ;
        String inputMode = inputNumber.getInputmode();
        if (inputMode == null) {
            boolean isIntegral = isIntegral(context, inputNumber, inputNumber.getValue());
            String decimalPlaces = getDecimalPlaces(isIntegral, inputNumber);
            inputMode = "0".equals(decimalPlaces) ? "numeric" : "decimal";
            inputNumber.setInputmode(inputMode);
        }
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", inputNumber.getType(), null);
        writer.writeAttribute("autocomplete", LangUtils.defaultIfBlank(inputNumber.getAutocomplete(), "off"), null);
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
        String emptyValue = isValueBlank(inputNumber.getEmptyValue()) || "empty".equalsIgnoreCase(inputNumber.getEmptyValue())
                ? "null"
                : inputNumber.getEmptyValue();
        String digitGroupSeparator = isValueBlank(inputNumber.getThousandSeparator())
                ? Constants.EMPTY_STRING
                : inputNumber.getThousandSeparator();

        String decimalSeparator = isValueBlank(inputNumber.getDecimalSeparator())
                    ? "."
                    : inputNumber.getDecimalSeparator();

        boolean isIntegral = isIntegral(context, inputNumber, value);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(InputNumber.class.getSimpleName(), inputNumber);
        wb.attr("disabled", inputNumber.isDisabled())
            .attr("valueToRender", valueToRender)
            .attr("decimalCharacter", decimalSeparator, ".")
            .attr("decimalCharacterAlternative", inputNumber.getDecimalSeparatorAlternative(), null)
            .attr("decimalPlaces", getDecimalPlaces(isIntegral, inputNumber))
            .attr("decimalPlacesRawValue", inputNumber.getDecimalPlacesRawValue())
            .attr("digitGroupSeparator", digitGroupSeparator, ",")
            .attr("currencySymbol", inputNumber.getSymbol())
            .attr("currencySymbolPlacement", inputNumber.getSymbolPosition(), "p")
            .attr("negativePositiveSignPlacement", inputNumber.getSignPosition(), null)
            .attr("minimumValue", getMinimum(context, isIntegral, inputNumber))
            .attr("maximumValue", getMaximum(context, isIntegral, inputNumber))
            .attr("emptyInputBehavior", emptyValue, "focus")
            .attr("leadingZero", inputNumber.getLeadingZero(), "deny")
            .attr("allowDecimalPadding", inputNumber.getPadControl(), "true")
            .attr("modifyValueOnWheel", inputNumber.isModifyValueOnWheel(), true)
            .attr("modifyValueOnUpDownArrow", inputNumber.isModifyValueOnUpDownArrow(), true)
            .attr("roundingMethod", inputNumber.getRoundMethod(), "S")
            .attr("caretPositionOnFocus", inputNumber.getCaretPositionOnFocus(), null)
            .attr("selectOnFocus", inputNumber.isSelectOnFocus(), true)
            .attr("showWarnings", false, true);

        wb.finish();
    }

    @Override
    protected String getHighlighter() {
        return "inputnumber";
    }

    /**
     * Get the effective minimum Value (as interpreted in the AutoNumeric plugin)
     * @param context faces context
     * @param component the InputNumber component
     * @return the minimumValue property as BigDecimal, or the AutoNumeric default value if empty
     */
    private BigDecimal getEffectiveMinValue(FacesContext context, InputNumber component) {
        String minimumValue = component.getMinValue();
        if (minimumValue == null) {
            minimumValue = determineMinimumFromType(context, component);
        }
        if (minimumValue == null) {
            minimumValue = DEFAULT_MIN_VALUE;
        }
        try {
            return new BigDecimal(minimumValue);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error converting  [" + minimumValue + "] to a decimal value for minValue", e);
        }
    }

    /**
     * Get the effective maximum Value (as interpreted in the AutoNumeric plugin)
     * @param context faces context
     * @param component the InputNumber component
     * @return the maximumValue property as BigDecimal, or the AutoNumeric default value if empty
     */
    private BigDecimal getEffectiveMaxValue(FacesContext context, InputNumber component) {
        String maximumValue = component.getMaxValue();
        if (maximumValue == null) {
            maximumValue = determineMaximumFromType(context, component);
        }
        if (maximumValue == null) {
            maximumValue = DEFAULT_MAX_VALUE;
        }

        try {
            return new BigDecimal(maximumValue);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error converting  [" + maximumValue + "] to a decimal value for maxValue", e);
        }
    }

    /**
     * Coerce the provided value to the range defined by the effective minimum and maximum numbers.
     * @param value the value to render
     * @param component the component for which the minValue and maxValue properties define the range
     * @return the value if inside the range, or else the nearest boundary that is still inside the range
     */
    private BigDecimal coerceValueInRange(FacesContext context, BigDecimal value, InputNumber component) {
        return coerceValueInRange(value, getEffectiveMinValue(context, component), getEffectiveMaxValue(context, component));
    }

    /**
     * Coerce the provided value to the range defined by the effective minimum and maximum numbers.
     * @param value the value to render
     * @param effectiveMinValue the effective minimum value
     * @param effectiveMaxValue the effective maximum number
     * @return the value if inside the range, or else the nearest boundary that is still inside the range
     */
    private BigDecimal coerceValueInRange(BigDecimal value, BigDecimal effectiveMinValue, BigDecimal effectiveMaxValue) {
        if (value.compareTo(effectiveMinValue) < 0) {
            return effectiveMinValue;
        }
        if (value.compareTo(effectiveMaxValue) > 0) {
            return effectiveMaxValue;
        }
        return value;
    }

    private String formatForPlugin(String valueToRender) {
        if (valueToRender == null) {
            return null;
        }
        if (isValueBlank(valueToRender)) {
            return "";
        }
        else {
            try {
                BigDecimal objectToRender = new BigDecimal(valueToRender);
                return formatForPlugin(objectToRender);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error converting  [" + valueToRender + "] to a decimal value;", e);
            }
        }
    }

    /**
     * Format a BigDecimal value as value/minValue/maxValue for the AutoNumeric plugin.
     * @param valueToRender the value to render
     * @return BigDecimal value as plain decimal String, without any exponential notation
     */
    private String formatForPlugin(BigDecimal valueToRender) {
        return valueToRender.toPlainString();
    }

    /**
     * Determines the number of decimal places to use.
     * First sees if a value is set and return that.
     * Else, if this is an integer type number then default to 0
     * decimal places if none was declared else default to 2.
     * @param isIntegral flag whether the (expected) value is integral
     * @param inputNumber the component
     * @return the number of decimal places to use
     */
    private String getDecimalPlaces(boolean isIntegral, InputNumber inputNumber) {
        if (inputNumber.getDecimalPlaces() != null) {
            return inputNumber.getDecimalPlaces();
        }
        if (isIntegral) {
            return "0";
        }
        return "2";
    }

    private String determineMinimumFromType(FacesContext context, InputNumber component) {
        Object value = component.getValue();
        if (value != null) {
            if (value instanceof Long) {
                return Long.toString(Long.MIN_VALUE);
            }
            else if (value instanceof Integer) {
                return Integer.toString(Integer.MIN_VALUE);
            }
            else if (value instanceof Double) {
                return Double.toString(-Double.MAX_VALUE);
            }
            else if (value instanceof Short) {
                return Short.toString(Short.MIN_VALUE);
            }
            else if (value instanceof Byte) {
                return Byte.toString(Byte.MIN_VALUE);
            }
            else if (value instanceof Float) {
                return Float.toString(-Float.MAX_VALUE);
            }
        }

        Class<?> type = getTypeFromValueExpression(context, component);
        if (type != null) {
            if (type == Long.class || type == long.class) {
                return Long.toString(Long.MIN_VALUE);
            }
            else if (type == Integer.class || type == int.class) {
                return Integer.toString(Integer.MIN_VALUE);
            }
            else if (type == Double.class || type == double.class) {
                return Double.toString(-Double.MAX_VALUE);
            }
            else if (type == Short.class || type == short.class) {
                return Short.toString(Short.MIN_VALUE);
            }
            else if (type == Byte.class || type == byte.class) {
                return Byte.toString(Byte.MIN_VALUE);
            }
            else if (type == Float.class || type == float.class) {
                return Float.toString(-Float.MAX_VALUE);
            }
        }

        return null;
    }

    /**
     * If using @Positive annotation and this is an Integer default to 0 instead of 0.0001.
     * @param context faces context
     * @param isIntegral flag whether the (expected) value is integral
     * @param component the component
     * @return the minimum value of the component
     */
    private String getMinimum(FacesContext context, boolean isIntegral, InputNumber component) {
        String minimum = component.getMinValue();
        if (isIntegral && PositiveClientValidationConstraint.MIN_VALUE.equals(minimum)) {
            minimum = "0";
        }

        if (minimum == null) {
            minimum = determineMinimumFromType(context, component);
        }

        return formatForPlugin(minimum);
    }

    private String determineMaximumFromType(FacesContext context, InputNumber component) {
        Object value = component.getValue();
        if (value != null) {
            if (value instanceof Long) {
                return Long.toString(Long.MAX_VALUE);
            }
            else if (value instanceof Integer) {
                return Integer.toString(Integer.MAX_VALUE);
            }
            else if (value instanceof Double) {
                return Double.toString(Double.MAX_VALUE);
            }
            else if (value instanceof Short) {
                return Short.toString(Short.MAX_VALUE);
            }
            else if (value instanceof Byte) {
                return Byte.toString(Byte.MAX_VALUE);
            }
            else if (value instanceof Float) {
                return Float.toString(Float.MAX_VALUE);
            }
        }

        Class<?> type = getTypeFromValueExpression(context, component);
        if (type != null) {
            if (type == Long.class || type == long.class) {
                return Long.toString(Long.MAX_VALUE);
            }
            else if (type == Integer.class || type == int.class) {
                return Integer.toString(Integer.MAX_VALUE);
            }
            else if (type == Double.class || type == double.class) {
                return Double.toString(Double.MAX_VALUE);
            }
            else if (type == Short.class || type == short.class) {
                return Short.toString(Short.MAX_VALUE);
            }
            else if (type == Byte.class || type == byte.class) {
                return Byte.toString(Byte.MAX_VALUE);
            }
            else if (type == Float.class || type == float.class) {
                return Float.toString(Float.MAX_VALUE);
            }
        }

        return null;
    }

    /**
     * If using @Negative annotation and this is an Integer default to 0 instead of -0.0001.
     * @param context faces context
     * @param isIntegral flag whether the (expected) value is integral
     * @param component the component
     * @return the maximum value of the component
     */
    private String getMaximum(FacesContext context, boolean isIntegral, InputNumber component) {
        String maximum = component.getMaxValue();
        if (isIntegral && NegativeClientValidationConstraint.MAX_VALUE.equals(maximum)) {
            maximum = "0";
        }

        if (maximum == null) {
            maximum = determineMaximumFromType(context, component);
        }

        return formatForPlugin(maximum);
    }

    protected boolean isIntegral(FacesContext context, InputNumber inputNumber, Object value) {
        if (value != null) {
            return value instanceof Long
                    || value instanceof Integer
                    || value instanceof Short
                    || value instanceof BigInteger
                    || value instanceof Byte;
        }

        Class<?> type = getTypeFromValueExpression(context, inputNumber);

        // #11791 Number is the superclass for all the types so it could be a decimal
        if (type == null || type == Number.class) {
            return false;
        }

        return type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Short.class)
                || type.isAssignableFrom(BigInteger.class)
                || type.isAssignableFrom(Byte.class);
    }

    public Class<?> getTypeFromValueExpression(FacesContext context, InputNumber inputNumber) {
        ValueExpression valueExpression = inputNumber.getValueExpression("value");
        if (valueExpression == null) {
            return null;
        }

        try {
            return valueExpression.getType(context.getELContext());
        }
        catch (PropertyNotFoundException e) {
            return null;
        }
    }
}
