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
		
		if(button.shouldBuildFromModel()) {
			button.buildMenuFromModel();
		}
		
		encodeMarkup(facesContext, button);
		encodeScript(facesContext, button);
	}
	
	protected void encodeMarkup(FacesContext facesContext, MenuButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		String buttonId = clientId + "_button";
		
		writer.startElement("span", button);
		writer.writeAttribute("id", clientId, "id");	
		if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), "class");
		if(button.getStyle() != null) writer.writeAttribute("style", button.getStyle(), "style");
		
		writer.startElement("button", null);
		writer.writeAttribute("id", buttonId, null);
		writer.writeAttribute("name", buttonId, null);
		writer.writeAttribute("type", "button", null);
		writer.writeAttribute("onclick", button.resolveWidgetVar() + ".showMenu()", null);
		if(button.getValue() != null) {
			writer.write(button.getValue());
		}
		writer.endElement("button");
		
		writer.startElement("span", button);
		writer.writeAttribute("id", clientId + "_menuContainer", "id");
		writer.endElement("span");
		
		writer.endElement("span");
	}

	protected void encodeScript(FacesContext facesContext, MenuButton button) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = button.getClientId(facesContext);
		
		UIComponent form = ComponentUtils.findParentForm(facesContext, button);
		if(form == null) {
			throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
		}
		
		String formClientId = form.getClientId(facesContext);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(button.resolveWidgetVar() + " = new PrimeFaces.widget.MenuButton('" + clientId + "', {");
		
		boolean firstItem = true;
		writer.write("items:[");
		for(UIComponent child : button.getChildren()) {
			if(child instanceof MenuItem && child.isRendered()) {
				MenuItem item = (MenuItem) child;
				String itemClientId = item.getClientId(facesContext);
				
				if(!firstItem)
					writer.write(",");
				else
					firstItem = false;
				
				writer.write("{text:'" + item.getValue() + "'");
				
				if(item.getUrl() != null) {
					writer.write(",url:'" + getResourceURL(facesContext, item.getUrl()) + "'");
				} else {
					String onclick = item.isAjax() ? buildAjaxRequest(facesContext, item, formClientId, itemClientId) : buildNonAjaxRequest(facesContext, item, formClientId, itemClientId);
					if(item.getOnclick() != null) {
						onclick = item.getOnclick() + ";" + onclick;
					}
						
					writer.write(",onclick:{fn:function() {" + onclick + "}}");
				}
				
				writer.write("}");
			}
		}
		writer.write("]");
		
		if(button.isDisabled()) {
			writer.write(",disabled:true");
		}

 		writer.write("});");
		
		writer.endElement("script");
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