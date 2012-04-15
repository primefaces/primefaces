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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;

public class ContextMenuRenderer extends BaseMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(context);
		String target = findTarget(context, menu);
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('ContextMenu','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");     
        writer.write(",target:" + target);
 
        if(menu.getNodeType() != null) {
            writer.write(",nodeType:'" + menu.getNodeType() + "'");
        }
        
        writer.write("});});");
		
		endScript(writer);
	}
	
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? ContextMenu.CONTAINER_CLASS : ContextMenu.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        writer.writeAttribute("role", "menu", null);

		writer.startElement("ul", null);
        writer.writeAttribute("class", ContextMenu.LIST_CLASS, null);

		encodeTieredMenuContent(context, menu);

		writer.endElement("ul");
        
        writer.endElement("div");
	}

    protected String findTarget(FacesContext context, ContextMenu menu) {
		String trigger = null;
		String _for = menu.getFor();

		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);

			if(forComponent == null)
				throw new FacesException("Cannot find component '" + _for + "' in view.");
			else 
                return "'" + forComponent.getClientId(context) + "'";
		}
        else {
            return "document";
        }
	}
}