/*
 * Copyright 2010 Prime Technology.
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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;

public class WizardRenderer extends CoreRenderer implements PartialRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Wizard wizard = (Wizard) component;
			
		encodeMarkup(facesContext, wizard);
		encodeScript(facesContext, wizard);
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		Wizard wizard = (Wizard) component;
		String clientId = wizard.getClientId(facesContext);
		String stepToDisplay = null;
		boolean success = isStepValid(facesContext, wizard);
		
		String currentStep = params.get(clientId + "_currentStep");
		String newStep = params.get(clientId + "_stepToGo");

		/**
		 * Decide which step do display
		 */
		if(success) {
			if(wizard.getFlowListener() != null) {
				FlowEvent flowEvent = new FlowEvent(wizard, currentStep, newStep);
				Object outcome = wizard.getFlowListener().invoke(facesContext.getELContext(), new Object[]{flowEvent});
				stepToDisplay = (String) outcome;
			} else {
				stepToDisplay = newStep;
			}
		} else {
			stepToDisplay = currentStep;
		}

		wizard.setStep(stepToDisplay);
		
		UIComponent tabToDisplay = null;
		for(UIComponent child : wizard.getChildren()) {
			if(child.getId().equals(stepToDisplay))
				tabToDisplay = child;
		}
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<?xml version=\"1.0\" encoding=\"" + response.getCharacterEncoding() + "\"?>");
		writer.write("<wizard-response>");
		
			writer.write("<content>");
			writer.startCDATA();
				renderChild(facesContext, tabToDisplay);
			writer.endCDATA();
			writer.write("</content>");
			
			writer.write("<success>");
			writer.write(String.valueOf(success));
			writer.write("</success>");
			
			writer.write("<current-step>");
			writer.write(stepToDisplay);
			writer.write("</current-step>");
			
			writer.write("<state>");
			writer.startCDATA();
			StateManager stateManager = facesContext.getApplication().getStateManager();
			stateManager.writeState(facesContext, stateManager.saveView(facesContext));
			writer.endCDATA();
			writer.write("</state>");
			
		writer.write("</wizard-response>");
	}

	private void encodeScript(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, wizard);
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, wizard);
		if(form == null) {
			throw new FacesException("Wizard : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(var + " = new PrimeFaces.widget.Wizard('" + clientId + "',{");
		writer.write("formId:'" + form.getClientId(facesContext) + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		
		if(wizard.getOnback() != null) writer.write(",onback:" + wizard.getOnback());
		if(wizard.getOnnext() != null) writer.write(",onnext:" + wizard.getOnnext());
				
		//all steps
		writer.write(",steps:[");
		boolean firstStep = true;
		String defaultStep = null;
		for(Iterator<UIComponent> children = wizard.getChildren().iterator(); children.hasNext();) {
			UIComponent child = children.next();

			if(child instanceof Tab && child.isRendered()) {
				Tab tab = (Tab) child;
				if(defaultStep == null)
					defaultStep = tab.getId();
				
				if(!firstStep)
					writer.write(",");
				else
					firstStep = false;
				
				writer.write("'" + tab.getId() + "'");
			}
		}
		writer.write("]");
		
		//current step
		if(wizard.getStep() == null) {
			wizard.setStep(defaultStep);
		}

		writer.write(",initialStep:'" + wizard.getStep() + "'");
		
		if(wizard.isEffect()) {
			writer.write(",effect:true");
			writer.write(",effectSpeed:'" + wizard.getEffectSpeed() + "'");
		}
						
		writer.write("});");

		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String styleClass = wizard.getStyleClass() == null ? "ui-wizard" : "ui-wizard " + wizard.getStyleClass(); 
		
		writer.startElement("div", wizard);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("class", styleClass, "styleClass");
		if(wizard.getStyle() != null) 
			writer.writeAttribute("style", wizard.getStyle(), "style");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_content", "id");
		writer.writeAttribute("class", "ui-wizard-content", null);
		
		encodeCurrentStep(facesContext, wizard);
		
		writer.endElement("div");
		
		if(wizard.isShowNavBar()) {
			encodeNavigators(facesContext, wizard);
		}
		
		writer.endElement("div");
	}

	protected void encodeCurrentStep(FacesContext facesContext, Wizard wizard) throws IOException {
		for(UIComponent child : wizard.getChildren()) {
			if(child instanceof Tab && child.isRendered()) {
				Tab tab = (Tab) child;
				
				if((wizard.getStep() == null || tab.getId().equals(wizard.getStep()))) {
					renderChildren(facesContext, tab);
					
					break;
				}
			}
		}
	}
	
	protected void encodeNavigators(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, wizard);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "ui-wizard-navbar ui-helper-clearfix", null);
		
		encodeNavigator(facesContext, wizard, clientId + "_back", var + ".back()", wizard.getBackLabel(), "ui-wizard-nav-back");
		encodeNavigator(facesContext, wizard, clientId + "_next", var + ".next()", wizard.getNextLabel(), "ui-wizard-nav-next");
		
		writer.endElement("div");
	}
	
	protected void encodeNavigator(FacesContext facesContext, Wizard wizard, String id, String onclick, String label, String styleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("button", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("onclick", onclick, null);
		writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", styleClass, null);
		writer.write(label);
		writer.endElement("button");
	}
	
	protected boolean isStepValid(FacesContext facesContext, Wizard wizard) {
		String wizardClientId = wizard.getClientId(facesContext);
		boolean valid = true;
		
		for(Iterator<String> iterator = facesContext.getClientIdsWithMessages(); iterator.hasNext();) {
			String clientId = iterator.next();
			
			if(clientId.indexOf(wizardClientId) != -1) {
				valid = false;
				
				break;
			}
		};
		
		return valid;
	}
	
	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}