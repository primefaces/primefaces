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