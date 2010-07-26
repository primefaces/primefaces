/*
 * Copyright 2009 Prime Technology.
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
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.RendererUtils;

public class WizardRenderer extends CoreRenderer implements PartialRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Wizard wizard = (Wizard) component;
			
		encodeMarkup(facesContext, wizard);
		encodeScript(facesContext, wizard);
	}
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Wizard wizard = (Wizard) component;
		String clientId = wizard.getClientId(facesContext);
		String tabIdToDisplay = null;
		boolean success = isStepValid(facesContext, wizard);
		
		String oldStep = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_currentStepId");
		String newStep = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_stepIdToGo");

		/**
		 * Decide which step do display
		 */
		if(success) {
			if(wizard.getFlowListener() != null) {
				FlowEvent flowEvent = new FlowEvent(wizard, oldStep, newStep);
				Object outcome = wizard.getFlowListener().invoke(facesContext.getELContext(), new Object[]{flowEvent});
				tabIdToDisplay = (String) outcome;
			} else {
				tabIdToDisplay = newStep;
			}
		} else {
			tabIdToDisplay = oldStep;
		}

		wizard.setStep(tabIdToDisplay);
		
		UIComponent tabToDisplay = null;
		for(UIComponent child : wizard.getChildren()) {
			if(child.getId().equals(tabIdToDisplay))
				tabToDisplay = child;
		}
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<?xml version=\"1.0\" encoding=\"" + response.getCharacterEncoding() + "\"?>");
		writer.write("<wizard-response>");
		
			writer.write("<content>");
				RendererUtils.startCDATA(facesContext);
				renderChild(facesContext, tabToDisplay);
				RendererUtils.endCDATA(facesContext);
			writer.write("</content>");
			
			writer.write("<success>");
			writer.write(String.valueOf(success));
			writer.write("</success>");
			
			writer.write("<state>");
			RendererUtils.startCDATA(facesContext);
			StateManager stateManager = facesContext.getApplication().getStateManager();
			stateManager.writeState(facesContext, stateManager.saveView(facesContext));
			RendererUtils.endCDATA(facesContext);
			writer.write("</state>");
			
		writer.write("</wizard-response>");
	}

	private void encodeScript(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, wizard);
		UIComponent back = wizard.getFacet("back");
		UIComponent next = wizard.getFacet("next");
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, wizard);
		if(form == null) {
			throw new FacesException("Wizard : \"" + clientId + "\" must be inside a form element");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(var + " = new PrimeFaces.widget.Wizard('" + clientId + "',{");
		writer.write("formId:'" + form.getClientId(facesContext) + "'");
		writer.write(",url:'" + getActionURL(facesContext) + "'");
		writer.write(",effect:'" + wizard.getEffect() + "'");
		if(wizard.getStep() != null ) writer.write(",step:'" + wizard.getStep() + "'");
		if(wizard.getOnback() != null) writer.write(",onback:" + wizard.getOnback());
		if(wizard.getOnnext() != null) writer.write(",onnext:" + wizard.getOnnext());
		
		writer.write(",steps:[");
		
		boolean firstStep = true;
		for(int i = 0; i < wizard.getChildCount(); i++) {
			UIComponent child = wizard.getChildren().get(i);
			if(child instanceof Tab && child.isRendered()) {
				Tab tab = (Tab) child;
				
				if(!firstStep)
					writer.write(",");
				else
					firstStep = false;
				
				writer.write("'" + tab.getId() + "'");
			}
		}
		writer.write("]");
		
		//Navigators
		String backNav = back != null ? back.getClientId(facesContext) : clientId + "_back";
		String nextNav = next != null ? next.getClientId(facesContext) : clientId + "_next";
		
		writer.write(",backNav:'" + backNav + "'");
		writer.write(",nextNav:'" + nextNav + "'");
		
		writer.write("});");

		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String styleClass = wizard.getStyleClass() == null ? "pf-wizard" : "pf-wizard " + wizard.getStyleClass(); 
		
		writer.startElement("div", wizard);
		writer.writeAttribute("id", clientId, "id");
		
		writer.writeAttribute("class", styleClass, "styleClass");
		if(wizard.getStyle() != null) writer.writeAttribute("style", wizard.getStyle(), "style");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "pf-wizard-content", null);
		
		encodeTabs(facesContext, wizard);
		
		writer.endElement("div");
		
		if(wizard.isShowNavBar()) {
			encodeNavigators(facesContext, wizard);
		}
		
		writer.endElement("div");
	}

	protected void encodeTabs(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		boolean contentWritten = false;
		for(UIComponent child : wizard.getChildren()) {
			if(child instanceof Tab && child.isRendered()) {
				Tab tab = (Tab) child;
				
				writer.startElement("div", null);
				writer.writeAttribute("id", tab.getId(), null);
				writer.writeAttribute("class", "step", null);
				
				if((wizard.getStep() == null || tab.getId().equals(wizard.getStep())) && !contentWritten) {
					renderChildren(facesContext, tab);
					contentWritten = true;
				}
				
				writer.endElement("div");
			}
		}
	}
	
	protected void encodeNavigators(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, wizard);
		UIComponent back = wizard.getFacet("back");
		UIComponent next = wizard.getFacet("next");
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "pf-wizard-navbar", null);
		
		if(back == null) 
			encodeDefaultNavigator(facesContext, wizard, clientId + "_back", "Back", var + ".back()", "/primefaces/wizard/assets/prev.png", "pf-wizard-nav-back");
		else
			renderChild(facesContext, back);
		
		if(next == null) 
			encodeDefaultNavigator(facesContext, wizard, clientId + "_next", "Next", var + ".next()", "/primefaces/wizard/assets/next.png", "pf-wizard-nav-next");
		else
			renderChild(facesContext, next);
		
		writer.endElement("div");
	}
	
	protected void encodeDefaultNavigator(FacesContext facesContext, Wizard wizard, String id, String label, String onclick, String img, String styleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("id", id, null);
		writer.writeAttribute("title", label, null);
		writer.writeAttribute("alt", label, null);
		writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, img), null);
		writer.writeAttribute("onclick", onclick, null);
		writer.writeAttribute("class", styleClass, null);
		writer.endElement("img");
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