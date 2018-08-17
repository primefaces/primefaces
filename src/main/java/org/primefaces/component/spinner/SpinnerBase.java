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
package org.primefaces.component.spinner;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.util.ComponentUtils;


abstract class SpinnerBase extends HtmlInputText implements org.primefaces.component.api.Widget, org.primefaces.component.api.InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SpinnerRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        stepFactor,
        min,
        max,
        prefix,
        suffix,
        decimalPlaces
    }

    public SpinnerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
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

    public double getStepFactor() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.stepFactor, 1.0);
    }

    public void setStepFactor(double _stepFactor) {
        getStateHelper().put(PropertyKeys.stepFactor, _stepFactor);
    }

    public double getMin() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.min, java.lang.Double.MIN_VALUE);
    }

    public void setMin(double _min) {
        getStateHelper().put(PropertyKeys.min, _min);
    }

    public double getMax() {
        return (java.lang.Double) getStateHelper().eval(PropertyKeys.max, java.lang.Double.MAX_VALUE);
    }

    public void setMax(double _max) {
        getStateHelper().put(PropertyKeys.max, _max);
    }

    public java.lang.String getPrefix() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.prefix, null);
    }

    public void setPrefix(java.lang.String _prefix) {
        getStateHelper().put(PropertyKeys.prefix, _prefix);
    }

    public java.lang.String getSuffix() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.suffix, null);
    }

    public void setSuffix(java.lang.String _suffix) {
        getStateHelper().put(PropertyKeys.suffix, _suffix);
    }

    public java.lang.String getDecimalPlaces() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.decimalPlaces, null);
    }

    public void setDecimalPlaces(java.lang.String _decimalPlaces) {
        getStateHelper().put(PropertyKeys.decimalPlaces, _decimalPlaces);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}