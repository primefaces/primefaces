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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;

public class TieredMenuRenderer extends BaseMenuRenderer {

	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        TieredMenu menu = (TieredMenu) abstractMenu;
		String clientId = menu.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('TieredMenu','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",autoDisplay:" + menu.isAutoDisplay());
        
        writer.write("});");
        
		endScript(writer);        	
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        TieredMenu menu = (TieredMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? TieredMenu.CONTAINER_CLASS : TieredMenu.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menubar", null);

		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

		encodeTieredMenuContent(context, menu);
		
		writer.endElement("ul");

        writer.endElement("div");
	}
}