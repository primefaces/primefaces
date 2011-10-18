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
package org.primefaces.component.menu;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;
import org.primefaces.component.submenu.Submenu;

public class MenuRenderer extends BaseMenuRenderer {

    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
		String widgetVar = menu.resolveWidgetVar();
        String position = menu.getPosition();

		startScript(writer, clientId);
        
        writer.write("$(function() {");

		writer.write(widgetVar + " = new PrimeFaces.widget.Menu('" + clientId + "',{");

        writer.write("position:'" + position + "'");
        writer.write(",type:'" + menu.getType() + "'");
        
        if(menu.getEasing() != null)
            writer.write(",easing:'" + menu.getEasing() + "'");
        
        //dynamic position
        if(position.equalsIgnoreCase("dynamic")) {
           writer.write(",my:'" + menu.getMy() + "'");
           writer.write(",at:'" + menu.getAt() + "'");

            UIComponent trigger = menu.findComponent(menu.getTrigger());
            String triggerClientId = trigger == null ? menu.getTrigger() : trigger.getClientId(context);
            
            writer.write(",trigger:'" + triggerClientId + "'");
            writer.write(",triggerEvent:'" + menu.getTriggerEvent() + "'");
        }

        writer.write("});});");

		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        boolean tiered = !menu.getType().equalsIgnoreCase("plain");
        boolean sliding = menu.getType().equalsIgnoreCase("sliding");
        boolean dynamic = menu.getPosition().equals("dynamic");
        
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = dynamic ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass+ " " + styleClass;

        if(sliding){
            styleClass += " " + Menu.MENU_SLIDING_CLASS;
        }
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if(sliding){
            writer.startElement("div", menu);
            writer.writeAttribute("class", Menu.MENU_SLIDING_SCROLL_CLASS, "scroll");
            writer.startElement("div", menu);
            writer.writeAttribute("class", Menu.MENU_SLIDING_STATE_CLASS, "state");
            writer.startElement("div", menu);
            writer.writeAttribute("class", Menu.MENU_SLIDING_WRAPPER_CLASS, "wrapper");
            writer.startElement("div", menu);
            writer.writeAttribute("class", Menu.MENU_SLIDING_CONTENT_CLASS, "sliding_content");
            writer.startElement("div", menu);
        }
        
		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        if(tiered) {
            encodeTieredMenuContent(context, menu);
        }
        else {
            encodePlainMenuContent(context, menu);
        }

		writer.endElement("ul");

        if(sliding){
            writer.endElement("div");
            writer.endElement("div");
            writer.endElement("div");
            writer.endElement("div");
            writer.endElement("div");
            writer.startElement("div", menu);
            writer.writeAttribute("class", Menu.BACKWARD_CLASS, style);
            
            writer.startElement("span", menu);
            writer.writeAttribute("class", Menu.BACKWARD_ICON_CLASS, style);
            writer.endElement("span");
            
            writer.write("back");
            writer.endElement("div");
        }
        
        writer.endElement("div");
	}

    protected void encodePlainMenuContent(FacesContext context, UIComponent component) throws IOException{
		ResponseWriter writer = context.getResponseWriter();

        for(Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {

                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                } 
                else if(child instanceof Submenu) {
                    encodePlainSubmenu(context, (Submenu) child);
                }
                else if(child instanceof Separator) {
                    encodeSeparator(context, (Separator) child);
                }
            }
        }
    }

    protected void encodePlainSubmenu(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("h3", null);
        if(label != null) {
            writer.writeText(label, "value");
        }
        writer.endElement("h3");
        
        writer.endElement("li");

        encodePlainMenuContent(context, submenu);
	}
}