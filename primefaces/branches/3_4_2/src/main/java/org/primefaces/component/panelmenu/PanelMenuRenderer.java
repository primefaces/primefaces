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
package org.primefaces.component.panelmenu;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;

public class PanelMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PanelMenu menu = (PanelMenu) abstractMenu;
		String clientId = menu.getClientId(context);
		String widgetVar = menu.resolveWidgetVar();
        
        startScript(writer, clientId);        
        writer.write("PrimeFaces.cw('PanelMenu','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("});");
		endScript(writer);
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PanelMenu menu = (PanelMenu) abstractMenu;
        String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ?  PanelMenu.CONTAINER_CLASS : PanelMenu.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);
        
        for(UIComponent child : menu.getChildren()) {
            if(child.isRendered()) {
                if(child instanceof Submenu) {
                    encodeRootSubmenu(context, menu, (Submenu) child);
                }
            }
        }
        
        writer.endElement("div");
    }

    protected void encodeRootSubmenu(FacesContext context, PanelMenu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? PanelMenu.PANEL_CLASS : PanelMenu.PANEL_CLASS + " " + styleClass;
        
        //wrapper
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        //header
        writer.startElement("h3", null);
        writer.writeAttribute("class", PanelMenu.HEADER_CLASS, null);
        writer.writeAttribute("role", "tab", null);

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", PanelMenu.HEADER_ICON_CLASS, null);
        writer.endElement("span");

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeText(submenu.getLabel(), null);
        writer.endElement("a");

        writer.endElement("h3");

        //content
        writer.startElement("div", null);
        writer.writeAttribute("id", submenu.getClientId(context), null);
        writer.writeAttribute("class", PanelMenu.ROOT_SUBMENU_CONTENT, null);
        writer.writeAttribute("role", "tabpanel", null);
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", PanelMenu.LIST_CLASS, null);

        for(UIComponent child : submenu.getChildren()) {
            if(child.isRendered()) {
                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                }
                else if(child instanceof Submenu) {
                    encodeDescendantSubmenu(context, (Submenu) child);
                }
            }
        }

		writer.endElement("ul");

        writer.endElement("div");   //content
        
        writer.endElement("div");   //wrapper
    }

    protected void encodeDescendantSubmenu(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? PanelMenu.DESCENDANT_SUBMENU_CLASS : PanelMenu.DESCENDANT_SUBMENU_CLASS + " " + styleClass;
        
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        writer.writeAttribute("role", "menuitem", null);
        
        writer.startElement("a", null);
        writer.writeAttribute("id", submenu.getClientId(context), null);
        writer.writeAttribute("class", PanelMenu.MENUITEM_LINK_CLASS, null);
        
        //toggle icon
        writer.startElement("span", null);
        writer.writeAttribute("class", PanelMenu.DESCENDANT_SUBMENU_COLLAPSED_ICON_CLASS, null); 
        writer.endElement("span");
        
        //user icon
        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", "ui-icon " + icon, null); 
            writer.endElement("span");
        }
        
        //submenu label
        writer.startElement("span", null);
        writer.writeAttribute("class", PanelMenu.MENUITEM_TEXT_CLASS, null); 
        writer.writeText(submenu.getLabel(), null);
        writer.endElement("span");
        
        writer.endElement("a");
        
        //submenu children
        writer.startElement("ul", null);
        writer.writeAttribute("class", PanelMenu.LIST_CLASS, null);
        
        for(UIComponent child : submenu.getChildren()) {
            if(child.isRendered()) {
                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                }
                else if(child instanceof Submenu) {
                    encodeDescendantSubmenu(context, (Submenu) child);
                }
            }
        }
        
        writer.endElement("ul");
        
        writer.endElement("li");
    }
    
}
