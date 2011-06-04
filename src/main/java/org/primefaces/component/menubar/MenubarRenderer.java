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
package org.primefaces.component.menubar;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;

public class MenubarRenderer extends BaseMenuRenderer {

    private final static Logger logger = Logger.getLogger(MenubarRenderer.class.getName());

	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
		String clientId = menubar.getClientId(context);
		String widgetVar = menubar.resolveWidgetVar();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(widgetVar + " = new PrimeFaces.widget.Menubar('" + clientId + "',{");

        writer.write("autoSubmenuDisplay:" + menubar.isAutoSubmenuDisplay());

        writer.write(",animation:{animated:'" + menubar.getEffect() + "',duration:" + menubar.getEffectDuration() + "}");

        if(menubar.getStyleClass() != null) writer.write(",styleClass:'" + menubar.getStyleClass() + "'");
        if(menubar.getStyle() != null) writer.write(",style:'" + menubar.getStyle() + "'");
		
        writer.write("});");

		writer.endElement("script");	
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
		String clientId = menubar.getClientId(context);

        writer.startElement("div", menubar);
        writer.writeAttribute("id", clientId, null);

		writer.startElement("ul", null);
		writer.writeAttribute("id", clientId + "_menu", null);

		encodeMenuContent(context, menubar);
		
		writer.endElement("ul");

        writer.endElement("div");
	}

    protected void encodeMenuContent(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for(Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {

                writer.startElement("li", null);

                if(child instanceof MenuItem) {
                    encodeMenuItem(context, (MenuItem) child);
                } else if(child instanceof Submenu) {
                    encodeSubmenu(context, (Submenu) child);
                }
                
                writer.endElement("li");
            }
        }
    }
	
	protected void encodeSubmenu(FacesContext context, Submenu submenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
		UIComponent labelFacet = submenu.getFacet("label");
        String icon = submenu.getIcon();

        //title
		if(labelFacet == null) {
            String label = submenu.getLabel();
            
			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:void(0)", null);

            if(icon != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", icon + " wijmo-wijmenu-icon-left", null);
                writer.endElement("span");
            }

            if(label != null) {
                writer.startElement("span", null);
                writer.writeAttribute("class", "wijmo-wijmenu-text", null);
                writer.write(submenu.getLabel());
                writer.endElement("span");
            }
			
			writer.endElement("a");
		}
        else {
            //backwards compatibility
            logger.info("label facet of a menubar item is deprecated, use a menuitem instead instead of a submenu.");
            encodeMenuItem(context, (MenuItem) labelFacet);
		}

        //submenus and menuitems
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
			
			encodeMenuContent(context, submenu);
			
			writer.endElement("ul");
		}
	}
}