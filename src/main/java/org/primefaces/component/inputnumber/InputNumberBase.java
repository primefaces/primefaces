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

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LocaleUtils;

public abstract class InputNumberBase extends HtmlInputText implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputNumberRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        type,
        symbol,
        symbolPosition,
        minValue,
        maxValue,
        roundMethod,
        decimalPlaces,
        decimalSeparator,
        thousandSeparator,
        emptyValue,
        inputStyle,
        inputStyleClass,
        padControl,
        leadingZero
    }

    public InputNumberBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, "text");
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getSymbol() {
        return (String) getStateHelper().eval(PropertyKeys.symbol, null);
    }

    public void setSymbol(String symbol) {
        getStateHelper().put(PropertyKeys.symbol, symbol);
    }

    public String getSymbolPosition() {
        return (String) getStateHelper().eval(PropertyKeys.symbolPosition, null);
    }

    public void setSymbolPosition(String symbolPosition) {
        getStateHelper().put(PropertyKeys.symbolPosition, symbolPosition);
    }

    public String getMinValue() {
        return (String) getStateHelper().eval(PropertyKeys.minValue, null);
    }

    public void setMinValue(String minValue) {
        getStateHelper().put(PropertyKeys.minValue, minValue);
    }

    public String getMaxValue() {
        return (String) getStateHelper().eval(PropertyKeys.maxValue, null);
    }

    public void setMaxValue(String maxValue) {
        getStateHelper().put(PropertyKeys.maxValue, maxValue);
    }

    public String getRoundMethod() {
        return (String) getStateHelper().eval(PropertyKeys.roundMethod, null);
    }

    public void setRoundMethod(String roundMethod) {
        getStateHelper().put(PropertyKeys.roundMethod, roundMethod);
    }

    public String getDecimalPlaces() {
        return (String) getStateHelper().eval(PropertyKeys.decimalPlaces, null);
    }

    public void setDecimalPlaces(String decimalPlaces) {
        getStateHelper().put(PropertyKeys.decimalPlaces, decimalPlaces);
    }

    public String getDecimalSeparator() {
        return ComponentUtils.eval(getStateHelper(), PropertyKeys.decimalSeparator,
            () -> LocaleUtils.getDecimalSeparator(getFacesContext()));
    }

    public void setDecimalSeparator(final String decimalSeparator) {
        getStateHelper().put(PropertyKeys.decimalSeparator, decimalSeparator);
    }

    public String getThousandSeparator() {
        return ComponentUtils.eval(getStateHelper(), PropertyKeys.thousandSeparator,
            () -> LocaleUtils.getThousandSeparator(getFacesContext()));
    }

    public void setThousandSeparator(final String thousandSeparator) {
        getStateHelper().put(PropertyKeys.thousandSeparator, thousandSeparator);
    }

    public String getEmptyValue() {
        return (String) getStateHelper().eval(PropertyKeys.emptyValue, "empty");
    }

    public void setEmptyValue(String emptyValue) {
        getStateHelper().put(PropertyKeys.emptyValue, emptyValue);
    }

    public String getInputStyle() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyle, null);
    }

    public void setInputStyle(String inputStyle) {
        getStateHelper().put(PropertyKeys.inputStyle, inputStyle);
    }

    public String getInputStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
    }

    public void setInputStyleClass(String inputStyleClass) {
        getStateHelper().put(PropertyKeys.inputStyleClass, inputStyleClass);
    }

    public boolean isPadControl() {
        return (Boolean) getStateHelper().eval(PropertyKeys.padControl, true);
    }

    public void setPadControl(boolean padControl) {
        getStateHelper().put(PropertyKeys.padControl, padControl);
    }

    public String getLeadingZero() {
        return (String) getStateHelper().eval(PropertyKeys.leadingZero, "allow");
    }

    public void setLeadingZero(String leadingZero) {
        getStateHelper().put(PropertyKeys.leadingZero, leadingZero);
    }
}