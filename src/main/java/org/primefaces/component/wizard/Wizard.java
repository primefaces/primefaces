/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.Collection;
import java.util.Map;
import javax.el.ELContext;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.component.tabview.Tab;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.FlowEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Wizard extends WizardBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Wizard";

    public static final String STEP_STATUS_CLASS = "ui-wizard-step-titles ui-helper-reset ui-helper-clearfix";
    public static final String STEP_CLASS = "ui-wizard-step-title ui-state-default ui-corner-all";
    public static final String ACTIVE_STEP_CLASS = "ui-wizard-step-title ui-state-default ui-state-highlight ui-corner-all";
    public static final String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
    public static final String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("next", null)
            .put("back", null)
            .build();

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

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
                getValueExpression(PropertyKeys.step.toString()));
        if (expr != null && !expr.isReadOnly(elContext)) {
            expr.setValue(elContext, getStep());
            resetStep();
        }
    }

    public Tab getStepToProcess() {
        String currentStepId = getStep();
        if (LangUtils.isValueBlank(currentStepId)) {
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