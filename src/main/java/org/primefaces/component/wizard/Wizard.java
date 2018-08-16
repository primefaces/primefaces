/*
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
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import java.util.Map;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="components.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="core.js"),
	@ResourceDependency(library="primefaces", name="components.js")
})
public class Wizard extends WizardBase implements org.primefaces.component.api.Widget {



    public final static String STEP_STATUS_CLASS = "ui-wizard-step-titles ui-helper-reset ui-helper-clearfix";
	public final static String STEP_CLASS = "ui-wizard-step-title ui-state-default ui-corner-all";
    public final static String ACTIVE_STEP_CLASS = "ui-wizard-step-title ui-state-default ui-state-highlight ui-corner-all";
	public final static String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
	public final static String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";
	
	private Tab current;

	public void processDecodes(FacesContext context) {
        this.decode(context);

		if(!isBackRequest(context) || (this.isUpdateModelOnPrev() && isBackRequest(context))) {
			getStepToProcess().processDecodes(context);
		}
    }
	
	public void processValidators(FacesContext context) {
        if(!isBackRequest(context) || (this.isUpdateModelOnPrev() && isBackRequest(context))) {
			current.processValidators(context);
		}
    }
	
	public void processUpdates(FacesContext context) {
		if(!isBackRequest(context) || (this.isUpdateModelOnPrev() && isBackRequest(context))) {
			current.processUpdates(context);
		}
	}
	
	public Tab getStepToProcess() {
		if(current == null) {
			String currentStepId = getStep();
			
			for(UIComponent child : getChildren()) {
				if(child.getId().equals(currentStepId)) {
					current = (Tab) child;
					
					break;
				}
			}
		}
		
		return current;
	}
	
	public boolean isWizardRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_wizardRequest");
	}
	
	public boolean isBackRequest(FacesContext context) {
		return isWizardRequest(context) && context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_backRequest");
	}

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if(event instanceof FlowEvent) {
            FlowEvent flowEvent = (FlowEvent) event;
            FacesContext context = getFacesContext();
            MethodExpression me = this.getFlowListener();

            if(me != null) {
                String step = (String) me.invoke(context.getELContext(), new Object[]{event});

                this.setStep(step);
            }
            else {
                this.setStep(flowEvent.getNewStep());
            }
        }
    }
}