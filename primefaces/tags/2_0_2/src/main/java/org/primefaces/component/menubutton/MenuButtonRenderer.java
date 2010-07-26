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
package org.primefaces.component.menubutton;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class MenuButtonRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
		MenuButton button = (MenuButton) component;
		
		if(button.isDynamic() && !isPostBack()) {
			button.buildMenuFromModel();
		}
		
		encodeMarkup(facesContext, button);
		encodeScript(facesContext, button);
	}

	private void encodeScript(FacesContext facesContext, MenuButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String var = createUniqueWidgetVar(facesContext, button);
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, button);
		if(form == null) {
			throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
		}
		
		String formClientId = form.getClientId(facesContext);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(var + " = new PrimeFaces.widget.MenuButton('" + clientId + "', {");
		
		boolean firstFn = true;
		writer.write("commands:[");
		for(UIComponent child : button.getChildren()) {
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem menuItem = (MenuItem) child;
				String menuItemClientId = menuItem.getClientId(facesContext);
				String command = null;
				
				if(menuItem.getUrl() != null) {
					command = "window.location.href = '" + menuItem.getUrl() + "'";
				} else {
					command = menuItem.isAjax() ? buildAjaxRequest(facesContext, menuItem, formClientId, menuItemClientId) : buildNonAjaxRequest(facesContext, menuItem, formClientId, menuItemClientId);
				}
				
				if(!firstFn) {
					writer.write(",");
				} else {
					firstFn = false;
				}
				
				writer.write("function() {" + command + "}");
			}
		}
		writer.write("]");

 		writer.write("});");
		
		writer.endElement("script");
	}
	
	private void encodeMarkup(FacesContext facesContext, MenuButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String inputId = clientId + "_input";
		String selectId = clientId + "_select";
		
		writer.startElement("span", button);
		writer.writeAttribute("id", clientId, "id");
		String style = button.getStyle() != null ? "display:none;" + button.getStyle() : "display:none;";
		
		writer.writeAttribute("style", style, "style");
		if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), "class");
		
		writer.startElement("input", null);
		writer.writeAttribute("id", inputId, null);
		writer.writeAttribute("name", inputId, null);
		writer.writeAttribute("type", "button", null);
		writer.writeAttribute("value", button.getValue(), null);
		writer.endElement("input");
		
		writer.startElement("select", null);
		writer.writeAttribute("id", selectId, null);
		writer.writeAttribute("name", selectId, null);
		
		for(UIComponent kid : button.getChildren()) {
			if(kid instanceof MenuItem && kid.isRendered()) {
				MenuItem menuItem = (MenuItem) kid;
				
				writer.startElement("option", null);
				writer.writeAttribute("value", menuItem.getClientId(facesContext), null);
				writer.write((String) menuItem.getValue());
				writer.endElement("option");
			}
		}
		
		writer.endElement("select");
		
		writer.endElement("span");
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}	
}