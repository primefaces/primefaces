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
package org.primefaces.component.slider;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class SliderBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SliderRenderer";

    public enum PropertyKeys {

        widgetVar,
        forValue("for"),
        display,
        minValue,
        maxValue,
        style,
        styleClass,
        animate,
        type,
        step,
        disabled,
        onSlideStart,
        onSlide,
        onSlideEnd,
        range,
        displayTemplate;

        private String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public SliderBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public String getDisplay() {
        return (String) getStateHelper().eval(PropertyKeys.display, null);
    }

    public void setDisplay(String display) {
        getStateHelper().put(PropertyKeys.display, display);
    }

    public double getMinValue() {
        return (Double) getStateHelper().eval(PropertyKeys.minValue, 0.0);
    }

    public void setMinValue(double minValue) {
        getStateHelper().put(PropertyKeys.minValue, minValue);
    }

    public double getMaxValue() {
        return (Double) getStateHelper().eval(PropertyKeys.maxValue, 100.0);
    }

    public void setMaxValue(double maxValue) {
        getStateHelper().put(PropertyKeys.maxValue, maxValue);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, "horizontal");
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public double getStep() {
        return (Double) getStateHelper().eval(PropertyKeys.step, 1.0);
    }

    public void setStep(double step) {
        getStateHelper().put(PropertyKeys.step, step);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getOnSlideStart() {
        return (String) getStateHelper().eval(PropertyKeys.onSlideStart, null);
    }

    public void setOnSlideStart(String onSlideStart) {
        getStateHelper().put(PropertyKeys.onSlideStart, onSlideStart);
    }

    public String getOnSlide() {
        return (String) getStateHelper().eval(PropertyKeys.onSlide, null);
    }

    public void setOnSlide(String onSlide) {
        getStateHelper().put(PropertyKeys.onSlide, onSlide);
    }

    public String getOnSlideEnd() {
        return (String) getStateHelper().eval(PropertyKeys.onSlideEnd, null);
    }

    public void setOnSlideEnd(String onSlideEnd) {
        getStateHelper().put(PropertyKeys.onSlideEnd, onSlideEnd);
    }

    public boolean isRange() {
        return (Boolean) getStateHelper().eval(PropertyKeys.range, false);
    }

    public void setRange(boolean range) {
        getStateHelper().put(PropertyKeys.range, range);
    }

    public String getDisplayTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.displayTemplate, null);
    }

    public void setDisplayTemplate(String displayTemplate) {
        getStateHelper().put(PropertyKeys.displayTemplate, displayTemplate);
    }
}