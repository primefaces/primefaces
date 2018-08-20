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

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SpinnerBase extends HtmlInputText implements Widget, InputHolder {

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

    public double getStepFactor() {
        return (Double) getStateHelper().eval(PropertyKeys.stepFactor, 1.0);
    }

    public void setStepFactor(double stepFactor) {
        getStateHelper().put(PropertyKeys.stepFactor, stepFactor);
    }

    public double getMin() {
        return (Double) getStateHelper().eval(PropertyKeys.min, Double.MIN_VALUE);
    }

    public void setMin(double min) {
        getStateHelper().put(PropertyKeys.min, min);
    }

    public double getMax() {
        return (Double) getStateHelper().eval(PropertyKeys.max, Double.MAX_VALUE);
    }

    public void setMax(double max) {
        getStateHelper().put(PropertyKeys.max, max);
    }

    public String getPrefix() {
        return (String) getStateHelper().eval(PropertyKeys.prefix, null);
    }

    public void setPrefix(String prefix) {
        getStateHelper().put(PropertyKeys.prefix, prefix);
    }

    public String getSuffix() {
        return (String) getStateHelper().eval(PropertyKeys.suffix, null);
    }

    public void setSuffix(String suffix) {
        getStateHelper().put(PropertyKeys.suffix, suffix);
    }

    public String getDecimalPlaces() {
        return (String) getStateHelper().eval(PropertyKeys.decimalPlaces, null);
    }

    public void setDecimalPlaces(String decimalPlaces) {
        getStateHelper().put(PropertyKeys.decimalPlaces, decimalPlaces);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}