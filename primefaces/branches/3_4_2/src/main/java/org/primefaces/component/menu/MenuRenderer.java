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

		startScript(writer, clientId);
        
        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('PlainMenu','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(menu.isOverlay()) {
            encodeOverlayConfig(context, menu);
        }

        writer.write("});});");

		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass+ " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);
        
		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

        encodeContent(context, menu);

		writer.endElement("ul");
        
        writer.endElement("div");
	}

    protected void encodeContent(FacesContext context, UIComponent component) throws IOException{
		ResponseWriter writer = context.getResponseWriter();

        for(Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {

                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                } 
                else if(child instanceof Submenu) {
                    encodeSubmenu(context, (Submenu) child);
                }
                else if(child instanceof Separator) {
                    encodeSeparator(context, (Separator) child);
                }
            }
        }
    }

    protected void encodeSubmenu(FacesContext context, Submenu submenu) throws IOException {
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

        encodeContent(context, submenu);
	}
}