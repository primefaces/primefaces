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
package org.primefaces.component.wizard;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class WizardBase extends UIComponentBase implements Widget {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}