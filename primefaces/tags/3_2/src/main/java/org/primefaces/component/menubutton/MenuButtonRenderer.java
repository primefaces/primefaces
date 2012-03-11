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
import org.primefaces.component.separator.Separator;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class MenuButtonRenderer extends BaseMenuRenderer {

   protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        MenuButton button = (MenuButton) abstractMenu;
		String clientId = button.getClientId(context);
        String styleClass = button.getStyleClass();
        styleClass = styleClass == null ? MenuButton.CONTAINER_CLASS : MenuButton.CONTAINER_CLASS + " " + styleClass;
        boolean disabled = button.isDisabled();
		
		writer.startElement("span", button);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "class");
        if(button.getStyle() != null) {
            writer.writeAttribute("style", button.getStyle(), "style");
        }

        encodeButton(context, button, clientId + "_button", disabled);
        if(!disabled) {
            encodeMenu(context, button, clientId + "_menu");
        }
        
        writer.endElement("span");
	}
   
    protected void encodeButton(FacesContext context, MenuButton button, String buttonId, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String value = button.getValue();
        String buttonClass = disabled ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " ui-state-disabled" : HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS;
        
        writer.startElement("button", null);
		writer.writeAttribute("id", buttonId, null);
		writer.writeAttribute("name", buttonId, null);
		writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", buttonClass, buttonId);
        if(button.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        
        //button icon
        String iconClass = HTML.BUTTON_LEFT_ICON_CLASS + " ui-icon-triangle-1-s";
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        
        if(value == null)
            writer.write("ui-button");
        else
            writer.writeText(value, "value");
        
        writer.endElement("span");

		writer.endElement("button");
    }
    
    protected void encodeMenu(FacesContext context, MenuButton button, String menuId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
            writer.writeAttribute("id", menuId, null);
            writer.writeAttribute("class", Menu.DYNAMIC_CONTAINER_CLASS, "styleClass");
            writer.writeAttribute("role", "menu", null);

            writer.startElement("ul", null);
            writer.writeAttribute("class", MenuButton.LIST_CLASS, "styleClass");

            for(UIComponent child : button.getChildren()) {
                if(child.isRendered()) {
                    if(child instanceof MenuItem) {
                        MenuItem item = (MenuItem) child;
                        
                        writer.startElement("li", item);
                        writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                        writer.writeAttribute("role", "menuitem", null);
                        encodeMenuItem(context, item);
                        writer.endElement("li");
                    }
                    else if(child instanceof Separator) {
                        encodeSeparator(context, (Separator) child);
                    }
                }
            }

            writer.endElement("ul");
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
 		writer.write("});});");
		
		endScript(writer);
	}
}