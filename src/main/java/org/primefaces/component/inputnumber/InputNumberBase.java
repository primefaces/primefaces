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
        leadingZero;
    }

    public InputNumberBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getPlaceholder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(java.lang.String _placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, _placeholder);
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getType() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "text");
    }

    public void setType(java.lang.String _type) {
        getStateHelper().put(PropertyKeys.type, _type);
    }

    public java.lang.String getSymbol() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.symbol, null);
    }

    public void setSymbol(java.lang.String _symbol) {
        getStateHelper().put(PropertyKeys.symbol, _symbol);
    }

    public java.lang.String getSymbolPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.symbolPosition, null);
    }

    public void setSymbolPosition(java.lang.String _symbolPosition) {
        getStateHelper().put(PropertyKeys.symbolPosition, _symbolPosition);
    }

    public java.lang.String getMinValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.minValue, null);
    }

    public void setMinValue(java.lang.String _minValue) {
        getStateHelper().put(PropertyKeys.minValue, _minValue);
    }

    public java.lang.String getMaxValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.maxValue, null);
    }

    public void setMaxValue(java.lang.String _maxValue) {
        getStateHelper().put(PropertyKeys.maxValue, _maxValue);
    }

    public java.lang.String getRoundMethod() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.roundMethod, null);
    }

    public void setRoundMethod(java.lang.String _roundMethod) {
        getStateHelper().put(PropertyKeys.roundMethod, _roundMethod);
    }

    public java.lang.String getDecimalPlaces() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.decimalPlaces, null);
    }

    public void setDecimalPlaces(java.lang.String _decimalPlaces) {
        getStateHelper().put(PropertyKeys.decimalPlaces, _decimalPlaces);
    }

    public java.lang.String getEmptyValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyValue, "empty");
    }

    public void setEmptyValue(java.lang.String _emptyValue) {
        getStateHelper().put(PropertyKeys.emptyValue, _emptyValue);
    }

    public java.lang.String getInputStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyle, null);
    }

    public void setInputStyle(java.lang.String _inputStyle) {
        getStateHelper().put(PropertyKeys.inputStyle, _inputStyle);
    }

    public java.lang.String getInputStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
    }

    public void setInputStyleClass(java.lang.String _inputStyleClass) {
        getStateHelper().put(PropertyKeys.inputStyleClass, _inputStyleClass);
    }

    public boolean isPadControl() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.padControl, true);
    }

    public void setPadControl(boolean _padControl) {
        getStateHelper().put(PropertyKeys.padControl, _padControl);
    }

    public java.lang.String getLeadingZero() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.leadingZero, "allow");
    }

    public void setLeadingZero(java.lang.String _leadingZero) {
        getStateHelper().put(PropertyKeys.leadingZero, _leadingZero);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}