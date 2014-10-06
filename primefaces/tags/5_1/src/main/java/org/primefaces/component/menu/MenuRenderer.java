/*
 * Copyright 2009-2014 PrimeTek.
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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class MenuRenderer extends BaseMenuRenderer {

    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("PlainMenu", menu.resolveWidgetVar(), clientId)
            .attr("toggleable", menu.isToggleable(), false);
        
        if(menu.isOverlay()) {
            encodeOverlayConfig(context, menu, wb);
        }

        wb.finish();
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? Menu.DYNAMIC_CONTAINER_CLASS : Menu.STATIC_CONTAINER_CLASS;
        if(menu.isToggleable()) {
            defaultStyleClass = defaultStyleClass + " " + Menu.TOGGLEABLE_MENU_CLASS;
        }
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);
        
        encodeKeyboardTarget(context, menu);

        if(menu.getElementsCount() > 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.LIST_CLASS, null);
            encodeElements(context, menu, menu.getElements());
            writer.endElement("ul");
        }

        writer.endElement("div");
	}

    protected void encodeElements(FacesContext context, Menu menu, List<MenuElement> elements) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        
        for(MenuElement element : elements) {
            if(element.isRendered()) {
                if(element instanceof MenuItem) {
                    MenuItem menuItem = (MenuItem) element;
                    String containerStyle = menuItem.getContainerStyle();
                    String containerStyleClass = menuItem.getContainerStyleClass();
                    containerStyleClass = (containerStyleClass == null) ? Menu.MENUITEM_CLASS: Menu.MENUITEM_CLASS + " " + containerStyleClass; 
                            
                    writer.startElement("li", null);
                    writer.writeAttribute("class", containerStyleClass, null);
                    writer.writeAttribute("role", "menuitem", null);
                    if(containerStyle != null) {
                        writer.writeAttribute("style", containerStyle, null);
                    }
                    encodeMenuItem(context, menu, menuItem);
                    writer.endElement("li");
                }
                else if(element instanceof Submenu) {
                    encodeSubmenu(context, menu, (Submenu) element);
                }
                else if(element instanceof Separator) {
                    encodeSeparator(context, (Separator) element);
                }
            }
        }
    }

    protected void encodeSubmenu(FacesContext context, Menu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;
        boolean toggleable = menu.isToggleable();

        //title
        writer.startElement("li", null);
        if(toggleable) {
            writer.writeAttribute("id", submenu.getClientId(), null);
        }
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("h3", null);

        if(menu.isToggleable()) {
            encodeIcon(context, label);
        }
        
        if(label != null) {
            writer.writeText(label, "value");
        }
        
        writer.endElement("h3");
        
        writer.endElement("li");

        encodeElements(context, menu, submenu.getElements());          
	}
    
     protected void encodeIcon(FacesContext context, String label) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("span", null);
        writer.writeAttribute("class", Menu.EXPANDED_SUBMENU_HEADER_ICON_CLASS, null);
        writer.endElement("span");
    }
}