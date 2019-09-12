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
package org.primefaces.component.progressbar;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class ProgressBarBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ProgressBarRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        disabled,
        ajax,
        interval,
        style,
        styleClass,
        labelTemplate,
        displayOnly,
        global,
        mode,
        animationDuration
    }

    public ProgressBarBase() {
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

    public int getValue() {
        return (Integer) getStateHelper().eval(PropertyKeys.value, 0);
    }

    public void setValue(int value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public boolean isAjax() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ajax, false);
    }

    public void setAjax(boolean ajax) {
        getStateHelper().put(PropertyKeys.ajax, ajax);
    }

    public int getInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.interval, 3000);
    }

    public void setInterval(int interval) {
        getStateHelper().put(PropertyKeys.interval, interval);
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

    public String getLabelTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.labelTemplate, null);
    }

    public void setLabelTemplate(String labelTemplate) {
        getStateHelper().put(PropertyKeys.labelTemplate, labelTemplate);
    }

    public boolean isDisplayOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.displayOnly, false);
    }

    public void setDisplayOnly(boolean displayOnly) {
        getStateHelper().put(PropertyKeys.displayOnly, displayOnly);
    }

    public boolean isGlobal() {
        return (Boolean) getStateHelper().eval(PropertyKeys.global, true);
    }

    public void setGlobal(boolean global) {
        getStateHelper().put(PropertyKeys.global, global);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "determinate");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public int getAnimationDuration() {
        return (Integer) getStateHelper().eval(PropertyKeys.animationDuration, 500);
    }

    public void setAnimationDuration(int animationDuration) {
        getStateHelper().put(PropertyKeys.animationDuration, animationDuration);
    }
}