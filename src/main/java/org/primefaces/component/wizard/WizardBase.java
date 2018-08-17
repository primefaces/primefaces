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

import org.primefaces.util.ComponentUtils;


abstract class WizardBase extends UIComponentBase implements org.primefaces.component.api.Widget {

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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getStep() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.step, null);
    }

    public void setStep(java.lang.String _step) {
        getStateHelper().put(PropertyKeys.step, _step);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public javax.el.MethodExpression getFlowListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.flowListener, null);
    }

    public void setFlowListener(javax.el.MethodExpression _flowListener) {
        getStateHelper().put(PropertyKeys.flowListener, _flowListener);
    }

    public boolean isShowNavBar() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showNavBar, true);
    }

    public void setShowNavBar(boolean _showNavBar) {
        getStateHelper().put(PropertyKeys.showNavBar, _showNavBar);
    }

    public boolean isShowStepStatus() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showStepStatus, true);
    }

    public void setShowStepStatus(boolean _showStepStatus) {
        getStateHelper().put(PropertyKeys.showStepStatus, _showStepStatus);
    }

    public java.lang.String getOnback() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onback, null);
    }

    public void setOnback(java.lang.String _onback) {
        getStateHelper().put(PropertyKeys.onback, _onback);
    }

    public java.lang.String getOnnext() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onnext, null);
    }

    public void setOnnext(java.lang.String _onnext) {
        getStateHelper().put(PropertyKeys.onnext, _onnext);
    }

    public java.lang.String getNextLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.nextLabel, "Next");
    }

    public void setNextLabel(java.lang.String _nextLabel) {
        getStateHelper().put(PropertyKeys.nextLabel, _nextLabel);
    }

    public java.lang.String getBackLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.backLabel, "Back");
    }

    public void setBackLabel(java.lang.String _backLabel) {
        getStateHelper().put(PropertyKeys.backLabel, _backLabel);
    }

    public boolean isUpdateModelOnPrev() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.updateModelOnPrev, false);
    }

    public void setUpdateModelOnPrev(boolean _updateModelOnPrev) {
        getStateHelper().put(PropertyKeys.updateModelOnPrev, _updateModelOnPrev);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}