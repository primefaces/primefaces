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
package org.primefaces.component.panel;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.Menu;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class PanelRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		Panel panel = (Panel) component;
		String clientId = panel.getClientId(facesContext);
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		//Restore toggle state
		String collapsedParam = params.get(clientId + "_collapsed");
		if(collapsedParam != null) {
			panel.setCollapsed(Boolean.valueOf(collapsedParam));
		}
		
		//Restore visibility state
		String visibleParam = params.get(clientId + "_visible");
		if(visibleParam != null) {
			panel.setVisible(Boolean.valueOf(visibleParam));
		}
		
		//Queue toggle event
		if(params.containsKey(clientId + "_toggled")) {
			boolean collapsed = Boolean.valueOf(collapsedParam);
			panel.setCollapsed(collapsed);
			Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;
			panel.queueEvent(new ToggleEvent(panel, visibility));
		}
		
		//Queue close event
		if(params.containsKey(clientId + "_closed")) {
			panel.setVisible(false);
			panel.queueEvent(new CloseEvent(panel));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Panel panel = (Panel) component;
		
		encodeMarkup(facesContext, panel);
		encodeScript(facesContext, panel);
	}
	
	protected void encodeScript(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, panel);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(var + " = new PrimeFaces.widget.Panel('" + clientId + "', {");
		writer.write("visible:" + panel.isVisible());
		
		if(panel.isToggleable()) {
			writer.write(",toggleSpeed:" + panel.getToggleSpeed());
			writer.write(",collapsed:" + panel.isCollapsed());
			
			if(panel.getToggleListener() != null || panel.getOnToggleUpdate() != null) {
				writer.write(",ajaxToggle:true");
				
				if(panel.getOnToggleUpdate() != null) 
					writer.write(",onToggleUpdate:'" + ComponentUtils.findClientIds(facesContext, panel, panel.getOnToggleUpdate()) + "'");
			}
		}
		
		if(panel.isClosable()) {
			writer.write(",closeSpeed:" + panel.getCloseSpeed());
			if(panel.getOnCloseStart() != null) writer.write(",onCloseStart:" + panel.getOnCloseStart());
			if(panel.getOnCloseComplete() != null) writer.write(",onCloseComplete:" + panel.getOnCloseComplete());
			
			if(panel.getCloseListener() != null || panel.getOnCloseUpdate() != null) {
				writer.write(",ajaxClose:true");
				
				if(panel.getOnCloseUpdate() != null) 
					writer.write(",onCloseUpdate:'" + ComponentUtils.findClientIds(facesContext, panel, panel.getOnCloseUpdate()) + "'");
			}
		}
		
		if(panel.getToggleListener() != null || panel.getCloseListener() != null) {
			writer.write(",url:'" + getActionURL(facesContext) + "'");
		}

		writer.write("});");
		writer.endElement("script");
	}
	
	protected void encodeMarkup(FacesContext facesContext, Panel panel) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		Menu optionsMenu = panel.getOptionsMenu();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		String styleClass = panel.getStyleClass() != null ? Panel.PANEL_CLASS + " " + panel.getStyleClass() : Panel.PANEL_CLASS;
		writer.writeAttribute("class", styleClass, "styleClass");
		if(panel.getStyle() != null) 
			writer.writeAttribute("style", panel.getStyle(), "style");
		
		encodeHeader(facesContext, panel);
		encodeContent(facesContext, panel);
		encodeFooter(facesContext, panel);

		if(panel.isToggleable())
			encodeStateHolder(facesContext, panel, clientId + "_collapsed", String.valueOf(panel.isCollapsed()));
		
		if(panel.isClosable())
			encodeStateHolder(facesContext, panel, clientId + "_visible", String.valueOf(panel.isVisible()));
		
		if(optionsMenu != null) {
			optionsMenu.setPosition("dynamic");
			optionsMenu.setContext("'" + clientId + "_menu','tl','bl'");
			
			optionsMenu.encodeAll(facesContext);
		}
		
		writer.endElement("div");
	}

	protected void encodeHeader(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String widgetVar = createUniqueWidgetVar(facesContext, panel);
		UIComponent header = panel.getFacet("header");
		String headerText = panel.getHeader();
		String clientId = panel.getClientId(facesContext);
		
		if(headerText == null && header == null)
			return;
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext) + "_header", null);
		writer.writeAttribute("class", Panel.PANEL_TITLEBAR_CLASS, null);
		
		//Title
		writer.startElement("span", null);
		writer.writeAttribute("class", Panel.PANEL_TITLE_CLASS, null);
				
		if(header != null)
			renderChild(facesContext, header);
		else if(headerText != null)
			writer.write(headerText);
		
		writer.endElement("span");
		
		//Options
		if(panel.isClosable()) {
			encodeIcon(facesContext, panel, "ui-icon-closethick", widgetVar + ".close()", clientId + "_closer");
		}
		
		if(panel.isToggleable()) {
			String icon = panel.isCollapsed() ? "ui-icon-plusthick" : "ui-icon-minusthick";
			encodeIcon(facesContext, panel, icon, widgetVar + ".toggle()", clientId + "_toggler");
		}

		if(panel.getOptionsMenu() != null) {
			String menuVar = createUniqueWidgetVar(facesContext, panel.getOptionsMenu());
			encodeIcon(facesContext, panel, "ui-icon-gear", menuVar + ".show()", clientId + "_menu");
		}
			
		writer.endElement("div");
	}
	
	protected void encodeContent(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", panel.getClientId(facesContext) + "_content", null);
		writer.writeAttribute("class", Panel.PANEL_CONTENT_CLASS, null);
		if(panel.isCollapsed()) {
			writer.writeAttribute("style", "display:none", null);
		}
		
		renderChildren(facesContext, panel);
		
		writer.endElement("div");
	}
	
	protected void encodeFooter(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		UIComponent footer = panel.getFacet("footer");
		String footerText = panel.getFooter();
		
		if(footer != null || footerText != null) {
			writer.startElement("div", null);
			writer.writeAttribute("id", panel.getClientId(facesContext) + "_footer", null);
			writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);
			
			if(footer != null)
				renderChild(facesContext, footer);
			else if(footerText != null)
				writer.write(footerText);
			
			writer.endElement("div");
		}
	}
	
	protected void encodeIcon(FacesContext facesContext, Panel panel, String iconClass, String onclick, String id) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String hover = "jQuery(this).toggleClass('ui-state-hover')";
		
		writer.startElement("a", null);
		writer.writeAttribute("href", "javascript:void(0)", null);
		writer.writeAttribute("class", Panel.PANEL_TITLE_ICON_CLASS, null);
		writer.writeAttribute("onmouseover", hover, null);
		writer.writeAttribute("onmouseout", hover, null);
		
		writer.startElement("span", null);
		if(id != null) {
			writer.writeAttribute("id", id, null);
		}
		writer.writeAttribute("class", "ui-icon " + iconClass, null);
		writer.writeAttribute("onclick", onclick, null);
		writer.endElement("span");
		
		writer.endElement("a");
	}
	
	protected void encodeOptionsControl(FacesContext facesContext, Panel panel) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = panel.getClientId(facesContext);
		String menuVar = createUniqueWidgetVar(facesContext, panel.getOptionsMenu());
		
		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "_menu", null);
		writer.writeAttribute("class", "pf-panel-options", null);
		writer.writeAttribute("onclick", menuVar + ".show()", null);		
		writer.endElement("span");
	}
	
	protected void encodeStateHolder(FacesContext facesContext, Panel panel, String name, String value) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", name, null);
		writer.writeAttribute("name", name, null);
		writer.writeAttribute("value", value, null);
		writer.endElement("input");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}
	
	public boolean getRendersChildren() {
		return true;
	}
}