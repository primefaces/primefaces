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

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
		MenuButton button = (MenuButton) component;
		
		if(button.shouldBuildFromModel()) {
			button.buildMenuFromModel();
		}
		
		encodeMarkup(context, button);
		encodeScript(context, button);
	}
	
	protected void encodeMarkup(FacesContext context, MenuButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		String buttonId = clientId + "_button";
        String menuId = clientId + "_menu";
		
		writer.startElement("span", button);
		writer.writeAttribute("id", clientId, "id");
        
		if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), "class");
		if(button.getStyle() != null) writer.writeAttribute("style", button.getStyle(), "style");

        //button
		writer.startElement("button", null);
		writer.writeAttribute("id", buttonId, null);
		writer.writeAttribute("name", buttonId, null);
		writer.writeAttribute("type", "button", null);
		if(button.getValue() != null) {
			writer.write(button.getValue());
		}
		writer.endElement("button");

        //menu
        writer.startElement("ul", null);
		writer.writeAttribute("id", menuId, null);

		for(UIComponent child : button.getChildren()) {
			MenuItem item = (MenuItem) child;

			if(item.isRendered()) {
                writer.startElement("li", item);
                encodeMenuItem(context, item);
                writer.endElement("li");
			}
		}

		writer.endElement("ul");
		
		writer.endElement("span");
	}

	protected void encodeScript(FacesContext context, MenuButton button) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		
		UIComponent form = ComponentUtils.findParentForm(context, button);
		if(form == null) {
			throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
		}
		
		String formClientId = form.getClientId(context);
		
		writer.startElement("script", button);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(button.resolveWidgetVar() + " = new PrimeFaces.widget.MenuButton('" + clientId + "', {");

        writer.write("animated:'" + button.getEffect() + "'");
        writer.write(",zindex:" + button.getZindex());

        if(button.getEffectDuration() != 400) {
            writer.write(",showDuration:" + button.getEffectDuration());
            writer.write(",hideDuration:" + button.getEffectDuration());
        }

        if(button.isDisabled()) {
			writer.write(",disabled:true");
		}

 		writer.write("});");
		
		writer.endElement("script");
	}

    protected void encodeMenuItem(FacesContext context, MenuItem menuItem) throws IOException {
		String clientId = menuItem.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();

		if(menuItem.shouldRenderChildren()) {
			renderChildren(context, menuItem);
		}
        else {
            writer.startElement("a", null);

			if(menuItem.getUrl() != null) {
				writer.writeAttribute("href", getResourceURL(context, menuItem.getUrl()), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
                
			} else {
				writer.writeAttribute("href", "javascript:void(0)", null);

				UIComponent form = ComponentUtils.findParentForm(context, menuItem);
				if(form == null) {
					throw new FacesException("Menu must be inside a form element");
				}

				String formClientId = form.getClientId(context);
				String command = menuItem.isAjax() ? buildAjaxRequest(context, menuItem, formClientId, clientId) : buildNonAjaxRequest(context, menuItem, formClientId, clientId);

				command = menuItem.getOnclick() == null ? command : menuItem.getOnclick() + ";" + command;

				writer.writeAttribute("onclick", command, null);
			}

			if(menuItem.getValue() != null)
                writer.write((String) menuItem.getValue());

            writer.endElement("a");
		}
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