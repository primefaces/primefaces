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
package org.primefaces.component.panelmenu;

import java.io.IOException;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class PanelMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        PanelMenu menu = (PanelMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PanelMenu", menu.resolveWidgetVar(), clientId)
            .attr("stateful", menu.isStateful());
        wb.finish();
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
        
        if(menu.getElementsCount() > 0) {
            List<MenuElement> elements = menu.getElements();
            
            for(MenuElement element : elements) {
                if(element.isRendered() && element instanceof Submenu) {
                    encodeRootSubmenu(context, menu, (Submenu) element);
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
        boolean expanded = submenu.isExpanded();
        String headerClass = expanded ? PanelMenu.ACTIVE_HEADER_CLASS : PanelMenu.INACTIVE_HEADER_CLASS;
        String headerIconClass = expanded ? PanelMenu.ACTIVE_TAB_HEADER_ICON_CLASS : PanelMenu.INACTIVE_TAB_HEADER_ICON_CLASS;
        String contentClass = expanded ? PanelMenu.ACTIVE_ROOT_SUBMENU_CONTENT : PanelMenu.INACTIVE_ROOT_SUBMENU_CONTENT;
        
        //wrapper
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        //header
        writer.startElement("h3", null);
        writer.writeAttribute("class", headerClass, null);
        writer.writeAttribute("role", "tab", null);

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", headerIconClass, null);
        writer.endElement("span");

        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("tabindex", "-1", null);
        writer.writeText(submenu.getLabel(), null);
        writer.endElement("a");

        writer.endElement("h3");

        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", contentClass, null);
        writer.writeAttribute("role", "tabpanel", null);
        writer.writeAttribute("id", menu.getClientId(context) + "_" + submenu.getId(), null);
        
        if(submenu.getElementsCount() > 0) {
            List<MenuElement> elements = submenu.getElements();
            
            writer.startElement("ul", null);
            writer.writeAttribute("class", PanelMenu.LIST_CLASS, null);

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
                        encodeDescendantSubmenu(context, menu, (Submenu) element);
                    }
                }
            }
            
            writer.endElement("ul");
        }

        writer.endElement("div");   //content
        
        writer.endElement("div");   //wrapper
    }

    protected void encodeDescendantSubmenu(FacesContext context, PanelMenu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? PanelMenu.DESCENDANT_SUBMENU_CLASS : PanelMenu.DESCENDANT_SUBMENU_CLASS + " " + styleClass;
        boolean expanded = submenu.isExpanded();
        String toggleIconClass = expanded ? PanelMenu.DESCENDANT_SUBMENU_EXPANDED_ICON_CLASS : PanelMenu.DESCENDANT_SUBMENU_COLLAPSED_ICON_CLASS;
        String listClass = expanded ? PanelMenu.DESCENDANT_SUBMENU_EXPANDED_LIST_CLASS :PanelMenu.DESCENDANT_SUBMENU_COLLAPSED_LIST_CLASS;
        boolean hasIcon = (icon != null);
        String linkClass = (hasIcon) ? PanelMenu.MENUITEM_LINK_WITH_ICON_CLASS : PanelMenu.MENUITEM_LINK_CLASS;
        
        writer.startElement("li", null);
        writer.writeAttribute("id", submenu.getClientId(), null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        writer.writeAttribute("role", "menuitem", null);
        
        writer.startElement("a", null);
        writer.writeAttribute("class", linkClass, null);
        
        //toggle icon
        writer.startElement("span", null);
        writer.writeAttribute("class", toggleIconClass, null); 
        writer.endElement("span");
        
        //user icon
        if(hasIcon) {
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
        if(submenu.getElementsCount() > 0) {
            List<MenuElement> elements = submenu.getElements();
            
            writer.startElement("ul", null);
            writer.writeAttribute("class", listClass, null);

            for(MenuElement element : elements) {
                if(element.isRendered()) {
                    if(element instanceof MenuItem) {
                        writer.startElement("li", null);
                        writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                        writer.writeAttribute("role", "menuitem", null);
                        encodeMenuItem(context, menu, (MenuItem) element);
                        writer.endElement("li");
                    }
                    else if(element instanceof Submenu) {
                        encodeDescendantSubmenu(context, menu, (Submenu) element);
                    }
                }
            }

            writer.endElement("ul");
        }
        
        writer.endElement("li");
    }
}
