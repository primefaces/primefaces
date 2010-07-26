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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;

public class AccordionPanelRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		AccordionPanel accordionPanel = (AccordionPanel) component;
		String activeIndexParam = accordionPanel.getClientId(facesContext) + "_active";
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(activeIndexParam)) {
			accordionPanel.setActiveIndex(Integer.valueOf(params.get(activeIndexParam)));
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		AccordionPanel accordionPanel = (AccordionPanel) component;
		
		encodeMarkup(facesContext, accordionPanel);
		encodeScript(facesContext, accordionPanel);
	}
	
	protected void encodeMarkup(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if(accordionPanel.getStyle() != null) writer.writeAttribute("style", accordionPanel.getStyle(), null);
		if(accordionPanel.getStyleClass() != null) writer.writeAttribute("class", accordionPanel.getStyleClass(), null);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_acco", null);
		
		encodeTabs(facesContext, accordionPanel);
		
		writer.endElement("div");
		
		encodeStateHolder(facesContext, accordionPanel);
		
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext facesContext, AccordionPanel acco) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = acco.getClientId(facesContext);
		String widgetVar = createUniqueWidgetVar(facesContext, acco);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.AccordionPanel('" + clientId + "', {");
		writer.write("active:" + acco.getActiveIndex());
		writer.write(",animated:'" + acco.getEffect() + "'");
		
		if(acco.getEvent() != null) writer.write(",event:'" + acco.getEvent() + "'");
		if(!acco.isAutoHeight()) writer.write(",autoHeight:false");
		if(acco.isCollapsible()) writer.write(",collapsible:true");
		if(acco.isFillSpace()) writer.write(",fillSpace:true");
		if(acco.isDisabled()) writer.write(",disabled:true");
		
		writer.write("});");
		
		writer.endElement("script");
	}

	protected void encodeStateHolder(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = accordionPanel.getClientId(facesContext);
		String stateHolderId = clientId + "_active"; 
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", stateHolderId, null);
		writer.writeAttribute("name", stateHolderId, null);
		writer.writeAttribute("value", accordionPanel.getActiveIndex(), null);
		writer.endElement("input");
	}
	
	protected void encodeTabs(FacesContext facesContext, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(Iterator<UIComponent> kids = accordionPanel.getChildren().iterator(); kids.hasNext();) {
			UIComponent kid = (UIComponent) kids.next();
			
			if(kid.isRendered() && kid instanceof Tab) {
				Tab tab = (Tab) kid;
				
				//title
				writer.startElement("h3", null);
				writer.startElement("a", null);
				writer.writeAttribute("href", "#", null);
				if(tab.getTitle() != null) {
					writer.write(tab.getTitle());
				}
				writer.endElement("a");
				writer.endElement("h3");
				
				//content
				writer.startElement("div", null);
				renderChild(facesContext, tab);
				writer.endElement("div");
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