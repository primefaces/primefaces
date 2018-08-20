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
package org.primefaces.component.slider;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class SliderBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}