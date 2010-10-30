/*
 * Copyright 2009,2010 Prime Technology.
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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class ContextMenuRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ContextMenu menu = (ContextMenu) component;
		
		encodeMarkup(facesContext, menu);
		encodeScript(facesContext, menu);
	}

	protected void encodeScript(FacesContext facesContext, ContextMenu menu) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(facesContext);
		String trigger = findTrigger(facesContext, menu);
		
		writer.startElement("script", menu);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write(widgetVar + " = new PrimeFaces.widget.ContextMenu('" + clientId + "',");
		writer.write("{trigger:" + trigger);

        if(menu.getZindex() != Integer.MAX_VALUE) writer.write(",zIndex:" + menu.getZindex());

        writer.write("});");
		
		encodeMenuitems(facesContext, menu, widgetVar);
		
		writer.write(widgetVar + ".render(document.body);");
		
		writer.endElement("script");
	}
	
	protected String findTrigger(FacesContext facesContext, ContextMenu menu) {
		String trigger = null;
		String _for = menu.getFor();
		
		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);
			
			if(forComponent == null)
				throw new FacesException("Cannot find component '" + _for + "' in view.");
			else {
                return "'" +  forComponent.getClientId(facesContext) + "'";
			}
		}
		else {
			trigger = "document";
		}
		
		return trigger;
	}
	
	protected void encodeMenuitems(FacesContext facesContext, ContextMenu menu, String widgetVar) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		boolean firstMenuitem = true;
		UIComponent form = ComponentUtils.findParentForm(facesContext, menu);
		
		writer.write(widgetVar + ".addItems([");
		
		for(UIComponent child : menu.getChildren()) {
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem item = (MenuItem) child;
				String menuItemClientId = item.getClientId(facesContext);
				String onclick = item.getOnclick();
				
				if(!firstMenuitem)
					writer.write(",");
				else
					firstMenuitem = false;
				
				writer.write("{");
				writer.write("text:'" + (String) item.getValue() + "'");
				
				if(item.getUrl() != null) {
					writer.write(",url:'" + getResourceURL(facesContext, item.getUrl() + "'"));
					if(item.getTarget() != null) writer.write(",target:'"+ item.getTarget() + "'");
					if(onclick != null) writer.write(",onclick:{fn:function() {" + onclick + "}}");
				} else {
					if(form == null) {
						throw new FacesException("ContextMenu : '" + menu.getClientId(facesContext) + "' must be inside a form element");
					}
					String formClientId = form.getClientId(facesContext);
					
					String command = item.isAjax() ? buildAjaxRequest(facesContext, item, formClientId, menuItemClientId) : buildNonAjaxRequest(facesContext, item, formClientId, menuItemClientId);
					command = onclick == null ? command : onclick + ";" + command;
					
					writer.write(",onclick:{fn: function() {" + command + "}}");
				}
				
				writer.write("}");
			}
		}
		writer.write("]);");
	}

	protected void encodeMarkup(FacesContext facesContext, ContextMenu menu) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("div", menu);
		writer.writeAttribute("id", menu.getClientId(facesContext), "id");
		if(menu.getStyle() != null) writer.writeAttribute("style", menu.getStyle(), "style");
		if(menu.getStyleClass() != null) writer.writeAttribute("class", menu.getStyle(), "styleClass");
		
		writer.endElement("div");
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