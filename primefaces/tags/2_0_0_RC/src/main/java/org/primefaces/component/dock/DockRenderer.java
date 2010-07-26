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
package org.primefaces.component.dock;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class DockRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Dock dock = (Dock) component;
		
		encodeStyle(context,dock);
		encodeScript(context, dock);
		encodeMarkup(context, dock);
	}
	
	private void encodeStyle(FacesContext context, Dock dock) throws IOException {
		//IE specific style class
		ResponseWriter responseWriter = context.getResponseWriter();
		responseWriter.write("<!--[if lt IE 7]>\n<style type=\"text/css\">\n");
		responseWriter.write(".pf-dock img { behavior: url(primefaces_resources:url:/primefaces/dock/assets/iepngfix.htc) }");
		responseWriter.write("</style><![endif]-->");
		
	}

	private void encodeScript(FacesContext facesContext, Dock dock) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dock.getClientId(facesContext);
		String position = dock.getPosition();
		String widgetVar = createUniqueWidgetVar(facesContext, dock);
		
		String containerClass = ".pf-dock-container-" + position;
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {\n");
		writer.write(widgetVar + " = new PrimeFaces.widget.Dock('" + clientId + "', {");
		writer.write("maxWidth: " + dock.getMaxWidth());
		writer.write(",items: 'a'");
		writer.write(",itemsText: 'span'");
		writer.write(",container: '"+ containerClass + "'");
		writer.write(",itemWidth: " + dock.getItemWidth());
		writer.write(",proximity: " + dock.getProximity());
		writer.write(",halign: '" + dock.getHalign() + "'");
		writer.write("});\n");
		
		writer.write("});");
		
		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, Dock dock) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dock.getClientId(facesContext);
		String position = dock.getPosition();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "pf-dock-" + position, "styleClass");
	
		writer.startElement("div", null);
		writer.writeAttribute("class", "pf-dock-container-" + position, null);
		
		encodeDockItems(facesContext, dock);
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	private void encodeDockItems(FacesContext facesContext, Dock dock) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String position = dock.getPosition();
		
		for(UIComponent child : dock.getChildren()) {
			if(child instanceof DockItem && child.isRendered()) {
				DockItem dockItem = (DockItem) child;
				
				writer.startElement("a", null);
				writer.writeAttribute("class", "pf-dock-item-" + position, null);
				writer.writeAttribute("href", dockItem.getUrl(), "href");
				if(dockItem.getOnclick() != null) {
					writer.writeAttribute("onclick", dockItem.getOnclick(), "onclick");
				}
				
				if(position.equalsIgnoreCase("top")) {
					encodeItemIcon(facesContext, dockItem);
					encodeItemLabel(facesContext, dockItem);
				}
				else{
					encodeItemLabel(facesContext, dockItem);
					encodeItemIcon(facesContext, dockItem);
				}
					
				writer.endElement("a");
			}
		}
	}
	
	private void encodeItemIcon(FacesContext facesContext, DockItem dockItem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(facesContext, dockItem.getIcon()), "icon");
		writer.endElement("img");
	}
	
	private void encodeItemLabel(FacesContext facesContext, DockItem dockItem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("span", null);
		if(dockItem.getLabel() != null)
			writer.write(dockItem.getLabel());
		writer.endElement("span");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}