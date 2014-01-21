/*
 * Copyright 2009-2013 PrimeTek.
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

public class MenuRenderer extends org.primefaces.component.menu.MenuRenderer {
    
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Menu menu = (Menu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        
        writer.startElement("ul", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("data-role", "listview", null);
        renderDynamicPassThruAttributes(context, menu);
        if (style != null) writer.writeAttribute("style", style, "style");
        if (styleClass != null)  writer.writeAttribute("class", styleClass, "styleClass");

        if (menu.getElementsCount() > 0) {
            encodeElements(context, menu, menu.getElements());
        }
        
        writer.endElement("ul");
    }
    
    @Override
    protected void encodeSubmenu(FacesContext context, Menu menu, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        
        writer.startElement("li", null);
        writer.writeAttribute("data-role", "list-divider", "style");
        if(style != null)  writer.writeAttribute("style", style, "style");
        if(styleClass != null)  writer.writeAttribute("class", styleClass, "styleClass");
        
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

        //title
        writer.startElement("li", null);
        writer.writeAttribute("data-role", "list-divider", "style");
        if(style != null)  writer.writeAttribute("style", style, "style");
        if(styleClass != null)  writer.writeAttribute("class", styleClass, "styleClass");   
                
        writer.endElement("li");
	}
    
    @Override
    protected void encodeElements(FacesContext context, Menu menu, List<MenuElement> elements) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        
        for(MenuElement element : elements) {
            if(element.isRendered()) {
                if(element instanceof MenuItem) {
                    MenuItem item = (MenuItem) element;
                    String icon = item.getIcon();
                    
                    writer.startElement("li", null);
                    if(icon != null) {
                        writer.writeAttribute("data-icon", icon, null);
                    }
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

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        //no widget required
    }
}
