/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.component.tabview.Tab;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.FlowEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Wizard.COMPONENT_TYPE, namespace = Wizard.COMPONENT_FAMILY)
@FacesComponentDescription("Wizard provides an enhanced UI to implement a workflow easily in a single page. "
        + "Wizard consists of several child tab components where each tab represents a step in the process.")
@FacesComponentHandler(WizardHandler.class)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Wizard extends WizardBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Wizard";

    public static final String STEP_STATUS_CLASS = "ui-wizard-step-titles ui-helper-reset ui-helper-clearfix";
    public static final String STEP_CLASS = "ui-wizard-step-title ui-state-default";
    public static final String ACTIVE_STEP_CLASS = "ui-wizard-step-title ui-state-default ui-state-highlight";
    public static final String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
    public static final String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";

    @Override
    public void processDecodes(FacesContext context) {
        decode(context);

        if (!isBackRequest(context) || (isUpdateModelOnPrev() && isBackRequest(context))) {
            Tab step = getStepToProcess();
            if (step != null) {
                step.processDecodes(context);
            }
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        if (!isBackRequest(context) || (isUpdateModelOnPrev() && isBackRequest(context))) {
            Tab step = getStepToProcess();
            if (step != null) {
                step.processValidators(context);
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        if (!isBackRequest(context) || (isUpdateModelOnPrev() && isBackRequest(context))) {
            Tab step = getStepToProcess();
            if (step != null) {
                step.processUpdates(context);
            }
        }

        ELContext elContext = getFacesContext().getELContext();
        ValueExpression expr = ValueExpressionAnalyzer.getExpression(elContext,
                getValueExpression(PropertyKeys.step.toString()), true);
        if (expr != null && !expr.isReadOnly(elContext)) {
            expr.setValue(elContext, getStep());
            resetStep();
        }
    }

    public Tab getStepToProcess() {
        String currentStepId = getStep();
        if (LangUtils.isBlank(currentStepId)) {
            return null;
        }

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.getId().equals(currentStepId)) {
                return (Tab) child;
            }
        }

        return null;
    }

    public boolean isBackRequest(FacesContext context) {
        return ComponentUtils.isRequestSource(this, context) && "back".equals(getRequestDirection(context));
    }

    public String getRequestDirection(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().get(getClientId(context) + "_direction");
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof FlowEvent) {
            FlowEvent flowEvent = (FlowEvent) event;
            FacesContext context = getFacesContext();
            MethodExpression me = getFlowListener();

            if (me != null) {
                String step = (String) me.invoke(context.getELContext(), new Object[]{event});

                setStep(step);
            }
            else {
                setStep(flowEvent.getNewStep());
            }
        }
    }

    protected void resetStep() {
        getStateHelper().remove(PropertyKeys.step);
    }
}