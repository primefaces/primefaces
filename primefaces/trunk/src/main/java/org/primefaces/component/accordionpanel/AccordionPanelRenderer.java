/*
 * Copyright 2009-2011 Prime Technology.
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

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;

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
        
        decodeBehaviors(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		AccordionPanel acco = (AccordionPanel) component;
        String clientId = acco.getClientId(context);

        if(acco.isContentLoadRequest(context)) {
            String tabClientId = params.get(clientId + "_newTab");
            Tab tabToLoad = (Tab) acco.findTab(tabClientId);

            tabToLoad.encodeAll(context);
        }
        else {
            encodeMarkup(context, acco);
            encodeScript(context, acco);
        }
	}
	
	protected void encodeMarkup(FacesContext context, AccordionPanel acco) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = acco.getClientId(context);
        String styleClass = acco.getStyleClass();
        styleClass = styleClass == null ? AccordionPanel.CONTAINER_CLASS : AccordionPanel.CONTAINER_CLASS + " " + styleClass;
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
		if(acco.getStyle() != null)
            writer.writeAttribute("style", acco.getStyle(), null);

		encodeTabs(context, acco);

        encodeStateHolder(context, acco);

		writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, AccordionPanel acco) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = acco.getClientId(context);
        boolean dynamic = acco.isDynamic();
 		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(acco.resolveWidgetVar() + " = new PrimeFaces.widget.AccordionPanel('" + clientId + "', {");
        writer.write("dynamic:" + dynamic);
		
        if(dynamic) writer.write(",cache:" + acco.isCache());
		if(acco.isCollapsible()) writer.write(",collapsible:true");
        if(acco.getOnTabChange() != null) writer.write(",onTabChange: function(event, ui) {" + acco.getOnTabChange() + "}");

        encodeClientBehaviors(context, acco);
		
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
        int i = 0;
		
		for(UIComponent child : acco.getChildren()) {
			if(child.isRendered() && child instanceof Tab) {
				Tab tab = (Tab) child;
                boolean active = (i == activeIndex);
                String headerClass = active ? AccordionPanel.ACTIVE_TAB_HEADER_CLASS : AccordionPanel.TAB_HEADER_CLASS;
                headerClass = tab.isDisabled() ? headerClass + " ui-state-disabled" : headerClass;
                String iconClass = active ? AccordionPanel.ACTIVE_TAB_HEADER_ICON_CLASS : AccordionPanel.TAB_HEADER_ICON_CLASS;
                String contentStyle = active ? "display:block" : "display:none";
				
				//header container
				writer.startElement("h3", null);
                writer.writeAttribute("class", headerClass, null);
                
                //icon
                writer.startElement("span", null);
                writer.writeAttribute("class", iconClass, null);
                writer.endElement("span");
                
				writer.startElement("a", null);
				writer.writeAttribute("href", "#", null);
                writer.writeAttribute("tabindex", "-1", null);
                if(tab.getTitletip() != null) writer.writeAttribute("title", tab.getTitletip(), null);
				if(tab.getTitle() != null) writer.write(tab.getTitle());
                
				writer.endElement("a");
                
				writer.endElement("h3");
				
				//content
				writer.startElement("div", null);
                writer.writeAttribute("id", child.getClientId(context), null);
                writer.writeAttribute("class", AccordionPanel.TAB_CONTENT_CLASS, null);
                writer.writeAttribute("style", contentStyle, null);

                if(acco.isDynamic() && active)
                    tab.encodeAll(context);
                else
                    tab.encodeAll(context);
                
				writer.endElement("div");
                
                i++;
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