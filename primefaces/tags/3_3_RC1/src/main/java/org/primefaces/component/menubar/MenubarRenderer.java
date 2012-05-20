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
package org.primefaces.component.menubar;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;

public class MenubarRenderer extends TieredMenuRenderer {

    @Override
	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
		String clientId = menubar.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Menubar','" + menubar.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",autoDisplay:" + menubar.isAutoDisplay());
        
        writer.write("});");
        
		endScript(writer);        	
	}

    @Override
	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
        String style = menubar.getStyle();
        String styleClass = menubar.getStyleClass();
        styleClass = styleClass == null ? Menubar.CONTAINER_CLASS : Menubar.CONTAINER_CLASS + " " + styleClass;

        encodeMenu(context, menubar, style, styleClass, "menubar");
	}
    
    @Override
    protected void encodeSubmenuIcon(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getParent() instanceof Menubar ? Menu.SUBMENU_DOWN_ICON_CLASS : Menu.SUBMENU_RIGHT_ICON_CLASS;
        
        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");
    }
}