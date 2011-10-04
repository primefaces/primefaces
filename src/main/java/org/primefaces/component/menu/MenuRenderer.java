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
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;

public class MenuRenderer extends BaseMenuRenderer {

    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
		String widgetVar = menu.resolveWidgetVar();
        String position = menu.getPosition();

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        
        writer.write("$(function() {");

		writer.write(widgetVar + " = new PrimeFaces.widget.Menu('" + clientId + "',{");

        writer.write("position:'" + position + "'");
        writer.write(",type:'" + menu.getType() + "'");

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

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        boolean tiered = !menu.getType().equalsIgnoreCase("plain");
        boolean dynamic = menu.getPosition().equals("dynamic");
        
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = dynamic ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass+ " " + styleClass;

        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
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

        writer.endElement("div");
	}

    protected void encodeTieredMenuContent(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for(Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {

                writer.startElement("li", null);
                
                if(child instanceof MenuItem) {
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    encodeMenuItem(context, (MenuItem) child);
                } 
                else if(child instanceof Submenu) {
                    writer.writeAttribute("class", Menu.TIERED_SUBMENU_CLASS, null);
                    encodeTieredSubmenu(context, (Submenu) child);
                }

                writer.endElement("li");
            }
        }
    }

	protected void encodeTieredSubmenu(FacesContext context, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String label = submenu.getLabel();

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Menu.MENUITEM_LINK_CLASS, null);

        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", icon + " " + Menu.MENUITEM_ICON_CLASS, null);
            writer.endElement("span");
        }

        if(label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.write(submenu.getLabel());
            writer.endElement("span");
        }
        
        writer.startElement("span", null);
        writer.writeAttribute("class", Menu.SUBMENU_ICON_CLASS, null);
        writer.endElement("span");

        writer.endElement("a");

        //submenus and menuitems
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);

			encodeTieredMenuContent(context, submenu);

			writer.endElement("ul");
		}
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
                
            }
        }
    }

    protected void encodePlainSubmenu(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", Menu.SUBMENU_TITLE_CLASS, null);
        writer.startElement("h3", null);
        if(label != null) {
            writer.write(label);
        }
        writer.endElement("h3");
        writer.endElement("li");

        encodePlainMenuContent(context, submenu);
	}
}