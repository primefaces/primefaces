/*
 * Generated, Do Not Modify
 */
/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.Map;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Wizard extends UIComponentBase implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Wizard";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.WizardRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,step
		,style
		,styleClass
		,flowListener
		,showNavBar
		,showStepStatus
		,onback
		,onnext
		,nextLabel
		,backLabel;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Wizard() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public java.lang.String getStep() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.step, null);
	}
	public void setStep(java.lang.String _step) {
		getStateHelper().put(PropertyKeys.step, _step);
		handleAttribute("step", _step);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}

	public javax.el.MethodExpression getFlowListener() {
		return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.flowListener, null);
	}
	public void setFlowListener(javax.el.MethodExpression _flowListener) {
		getStateHelper().put(PropertyKeys.flowListener, _flowListener);
		handleAttribute("flowListener", _flowListener);
	}

	public boolean isShowNavBar() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showNavBar, true);
	}
	public void setShowNavBar(boolean _showNavBar) {
		getStateHelper().put(PropertyKeys.showNavBar, _showNavBar);
		handleAttribute("showNavBar", _showNavBar);
	}

	public boolean isShowStepStatus() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showStepStatus, true);
	}
	public void setShowStepStatus(boolean _showStepStatus) {
		getStateHelper().put(PropertyKeys.showStepStatus, _showStepStatus);
		handleAttribute("showStepStatus", _showStepStatus);
	}

	public java.lang.String getOnback() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onback, null);
	}
	public void setOnback(java.lang.String _onback) {
		getStateHelper().put(PropertyKeys.onback, _onback);
		handleAttribute("onback", _onback);
	}

	public java.lang.String getOnnext() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onnext, null);
	}
	public void setOnnext(java.lang.String _onnext) {
		getStateHelper().put(PropertyKeys.onnext, _onnext);
		handleAttribute("onnext", _onnext);
	}

	public java.lang.String getNextLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.nextLabel, "Next");
	}
	public void setNextLabel(java.lang.String _nextLabel) {
		getStateHelper().put(PropertyKeys.nextLabel, _nextLabel);
		handleAttribute("nextLabel", _nextLabel);
	}

	public java.lang.String getBackLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.backLabel, "Back");
	}
	public void setBackLabel(java.lang.String _backLabel) {
		getStateHelper().put(PropertyKeys.backLabel, _backLabel);
		handleAttribute("backLabel", _backLabel);
	}


    public final static String STEP_STATUS_CLASS = "ui-wizard-step-titles ui-helper-reset ui-helper-clearfix";
	public final static String STEP_CLASS = "ui-wizard-step-title ui-state-default ui-corner-all";
    public final static String ACTIVE_STEP_CLASS = "ui-wizard-step-title ui-state-default ui-state-highlight ui-corner-all";
	public final static String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
	public final static String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";
	
	private Tab current;

	public void processDecodes(FacesContext context) {
        this.decode(context);

		if(!isBackRequest(context)) {
			getStepToProcess().processDecodes(context);
		}
    }
	
	public void processValidators(FacesContext context) {
        if(!isBackRequest(context)) {
			current.processValidators(context);
		}
    }
	
	public void processUpdates(FacesContext context) {
		if(!isBackRequest(context)) {
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
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = this.getClientId(context);
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

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
	}

	public void handleAttribute(String name, Object value) {
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}