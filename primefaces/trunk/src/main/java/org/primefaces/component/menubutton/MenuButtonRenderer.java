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
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.util.ComponentUtils;

public class MenuButtonRenderer extends BaseMenuRenderer {

   protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        MenuButton button = (MenuButton) abstractMenu;
		String clientId = button.getClientId(context);
		String buttonId = clientId + "_button";
		String menuId = clientId + "_menu";
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? MenuButton.CONTAINER_CLASS : MenuButton.CONTAINER_CLASS + " " + styleClass; 
		
		writer.startElement("div", button);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "class");

		if(button.getStyle() != null) 
            writer.writeAttribute("style", button.getStyle(), "style");

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
        writer.startElement("div", null);
        writer.writeAttribute("id", menuId, null);
		writer.writeAttribute("class", Menu.DYNAMIC_CONTAINER_CLASS, "styleClass");
        
        writer.startElement("ul", null);
		writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

		for(UIComponent child : button.getChildren()) {
			MenuItem item = (MenuItem) child;

			if(item.isRendered()) {
                writer.startElement("li", item);
                writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                encodeMenuItem(context, item);
                writer.endElement("li");
			}
		}

		writer.endElement("ul");
		writer.endElement("div");
        writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        MenuButton button = (MenuButton) abstractMenu;
		String clientId = button.getClientId(context);
		
		UIComponent form = ComponentUtils.findParentForm(context, button);
		if(form == null) {
			throw new FacesException("MenuButton : \"" + clientId + "\" must be inside a form element");
		}
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('MenuButton','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        if(button.isDisabled()) {
			writer.write(",disabled:true");
		}

 		writer.write("});});");
		
		endScript(writer);
	}
}