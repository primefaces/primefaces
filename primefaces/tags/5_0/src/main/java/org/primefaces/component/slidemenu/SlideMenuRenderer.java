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
package org.primefaces.component.slidemenu;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.util.WidgetBuilder;

public class SlideMenuRenderer extends TieredMenuRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        SlideMenu menu = (SlideMenu) abstractMenu;
		String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("SlideMenu", menu.resolveWidgetVar(), clientId);
        
        if(menu.isOverlay()) {
            encodeOverlayConfig(context, menu, wb);
        }

        wb.finish();
	}

    @Override
	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        SlideMenu menu = (SlideMenu) abstractMenu;
        
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        String defaultStyleClass = menu.isOverlay() ? SlideMenu.DYNAMIC_CONTAINER_CLASS : SlideMenu.STATIC_CONTAINER_CLASS;
        styleClass = styleClass == null ?  defaultStyleClass : defaultStyleClass + " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", menu.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menu", null);
        
        //wrapper
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.WRAPPER_CLASS, "styleClass");
        
        //content
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.CONTENT_CLASS, "styleClass");

        //root menu
        if(menu.getElementsCount() > 0) {
            writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.LIST_CLASS, null);
            encodeElements(context, abstractMenu, menu.getElements());
            writer.endElement("ul");
        }
        
        //content
        writer.endElement("div");
                
        //back navigator
        writer.startElement("div", menu);
        writer.writeAttribute("class", SlideMenu.BACKWARD_CLASS, null);
        writer.startElement("span", menu);
        writer.writeAttribute("class", SlideMenu.BACKWARD_ICON_CLASS, null);
        writer.endElement("span");
        writer.write(menu.getBackLabel());
        writer.endElement("div");
        
        //wrapper
        writer.endElement("div");

        writer.endElement("div");
	}
}