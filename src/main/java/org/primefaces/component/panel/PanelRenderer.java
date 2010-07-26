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
package org.primefaces.component.panel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class PanelRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Panel panel = (Panel) component;
		String clientId = panel.getClientId(facesContext);
		
		//Restore toggle state
		String collapsedParam = facesContext.getExternalContext().getRequestParameterMap().get(clientId + "_state");
		if(collapsedParam != null) {
			panel.setCollapsed(collapsedParam.equals("1"));
		}
		
		encodeScript(facesContext, panel);
		encodeMarkup(facesContext, panel);
	}
	
	private void encodeScript(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, panel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(var + " = new PrimeFaces.widget.Panel('" + clientId + "', {");
		if(panel.isToggleable()) {
			writer.write("toggleSpeed:" + panel.getToggleSpeed());
			writer.write(",collapsed:" + panel.isCollapsed());
		}
		writer.write("});});");
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext), null);
		
		String styleClass = panel.getStyleClass() != null ? Panel.PANEL_CLASS + " " + panel.getStyleClass() : Panel.PANEL_CLASS;
		writer.writeAttribute("class", styleClass, "styleClass");
		if(panel.getStyle() != null) {
			writer.writeAttribute("style", panel.getStyle(), "style");
		}
		
		encodeHeader(facesContext, panel);
		encodeContent(facesContext, panel);
		encodeFooter(facesContext, panel);
		
		if(panel.isToggleable()) {
			encodeToggler(facesContext, panel);
			encodeStateHolder(facesContext, panel);
		}
		
		writer.endElement("div");
	}

	private void encodeHeader(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String var = createUniqueWidgetVar(facesContext, panel);
		String headerClass = panel.isToggleable() ? Panel.PANEL_HEADER_CLASS + " " + Panel.PANEL_TOGGLEABLE_HEADER_CLASS : Panel.PANEL_HEADER_CLASS;
		UIComponent header = panel.getFacet("header");
		String headerText = panel.getHeader();
		
		if(headerText == null && header == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext) + "_hd", null);
		writer.writeAttribute("class", headerClass, null);
		
		if(panel.isToggleable())
			writer.writeAttribute("onclick", var + ".toggle();", null);
		
		if(header != null)
			renderChild(facesContext, header);
		else if(headerText != null)
			writer.write(headerText);
		
		writer.endElement("div");
	}
	
	private void encodeContent(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext) + "_bd", null);
		writer.writeAttribute("class", Panel.PANEL_BODY_CLASS, null);
		if(panel.isCollapsed()) {
			writer.writeAttribute("style", "display:none", null);
		}
		
		renderChildren(facesContext, panel);
		
		writer.endElement("div");
	}
	
	private void encodeFooter(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent footer = panel.getFacet("footer");
		String footerText = panel.getFooter();
		
		if(footerText == null && footer == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext) + "_ft", null);
		writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);
		
		if(footer != null)
			renderChild(facesContext, footer);
		if(footerText != null)
			writer.write(footerText);
		
		writer.endElement("div");
	}
	
	private void encodeToggler(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, panel);
		
		String styleClass = panel.isCollapsed() ? Panel.PANEL_TOGGLER_COLLAPSED_CLASS : Panel.PANEL_TOGGLER_EXPANDED_CLASS;
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "_toggler", null);
		writer.writeAttribute("class", styleClass, null);
		writer.writeAttribute("onclick", var + ".toggle();", null);		
		writer.endElement("span");
	}
	
	private void encodeStateHolder(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		String stateHolderId = clientId + "_state";
		int state = panel.isCollapsed() ? 1 : 0;
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", stateHolderId, null);
		writer.writeAttribute("name", stateHolderId, null);
		writer.writeAttribute("value", state, null);
		writer.endElement("input");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}
