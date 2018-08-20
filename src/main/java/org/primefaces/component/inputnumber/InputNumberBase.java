/**
 * Copyright 2009-2018 PrimeTek.
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

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class InputNumberBase extends HtmlInputText implements Widget, InputHolder {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}