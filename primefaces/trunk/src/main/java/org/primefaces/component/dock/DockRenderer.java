/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class DockRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Dock dock = (Dock) component;
		
		if(dock.isDynamic()) {
			dock.buildMenuFromModel();
		}
		
		encodeStyle(context,dock);
		
		encodeMarkup(context, dock);
		encodeScript(context, dock);
	}
	
	protected void encodeStyle(FacesContext context, Dock dock) throws IOException {
		//IE specific style class
		ResponseWriter responseWriter = context.getResponseWriter();
		responseWriter.write("<!--[if lt IE 7]>\n<style type=\"text/css\">\n");
		responseWriter.write(".ui-dock img { behavior: url('" + getResourceRequestPath(context, "dock/assets/iepngfix.htc") + "');}");
		responseWriter.write("</style><![endif]-->");
	}

	protected void encodeScript(FacesContext facesContext, Dock dock) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dock.getClientId(facesContext);
		String position = dock.getPosition();
		String containerClass = ".ui-dock-container-" + position;
		
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Dock','" + dock.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write(",maxWidth: " + dock.getMaxWidth());
		writer.write(",items: 'a'");
		writer.write(",itemsText: 'span'");
		writer.write(",container: '"+ containerClass + "'");
		writer.write(",itemWidth: " + dock.getItemWidth());
		writer.write(",proximity: " + dock.getProximity());
		writer.write(",halign: '" + dock.getHalign() + "'");
		writer.write("},'dock');");

		endScript(writer);
	}

	protected void encodeMarkup(FacesContext facesContext, Dock dock) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = dock.getClientId(facesContext);
		String position = dock.getPosition();
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "ui-dock-" + position + " ui-widget", "styleClass");
	
		writer.startElement("div", null);
		writer.writeAttribute("class", "ui-dock-container-" + position + " ui-widget-header", null);
		
		encodeMenuItems(facesContext, dock);
		
		writer.endElement("div");
		
		writer.endElement("div");
	}
	
	protected void encodeMenuItems(FacesContext context, Dock dock) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String position = dock.getPosition();
		
		for(UIComponent child : dock.getChildren()) {
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem menuitem = (MenuItem) child;
				String clientId = menuitem.getClientId(context);
				
				String styleClass = "ui-dock-item-" + position;
				if(menuitem.getStyleClass() != null) {
					styleClass = styleClass + " " + menuitem.getStyleClass();
				}
				
				writer.startElement("a", null);
				writer.writeAttribute("id", menuitem.getClientId(context), null);
				writer.writeAttribute("class", styleClass, null);
				
				if(menuitem.getStyle() != null) writer.writeAttribute("style", menuitem.getStyle(), null);
				
				if(menuitem.getUrl() != null) {
					writer.writeAttribute("href", getResourceURL(context, menuitem.getUrl()), null);
					if(menuitem.getOnclick() != null) writer.writeAttribute("onclick", menuitem.getOnclick(), null);
					if(menuitem.getTarget() != null) writer.writeAttribute("target", menuitem.getTarget(), null);
				} 
                else {
                    writer.writeAttribute("href", "#", null);
                    
                    UIComponent form = ComponentUtils.findParentForm(context, menuitem);
					if(form == null) {
						throw new FacesException("Dock must be inside a form element");
					}
                    
                    String command = menuitem.isAjax() ? buildAjaxRequest(context, menuitem, form) : buildNonAjaxRequest(context, menuitem, form, clientId, true);

					command = menuitem.getOnclick() == null ? command : menuitem.getOnclick() + ";" + command;
					
					writer.writeAttribute("onclick", command, null);
				}
				
				if(position.equalsIgnoreCase("top")) {
					encodeItemIcon(context, menuitem);
					encodeItemLabel(context, menuitem);
				}
				else{
					encodeItemLabel(context, menuitem);
					encodeItemIcon(context, menuitem);
				}
				
				writer.endElement("a");
			}
		}
	}
	
	protected void encodeItemIcon(FacesContext facesContext, MenuItem menuitem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("img", null);
		writer.writeAttribute("src", getResourceURL(facesContext, menuitem.getIcon()), null);
		writer.endElement("img");
	}
	
	protected void encodeItemLabel(FacesContext facesContext, MenuItem menuitem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("span", null);

		if(menuitem.getValue() != null) writer.write((String) menuitem.getValue());
		
		writer.endElement("span");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}