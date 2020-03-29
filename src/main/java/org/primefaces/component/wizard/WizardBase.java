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
package org.primefaces.component.wizard;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.PrimeClientBehaviorHolder;

import org.primefaces.component.api.Widget;

public abstract class WizardBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.WizardRenderer";

    public enum PropertyKeys {

        widgetVar,
        step,
        style,
        styleClass,
        flowListener,
        showNavBar,
        showStepStatus,
        onback,
        onnext,
        nextLabel,
        backLabel,
        updateModelOnPrev
    }

    public WizardBase() {
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

    public String getStep() {
        return (String) getStateHelper().eval(PropertyKeys.step, null);
    }

    public void setStep(String step) {
        getStateHelper().put(PropertyKeys.step, step);
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

    public javax.el.MethodExpression getFlowListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.flowListener, null);
    }

    public void setFlowListener(javax.el.MethodExpression flowListener) {
        getStateHelper().put(PropertyKeys.flowListener, flowListener);
    }

    public boolean isShowNavBar() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showNavBar, true);
    }

    public void setShowNavBar(boolean showNavBar) {
        getStateHelper().put(PropertyKeys.showNavBar, showNavBar);
    }

    public boolean isShowStepStatus() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showStepStatus, true);
    }

    public void setShowStepStatus(boolean showStepStatus) {
        getStateHelper().put(PropertyKeys.showStepStatus, showStepStatus);
    }

    public String getOnback() {
        return (String) getStateHelper().eval(PropertyKeys.onback, null);
    }

    public void setOnback(String onback) {
        getStateHelper().put(PropertyKeys.onback, onback);
    }

    public String getOnnext() {
        return (String) getStateHelper().eval(PropertyKeys.onnext, null);
    }

    public void setOnnext(String onnext) {
        getStateHelper().put(PropertyKeys.onnext, onnext);
    }

    public String getNextLabel() {
        return (String) getStateHelper().eval(PropertyKeys.nextLabel, "Next");
    }

    public void setNextLabel(String nextLabel) {
        getStateHelper().put(PropertyKeys.nextLabel, nextLabel);
    }

    public String getBackLabel() {
        return (String) getStateHelper().eval(PropertyKeys.backLabel, "Back");
    }

    public void setBackLabel(String backLabel) {
        getStateHelper().put(PropertyKeys.backLabel, backLabel);
    }

    public boolean isUpdateModelOnPrev() {
        return (Boolean) getStateHelper().eval(PropertyKeys.updateModelOnPrev, false);
    }

    public void setUpdateModelOnPrev(boolean updateModelOnPrev) {
        getStateHelper().put(PropertyKeys.updateModelOnPrev, updateModelOnPrev);
    }
}