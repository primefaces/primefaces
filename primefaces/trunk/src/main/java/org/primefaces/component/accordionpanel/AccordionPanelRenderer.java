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
package org.primefaces.component.accordionpanel;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;

import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class AccordionPanelRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		AccordionPanel acco = (AccordionPanel) component;
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String activeIndex = params.get(acco.getClientId(context) + "_active");
		
		if(activeIndex != null) {
            if(activeIndex.equals("false"))         //collapsed all
                acco.setActiveIndex(-1);
            else
                acco.setActiveIndex(Integer.valueOf(activeIndex));
		}

        if(acco.isTabChangeRequest(context)) {
            TabChangeEvent changeEvent = new TabChangeEvent(acco, acco.findTabToLoad(context));
            changeEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);

            acco.queueEvent(changeEvent);
        }
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		AccordionPanel acco = (AccordionPanel) component;

        if(acco.isContentLoadRequest(context)) {
            Tab tabToLoad = (Tab) acco.findTabToLoad(context);

            tabToLoad.encodeAll(context);
        }else {
            encodeMarkup(context, acco);
            encodeScript(context, acco);
        }
		
	}
	
	protected void encodeMarkup(FacesContext context, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = accordionPanel.getClientId(context);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		if(accordionPanel.getStyle() != null) writer.writeAttribute("style", accordionPanel.getStyle(), null);
		if(accordionPanel.getStyleClass() != null) writer.writeAttribute("class", accordionPanel.getStyleClass(), null);

        writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_acco", null);

		encodeTabs(context, accordionPanel);
  
        writer.endElement("div");

        encodeStateHolder(context, accordionPanel);

		writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, AccordionPanel acco) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = acco.getClientId(context);
        int activeIndex = acco.getActiveIndex();
        boolean hasTabChangeListener = acco.getTabChangeListener() != null;
 		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(acco.resolveWidgetVar() + " = new PrimeFaces.widget.AccordionPanel('" + clientId + "', {");
		writer.write("active:" + (activeIndex == -1 ? false : activeIndex));
        writer.write(",dynamic:" + acco.isDynamic());
		writer.write(",animated:'" + acco.getEffect() + "'");
		
		if(acco.getEvent() != null) writer.write(",event:'" + acco.getEvent() + "'");
		if(!acco.isAutoHeight()) writer.write(",autoHeight:false");
		if(acco.isCollapsible()) writer.write(",collapsible:true");
		if(acco.isFillSpace()) writer.write(",fillSpace:true");
		if(acco.isDisabled()) writer.write(",disabled:true");
        if(acco.getOnTabChange() != null) writer.write(",onTabChange: function(event, ui) {" + acco.getOnTabChange() + "}");

        if(acco.isDynamic() || hasTabChangeListener) {
            writer.write(",cache:" + acco.isCache());
        }

        if(hasTabChangeListener) {
            writer.write(",ajaxTabChange:true");

            if(acco.getOnTabChangeUpdate() != null) {
                writer.write(",onTabChangeUpdate:'" + ComponentUtils.findClientIds(context, acco, acco.getOnTabChangeUpdate()) + "'");
            }
        }
		
		writer.write("});");
		
		writer.endElement("script");
	}

	protected void encodeStateHolder(FacesContext context, AccordionPanel accordionPanel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = accordionPanel.getClientId(context);
		String stateHolderId = clientId + "_active"; 
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", stateHolderId, null);
		writer.writeAttribute("name", stateHolderId, null);
		writer.writeAttribute("value", accordionPanel.getActiveIndex(), null);
		writer.endElement("input");
	}
	
	protected void encodeTabs(FacesContext context, AccordionPanel acco) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        int activeIndex = acco.getActiveIndex();
		
		for(int i=0; i < acco.getChildCount(); i++) {
			UIComponent kid = acco.getChildren().get(i);
			
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
                writer.writeAttribute("id", kid.getClientId(context), null);

                if(acco.isDynamic()) {
                    if(i == activeIndex)
                        tab.encodeAll(context);
                }
                else {
                    tab.encodeAll(context);
                }
                
				writer.endElement("div");
			}
		}
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}