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
package org.primefaces.component.knob;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class KnobBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.KnobRenderer";

    public enum PropertyKeys {

        widgetVar,
        min,
        max,
        step,
        thickness,
        width,
        height,
        foregroundColor,
        backgroundColor,
        colorTheme,
        disabled,
        showLabel,
        cursor,
        labelTemplate,
        onchange,
        lineCap,
        styleClass
    }

    public KnobBase() {
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

    public int getMin() {
        return (Integer) getStateHelper().eval(PropertyKeys.min, 0);
    }

    public void setMin(int min) {
        getStateHelper().put(PropertyKeys.min, min);
    }

    public int getMax() {
        return (Integer) getStateHelper().eval(PropertyKeys.max, 100);
    }

    public void setMax(int max) {
        getStateHelper().put(PropertyKeys.max, max);
    }

    public int getStep() {
        return (Integer) getStateHelper().eval(PropertyKeys.step, 1);
    }

    public void setStep(int step) {
        getStateHelper().put(PropertyKeys.step, step);
    }

    public Float getThickness() {
        return (Float) getStateHelper().eval(PropertyKeys.thickness, null);
    }

    public void setThickness(Float thickness) {
        getStateHelper().put(PropertyKeys.thickness, thickness);
    }

    public Object getWidth() {
        return getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(Object width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public Object getHeight() {
        return getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(Object height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public Object getForegroundColor() {
        return getStateHelper().eval(PropertyKeys.foregroundColor, null);
    }

    public void setForegroundColor(Object foregroundColor) {
        getStateHelper().put(PropertyKeys.foregroundColor, foregroundColor);
    }

    public Object getBackgroundColor() {
        return getStateHelper().eval(PropertyKeys.backgroundColor, null);
    }

    public void setBackgroundColor(Object backgroundColor) {
        getStateHelper().put(PropertyKeys.backgroundColor, backgroundColor);
    }

    public String getColorTheme() {
        return (String) getStateHelper().eval(PropertyKeys.colorTheme, null);
    }

    public void setColorTheme(String colorTheme) {
        getStateHelper().put(PropertyKeys.colorTheme, colorTheme);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public boolean isShowLabel() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showLabel, true);
    }

    public void setShowLabel(boolean showLabel) {
        getStateHelper().put(PropertyKeys.showLabel, showLabel);
    }

    public boolean isCursor() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cursor, false);
    }

    public void setCursor(boolean cursor) {
        getStateHelper().put(PropertyKeys.cursor, cursor);
    }

    public String getLabelTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.labelTemplate, "{value}");
    }

    public void setLabelTemplate(String labelTemplate) {
        getStateHelper().put(PropertyKeys.labelTemplate, labelTemplate);
    }

    public String getOnchange() {
        return (String) getStateHelper().eval(PropertyKeys.onchange, null);
    }

    public void setOnchange(String onchange) {
        getStateHelper().put(PropertyKeys.onchange, onchange);
    }

    public String getLineCap() {
        return (String) getStateHelper().eval(PropertyKeys.lineCap, "butt");
    }

    public void setLineCap(String lineCap) {
        getStateHelper().put(PropertyKeys.lineCap, lineCap);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}