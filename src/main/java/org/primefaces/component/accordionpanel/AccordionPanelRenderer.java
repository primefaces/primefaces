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
package org.primefaces.component.accordionpanel;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;

public class AccordionPanelRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		AccordionPanel accordionPanel = (AccordionPanel) component;
		
		//restore active tab states
		String clientId = accordionPanel.getClientId(facesContext);
		String activeTabIndex = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_container_state");
		if(activeTabIndex != null) {
			if(activeTabIndex.equals(""))
				accordionPanel.setActiveIndex(null);
			else
				accordionPanel.setActiveIndex(activeTabIndex);
		}
		
		encodeMarkup(facesContext, accordionPanel);
		encodeScript(facesContext, accordionPanel);
	}

	private void encodeScript(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, accordionPanel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(var + " = new PrimeFaces.widget.AccordionPanel('" + clientId + "_container',");
		writer.write("{");
		
		writer.write("collapsible:true");
		writer.write(",width:'100%'");
		
		if(accordionPanel.getActiveIndex() != null) 
			writer.write(",expandItem:[" + accordionPanel.getActiveIndex() + "]");
		else
			writer.write(",expandItem:[]");
			
		if(accordionPanel.isMultiple()) writer.write(",expandable:true");
		if(accordionPanel.getSpeed() != 0.5) writer.write(",animationSpeed:" + accordionPanel.getSpeed());
		if(!accordionPanel.isAnimate()) writer.write(",animate:false");
		if(accordionPanel.isHover()) writer.write(",hoverActivated:true");
		if(accordionPanel.getHoverDelay() != 500) writer.write(",hoverTimeout:" + accordionPanel.getSpeed());
		
		writer.write("});");
		
		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if(accordionPanel.getStyle() != null)
			writer.writeAttribute("style", accordionPanel.getStyle(), null);
		if(accordionPanel.getStyleClass() != null)
			writer.writeAttribute("class", accordionPanel.getStyleClass(), null);
		
		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId + "_container", null);
		encodeTabs(facesContext, accordionPanel);
		writer.endElement("ul");
		
		encodeStateHolder(facesContext, accordionPanel);
		
		writer.endElement("div");
	}
	
	private void encodeStateHolder(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String stateHolderId = clientId + "_container_state"; 
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", stateHolderId, null);
		writer.writeAttribute("name", stateHolderId, null);
		
		if(accordionPanel.getActiveIndex() != null) {
			writer.writeAttribute("value", accordionPanel.getActiveIndex(), null);
		}
		writer.endElement("input");
	}
	
	private void encodeTabs(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(Iterator<UIComponent> kids = accordionPanel.getChildren().iterator(); kids.hasNext();) {
			UIComponent kid = (UIComponent) kids.next();
			
			if(kid.isRendered() && kid instanceof Tab) {
				Tab tab = (Tab) kid;
				
				writer.startElement("li", null);
				writer.startElement("p", null);
				if(tab.getTitle() != null)
					writer.write(tab.getTitle());
				writer.endElement("p");
				
				writer.startElement("div", null);
				renderChild(facesContext, tab);
				writer.endElement("div");
				writer.endElement("li");
			}
		}
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}