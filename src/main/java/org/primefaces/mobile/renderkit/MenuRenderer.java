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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.Separator;
import org.primefaces.model.menu.Submenu;
import org.primefaces.util.WidgetBuilder;

public class MenuRenderer extends BaseMenuRenderer {
    
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = (styleClass == null) ? Menu.MOBILE_CONTAINER_CLASS: Menu.MOBILE_CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("ul", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        renderDynamicPassThruAttributes(context, menu);

        if (menu.getElementsCount() > 0) {
            encodeElements(context, menu, menu.getElements());
        }
        
        writer.endElement("ul");
    }
    
    protected void encodeSubmenu(FacesContext context, Menu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = (styleClass == null) ? Menu.MOBILE_DIVIDER_CLASS: Menu.MOBILE_DIVIDER_CLASS + " " + styleClass;
        
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        if(label != null) {
            writer.writeText(label, "value");
        }
        
        writer.endElement("li");
        
        encodeElements(context, menu, submenu.getElements());
	}
    
    @Override
    protected void encodeSeparator(FacesContext context, Separator separator) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = separator.getStyle();
        String styleClass = separator.getStyleClass();
        styleClass = (styleClass == null) ? Menu.MOBILE_DIVIDER_CLASS: Menu.MOBILE_DIVIDER_CLASS + " " + styleClass;

        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, "styleClass");   
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
                
        writer.endElement("li");
	}
    
    protected void encodeElements(FacesContext context, Menu menu, List<MenuElement> elements) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        int elementCount = elements.size();
        
        for (int i = 0; i < elementCount; i++) {
            MenuElement element = elements.get(i);
            
            if(element.isRendered()) {                
                if(element instanceof MenuItem) {
                    writer.startElement("li", null);
                    encodeMenuItem(context, menu, (MenuItem) element);
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

    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("PlainMenu", menu.resolveWidgetVar(), clientId);
        wb.finish();
	}
    
    @Override
    protected String getLinkStyleClass(MenuItem menuitem) {
        String icon = menuitem.getIcon();
        if(icon == null) {
            icon = "ui-icon-carat-r";
        }
        String iconPos = menuitem.getIconPos();
        iconPos = (iconPos == null) ? "ui-btn-icon-right": "ui-btn-icon-" + iconPos;
        String styleClass = AbstractMenu.MOBILE_MENUITEM_LINK_CLASS + " " + icon + " " + iconPos;
        String userStyleClass = menuitem.getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
        
        return styleClass;
    }
}
