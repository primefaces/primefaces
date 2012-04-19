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
package org.primefaces.component.tieredmenu;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;
import org.primefaces.component.submenu.Submenu;

public class TieredMenuRenderer extends BaseMenuRenderer {

	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        TieredMenu menu = (TieredMenu) abstractMenu;
		String clientId = menu.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('TieredMenu','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",autoDisplay:" + menu.isAutoDisplay());
        
        if(menu.isOverlay()) {
            encodeOverlayConfig(context, menu);
        }
        
        writer.write("});");
        
		endScript(writer);        	
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        TieredMenu menu = (TieredMenu) abstractMenu;
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? TieredMenu.DYNAMIC_CONTAINER_CLASS : TieredMenu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        encodeMenu(context, menu, style, styleClass, "menu");
	}
    
    protected void encodeMenu(FacesContext context, AbstractMenu component, String style, String styleClass, String role) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menubar", null);

		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

		encodeMenuContent(context, component);
		
		writer.endElement("ul");

        writer.endElement("div");
    }
    
    protected void encodeMenuContent(FacesContext context, UIComponent component) throws IOException {
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
                    Submenu submenu = (Submenu) child;
                    String style = submenu.getStyle();
                    String styleClass = submenu.getStyleClass();
                    styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;
        
                    writer.startElement("li", null);
                    writer.writeAttribute("class", styleClass, null);
                    if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }
                    writer.writeAttribute("role", "menuitem", null);
                    writer.writeAttribute("aria-haspopup", "true", null);
                    encodeSubmenu(context, (Submenu) child);
                    writer.endElement("li");
                } 
                else if(child instanceof Separator) {
                    encodeSeparator(context, (Separator) child);
                }
            }
        }
    }
    
    protected void encodeSubmenu(FacesContext context, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String label = submenu.getLabel();

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Menu.MENUITEM_LINK_CLASS, null);

        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        if(label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(submenu.getLabel(), "value");
            writer.endElement("span");
        }
        
        encodeSubmenuIcon(context, submenu);

        writer.endElement("a");

        //submenus and menuitems
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
            writer.writeAttribute("role", "menu", null);

			encodeMenuContent(context, submenu);

			writer.endElement("ul");
		}
	}
    
    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("span", null);
        writer.writeAttribute("class", Menu.SUBMENU_RIGHT_ICON_CLASS, null);
        writer.endElement("span");
    }
}