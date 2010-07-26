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

import javax.faces.application.StateManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletResponse;

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.PartialRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.RendererUtils;

public class WizardRenderer extends CoreRenderer implements PartialRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Wizard wizard = (Wizard) component;
			
		encodeScript(facesContext, wizard);
		encodeMarkup(facesContext, wizard);
	}
	
	private boolean isStepValid(FacesContext facesContext, Wizard wizard) {
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
	
	public void encodePartially(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Wizard wizard = (Wizard) component;
		
		boolean success = isStepValid(facesContext, wizard);
		int stepToGo = Integer.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("stepToGo")).intValue();
		int currentStep = Integer.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("currentStep")).intValue();
		int tabIndexToDisplay = success ? stepToGo : currentStep;
		wizard.setStep(tabIndexToDisplay);	//save state
		
		Tab tabToDisplay = (Tab) wizard.getChildren().get(tabIndexToDisplay);
		
		ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("text/xml");
		
		writer.write("<wizardresponse>");
			writer.write("<wizardtab>");
				writer.write("<success>");
					writer.write(String.valueOf(success));
				writer.write("</success>");
				writer.write("<content>");
					RendererUtils.startCDATA(facesContext);
					renderChild(facesContext, tabToDisplay);
					RendererUtils.endCDATA(facesContext);
				writer.write("</content>");
			writer.write("</wizardtab>");
			
			writer.write("<state>");
			RendererUtils.startCDATA(facesContext);
			StateManager stateManager = facesContext.getApplication().getStateManager();
			stateManager.writeState(facesContext, stateManager.saveView(facesContext));
			RendererUtils.endCDATA(facesContext);
			writer.write("</state>");
			
		writer.write("</wizardresponse>");
	}

	private void encodeScript(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, wizard);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(widgetVar + " = new PrimeFaces.widget.Wizard('" + clientId + "',{");
		writer.write("type:'" + wizard.getEffect() + "'");
		writer.write(",actionURL:'" + getActionURL(facesContext) + "'");
		writer.write(",size:" + wizard.getChildCount());
		writer.write(",formClientId:'" + ComponentUtils.findParentForm(facesContext, wizard).getClientId(facesContext) + "'");
		writer.write(",viewportClass:'pf-wizard-viewport'");
		
		if(wizard.getStep() != 0) writer.write(",index:" + wizard.getStep() + "");
		if(wizard.getSpeed() != 500) writer.write(",speed:" + wizard.getSpeed());
		if(wizard.getWidth() != 400) writer.write(",width:" + wizard.getWidth());
		if(wizard.getHeight() != 400) writer.write(",height:" + wizard.getHeight());
		
		writer.write("});\n});\n");

		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, wizard);
		
		writer.startElement("div", wizard);
		writer.writeAttribute("id", clientId, "id");
		
		String dim = "height:" + wizard.getHeight() + "px;width:" + wizard.getWidth() + "px";
		String style = wizard.getStyle() == null ? dim : dim + ";" + wizard.getStyle();

		writer.writeAttribute("style", style, "style");
		
		if(wizard.getStyleClass() != null) {
			writer.writeAttribute("class", wizard.getStyleClass(), "styleClass");
		}
		
		if(!wizard.isCustomUI()) {
			encodeNavigators(facesContext, wizard, widgetVar);
		}
		
		encodeTabs(facesContext, wizard);
		
		writer.endElement("div");
	}
	
	private void encodeNavigators(FacesContext facesContext, Wizard wizard, String widgetVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("class", "pf-wizard-nav", null);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_prev", null);
		writer.writeAttribute("class", "pf-wizard-prev", null);
		if(wizard.getStep() == 0) {
			writer.writeAttribute("style", "display:none", null);
		}
		writer.writeAttribute("onclick", widgetVar + ".previous();", null);
		writer.endElement("div");
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_next", null);
		writer.writeAttribute("onclick", widgetVar + ".next();", null);
		writer.writeAttribute("class", "pf-wizard-next", null);
		if(wizard.getStep() == (wizard.getChildCount()-1)) {
			writer.writeAttribute("style", "display:none", null);
		}
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	private void encodeTabs(FacesContext facesContext, Wizard wizard) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = wizard.getClientId(facesContext);
		
		for (int i = 0; i < wizard.getChildren().size(); i++) {
			UIComponent kid = wizard.getChildren().get(i);
			
			if(kid.isRendered()) {
				Tab tab = (Tab) kid;
				
				writer.startElement("div", wizard);
				writer.writeAttribute("id", clientId + "_tab" + i, null);
				writer.writeAttribute("class", "pf-wizard-tab", null);
				
				if(i == wizard.getStep()) {
					renderChild(facesContext, tab);
				}
				
				writer.endElement("div");
			}
			
		}
	}
	
	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Render children in encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}