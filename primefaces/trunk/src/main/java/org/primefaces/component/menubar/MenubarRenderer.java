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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;

public class MenubarRenderer extends BaseMenuRenderer {

	protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
		String clientId = menubar.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Menubar','" + menubar.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("});");

		endScript(writer);        	
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Menubar menubar = (Menubar) abstractMenu;
		String clientId = menubar.getClientId(context);
        String style = menubar.getStyle();
        String styleClass = menubar.getStyleClass();
        styleClass = styleClass == null ? Menubar.CONTAINER_CLASS : Menubar.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", menubar);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

		encodeTieredMenuContent(context, menubar);
		
		writer.endElement("ul");

        writer.endElement("div");
	}
}